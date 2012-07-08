/*
 * Copyright (c) 2012, Eka Heksanov Lie
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ehxnv.util.config;

import com.ehxnv.util.config.annotation.Config;
import com.ehxnv.util.config.annotation.Property;
import com.ehxnv.util.config.extractor.ConfigPropertyNameExtractor;
import com.ehxnv.util.config.extractor.JavaBeanPropertyNameExtractor;
import com.ehxnv.util.config.translator.ConfigPropertyNameTranslator;
import com.ehxnv.util.config.translator.HypenedPropertyNameTranslator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Responsible for resolving {@link ConfigMetadata} from a given configuration interface.
 *
 * @author Eka Lie
 * @param <T> configuration interface type
 */
final class JavaBeanMetadataResolver<T> {

    /** Default translation strategy used when no extraction strategy is defined e.g. configuration interface is a plain Java interface. **/
    private static final Class<? extends ConfigPropertyNameTranslator> DEFAULT_TRANSLATOR_CLASS = HypenedPropertyNameTranslator.class;
    /** Default extraction strategy used when no extraction strategy is defined e.g. configuration interface is a plain Java interface. **/
    private static final Class<? extends ConfigPropertyNameExtractor> DEFAULT_EXTRACTOR_CLASS = JavaBeanPropertyNameExtractor.class;

    /**
     * Resolve {@link ConfigMetadata} from given configuration interface.
     * @param configInterface configuration interface
     * @return configuration metadata for the interface
     */
    public ConfigMetadata resolveMetadata(final Class<T> configInterface) {
        Set<ConfigProperty> configProperties = new HashSet<ConfigProperty>();
        Class<? extends ConfigPropertyNameExtractor> configPropertyNameExtractorClass = DEFAULT_EXTRACTOR_CLASS;
        Class<? extends ConfigPropertyNameTranslator> configPropertyNameTranslatorClass = DEFAULT_TRANSLATOR_CLASS;

        // check if configuration interface annotated with @Config annotation
        Config configAnnotation = configInterface.getAnnotation(Config.class);
        if (configAnnotation != null) {
            // use the extraction and translation strategies if given
            configPropertyNameExtractorClass = configAnnotation.extractor();
            configPropertyNameTranslatorClass = configAnnotation.translator();
        }

        ConfigPropertyNameExtractor configPropertyNameExtractor = ClassUtil.newInstance(configPropertyNameExtractorClass);

        Method[] methods = configInterface.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class methodReturnType = method.getReturnType();

            if (configPropertyNameExtractor.isValidMethodForExtraction(method)) {

                // check if custom validator is passed through @Property annotation
                Property propertyAnnotation = method.getAnnotation(Property.class);
                if (propertyAnnotation == null) {
                    configProperties.add(new ConfigProperty(methodName, configPropertyNameExtractor.extractPropertyNameFromMethodName(methodName),
                            ConfigProperty.Type.fromClazz(methodReturnType)));
                } else {
                    configProperties.add(new ConfigProperty(methodName, configPropertyNameExtractor.extractPropertyNameFromMethodName(methodName),
                            ConfigProperty.Type.fromClazz(methodReturnType), propertyAnnotation.validator()));
                }
            }
        }

        return new ConfigMetadata(configPropertyNameTranslatorClass, configProperties);
    }
}

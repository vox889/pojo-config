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

import com.ehxnv.util.config.converter.ConfigPropertyConverter;
import com.ehxnv.util.config.translator.ConfigPropertyNameTranslator;
import com.ehxnv.util.config.validator.ConfigPropertyValidator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Allow user to access configuration through POJO created automatically from a given configuration interface.
 *
 * @author Eka Lie
 */
public class Configuration {

    /** Caches of converter by converter class. **/
    private static final Map<Class, ConfigPropertyValidator> VALIDATOR_CACHES = new HashMap<Class, ConfigPropertyValidator>();
    /** Caches of validator by validator class. **/
    private static final Map<Class, ConfigPropertyConverter> CONVERTER_CACHES = new HashMap<Class, ConfigPropertyConverter>();

    /**
     * Creates a configuration object which conforms to given configuration interface.
     * @param properties properties that represents a configuration
     * @param configInterface configuration interface
     * @param <T> configuration interface type
     * @return configuration object which can be used to access the configuration properties
     */
    public static <T> T fromProperties(final Properties properties, final Class<T> configInterface) {
        // make sure we're given an interface, not a class
        if(!configInterface.isInterface()) {
            throw new IllegalArgumentException("configInterface must be an interface");
        }

        JavaBeanMetadataResolver<T> configMetadataResolver = new JavaBeanMetadataResolver<T>();
        ConfigMetadata configMetadata = configMetadataResolver.resolveMetadata(configInterface);

        ConfigPropertyNameTranslator configPropertyNameTranslator = ClassUtil.newInstance(configMetadata.getTranslator());
        Map<String, Object> mappedProperties = new HashMap<String, Object>();

        for (ConfigProperty configProperty : configMetadata.getConfigProperties()) {
            // translate property name based on translation strategy
            String propertyName = configPropertyNameTranslator.translatePropertyNameIntoReadablePropertyName(configProperty.getNameInWords());

            // TODO: check if we have optional config here...
            if (!properties.containsKey(propertyName)) {
                throw new ConfigurationException(String.format("No property \"%s\" found in given properties", propertyName));
            }

            Object propertyValue = properties.get(propertyName);
            boolean typeMatched = configProperty.getType().getClazz().isAssignableFrom(propertyValue.getClass());
            if (!typeMatched) {
                // convert property value using each property converter
                ConfigPropertyConverter configPropertyConverter = createConverter(configProperty.getConverter());
                propertyValue = configPropertyConverter.convertFromString(propertyValue.toString());
            }

            // validate property value using each property validator
            ConfigPropertyValidator configPropertyValidator = createValidator(configProperty.getValidator());
            if (!configPropertyValidator.isValid(propertyValue)) {
                throw new ConfigurationException(String.format("Property \"%s\" value is invalid (value is %s)", propertyName, propertyValue));
            }

            mappedProperties.put(configProperty.getMethodName(), propertyValue);
        }

        // return the dang configuration proxy
        return (T) Proxy.newProxyInstance(Configuration.class.getClassLoader(), new Class[] {configInterface},
                                            new MappedInvocationHandler(mappedProperties));
    }

    /**
     * Create property converter by class name or use the one in cache if available.
     * @param converterClass property converter class
     * @return property converter instance
     */
    private static ConfigPropertyConverter createConverter(final Class<? extends ConfigPropertyConverter> converterClass) {
        if (!CONVERTER_CACHES.containsKey(converterClass)) {
            CONVERTER_CACHES.put(converterClass, ClassUtil.newInstance(converterClass));
        }

        return CONVERTER_CACHES.get(converterClass);
    }

    /**
     * Create property validator by class name or use the one in cache if available.
     * @param validatorClass property validator class
     * @return property validator instance
     */
    private static ConfigPropertyValidator createValidator(final Class<? extends ConfigPropertyValidator> validatorClass) {
        if (!VALIDATOR_CACHES.containsKey(validatorClass)) {
            VALIDATOR_CACHES.put(validatorClass, ClassUtil.newInstance(validatorClass));
        }

        return VALIDATOR_CACHES.get(validatorClass);
    }

    /**
     * An {@link InvocationHandler} which uses map<key, value> in which the
     * key is a method name, mapped to method result.
     */
    private static class MappedInvocationHandler implements InvocationHandler {
        private Map<String, Object> mappedValues;

        /**
         * Construcor.
         * @param mappedValues a map of method name <-> method result
         */
        private MappedInvocationHandler(final Map<String, Object> mappedValues) {
            this.mappedValues = mappedValues;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return mappedValues.get(method.getName());
        }
    }
}

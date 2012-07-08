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

package com.ehxnv.util.config.annotation;

import com.ehxnv.util.config.extractor.ConfigPropertyNameExtractor;
import com.ehxnv.util.config.extractor.JavaBeanPropertyNameExtractor;
import com.ehxnv.util.config.translator.ConfigPropertyNameTranslator;
import com.ehxnv.util.config.translator.HypenedPropertyNameTranslator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>An optional annotation to be used by the configuration interface to customize extraction and
 * translation strategy of configuration interface to and from actual configuration.</p>
 *
 * <p>By default, {@link JavaBeanPropertyNameExtractor} is used for extraction strategy and
 * {@link HypenedPropertyNameTranslator} is used for translation strategy</p>
 *
 * @author Eka Lie
 * @see ConfigPropertyNameExtractor
 * @see ConfigPropertyNameTranslator
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * Extraction strategy class to be used.
     * @return extraction strategy class
     */
    Class<? extends ConfigPropertyNameExtractor> extractor() default JavaBeanPropertyNameExtractor.class;

    /**
     * Translation strategy class to be used.
     * @return translation strategy class
     */
    Class<? extends ConfigPropertyNameTranslator> translator() default HypenedPropertyNameTranslator.class;
}

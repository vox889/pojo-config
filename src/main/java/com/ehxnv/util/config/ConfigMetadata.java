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

import com.ehxnv.util.config.translator.ConfigPropertyNameTranslator;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a metadata about a particular configuration interface.
 * <p>A configuration metadata contains information such as:</p>
 * <ul>
 *     <li>which {@link ConfigPropertyNameTranslator} should be used</li>
 *     <li>available {@link ConfigProperty}</li>
 * </ul>
 *
 * @author Eka Lie
 */
final class ConfigMetadata {

    /** Translator class. **/
    private Class<? extends ConfigPropertyNameTranslator> translator;
    /** Available properties. **/
    private Set<ConfigProperty> configProperties;

    /**
     * Constructor.
     * @param translator translator class to be used
     * @param configProperties available properties
     */
    public ConfigMetadata(final Class<? extends ConfigPropertyNameTranslator> translator,
                          final Set<ConfigProperty> configProperties) {
        this.translator = translator;
        this.configProperties = configProperties;
    }

    /**
     * Get translator class to be used.
     * @return translator class
     */
    public Class<? extends ConfigPropertyNameTranslator> getTranslator() {
        return translator;
    }

    /**
     * Get available properties.
     * @return available properties
     */
    public Set<ConfigProperty> getConfigProperties() {
        return Collections.unmodifiableSet(configProperties);
    }
}
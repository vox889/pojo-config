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

package com.ehxnv.util.config.extractor;

import com.ehxnv.util.config.ConfigProperty;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ConfigPropertyNameExtractor} which recognizes JavaBean standard method name and extract the
 * name into property name.
 *
 * <p>In detail, the following strategy is used:</p>
 * <ul>
 *     <li>only method which have "getXXX" pattern will be extracted</li>
 *     <li>only getter method (method with no argument) will be extracted</li>
 *     <li>
 *         method name "get" prefix will be stripped and the remaining characters will be added to the words list
 *         e.g. "getMyCoolMethodName" will be translated into {"my", "cool", "method", "name"} (in exact order)
 *     </li>
 * </ul>
 *
 * @author Eka Lie
 */
public class JavaBeanPropertyNameExtractor implements ConfigPropertyNameExtractor {

    /** Getter "get" method prefix. **/
    private static final String GETTER_METHOD_PREFIX = "get";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidMethodForExtraction(final Method method) {
        String methodName = method.getName();
        Class<?> methodReturnType = method.getReturnType();
        boolean validPropertyType = (ConfigProperty.Type.fromClazz(methodReturnType) != null);

        // make sure we don't have method which name is only "get"
        boolean validMethodName = methodName.startsWith(JavaBeanPropertyNameExtractor.GETTER_METHOD_PREFIX) &&
                (methodName.length() > JavaBeanPropertyNameExtractor.GETTER_METHOD_PREFIX.length());

        return (validPropertyType && validMethodName && method.getParameterTypes().length == 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConfigProperty.Word> extractPropertyNameFromMethodName(final String methodName) {
        String strippedMethodName = methodName.substring(GETTER_METHOD_PREFIX.length());
        List<ConfigProperty.Word> propertyNameInWords = new ArrayList<ConfigProperty.Word>();

        int startIdx = 0, curIdx = 0;
        while (curIdx < strippedMethodName.length()) {
            if (curIdx > 0 && Character.isUpperCase(strippedMethodName.charAt(curIdx))) {
                propertyNameInWords.add(new ConfigProperty.Word(strippedMethodName.substring(startIdx, curIdx).toLowerCase()));
                startIdx = curIdx;
            }
            curIdx++;
        }

        propertyNameInWords.add(new ConfigProperty.Word(strippedMethodName.substring(startIdx, curIdx).toLowerCase()));
        return propertyNameInWords;
    }
}

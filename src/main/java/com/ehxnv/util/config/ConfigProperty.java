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

import com.ehxnv.util.config.converter.*;
import com.ehxnv.util.config.validator.*;

import java.util.Collections;
import java.util.List;

/**
 * Represents a single configuration property information.
 * <p>Each configuration property includes information such as:</p>
 * <ul>
 *     <li>original method name this property from</li>
 *     <li>property name in words</li>
 *     <li>property type</li>
 *     <li>property validator to be used</li>
 * </ul>
 *
 * @author Eka Lie
 */
public final class ConfigProperty {

    /** Original method name this property from. **/
    private String methodName;
    /** Property name in words. **/
    private List<Word> nameInWords;
    /** Property type. **/
    private Type type;
    /** Property validator. **/
    private Class<? extends ConfigPropertyValidator> validator;

    /**
     * Constructor.
     * @param methodName original method name this property from
     * @param nameInWords property name in words
     * @param type property type
     * @param validator property validator
     */
    public ConfigProperty(final String methodName, final List<Word> nameInWords, final Type type,
                          final Class<? extends ConfigPropertyValidator> validator) {
        if (methodName == null) {
            throw new IllegalArgumentException("methodName can't be null");
        }

        if (nameInWords == null) {
            throw new IllegalArgumentException("nameInWords can't be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("type can't be null");
        }

        if (validator == null) {
            throw new IllegalArgumentException("validator can't be null");
        }

        this.methodName = methodName;
        this.nameInWords = nameInWords;
        this.type = type;
        this.validator = validator;
    }

    /**
     * Constructor (uses default validator which is {@link IgnorantPropertyValidator}).
     * @param methodName original method name this property from
     * @param nameInWords property name in words
     * @param type property type
     */
    public ConfigProperty(final String methodName, final List<Word> nameInWords, final Type type) {
        this(methodName, nameInWords, type, type.getDefaultValidator());
    }

    /**
     * Get method name this property from.
     * @return method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Get property name in words.
     * @return property name in words
     */
    public List<Word> getNameInWords() {
        return Collections.unmodifiableList(nameInWords);
    }

    /**
     * Get property type.
     * @return property type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get property validator.
     * @return property validator
     */
    public Class<? extends ConfigPropertyValidator> getValidator() {
        return validator;
    }

    /**
     * Convenient method to get property converter.
     * Property converter returned will be based on the type of the property
     * e.g. {@link FloatPropertyConverter} if property type is {@link Float},
     *      {@link BooleanPropertyConverter} if property type is {@link Boolean} etc.
     * @return property converter
     */
    public Class<? extends ConfigPropertyConverter> getConverter() {
        return type.getDefaultConverter();
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = methodName.hashCode();
        result = 31 * result + nameInWords.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + validator.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigProperty that = (ConfigProperty) o;

        if (!methodName.equals(that.methodName)) return false;
        if (!nameInWords.equals(that.nameInWords)) return false;
        if (type != that.type) return false;
        if (!validator.equals(that.validator)) return false;

        return true;
    }

    /**
     * Represents a single word out of many words that constitutes property name.
     */
    public static final class Word {
        /** The word itself. **/
        private String value;

        /**
         * Constructor.
         * @param value the word
         */
        public Word(final String value) {
            if (value == null) {
                throw new IllegalArgumentException("Value can't be null");
            }

            this.value = value;
        }

        /**
         * Get the word.
         * @return word
         */
        public String getValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Word word = (Word) o;

            if (!value.equals(word.value)) return false;

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return value.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Word{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    /**
     * Represents available property types.
     */
    public static enum Type {

        BOOLEAN (Boolean.class,
                 IgnorantPropertyValidator.class,
                 BooleanPropertyConverter.class),
        BYTE (Byte.class,
              IgnorantPropertyValidator.class,
              BytePropertyConverter.class),
        SHORT (Short.class,
               IgnorantPropertyValidator.class,
               ShortPropertyConverter.class),
        INTEGER (Integer.class,
                 IgnorantPropertyValidator.class,
                 IntegerPropertyConverter.class),
        LONG (Long.class,
              IgnorantPropertyValidator.class,
              LongPropertyConverter.class),
        FLOAT (Float.class,
               IgnorantPropertyValidator.class,
               FloatPropertyConverter.class),
        DOUBLE (Double.class,
                IgnorantPropertyValidator.class,
                DoublePropertyConverter.class),
        CHAR (Character.class,
              IgnorantPropertyValidator.class,
              CharacterPropertyConverter.class),
        STRING (String.class,
                IgnorantPropertyValidator.class,
                StringPropertyConverter.class);

        /** Java class representation of the type. **/
        private Class clazz;
        /** Default validator for the property. **/
        private Class<? extends ConfigPropertyConverter> defaultConverter;
        /** Default converter for the property. **/
        private Class<? extends ConfigPropertyValidator> defaultValidator;

        /**
         * Constructor.
         * @param clazz Java class representation of the type
         * @param defaultValidator default validator
         * @param defaultConverter default converter
         */
        private Type(final Class clazz,
                     final Class<? extends ConfigPropertyValidator> defaultValidator,
                     final Class<? extends ConfigPropertyConverter> defaultConverter) {
            this.clazz = clazz;
            this.defaultValidator = defaultValidator;
            this.defaultConverter = defaultConverter;
        }

        /**
         * Get Java class representation of the type.
         * @return Java class representation of the type
         */
        public Class getClazz() {
            return clazz;
        }

        /**
         * Get property default validator.
         * @return property default validator
         */
        public Class<? extends ConfigPropertyValidator> getDefaultValidator() {
            return defaultValidator;
        }

        /**
         * Get property default converter.
         * @return property default converter
         */
        public Class<? extends ConfigPropertyConverter> getDefaultConverter() {
            return defaultConverter;
        }

        /**
         * Return a suitable type from given Java class.
         * @param typeClazz given Java class type
         * @return matching type or null if no matches
         */
        public static Type fromClazz(final Class typeClazz) {
            for (Type type : values()) {
                if (type.getClazz().equals(typeClazz)) {
                    return type;
                }
            }

            return null;
        }
    }
}

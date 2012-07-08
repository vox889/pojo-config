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

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Properties;

/**
 * Unit test for {@link Configuration}.
 *
 * @author Eka Lie
 */
public class ConfigurationTest {

    private interface MyBasicConfig {
        Boolean getBooleanValue();
        Character getCharValue();
        Byte getByteValue();
        Short getShortValue();
        Integer getIntegerValue();
        Long getLongValue();
        Float getFloatValue();
        Double getDoubleValue();
        String getStringValue();
    }

    private interface MyMissingConfig {
        Boolean getSomeBool();
    }

    /**
     * Test {@code fromProperties} of {@link Configuration}.
     * <p>This test covers happy scenario where user passed in a configuration interface and those configuration properties
     * exactly found in {@code Properties} given</p>
     */
    @Test
    public void testFromPropertiesBasicWithoutAnyConversion() {
        Properties properties = new Properties();
        properties.put("boolean-value", Boolean.TRUE);
        properties.put("char-value", 'x');
        properties.put("byte-value", Byte.valueOf((byte) 100));
        properties.put("short-value", Short.valueOf((short) 1000));
        properties.put("integer-value", 10000);
        properties.put("long-value", 1000000);
        properties.put("float-value", 128.128f);
        properties.put("double-value", 256.256d);
        properties.put("string-value", "foo bar");

        MyBasicConfig config = Configuration.fromProperties(properties, MyBasicConfig.class);
        assertEquals(Boolean.TRUE, config.getBooleanValue());
        assertEquals(Character.valueOf('x'), config.getCharValue());
        assertEquals(Byte.valueOf((byte) 100), config.getByteValue());
        assertEquals(Short.valueOf((short) 1000), config.getShortValue());
        assertEquals(Integer.valueOf(10000), config.getIntegerValue());
        assertEquals(Long.valueOf(1000000), config.getLongValue());
        assertEquals(Float.valueOf(128.128f), config.getFloatValue());
        assertEquals(Double.valueOf(256.256d), config.getDoubleValue());
        assertEquals("foo bar", config.getStringValue());
    }

    /**
     * Test {@code fromProperties} of {@link Configuration}.
     * <p>This test covers scenario where user passed in a configuration interface and those configuration properties
     * types are all String i.e. all those properties need to be converted by each property {@link com.ehxnv.util.config.converter.ConfigPropertyConverter}</p>
     */
    @Test
    public void testFromPropertiesWithAllConversions() {
        Properties properties = new Properties();
        properties.put("boolean-value", "true");
        properties.put("char-value", "x");
        properties.put("byte-value", "100");
        properties.put("short-value", "1000");
        properties.put("integer-value", "10000");
        properties.put("long-value", "1000000");
        properties.put("float-value", "128.128");
        properties.put("double-value", "256.256");
        properties.put("string-value", "foo bar");

        MyBasicConfig config = Configuration.fromProperties(properties, MyBasicConfig.class);
        assertEquals(Boolean.TRUE, config.getBooleanValue());
        assertEquals(Character.valueOf('x'), config.getCharValue());
        assertEquals(Byte.valueOf((byte) 100), config.getByteValue());
        assertEquals(Short.valueOf((short) 1000), config.getShortValue());
        assertEquals(Integer.valueOf(10000), config.getIntegerValue());
        assertEquals(Long.valueOf(1000000), config.getLongValue());
        assertEquals(Float.valueOf(128.128f), config.getFloatValue());
        assertEquals(Double.valueOf(256.256d), config.getDoubleValue());
        assertEquals("foo bar", config.getStringValue());
    }

    /**
     * Test {@code fromProperties} of {@link Configuration}.
     * <p>This test covers scenario where user passed in a configuration interface and those configuration properties
     * can't be found in the given {@link Properties}</p>
     */
    @Test(expected = ConfigurationException.class)
    public void testFromPropertiesWithMissingProperty() {
        Properties properties = new Properties();
        properties.put("bool-value", Boolean.FALSE);

        Configuration.fromProperties(properties, MyMissingConfig.class);
    }

    /**
     * Test {@code fromProperties} of {@link Configuration}.
     * <p>This test covers scenario where user passed in a configuration interface and those configuration properties
     * types are in String but can't be converted to expected property type</p>
     */
    @Test
    public void testFromPropertiesWithInvalidProperty() {
        Properties properties = new Properties();
        properties.put("some-bool", "we should pass boolean value here");

        Configuration.fromProperties(properties, MyMissingConfig.class);
    }
}

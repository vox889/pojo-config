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
import com.ehxnv.util.config.translator.ConfigPropertyNameTranslator;
import com.ehxnv.util.config.translator.HypenedPropertyNameTranslator;
import com.ehxnv.util.config.validator.ConfigPropertyValidator;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit test for {@link JavaBeanMetadataResolver}.
 *
 * @author Eka Lie
 */
public class JavaBeanMetadataResolverTest {

    private interface BasicConfig {
        Float getMyFloat();
        Double getMyDouble();
        Byte getMyByte();
        Short getMyShort();
        Integer getMyInteger();
        Long getMyLong();
        Character getMyChar();
        String getMyString();
        Boolean getMyBoolean();
        String get();
        Boolean nonConventionalName();
    }

    private interface BaseConfig {
        Character getSomeChar();
    }

    private interface SubConfig extends BaseConfig {
        Float getMySubFloat();
        Boolean getAnotherBoolean();
    }

    private class MyCoolDoubleValidator implements ConfigPropertyValidator<Double> {
        @Override
        public boolean isValid(final Double propertyValue) {
            return true;
        }
    }

    private interface BasicAnnotatedConfig {
        @Property(validator = MyCoolDoubleValidator.class)
        Double getCoolDouble();
        Short getNormalShort();
    }

    private class MyPropertyNameTranslator implements ConfigPropertyNameTranslator {
        @Override
        public String translatePropertyNameIntoReadablePropertyName(final List<ConfigProperty.Word> propertyNameInWords) {
            return "";
        }
    }

    private interface BlankConfig {

    }

    @Config(translator = MyPropertyNameTranslator.class)
    private interface CustomTranslatorConfig {

    }

    /**
     * Test {@code resolveMetadata} of {@link JavaBeanMetadataResolver}.
     * <p>This test covers the simplest scenario where there are mixtures of valid and non-valid methods (for extraction)
     * in the given configuration interface.</p>
     */
    @Test
    public void testResolveMetadataBasic() {
        JavaBeanMetadataResolver<BasicConfig> javaBeanMetadataResolver = new JavaBeanMetadataResolver<BasicConfig>();
        ConfigMetadata configMetadata = javaBeanMetadataResolver.resolveMetadata(BasicConfig.class);

        Set<ConfigProperty> expectedConfigProperties = new HashSet<ConfigProperty>();
        expectedConfigProperties.add(new ConfigProperty("getMyBoolean", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("boolean")),
                                                        ConfigProperty.Type.BOOLEAN));
        expectedConfigProperties.add(new ConfigProperty("getMyByte", Arrays.asList(new ConfigProperty.Word("my"),
                                                                    new ConfigProperty.Word("byte")),
                                                        ConfigProperty.Type.BYTE));
        expectedConfigProperties.add(new ConfigProperty("getMyShort", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("short")),
                                                        ConfigProperty.Type.SHORT));
        expectedConfigProperties.add(new ConfigProperty("getMyInteger", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("integer")),
                                                        ConfigProperty.Type.INTEGER));
        expectedConfigProperties.add(new ConfigProperty("getMyLong", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("long")),
                                                        ConfigProperty.Type.LONG));
        expectedConfigProperties.add(new ConfigProperty("getMyFloat", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("float")),
                                                        ConfigProperty.Type.FLOAT));
        expectedConfigProperties.add(new ConfigProperty("getMyDouble", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("double")),
                                                        ConfigProperty.Type.DOUBLE));
        expectedConfigProperties.add(new ConfigProperty("getMyChar", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("char")),
                                                        ConfigProperty.Type.CHAR));
        expectedConfigProperties.add(new ConfigProperty("getMyString", Arrays.asList(new ConfigProperty.Word("my"),
                                                                      new ConfigProperty.Word("string")),
                                                        ConfigProperty.Type.STRING));

        assertEquals(expectedConfigProperties, configMetadata.getConfigProperties());
    }

    /**
     * Test {@code resolveMetadata} of {@link JavaBeanMetadataResolver}.
     * <p>This test covers scenario where user passed in a configuration interface in which the configuration interface
     * extends other interface. All those methods (incl. inherited methods) should be resolved by the resolver.</p>
     */
    @Test
    public void testResolveMetadataWithInheritance() {
        JavaBeanMetadataResolver<SubConfig> javaBeanMetadataResolver = new JavaBeanMetadataResolver<SubConfig>();
        ConfigMetadata configMetadata = javaBeanMetadataResolver.resolveMetadata(SubConfig.class);

        Set<ConfigProperty> expectedConfigProperties = new HashSet<ConfigProperty>();
        expectedConfigProperties.add(new ConfigProperty("getSomeChar", Arrays.asList(new ConfigProperty.Word("some"),
                                                                      new ConfigProperty.Word("char")),
                                                        ConfigProperty.Type.CHAR));
        expectedConfigProperties.add(new ConfigProperty("getMySubFloat", Arrays.asList(new ConfigProperty.Word("my"),
                                                                    new ConfigProperty.Word("sub"),
                                                                    new ConfigProperty.Word("float")),
                                                        ConfigProperty.Type.FLOAT));
        expectedConfigProperties.add(new ConfigProperty("getAnotherBoolean", Arrays.asList(new ConfigProperty.Word("another"),
                                                                      new ConfigProperty.Word("boolean")),
                                                        ConfigProperty.Type.BOOLEAN));

        assertEquals(expectedConfigProperties, configMetadata.getConfigProperties());
    }

    /**
     * Test {@code resolveMetadata} of {@link JavaBeanMetadataResolver}.
     * <p>This test covers scenario where user annotation configuration property with @Property annotation
     * and also supplies a {@link ConfigPropertyValidator}. This information should be reflected in {@link ConfigProperty}
     * instance of the returned {@link ConfigMetadata}.</p>
     */
    @Test
    public void testResolveMetadataWithAnnotation() {
        JavaBeanMetadataResolver<BasicAnnotatedConfig> javaBeanMetadataResolver = new JavaBeanMetadataResolver<BasicAnnotatedConfig>();
        ConfigMetadata configMetadata = javaBeanMetadataResolver.resolveMetadata(BasicAnnotatedConfig.class);

        Set<ConfigProperty> expectedConfigProperties = new HashSet<ConfigProperty>();
        expectedConfigProperties.add(new ConfigProperty("getCoolDouble", Arrays.asList(new ConfigProperty.Word("cool"),
                                                                      new ConfigProperty.Word("double")),
                                                        ConfigProperty.Type.DOUBLE, MyCoolDoubleValidator.class));
        expectedConfigProperties.add(new ConfigProperty("getNormalShort", Arrays.asList(new ConfigProperty.Word("normal"),
                                                                    new ConfigProperty.Word("short")),
                                                        ConfigProperty.Type.SHORT));

        assertEquals(expectedConfigProperties, configMetadata.getConfigProperties());
    }

    /**
     * Test {@code resolveMetadata} of {@link JavaBeanMetadataResolver}.
     * <p>This test covers scenario where user passed in empty configuration interface. The returned
     * {@link ConfigMetadata} should have empty configuraton properties with {@link HypenedPropertyNameTranslator} as the default translator</p>
     */
    @Test
    public void testResolveMetadataWithBlankConfig() {
        JavaBeanMetadataResolver<BlankConfig> javaBeanMetadataResolver = new JavaBeanMetadataResolver<BlankConfig>();
        ConfigMetadata configMetadata = javaBeanMetadataResolver.resolveMetadata(BlankConfig.class);

        assertEquals(HypenedPropertyNameTranslator.class, configMetadata.getTranslator());
        assertTrue(configMetadata.getConfigProperties().isEmpty());

    }

    /**
     * Test {@code resolveMetadata} of {@link JavaBeanMetadataResolver}.
     * <p>This test covers scenario where user passed int their custom {@link ConfigPropertyNameTranslator} class</p>
     */
    @Test
    public void testResolveMetadataWithCustomTranslator() {
        JavaBeanMetadataResolver<CustomTranslatorConfig> javaBeanMetadataResolver = new JavaBeanMetadataResolver<CustomTranslatorConfig>();
        ConfigMetadata configMetadata = javaBeanMetadataResolver.resolveMetadata(CustomTranslatorConfig.class);

        assertEquals(MyPropertyNameTranslator.class, configMetadata.getTranslator());
        assertTrue(configMetadata.getConfigProperties().isEmpty());
    }
}

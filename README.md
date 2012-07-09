## Introduction
---------------
**pojo-config** is a small utility library which allows developer to read properties through given configuration interface.



## Usage
--------

### Basic

Assume we have an interface *MyEngineConfiguration* that represents our configuration as follow:

    interface MyEngineConfiguration {
        String getVendorName();
        Integer getEngineThreshold();
    }

We would then want to access our configuration which is stored inside properties file. We can simply access our configuration easily as follow:

    InputStream inputStream = // get some input stream here;
    Properties prop = new Properties();
    prop.load(inputStream);

    MyEngineConfiguration myConfig = Configuration.fromProperties(prop, MyEngineConfiguration.class)
    String vendorName = myConfig.getVendorName();
    Integer engineThreshold = myConfig.getEngineThreshold();

In this example, the properties file is expected to have the following keys: *vendor-name* and *engine-threshold*.
 

### Custom property validator

Supposed that we want to only accept our engine configuration only when the engine threshold is always greater than *250*, we can achieve this by annotate our *getEngineThreshold* method as follow:

    class EngineThresholdPropertyValidator implements ConfigPropertyValidator<Integer> {
        @Override
        public boolean isValid(Integer propertyValue) {
            return (propertyValue.intValue() > 250);
        }
    }

    interface MyEngineConfiguration {
        String getVendorName();
 
        // here we annotate our method directly and specify which validator class we want to use
        @Property(validator = EngineThresholdPropertyValidator.class)
        Integer getEngineThreshold();
    }

When we try to build our configuration using the following code:

    MyEngineConfiguration myConfig = Configuration.fromProperties(prop, MyEngineConfiguration.class)

a runtime *ConfigurationException* will be thrown if *prop* value of *engine-threshold* isn't greater than *250*.


### Custom extraction and translation strategy

Notice that in above examples, we always have *getter* methods inside our configuration interface. How if we want to use normal method such as *engineThreshold* to make our configuration methods more readable. At the same time we want this to be translated as *engine.threshold* as our properties key. We can achieve this by supplying our own *ConfigPropertyNameExtractor* and *ConfigPropertyNameTranslator* by annotating our configuration interface using *Config* annotation.

When a *ConfigPropertyNameExtractor* is defined, the library will use given extraction strategy to extract available properties keys from the given configuration interface. Each of this properties key will be then *translated* into properties human readable key using given *ConfigPropertyNameTranslator*.

Example:

    class SimpleExtractor implements ConfigPropertyNameExtractor {

        @Override
        public boolean isValidMethodForExtraction(Method method) {
            return true;
        }
 
        @Override
        public List<ConfigProperty.Word> extractPropertyNameFromMethodName(String methodName) {
            List<ConfigProperty.Word> propertyNameInWords = new ArrayList<ConfigProperty.Word>();

            int startIdx = 0, curIdx = 0;
            while (curIdx < methodName.length()) {
                if (curIdx > 0 && Character.isUpperCase(methodName.charAt(curIdx))) {
                    propertyNameInWords.add(new ConfigProperty.Word(methodName.substring(startIdx, curIdx).toLowerCase()));
                    startIdx = curIdx;
                }
                curIdx++;
            }

            propertyNameInWords.add(new ConfigProperty.Word(methodName.substring(startIdx, curIdx).toLowerCase()));
            return propertyNameInWords;
        }
    }

    class DotTranslator implements ConfigPropertyNameTranslator {

        @Override
        public String translatePropertyNameIntoReadablePropertyName(List<ConfigProperty.Word> propertyNameInWords) {
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<propertyNameInWords.size(); i++) {
                if (i > 0) {
                    buffer.append(".");
                }

                buffer.append(propertyNameInWords.get(i).getValue());
            }

            return buffer.toString();
        }
    }

    @Config(extractor = SimpleExtractor.class, translator = DotTranslator.class)
    interface MyEngineConfiguration {
        Double engineThreshold();
        String vendorName();
    }

Using this approach, our *Properties* is expected to have *engine.threshold* and *vendor.name* keys respectively.



## Limitation
-------------

* Only basic Java types are supported i.e. *Float*, *Double*, *Byte*, *Short*, *Integer*, *Long*, *String*, *Boolean* and *Character* 
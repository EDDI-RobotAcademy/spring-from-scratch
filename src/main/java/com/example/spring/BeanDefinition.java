package com.example.spring;

public interface BeanDefinition {

    void setBeanClassName(String beanClassName);

    String getBeanClassName();

    void setDependsOn(String... dependsOn);

    String[] getDependsOn();
}

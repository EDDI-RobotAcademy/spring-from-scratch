package com.example.spring;

public interface BeanFactory {

    <T> T getBean(String name, Class<T> requiredType);
}

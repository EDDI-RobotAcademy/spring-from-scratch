package com.example.spring;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultApplicationContext implements ApplicationContext {

    private final Set<Class<?>> candidates;

    private final Map<String, Object> beanRegistry = new HashMap<>();

    public DefaultApplicationContext() {
        final Reflections reflections = new Reflections(
                this.getClass().getPackageName(), // "com.example.spring"
                Scanners.TypesAnnotated
        );
        this.candidates = reflections.getTypesAnnotatedWith(Component.class);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        final Object maybeBeanWithGivenName = beanRegistry.get(name);
        if (maybeBeanWithGivenName != null) {
            if (!requiredType.equals(maybeBeanWithGivenName.getClass())) {
                throw new RuntimeException("Invalid bean type.");
            }
            return requiredType.cast(maybeBeanWithGivenName);
        }

        final Class<?> foundCandidate = findCandidate(requiredType);
        if (foundCandidate == null) {
            throw new RuntimeException("Unable to find bean");
        }

        final Constructor<?> ctor = foundCandidate.getConstructors()[0];
        final Parameter[] parameters = ctor.getParameters();
        final List<Object> depsList = new ArrayList<>(parameters.length);

        for (Parameter parameter : parameters) {
            final Class<?> typeOfDependency = parameter.getType();
            final String beanNameOfDependency = generateBeanName(typeOfDependency);
            final Object beanOfDependency = getBean(beanNameOfDependency, typeOfDependency);
            depsList.add(beanOfDependency);
        }

        try {
            final String newBeanName = generateBeanName(requiredType);
            final Object newBean = ctor.newInstance(depsList.toArray());
            beanRegistry.put(newBeanName, newBean);
            return requiredType.cast(newBean);
        } catch (Throwable e) {
            throw new RuntimeException("Bean 생성 중에 문제가 발생했어요.", e);
        }
    }

    private <T> Class<?> findCandidate(Class<T> requiredType) {
        for (Class<?> candidate : candidates) {
            if (requiredType.equals(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private String generateBeanName(Class<?> clazz) {
        final String origin = clazz.getSimpleName();
        final String firstLetter = origin.substring(0, 1);
        final String rest = origin.substring(1);
        return firstLetter.toLowerCase() + rest;
    }
}

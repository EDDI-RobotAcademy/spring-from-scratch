package com.example.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultApplicationContextTest {

    @DisplayName("reflection API")
    @Test
    void learningTestForReflectionAPI() {

        final Constructor<?>[] ctors = CandidateModule.class.getConstructors();
        for (Constructor<?> ctor : ctors) {
            System.out.println("ctor = " + ctor);
            final BeanDefinition beanDefinition = new AnnotatedBeanDefinition();
            beanDefinition.setBeanClassName("candidateModule");
            final Parameter[] parameters = ctor.getParameters();

            // 의존성들
            for (Parameter parameter : parameters) {
                System.out.println("parameter = " + parameter);
                final Class<?> typeOfDependency = parameter.getType();
                System.out.println(typeOfDependency);
                // var beanName = beanNameGenerator.generate(typeOfDependency)
                beanDefinition.setDependsOn("beanName");
                for (Annotation annotation : typeOfDependency.getAnnotations()) {
                    System.out.println("annotation = " + annotation);
                    final Component component = (Component) annotation;
                    System.out.println("component = " + component.value());
                }

            }
        }
    }

    @DisplayName("러닝 테스트")
    @Test
    void learningTest() {
        final Reflections reflections = new Reflections(
                this.getClass().getPackageName(), // "com.example.spring"
                Scanners.TypesAnnotated
        );
        final Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        for (Class<?> aClass : classes) {
            System.out.println("aClass = " + aClass);
        }

        final Constructor<?> ctor = CandidateModule.class.getConstructors()[0];
        final Parameter[] parameters = ctor.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println("parameter.getType() = " + parameter.getType());
        }
    }

    @DisplayName("getBean으로 bean을 조회하면 bean이 반환된다.")
    @Test
    void test() {
        final var sut = new DefaultApplicationContext();

        final CandidateModule bean = sut.getBean("candidateModule", CandidateModule.class);

        assertNotNull(bean);
        assertInstanceOf(CandidateModule.class, bean);
        assertNotNull(bean.sayHello());
    }
}
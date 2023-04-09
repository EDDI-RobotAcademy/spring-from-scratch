package com.example.spring;

@Component
public class CandidateModule {

    private final CandidateModuleDependency dependency;

    public CandidateModule(CandidateModuleDependency dependency) {
        this.dependency = dependency;
    }

    public String sayHello() {
        return dependency.getData();
    }
}

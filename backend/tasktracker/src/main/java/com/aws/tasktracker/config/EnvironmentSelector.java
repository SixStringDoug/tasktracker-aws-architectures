package com.aws.tasktracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentSelector {

    @Value("${app.env}")
    private String environment;

    public boolean useDynamoDb() {
        return "fargate".equalsIgnoreCase(environment);
    }

    public boolean useRds() {
        return "ec2".equalsIgnoreCase(environment) || "beanstalk".equalsIgnoreCase(environment);
    }
}

package com.sangngo552004.performancemonitor.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorPerformance {

    String metricName() default "";
}

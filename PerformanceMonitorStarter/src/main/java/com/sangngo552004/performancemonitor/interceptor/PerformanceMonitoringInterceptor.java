package com.sangngo552004.performancemonitor.interceptor;

import com.sangngo552004.performancemonitor.annotation.MonitorPerformance;
import org.aopalliance.intercept.MethodInterceptor;

import io.micrometer.core.instrument.*;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Objects;

public class PerformanceMonitoringInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringInterceptor.class);
    private final MeterRegistry meterRegistry; // Micrometer Registry

    public PerformanceMonitoringInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MonitorPerformance annotation = AnnotationUtils.findAnnotation(invocation.getMethod(), MonitorPerformance.class);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(Objects.requireNonNull(invocation.getThis()).getClass(), MonitorPerformance.class);
        }
        if (annotation == null) {
            return invocation.proceed();
        }
        // 1. Xác định Tên Metric
        String metricName = getMetricName(annotation, invocation);
        Timer timer = meterRegistry.timer(metricName);

        long startTime = System.currentTimeMillis();

        try {
            Object result;
            try {
                result = timer.recordCallable(() -> {
                    try {
                        return invocation.proceed();
                    } catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
                });
            } catch (RuntimeException e) {
                if (e.getCause() != null) {
                    throw e.getCause(); // ném lại Throwable gốc
                }
                throw e;
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("✅ [PerformanceMonitor] Method: {} executed in {} ms. Metrics recorded: {}",
                    invocation.getMethod().getName(), executionTime, metricName);

            return result;

        } catch (Throwable t) {

            String failureMetricName = metricName + ".failure_count";

            Counter failureCounter = meterRegistry.counter(
                    failureMetricName,
                    Tags.of("exception", t.getClass().getSimpleName())
            );

            failureCounter.increment();

            long executionTime = System.currentTimeMillis() - startTime;
            log.error("❌ [PerformanceMonitor] Method: {} FAILED in {} ms. Failure Count Metric increased: {}",
                    invocation.getMethod().getName(), executionTime, failureMetricName, t);

            throw t;
        }
    }

    private String getMetricName(MonitorPerformance annotation, MethodInvocation invocation) {
        if (!annotation.metricName().isEmpty()) {
            return annotation.metricName();
        }

        String className = Objects.requireNonNull(invocation.getThis()).getClass().getSimpleName();
        String methodName = invocation.getMethod().getName();
        return String.format("%s.%s.execution.time", className, methodName).toLowerCase();
    }
}

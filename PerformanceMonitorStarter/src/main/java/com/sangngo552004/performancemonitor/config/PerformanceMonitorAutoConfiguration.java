package com.sangngo552004.performancemonitor.config;


import com.sangngo552004.performancemonitor.interceptor.PerformanceMonitoringInterceptor;
import com.sangngo552004.performancemonitor.processor.PerformanceMonitoringBeanPostProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MeterRegistry.class)
public class PerformanceMonitorAutoConfiguration {

    @Bean
    public PerformanceMonitoringInterceptor performanceMonitoringInterceptor(MeterRegistry meterRegistry) {
        // Inject MeterRegistry vào Interceptor
        return new PerformanceMonitoringInterceptor(meterRegistry);
    }

    // 2. Định nghĩa BeanPostProcessor
    @Bean
    public PerformanceMonitoringBeanPostProcessor performanceMonitoringBeanPostProcessor(
            PerformanceMonitoringInterceptor interceptor) {
        // Inject Interceptor vào BeanPostProcessor
        return new PerformanceMonitoringBeanPostProcessor(interceptor);
    }
}

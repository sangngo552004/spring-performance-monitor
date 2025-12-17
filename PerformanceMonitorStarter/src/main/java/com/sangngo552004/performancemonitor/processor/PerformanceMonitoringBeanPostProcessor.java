package com.sangngo552004.performancemonitor.processor;

import com.sangngo552004.performancemonitor.annotation.MonitorPerformance;
import com.sangngo552004.performancemonitor.interceptor.PerformanceMonitoringInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

public class PerformanceMonitoringBeanPostProcessor implements BeanPostProcessor {

    private final PerformanceMonitoringInterceptor interceptor;

    public PerformanceMonitoringBeanPostProcessor(PerformanceMonitoringInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        MonitorPerformance classAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), MonitorPerformance.class);

        boolean hasMonitoredMethod = false;
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (AnnotationUtils.findAnnotation(method, MonitorPerformance.class) != null) {
                hasMonitoredMethod = true;
                break;
            }
        }

        if (classAnnotation != null || hasMonitoredMethod) {

            ProxyFactory proxyFactory = new ProxyFactory(bean);

            proxyFactory.addAdvice(interceptor);

            return proxyFactory.getProxy();
        }

        return bean;
    }
}

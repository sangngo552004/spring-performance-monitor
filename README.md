# Spring Performance Monitor Starter

This project is a custom **Spring Boot Starter** designed to monitor method execution time and failure rates using **Aspect-Oriented Programming (AOP)** and **Micrometer**. It allows developers to easily collect metrics by simply adding a custom annotation to any class or method.

---

## 🏗 Project Architecture

The repository consists of two main modules:
1.  **PerformanceMonitorStarter**: The core engine that provides the `@MonitorPerformance` annotation and the monitoring logic.
2.  **Demo**: A sample application that demonstrates how to integrate and use the starter.

### How it works (Internal Flow)
-   **Custom Annotation**: We define `@MonitorPerformance` to mark the targets we want to track.
-   **AOP Interceptor**: The `PerformanceMonitoringInterceptor` uses Micrometer's `Timer` to record execution time and `Counter` to track exceptions.
-   **BeanPostProcessor**: During Spring's startup, the `PerformanceMonitoringBeanPostProcessor` scans all beans. If a bean or its methods have the annotation, it wraps that bean in a **Proxy** to apply the monitoring logic.
-   **Auto-Configuration**: The starter is automatically loaded via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`, so you don't need to manually define beans in your main app.

---

## 🚀 Getting Started

### 1. Install the Starter to Local Repository
Since this is a custom starter, you must install it to your local Maven repository (`.m2`) first so other projects can find it.
```bash
cd PerformanceMonitorStarter
mvn clean install
```
### 2. Add Dependency to your Project
In your application's pom.xml, add the following:
XML
```bash
<dependency>
    <groupId>com.sangngo552004</groupId>
    <artifactId>performancemonitorstarter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
### 3. Usage

Simply annotate your Service or Controller:
```bash
@Service
@MonitorPerformance(metricName = "user_service_monitor")
public class UserService {
    // All methods in this class will be monitored
}
```
## 📊 Monitoring & Visualization
### Phase 1: Local Metrics (JVM RAM)

By default, metrics are stored in the JVM's memory. You can view them by accessing:
```bash
curl http://localhost:8080/actuator/prometheus
```

Note: Since metrics are in RAM, they will be lost whenever the application restarts. To persist and analyze historical data, we need a monitoring stack.

### Phase 2: Persistence with Prometheus

Prometheus acts as a time-series database that periodically "scrapes" (pulls) data from your application.

Create a prometheus.yml:
```bash
scrape_configs:
  - job_name: 'spring-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
```

Run Docker:
```bash
docker run -d -p 9090:9090 -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
```

### Phase 3: Visualization with Grafana

Grafana connects to Prometheus to turn raw numbers into beautiful dashboards.

Run Grafana:
```bash
docker run -d -p 3000:3000 grafana/grafana
```

Add Data Source:
Select "Prometheus" and enter your Prometheus URL (http://host.docker.internal:9090).

Create Dashboard:
Query your custom metrics like user_management_service_seconds_sum.

## 🧪 Running the Demo

Ensure the Starter is installed (mvn install).

Run the Demo application:
```bash
cd Demo
mvn spring-boot:run
```

Generate some data:

S

success: http://localhost:8080/process?id=123ailure: http://localhost:8080/create?name=abc
 (triggers short name error)

CheCheck metrics: http://localhost:8080/actuator/prometheus

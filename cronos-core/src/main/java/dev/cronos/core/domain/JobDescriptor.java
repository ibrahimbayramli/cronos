package dev.cronos.core.domain;

import dev.cronos.core.model.JobSourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "job_descriptor")
public class JobDescriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private JobSourceType sourceType;

    @Column(name = "bean_name")
    private String beanName;

    @Column(name = "method_or_class")
    private String methodOrClass;

    @Column(name = "trigger_info", length = 512)
    private String triggerInfo;

    @Column(name = "discovered_at", nullable = false)
    private Instant discoveredAt;

    @Column(nullable = false)
    private boolean enabled = true;

    protected JobDescriptor() {
    }

    public JobDescriptor(String name, JobSourceType sourceType, String beanName,
                         String methodOrClass, String triggerInfo) {
        this.name = name;
        this.sourceType = sourceType;
        this.beanName = beanName;
        this.methodOrClass = methodOrClass;
        this.triggerInfo = triggerInfo;
        this.discoveredAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JobSourceType getSourceType() {
        return sourceType;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getMethodOrClass() {
        return methodOrClass;
    }

    public String getTriggerInfo() {
        return triggerInfo;
    }

    public Instant getDiscoveredAt() {
        return discoveredAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

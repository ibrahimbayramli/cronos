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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "job_descriptor")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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

    @Builder.Default
    @Column(name = "discovered_at", nullable = false)
    private Instant discoveredAt = Instant.now();

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;
}

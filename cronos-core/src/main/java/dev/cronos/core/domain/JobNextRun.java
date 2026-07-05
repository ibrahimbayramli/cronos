package dev.cronos.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "job_next_run")
public class JobNextRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false, unique = true)
    private JobDescriptor job;

    @Column(name = "calculated_next_run_at")
    private Instant calculatedNextRunAt;

    protected JobNextRun() {
    }

    public JobNextRun(JobDescriptor job, Instant calculatedNextRunAt) {
        this.job = job;
        this.calculatedNextRunAt = calculatedNextRunAt;
    }

    public Long getId() {
        return id;
    }

    public JobDescriptor getJob() {
        return job;
    }

    public Instant getCalculatedNextRunAt() {
        return calculatedNextRunAt;
    }

    public void setCalculatedNextRunAt(Instant calculatedNextRunAt) {
        this.calculatedNextRunAt = calculatedNextRunAt;
    }
}

package io.github.ibrahimbayramli.cronos.starter.persistence;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import io.github.ibrahimbayramli.cronos.core.domain.JobNextRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobNextRunRepository extends JpaRepository<JobNextRun, Long> {

    Optional<JobNextRun> findByJob(JobDescriptor job);
}

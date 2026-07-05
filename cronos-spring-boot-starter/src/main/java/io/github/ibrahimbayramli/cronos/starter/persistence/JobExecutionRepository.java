package io.github.ibrahimbayramli.cronos.starter.persistence;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import io.github.ibrahimbayramli.cronos.core.domain.JobExecution;
import io.github.ibrahimbayramli.cronos.core.model.ExecutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    Page<JobExecution> findByJobOrderByStartedAtDesc(JobDescriptor job, Pageable pageable);

    Optional<JobExecution> findFirstByJobOrderByStartedAtDesc(JobDescriptor job);

    boolean existsByJobAndStatus(JobDescriptor job, ExecutionStatus status);

    @Query("SELECT e FROM JobExecution e WHERE e.job = :job AND e.status = 'RUNNING' ORDER BY e.startedAt DESC")
    Optional<JobExecution> findLatestRunning(@Param("job") JobDescriptor job);
}

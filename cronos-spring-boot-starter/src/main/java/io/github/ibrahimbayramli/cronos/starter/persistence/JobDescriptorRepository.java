package io.github.ibrahimbayramli.cronos.starter.persistence;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import io.github.ibrahimbayramli.cronos.core.model.JobSourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobDescriptorRepository extends JpaRepository<JobDescriptor, Long> {

    Optional<JobDescriptor> findByName(String name);

    List<JobDescriptor> findBySourceType(JobSourceType sourceType);
}

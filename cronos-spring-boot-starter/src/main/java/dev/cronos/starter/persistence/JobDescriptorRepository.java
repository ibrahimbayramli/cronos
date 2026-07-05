package dev.cronos.starter.persistence;

import dev.cronos.core.domain.JobDescriptor;
import dev.cronos.core.model.JobSourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobDescriptorRepository extends JpaRepository<JobDescriptor, Long> {

    Optional<JobDescriptor> findByName(String name);

    List<JobDescriptor> findBySourceType(JobSourceType sourceType);
}

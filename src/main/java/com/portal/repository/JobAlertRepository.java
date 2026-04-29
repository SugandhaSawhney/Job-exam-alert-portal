package com.portal.repository;

import com.portal.model.JobAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
    List<JobAlert> findByType(String type);
    List<JobAlert> findByOrganizationContainingIgnoreCase(String keyword);
}

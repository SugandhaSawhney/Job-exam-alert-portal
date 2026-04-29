package com.portal.service;

import com.portal.model.JobAlert;
import com.portal.repository.JobAlertRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class JobAlertService {

    private final JobAlertRepository repository;

    public JobAlertService(JobAlertRepository repository) {
        this.repository = repository;
    }

    public List<JobAlert> getAllAlerts() {
        return repository.findAll();
    }

    public Optional<JobAlert> getAlertById(Long id) {
        return repository.findById(id);
    }

    public JobAlert createAlert(JobAlert alert) {
        return repository.save(alert);
    }

    public JobAlert updateAlert(Long id, JobAlert updated) {
        return repository.findById(id).map(alert -> {
            alert.setTitle(updated.getTitle());
            alert.setOrganization(updated.getOrganization());
            alert.setType(updated.getType());
            alert.setLastDate(updated.getLastDate());
            alert.setDescription(updated.getDescription());
            alert.setLink(updated.getLink());
            return repository.save(alert);
        }).orElseThrow(() -> new RuntimeException("Alert not found: " + id));
    }

    public void deleteAlert(Long id) {
        repository.deleteById(id);
    }

    public List<JobAlert> getAlertsByType(String type) {
        return repository.findByType(type.toUpperCase());
    }
}

package com.portal.controller;

import com.portal.model.JobAlert;
import com.portal.service.JobAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class JobAlertController {

    private final JobAlertService service;

    public JobAlertController(JobAlertService service) {
        this.service = service;
    }

    // GET all alerts
    @GetMapping
    public List<JobAlert> getAllAlerts() {
        return service.getAllAlerts();
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobAlert> getAlertById(@PathVariable Long id) {
        return service.getAlertById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET by type (JOB or EXAM)
    @GetMapping("/type/{type}")
    public List<JobAlert> getByType(@PathVariable String type) {
        return service.getAlertsByType(type);
    }

    // POST create
    @PostMapping
    public JobAlert createAlert(@RequestBody JobAlert alert) {
        return service.createAlert(alert);
    }

    // PUT update
    @PutMapping("/{id}")
    public ResponseEntity<JobAlert> updateAlert(@PathVariable Long id,
                                                 @RequestBody JobAlert alert) {
        try {
            return ResponseEntity.ok(service.updateAlert(id, alert));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        service.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Job Alert Portal is UP");
    }
}

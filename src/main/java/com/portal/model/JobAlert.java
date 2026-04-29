package com.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "job_alerts")
public class JobAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String organization;
    private String type;          // JOB or EXAM
    private String lastDate;
    private String description;
    private String link;

    // Constructors
    public JobAlert() {}

    public JobAlert(String title, String organization, String type,
                    String lastDate, String description, String link) {
        this.title = title;
        this.organization = organization;
        this.type = type;
        this.lastDate = lastDate;
        this.description = description;
        this.link = link;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLastDate() { return lastDate; }
    public void setLastDate(String lastDate) { this.lastDate = lastDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}

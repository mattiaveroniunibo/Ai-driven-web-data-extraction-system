package com.kayak.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class NewsResult extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    public Event event;

    @Column(length = 10000)
    public String textContent;

    @Column(length = 1000)
    public String summary;

    public LocalDateTime createdAt = LocalDateTime.now();

    public NewsResult() {
    }

    public NewsResult(Event event, String textContent, String summary) {
        this.event = event;
        this.textContent = textContent;
        this.summary = summary;
    }
}
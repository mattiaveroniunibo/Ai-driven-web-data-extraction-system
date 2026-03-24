package com.kayak.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ImageResult extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    public Event event;

    @Column(length = 1000)
    public String imagePath;

    public Long fileSize;

    public LocalDateTime createdAt = LocalDateTime.now();

    public ImageResult() {
    }

    public ImageResult(Event event, String imagePath) {
        this.event = event;
        this.imagePath = imagePath;
    }
}
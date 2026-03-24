package com.kayak.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Event extends PanacheEntity {

    @Column(nullable = false, length = 1000)
    public String url;

    @Column(nullable = false)
    public String tipologia;  // ← Ora è String!

    @Column(nullable = false)
    public LocalDateTime data;

    @Column(nullable = false)
    public boolean lavorato = false;

    @Column(length = 5000)
    public String risultato;

    public LocalDateTime createdAt = LocalDateTime.now();

    public static List<Event> findPendingEvents(LocalDateTime now) {
        return list("lavorato = false and data <= ?1", now);
    }
}
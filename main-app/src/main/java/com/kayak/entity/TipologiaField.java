package com.kayak.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class TipologiaField extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "tipologia_id", nullable = false)
    @JsonIgnore
    public Tipologia tipologia;

    @Column(nullable = false)
    public String fieldName;

    @Column(nullable = false)
    public String fieldType;  // STRING, NUMBER, DATE, FILE, URL

    public boolean requiresDownload;

    public TipologiaField() {
    }

    public TipologiaField(String fieldName, String fieldType, boolean requiresDownload) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.requiresDownload = requiresDownload;
    }
}
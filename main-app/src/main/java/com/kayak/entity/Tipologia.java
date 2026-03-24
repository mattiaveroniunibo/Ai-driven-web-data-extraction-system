package com.kayak.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Tipologia extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String name;

    public String description;

    @Column(nullable = false)
    public String mcpToolName;  // downloadImage, readNews, ecc.

    @OneToMany(mappedBy = "tipologia", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<TipologiaField> fields;

    public LocalDateTime createdAt;

    public Tipologia() {
        this.createdAt = LocalDateTime.now();
    }

    public static Tipologia findByName(String name) {
        return find("name", name).firstResult();
    }



}
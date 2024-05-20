package com.company.app.databaseEntities;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cd")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cd {
    @Id
    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "published_at")
    private Timestamp publishedAt;

    @ManyToMany(mappedBy = "cds")
    private Set<Title> titles = new HashSet<>();

    // min 1 label constraint
    @ManyToMany(mappedBy = "cds")
    private Set<Label> labels = new HashSet<>();

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_number")
    private Product product;

}

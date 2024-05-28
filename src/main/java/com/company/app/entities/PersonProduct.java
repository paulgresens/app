package com.company.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person_product")
public class PersonProduct {
    @EmbeddedId
    private PersonProductId id;

    @MapsId("name")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "name", nullable = false)
    private Person name;

    @MapsId("productNumber")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number", nullable = false)
    private Product productNumber;


    public enum Role {
        author,
        actor,
        creator,
        director,
        artist
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "Role")
    private Role role;
}
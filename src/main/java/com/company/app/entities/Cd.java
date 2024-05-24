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
@Table(name = "cd")
public class Cd {
    @Id
    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number", nullable = false)
    private Product product;

    @Column(name = "published_at", nullable = false)
    private String publishedAt;

}
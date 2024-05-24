package com.company.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class Book {
    @Id
    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number", nullable = false)
    private Product product;

    @Column(name = "number_pages", nullable = false)
    private Integer numberPages;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "isbn", nullable = false)
    private String isbn;

}
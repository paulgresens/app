package com.company.app.databaseEntities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @Column(name = "product_number")
    private String productNumber;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_number")
    private Product product;


    @Column(name = "number_pages")
    private Integer numberPages;

    @Column(name = "published_at")
    private Timestamp publishedAt;

    @Column
    private String isbn;

    @ManyToMany(mappedBy = "books")
    private Set<Publisher> publishers = new HashSet<>();
}

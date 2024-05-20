package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "publisher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Publisher {
    @Id
    private String name;

    @ManyToMany
    @JoinTable(name = "book_publisher", joinColumns = {@JoinColumn(name = "name")}, inverseJoinColumns = {
            @JoinColumn(name = "product_number")})
    private Set<Book> books = new HashSet<>();
}



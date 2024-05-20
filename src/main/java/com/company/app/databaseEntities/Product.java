package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @Column(name = "product_number")
    private String productNumber;

    @Column
    private String title;

    @Column(name = "times_sold")
    private Integer timesSold;

    @Column
    private String image;

    @ManyToMany(mappedBy = "products")
    private Set<Category> categories = new HashSet<>();
}
//done

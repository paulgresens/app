package com.company.app.databaseEntities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    private Integer id;

    @Column
    private String name;

    @Column(name = "super_category")
    @ManyToOne
    private Category superCategory;

    @ManyToMany
    @JoinTable(name = "product_category", joinColumns = {@JoinColumn(name = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "product_number")})
    private Set<Product> products;

}

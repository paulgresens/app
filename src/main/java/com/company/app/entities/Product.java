package com.company.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "salesrank")
    private Integer salesrank;

    @Column(name = "image")
    private String image;

}
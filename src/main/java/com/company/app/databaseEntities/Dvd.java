package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dvd")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dvd {
    @Id
    @Column(name = "product_number")
    private String productNumber;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_number")
    private Product product;

    @Column
    private String format;

    @Column
    private String runtime;

    @Column(name = "region_code")
    private String regionCode;
}

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
@Table(name = "dvd")
public class Dvd {
    @Id
    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_number", nullable = false)
    private Product product;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "runtime", nullable = false)
    private String runtime;

    @Column(name = "region_code", nullable = false)
    private String regionCode;

}


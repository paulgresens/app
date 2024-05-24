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
@Table(name = "\"similar\"")
public class Similar {
    @EmbeddedId
    private SimilarId id;

    @MapsId("productNumber1")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number_1", nullable = false)
    private Product productNumber1;

    @MapsId("productNumber2")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number_2", nullable = false)
    private Product productNumber2;

}
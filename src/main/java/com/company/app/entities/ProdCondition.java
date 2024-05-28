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
@Table(name = "prod_condition")
public class ProdCondition {
    @EmbeddedId
    private ProdConditionId id;

    @MapsId("productNumber")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number", nullable = false)
    private Product productNumber;

    @MapsId("branchOfficeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_office_id", nullable = false)
    private BranchOffice branchOffice;

    @Column(name = "price")
    private Integer price;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "currency")
    private String currency;

}
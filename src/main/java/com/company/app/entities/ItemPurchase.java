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
@Table(name = "item_purchase")
public class ItemPurchase {
    @Id
    @Column(name = "\"itemPurchaseId\"", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProdCondition prodCondition;

    @Column(name = "bought_at")
    private Instant boughtAt;

    @Column(name = "count", nullable = false)
    private Integer count;

}
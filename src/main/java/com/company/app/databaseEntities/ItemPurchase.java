package com.company.app.databaseEntities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "item_purchase")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPurchase {
    @Id
    private Integer itemPurchaseId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "product_number", referencedColumnName = "product_number")
            ,
            @JoinColumn(name = "branch_office_id", referencedColumnName = "branch_office_id")
            ,
            @JoinColumn(name = "condition", referencedColumnName = "condition")})

    private Condition condition;

    @Column(name = "bought_at")
    private String boughtAt;

    @Column
    private Integer count;

    @ManyToMany(mappedBy = "itemPurchases")
    private Set<Customer> customers = new HashSet<>();
}

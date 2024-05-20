package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    private String id;

    @Column
    private String street;

    @Column
    private String zip;

    @Column
    private String username;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @ManyToMany
    @JoinTable(name = "order", joinColumns = {@JoinColumn(name = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "itemPurchaseId")})
    private Set<ItemPurchase> itemPurchases;
}

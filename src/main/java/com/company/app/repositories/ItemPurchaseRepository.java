package com.company.app.repositories;

import com.company.app.entities.ItemPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPurchaseRepository extends JpaRepository<ItemPurchase, Integer> {
}

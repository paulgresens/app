package com.company.app.repositories;

import com.company.app.entities.PersonProduct;
import com.company.app.entities.PersonProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonProductRepository extends JpaRepository<PersonProduct, PersonProductId> {
}

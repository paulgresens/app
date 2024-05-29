package com.company.app.repositories;

import com.company.app.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query(value = "SELECT MAX(customer_id) FROM customer;", nativeQuery = true)
    Integer findEntityWithHighestId();

    @Query(value = "SELECT customer_id, street, zip, username, bank_account_number  from customer WHERE username = :username", nativeQuery = true)
    Customer findByUsername(@Param("username") String username);
}



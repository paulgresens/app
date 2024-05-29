package com.company.app.repositories;

import com.company.app.entities.BranchOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Integer> {
    @Query(value = "SELECT MAX(id) FROM branch_office;", nativeQuery = true)
    Integer findEntityWithHighestId();

    @Query(value = "SELECT id, name, street, zip FROM branch_office WHERE name = 'unknown' AND street = 'unknown' AND zip = 'unknown'", nativeQuery = true)
    BranchOffice findDefaultBranchOffice();
}



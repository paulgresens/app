package com.company.app.repositories;

import com.company.app.DTOs.ProdConditionDTO;
import com.company.app.entities.ProdCondition;
import com.company.app.entities.ProdConditionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ConditionRepository extends JpaRepository<ProdCondition, ProdConditionId> {
    @Query(value = "SELECT new com.company.app.DTOs.ProdConditionDTO(p.productNumber.productNumber, p.branchOffice.id, p.id.condition, p.price, p.count, p.currency) FROM ProdCondition p WHERE p.price IS NOT NULL AND p.productNumber.productNumber = :productNumber")
    List<ProdConditionDTO> getOffers(@Param("productNumber") String productNumber);
}

package com.company.app.repositories;

import com.company.app.entities.ProdCondition;
import com.company.app.entities.ProdConditionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionRepository extends JpaRepository<ProdCondition, ProdConditionId> {
}

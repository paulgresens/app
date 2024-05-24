package com.company.app.repositories;

import com.company.app.entities.Condition;
import com.company.app.entities.ConditionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, ConditionId> {
}

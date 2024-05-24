package com.company.app.repositories;

import com.company.app.entities.CdLabel;
import com.company.app.entities.CdLabelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdLabelRepository extends JpaRepository<CdLabel, CdLabelId> {
}

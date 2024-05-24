package com.company.app.repositories;

import com.company.app.entities.Similar;
import com.company.app.entities.SimilarId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimilarRepository extends JpaRepository<Similar, SimilarId> {
}

package com.company.app.repositories;

import com.company.app.entities.CdTitle;
import com.company.app.entities.CdTitleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdTitleRepository extends JpaRepository<CdTitle, CdTitleId> {
}

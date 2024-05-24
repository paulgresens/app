package com.company.app.repositories;

import com.company.app.entities.BookPublisher;
import com.company.app.entities.BookPublisherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookPublisherRepository extends JpaRepository<BookPublisher, BookPublisherId> {
}

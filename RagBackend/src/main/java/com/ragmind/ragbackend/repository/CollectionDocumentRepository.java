package com.ragmind.ragbackend.repository;

import com.ragmind.ragbackend.model.Collection;
import com.ragmind.ragbackend.model.CollectionDocuments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionDocumentRepository extends JpaRepository<CollectionDocuments,Long> {
    Page<CollectionDocuments> findByCollection(Collection collection, Pageable pageable);
}

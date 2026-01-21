package com.ragmind.ragbackend.repository;

import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.CollectionDocuments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionDocumentRepository extends JpaRepository<CollectionDocuments,Long> {
    Page<CollectionDocuments> findByCollection(Collection collection, Pageable pageable);
    List<CollectionDocuments> findByCollection_Id(Long collectionId);

    Optional<CollectionDocuments> findByIdAndCollection_Id(
            Long documentId,
            Long collectionId
    );

}

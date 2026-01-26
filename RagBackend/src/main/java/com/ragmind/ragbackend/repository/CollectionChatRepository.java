package com.ragmind.ragbackend.repository;

import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.CollectionChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionChatRepository extends JpaRepository<CollectionChat, Long> {
    Page<CollectionChat> findByCollection(Collection collection, Pageable pageable);

    List<CollectionChat> findAllByCollection_Id(Long collectionId);
}

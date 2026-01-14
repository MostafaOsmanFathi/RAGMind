package com.ragmind.ragbackend.repository;

import com.ragmind.ragbackend.model.Collection;
import com.ragmind.ragbackend.model.CollectionChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionChatRepository extends JpaRepository<CollectionChat, Long> {
    Page<CollectionChat> findByCollection(Collection collection, Pageable pageable);
}

package com.ragmind.ragbackend.repository;

import com.ragmind.ragbackend.model.Collection;
import com.ragmind.ragbackend.model.Notification;
import com.ragmind.ragbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository  extends JpaRepository<Collection,Long> {
    Page<Collection> findByUser(User user, Pageable pageable);

}

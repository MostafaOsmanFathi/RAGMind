package com.ragmind.ragbackend.repository;

import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Page<Collection> findByUser(User user, Pageable pageable);

    Page<Collection> findByUser_Email(String userEmail, Pageable pageable);

    @Override
    Optional<Collection> findById(Long id);

    @Override
    boolean existsById(Long aLong);
}

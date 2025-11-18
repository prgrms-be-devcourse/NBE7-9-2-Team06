package com.backend.petplace.domain.bookmark.repository;

import com.backend.petplace.domain.bookmark.entity.Bookmark;
import java.lang.ScopedValue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  boolean existsByUserIdAndPlace_Id(Long userId, Long placeId);

  Optional<Bookmark> findByUserIdAndPlace_Id(Long userId, Long placeId);
}

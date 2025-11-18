package com.backend.petplace.domain.bookmark.repository;

import com.backend.petplace.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  boolean existsByUserIdAndPlace_Id(Long userId, Long placeId);

}

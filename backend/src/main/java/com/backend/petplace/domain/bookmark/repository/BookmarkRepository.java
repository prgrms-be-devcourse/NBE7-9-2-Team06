package com.backend.petplace.domain.bookmark.repository;

import com.backend.petplace.domain.bookmark.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  boolean existsByUserIdAndPlace_Id(Long userId, Long placeId);

  Optional<Bookmark> findByUserIdAndPlace_Id(Long userId, Long placeId);

  @Query("""
      select b 
      from Bookmark b 
      join fetch b.place 
      where b.userId = :userId
      """)
  List<Bookmark> findAllByUserIdWithPlace(@Param("userId") Long userId);
}

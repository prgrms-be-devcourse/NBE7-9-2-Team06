package com.backend.petplace.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.petplace.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

}

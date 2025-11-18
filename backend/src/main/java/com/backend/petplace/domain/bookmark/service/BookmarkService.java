package com.backend.petplace.domain.bookmark.service;

import static com.backend.petplace.global.response.ErrorCode.ALREADY_BOOKMARKED;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_BOOKMARK;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PLACE;

import com.backend.petplace.domain.bookmark.entity.Bookmark;
import com.backend.petplace.domain.bookmark.repository.BookmarkRepository;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;
  private final BookmarkRepository bookmarkRepository;

  public Long addBookmark(Long userId, Long placeId) {

    User user = findUserById(userId);
    Place place = findPlaceById(placeId);

    if (bookmarkRepository.existsByUserIdAndPlace_Id(userId, placeId)) {
      throw new BusinessException(ALREADY_BOOKMARKED);
    }

    Bookmark saved = bookmarkRepository.save(Bookmark.createNewBookmark(user.getId(), place));
    return saved.getId();
  }

  public void removeBookmark(Long userId, Long placeId) {

    findPlaceById(userId);
    findPlaceById(placeId);

    bookmarkRepository.delete(findBookmark(userId, placeId));
  }

  private User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
  }

  private Place findPlaceById(Long placeId) {
    return placeRepository.findById(placeId)
        .orElseThrow(() -> new BusinessException(NOT_FOUND_PLACE));
  }

  private Bookmark findBookmark(Long userId, Long placeId) {
    return bookmarkRepository.findByUserIdAndPlace_Id(userId, placeId)
        .orElseThrow(() -> new BusinessException(NOT_FOUND_BOOKMARK));
  }

}

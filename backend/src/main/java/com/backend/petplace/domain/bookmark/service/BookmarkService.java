package com.backend.petplace.domain.bookmark.service;

import static com.backend.petplace.global.response.ErrorCode.ALREADY_BOOKMARKED;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_BOOKMARK;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PLACE;

import com.backend.petplace.domain.bookmark.dto.response.MyBookmarkPlaceResponse;
import com.backend.petplace.domain.bookmark.entity.Bookmark;
import com.backend.petplace.domain.bookmark.repository.BookmarkRepository;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

  private static final String BOOKMARK_USER_KEY_PREFIX = "bookmark:user:";

  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;
  private final BookmarkRepository bookmarkRepository;
  private final StringRedisTemplate stringRedisTemplate;

  public Long addBookmark(Long userId, Long placeId) {

    User user = findUserById(userId);
    Place place = findPlaceById(placeId);

    if (bookmarkRepository.existsByUserIdAndPlace_Id(userId, placeId)) {
      throw new BusinessException(ALREADY_BOOKMARKED);
    }

    Bookmark saved = bookmarkRepository.save(Bookmark.createNewBookmark(user.getId(), place));

    // Redis 캐시 갱신: 이 유저의 북마크 Set에 placeId 추가
    String key = BOOKMARK_USER_KEY_PREFIX + userId;
    stringRedisTemplate.opsForSet().add(key, placeId.toString());

    return saved.getId();
  }

  public void removeBookmark(Long userId, Long placeId) {

    findUserById(userId);
    findPlaceById(placeId);

    bookmarkRepository.delete(findBookmark(userId, placeId));

    // Redis 캐시 갱신: Set에서 placeId 제거
    String key = BOOKMARK_USER_KEY_PREFIX + userId;
    stringRedisTemplate.opsForSet().remove(key, placeId.toString());
  }

  @Transactional(readOnly = true)
  public List<MyBookmarkPlaceResponse> getMyBookmarks(Long userId) {
    findUserById(userId);

    String key = BOOKMARK_USER_KEY_PREFIX + userId;

    // 1) Redis에서 이 유저의 북마크 placeId Set 조회
    Set<String> cachedIds = stringRedisTemplate.opsForSet().members(key);

    // 1-1) 캐시에 데이터가 있다면 → placeId들로 Place 조회
    if (cachedIds != null && !cachedIds.isEmpty()) {

      List<Long> placeIds = cachedIds.stream()
          .map(Long::valueOf)
          .toList();

      List<Place> places = placeRepository.findAllById(placeIds);

      // Optional: 순서를 정해주고 싶다면 여기서 정렬 (예: 이름 순, 생성일 순 등)
      places.sort(Comparator.comparing(Place::getName));

      return places.stream()
          .map(MyBookmarkPlaceResponse::from)
          .toList();
    }

    // 1-2) 캐시에 없으면 → DB에서 북마크 + place join으로 조회
    List<Bookmark> bookmarks = bookmarkRepository.findAllByUserIdWithPlace(userId);

    if (bookmarks.isEmpty()) {
      return Collections.emptyList();
    }

    // 2) Redis에 placeId들 적재
    List<String> placeIdStrings = bookmarks.stream()
        .map(b -> b.getPlace().getId().toString())
        .toList();

    stringRedisTemplate.opsForSet().add(key, placeIdStrings.toArray(String[]::new));

    // 3) 응답 DTO 변환
    return bookmarks.stream()
        .map(bookmark -> MyBookmarkPlaceResponse.from(bookmark.getPlace()))
        .toList();
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

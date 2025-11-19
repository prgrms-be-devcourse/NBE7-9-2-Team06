package com.backend.petplace.domain.bookmark.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;

import com.backend.petplace.domain.bookmark.dto.response.MyBookmarkPlaceResponse;
import com.backend.petplace.domain.bookmark.entity.Bookmark;
import com.backend.petplace.domain.bookmark.repository.BookmarkRepository;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

  private static final String BOOKMARK_USER_KEY_PREFIX = "bookmark:user:";

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlaceRepository placeRepository;

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private StringRedisTemplate stringRedisTemplate;

  @Mock
  private SetOperations<String, String> setOperations;

  @InjectMocks
  private BookmarkService bookmarkService;

  private User createUser(Long id) {
    // 실제 엔티티 사용 (mock 아님) → 불필요 stubbing 방지
    return User.builder()
        .id(id)
        .build();
  }

  private Place createPlace(Long id, String name) {
    return Place.builder()
        .id(id)
        .name(name)
        .build();
  }

  @BeforeEach
  void setUp() {
    // 여기서는 opsForSet 미리 stub 안 함 (어떤 테스트에서는 안 쓰여서 UnnecessaryStubbing 발생)
  }

  @Nested
  @DisplayName("addBookmark()")
  class AddBookmark {

    @Test
    @DisplayName("성공 - 유저/장소 존재 & 아직 북마크 안 되어 있으면 저장 후 Redis에 추가")
    void addBookmark_success() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      User user = createUser(userId);
      Place place = createPlace(placeId, "행복동물병원");

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
      given(bookmarkRepository.existsByUserIdAndPlace_Id(userId, placeId)).willReturn(false);

      Bookmark saved = Bookmark.builder()
          .id(100L)
          .userId(userId)
          .place(place)
          .build();

      // 서비스 안에서 Bookmark.createNewBookmark(...)로 새 인스턴스를 만들어서 save에 넘기기 때문에
      // 정확한 인스턴스로 stub 하면 안 되고 any(Bookmark.class)로 받아야 함
      given(bookmarkRepository.save(any(Bookmark.class))).willReturn(saved);

      given(stringRedisTemplate.opsForSet()).willReturn(setOperations);

      // when
      Long resultId = bookmarkService.addBookmark(userId, placeId);

      // then
      assertEquals(100L, resultId);

      String key = BOOKMARK_USER_KEY_PREFIX + userId;
      verify(setOperations).add(key, placeId.toString());
      verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("실패 - 유저가 존재하지 않으면 BusinessException 발생")
    void addBookmark_fail_userNotFound() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.addBookmark(userId, placeId));

      verify(bookmarkRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패 - 장소가 존재하지 않으면 BusinessException 발생")
    void addBookmark_fail_placeNotFound() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      User user = createUser(userId);

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(placeRepository.findById(placeId)).willReturn(Optional.empty());

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.addBookmark(userId, placeId));

      verify(bookmarkRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패 - 이미 북마크 되어 있으면 BusinessException 발생")
    void addBookmark_fail_alreadyBookmarked() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      User user = createUser(userId);
      Place place = createPlace(placeId, "행복동물병원");

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
      given(bookmarkRepository.existsByUserIdAndPlace_Id(userId, placeId)).willReturn(true);

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.addBookmark(userId, placeId));

      verify(bookmarkRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("removeBookmark()")
  class RemoveBookmark {

    @Test
    @DisplayName("성공 - 유저/장소/북마크 모두 존재하면 삭제 후 Redis에서 제거")
    void removeBookmark_success() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      User user = createUser(userId);
      Place place = createPlace(placeId, "행복동물병원");

      Bookmark bookmark = Bookmark.builder()
          .id(100L)
          .userId(userId)
          .place(place)
          .build();

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
      given(bookmarkRepository.findByUserIdAndPlace_Id(userId, placeId))
          .willReturn(Optional.of(bookmark));

      given(stringRedisTemplate.opsForSet()).willReturn(setOperations);

      // when
      bookmarkService.removeBookmark(userId, placeId);

      // then
      verify(bookmarkRepository).delete(bookmark);

      String key = BOOKMARK_USER_KEY_PREFIX + userId;
      verify(setOperations).remove(key, placeId.toString());
    }

    @Test
    @DisplayName("실패 - 유저가 존재하지 않으면 BusinessException 발생")
    void removeBookmark_fail_userNotFound() {
      // given
      Long userId = 1L;
      Long placeId = 10L;

      given(userRepository.findById(userId)).willReturn(Optional.empty());

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.removeBookmark(userId, placeId));

      verify(bookmarkRepository, never()).delete(any());
    }

    @Test
    @DisplayName("실패 - 장소가 존재하지 않으면 BusinessException 발생")
    void removeBookmark_fail_placeNotFound() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      User user = createUser(userId);

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(placeRepository.findById(placeId)).willReturn(Optional.empty());

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.removeBookmark(userId, placeId));

      verify(bookmarkRepository, never()).delete(any());
    }

    @Test
    @DisplayName("실패 - 북마크가 존재하지 않으면 BusinessException 발생")
    void removeBookmark_fail_bookmarkNotFound() {
      // given
      Long userId = 1L;
      Long placeId = 10L;
      User user = createUser(userId);
      Place place = createPlace(placeId, "행복동물병원");

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
      given(bookmarkRepository.findByUserIdAndPlace_Id(userId, placeId))
          .willReturn(Optional.empty());

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.removeBookmark(userId, placeId));

      verify(bookmarkRepository, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("getMyBookmarks()")
  class GetMyBookmarks {

    @Test
    @DisplayName("성공 - 캐시 히트 시 Redis에서 placeId Set 조회 후 Place로 변환하여 정렬된 응답 반환")
    void getMyBookmarks_cacheHit() {
      // given
      Long userId = 1L;
      User user = createUser(userId);

      given(userRepository.findById(userId)).willReturn(Optional.of(user));

      String key = BOOKMARK_USER_KEY_PREFIX + userId;
      Set<String> cachedIds = Set.of("10", "20");
      given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
      given(setOperations.members(key)).willReturn(cachedIds);

      Place placeA = createPlace(10L, "AAA 병원");
      Place placeB = createPlace(20L, "BBB 카페");

      given(placeRepository.findAllById(anyList()))
          .willReturn(new ArrayList<>(List.of(placeB, placeA)));
      // 또는 Arrays.asList(placeB, placeA) 도 됨

      // when
      List<MyBookmarkPlaceResponse> results = bookmarkService.getMyBookmarks(userId);

      // then - 이름 오름차순으로 정렬되었는지 확인
      assertEquals(2, results.size());
      assertEquals("AAA 병원", results.get(0).name());
      assertEquals("BBB 카페", results.get(1).name());
    }

    @Test
    @DisplayName("성공 - 캐시 미스 시 DB에서 북마크+장소 조회 후 Redis에 적재하고 응답")
    void getMyBookmarks_cacheMiss_thenLoadFromDb() {
      // given
      Long userId = 1L;
      User user = createUser(userId);

      given(userRepository.findById(userId)).willReturn(Optional.of(user));

      String key = BOOKMARK_USER_KEY_PREFIX + userId;
      given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
      // 캐시 미스: null 또는 빈 Set
      given(setOperations.members(key)).willReturn(null);

      Place place1 = createPlace(10L, "AAA 병원");
      Place place2 = createPlace(20L, "BBB 카페");

      Bookmark bookmark1 = Bookmark.builder()
          .id(100L)
          .userId(userId)
          .place(place1)
          .build();
      Bookmark bookmark2 = Bookmark.builder()
          .id(200L)
          .userId(userId)
          .place(place2)
          .build();

      given(bookmarkRepository.findAllByUserIdWithPlace(userId))
          .willReturn(List.of(bookmark1, bookmark2));

      // when
      List<MyBookmarkPlaceResponse> results = bookmarkService.getMyBookmarks(userId);

      // then
      assertEquals(2, results.size());
      // Bookmark 순서 그대로 매핑
      assertEquals(place1.getId(), results.get(0).placeId());
      assertEquals(place2.getId(), results.get(1).placeId());

      // Redis에 placeId들 적재
      verify(setOperations).add(key, "10", "20");
    }

    @Test
    @DisplayName("성공 - 캐시 미스 & 북마크가 하나도 없으면 빈 리스트 반환 및 Redis 적재 안 함")
    void getMyBookmarks_cacheMiss_andNoBookmarks() {
      // given
      Long userId = 1L;
      User user = createUser(userId);

      given(userRepository.findById(userId)).willReturn(Optional.of(user));

      String key = BOOKMARK_USER_KEY_PREFIX + userId;
      given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
      given(setOperations.members(key)).willReturn(null);
      given(bookmarkRepository.findAllByUserIdWithPlace(userId))
          .willReturn(List.of());

      // when
      List<MyBookmarkPlaceResponse> results = bookmarkService.getMyBookmarks(userId);

      // then
      assertNotNull(results);
      assertTrue(results.isEmpty());

      verify(setOperations, never()).add(eq(key), any());
    }

    @Test
    @DisplayName("실패 - 유저가 존재하지 않으면 BusinessException 발생")
    void getMyBookmarks_fail_userNotFound() {
      // given
      Long userId = 1L;
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      // when & then
      assertThrows(BusinessException.class,
          () -> bookmarkService.getMyBookmarks(userId));

      // Redis 쪽은 전혀 호출되지 않아야 함
      verify(stringRedisTemplate, never()).opsForSet();
    }
  }
}

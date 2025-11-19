package com.backend.petplace.domain.point.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.petplace.domain.point.dto.response.PointHistoryResponse;
import com.backend.petplace.domain.point.entity.PointDescription;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PointHistoryResponseTest {

  @Test
  @DisplayName("총 포인트와 내역 목록을 정확히 초기화하는 생성자 테스트")
  void testConstructor() {
    // given
    int totalPoints = 2500;

    PointTransaction mockTransaction = new PointTransaction(
        1L,
        10L,
        "멍멍카페",
        "서울 강남구 테헤란로 123",
        PointDescription.REVIEW_TEXT,
        LocalDate.now(),
        100
    );
    List<PointTransaction> history = List.of(mockTransaction);

    // when
    PointHistoryResponse response = new PointHistoryResponse(totalPoints, history);

    // then
    assertThat(response.getTotalPoints()).isEqualTo(totalPoints);
    assertThat(response.getHistory()).hasSize(1);
    assertThat(response.getHistory().get(0).getPointId()).isEqualTo(1L);
  }
}

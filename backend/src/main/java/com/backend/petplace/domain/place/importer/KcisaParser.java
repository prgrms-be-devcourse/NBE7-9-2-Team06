package com.backend.petplace.domain.place.importer;

import com.backend.petplace.domain.place.dto.KcisaDto;
import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.entity.mapper.CategoryMapper;
import com.backend.petplace.domain.place.importer.model.ImportParsed;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class KcisaParser {

  // 좌표 패턴
  private static final Pattern COORD_P = Pattern.compile(
      "([NS])\\s*([0-9.]+)\\s*,\\s*([EW])\\s*([0-9.]+)");

  // 우편번호 패턴
  private static final Pattern POSTAL_P = Pattern.compile("^\\((\\d{5})\\)\\s*(.+)$");

  // 운영시간 패턴
  private static final Pattern OPENING_P = Pattern.compile("^\\s*운영\\s*시간\\s*:?\\s*(.+)$");

  // 휴무일 패턴
  private static final Pattern CLOSED_P = Pattern.compile("^\\s*휴\\s*무\\s*일\\s*:?\\s*(.+)$");

  // 반려동물 제한사항 패턴
  private static final Pattern PET_LIMIT_P = Pattern.compile("^\\s*반려동물\\s*제한사항\\s*:?\\s*(.+)$");

  public ImportParsed parse(KcisaDto.Item it) {
    //  주소/우편번호
    String postal = null, addr = null;
    if (it.address() != null) {
      Matcher m = POSTAL_P.matcher(it.address().trim());
      if (m.find()) {
        postal = m.group(1);
        addr = m.group(2);
      } else {
        addr = it.address().trim();
      }
    }

    // 좌표
    Double lat = null, lng = null;
    if (it.coordinates() != null) {
      Matcher m = COORD_P.matcher(it.coordinates());
      if (m.find()) {
        lat = Double.parseDouble(m.group(2)) * ("S".equalsIgnoreCase(m.group(1)) ? -1 : 1);
        lng = Double.parseDouble(m.group(4)) * ("W".equalsIgnoreCase(m.group(3)) ? -1 : 1);
      }
    }

    // description 토큰 파싱
    String opening = null, closed = null, petLimit = null;
    Boolean parking = null, petAllowed = null;
    if (it.description() != null && !it.description().isBlank()) {
      String[] tokens = it.description().split("\\|");
      for (String raw : tokens) {
        String s = raw.trim();
        if (OPENING_P.matcher(s).find()) {
          opening = s.replaceFirst("^\\s*운영\\s*시간\\s*:?\\s*", "").trim();
        } else if (CLOSED_P.matcher(s).find()) {
          closed = s.replaceFirst("^\\s*휴\\s*무\\s*일\\s*:?\\s*", "").trim();
        } else if (s.contains("주차가능")) {
          parking = true;
        } else if (s.contains("주차 불가")) {
          parking = false;
        } else if (s.contains("동반가능")) {
          petAllowed = true;
        } else if (s.contains("동반불가")) {
          petAllowed = false;
        } else if (PET_LIMIT_P.matcher(s).find()) {
          petLimit = s.replaceFirst("^\\s*반려동물\\s*제한사항\\s*:?\\s*", "").trim();
        }
      }
    }

    // tel/url
    String tel = it.tel() == null ? null : it.tel().replaceAll("[^0-9-]", "");
    String url = normalizeUrl(it.url());

    // 카테고리 매핑
    Category1Type c1 = CategoryMapper.mapCategory1(it.category1());
    Category2Type c2 = CategoryMapper.mapCategory2(it.category2());

    // uniqueKey: title + 우편번호(없으면 주소) → SHA-256 해싱
    String uniqueKey = buildUniqueKey(it.title(), postal, addr);

    return new ImportParsed(
        it.title(), c1, c2, opening, closed, parking, petAllowed, petLimit, tel, url, postal, addr,
        lat, lng, it.description(), uniqueKey
    );
  }

  private static String normalizeTitle(String t) {
    if (t == null) {
      return "";
    }
    return t.trim().toLowerCase().replaceAll("\\s+", " ");
  }

  private static String normalizeAddrForKey(String addr) {
    if (addr == null) {
      return "";
    }
    return addr.trim().toLowerCase().replaceAll("\\s+", " ");
  }

  private static String buildUniqueKey(String title, String postal, String addr) {
    String t = normalizeTitle(title);
    String base = (postal != null && !postal.isBlank())
        ? (t + "|" + postal)
        : (t + "|" + normalizeAddrForKey(addr));
    return sha256Hex(base);
  }

  private static String normalizeUrl(String u) {
    if (u == null || u.isBlank()) {
      return null;
    }
    String s = u.trim();
    if (!s.matches("^(?i)https?://.*")) {
      s = "http://" + s;
    }
    return s;
  }

  private static String sha256Hex(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] out = md.digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : out) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}

package com.backend.petplace.global.component;

import java.util.regex.Pattern;

public class KcisaParser {

  // 좌표 패턴
  private static final Pattern COORD_P = Pattern.compile("([NS])\\s*([0-9.]+)\\s*,\\s*([EW])\\s*([0-9.]+)");

  // 우편번호 패턴
  private static final Pattern POSTAL_P = Pattern.compile("^\\((\\d{5})\\)\\s*(.+)$");

  // 운영시간 패턴
  private static final Pattern OPENING_P = Pattern.compile("^\\s*운영\\s*시간\\s*:?\\s*(.+)$");

  // 휴무일 패턴
  private static final Pattern CLOSED_P  = Pattern.compile("^\\s*휴\\s*무\\s*일\\s*:?\\s*(.+)$");

  // 반려동물 제한사항 패턴
  private static final Pattern PET_LIMIT_P = Pattern.compile("^\\s*반려동물\\s*제한사항\\s*:?\\s*(.+)$");

}

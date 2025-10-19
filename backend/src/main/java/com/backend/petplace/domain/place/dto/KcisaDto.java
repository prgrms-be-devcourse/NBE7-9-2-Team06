package com.backend.petplace.domain.place.dto;

import java.util.List;

public record KcisaDto(Response response) {

  public record Response(Header header, Body body) {}

  public record Header(String resultCode, String resultMsg) {}

  public record Body(Items items, String numOfRows, String pageNo, String totalCount) {}

  public record Items(List<Item> item) {}

  public record Item(
      String title,
      String issuedDate,
      String category1,
      String category2,
      String category3,
      String description,
      String tel,
      String url,
      String address,
      String coordinates,
      String charge
  ) {}
}

package com.backend.petplace.global.config.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ExampleHolder {

  private final Example holder;
  private final int code;
  private final String name;

  @Builder
  private ExampleHolder(Example holder, int code, String name) {
    this.holder = holder;
    this.code = code;
    this.name = name;
  }
}


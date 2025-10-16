package com.backend.petplace.global.config.swagger;

import com.backend.petplace.global.response.ApiResponse;
import com.backend.petplace.global.response.ErrorCode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("PetPlace")
            .version("1.0")
            .description("PetPlace API 서버 문서입니다")
        );
  }

  @Bean
  public OperationCustomizer customize() {
    return (operation, handlerMethod) -> {
      ApiErrorCodeExamples apiErrorCodeExamples = handlerMethod.getMethodAnnotation(
          ApiErrorCodeExamples.class);

      if (apiErrorCodeExamples != null) {
        generateErrorCodeResponseExample(operation, apiErrorCodeExamples.value());
      }
      return operation;
    };
  }

  private void generateErrorCodeResponseExample(Operation operation, ErrorCode[] errorCodes) {
    ApiResponses responses = operation.getResponses();

    Map<Integer, List<ExampleHolder>> statusWithExampleHolders;
    statusWithExampleHolders = Arrays.stream(errorCodes)
        .map(
            errorCode -> ExampleHolder.builder()
                .holder(getSwaggerExample(errorCode))
                .code(errorCode.getStatus().value())
                .name(errorCode.name())
                .build()
        )
        .collect(Collectors.groupingBy(ExampleHolder::getCode));

    addExamplesToResponses(responses, statusWithExampleHolders);
  }

  private Example getSwaggerExample(ErrorCode errorCode) {
    Example example = new Example();
    example.setSummary(errorCode.getMessage());
    example.setValue(ApiResponse.error(errorCode));
    return example;
  }

  private void addExamplesToResponses(ApiResponses responses,
      Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
    statusWithExampleHolders.forEach(
        (status, v) -> {
          Content content = new Content();
          MediaType mediaType = new MediaType();
          io.swagger.v3.oas.models.responses.ApiResponse apiResponse =
              new io.swagger.v3.oas.models.responses.ApiResponse();

          v.forEach(
              exampleHolder -> mediaType.addExamples(
                  exampleHolder.getName(),
                  exampleHolder.getHolder()
              )
          );

          content.addMediaType("application/json", mediaType);
          apiResponse.setContent(content);
          responses.addApiResponse(String.valueOf(status), apiResponse);
        });
  }
}
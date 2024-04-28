package com.clearsolutions.service.specification;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {

  @Parameter(description = "a minimal value in the birthdate range", example = "1990-01-01")
  private LocalDate minBirthdate;

  @Parameter(description = "a maximum value in the birthdate range", example = "2000-01-01")
  private LocalDate maxBirthdate;
}

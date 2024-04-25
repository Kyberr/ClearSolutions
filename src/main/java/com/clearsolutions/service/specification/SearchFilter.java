package com.clearsolutions.service.specification;

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

  private LocalDate minBirthdate;
  private LocalDate maxBirthdate;
}

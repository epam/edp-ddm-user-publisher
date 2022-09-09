/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.user.validate.custom;

import static com.epam.digital.data.platform.user.validate.custom.CustomAttributeLengthValidator.MAX_LENGTH;
import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.user.model.ValidationResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomAttributeLengthValidatorTest {

  private CustomAttributeLengthValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new CustomAttributeLengthValidator();
  }

  @Test
  void happyPathDoesNotWriteAnyErrors() {
    var result = validator.validate(1,
        user().attributes(Map.of("A".repeat(MAX_LENGTH), List.of("B".repeat(MAX_LENGTH)))).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void shouldWriteErrorsWhenKeyLongerThenMaxLength() {
    var result = validator.validate(1,
        user().attributes(Map.of("A".repeat(MAX_LENGTH + 1), List.of("B"))).build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0))
        .contains("a name longer than 255 symbols, but it is '256' symbols long");
  }

  @Test
  void shouldWriteErrorsWhenValueLongerThenMaxLength() {
    var result = validator.validate(1,
        user().attributes(Map.of("A", List.of("B".repeat(MAX_LENGTH + 1)))).build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0))
        .contains("a value longer than 255 symbols, but it is '256' symbols long");
  }

  @Test
  void shouldCorrectlyCountLengthOfValue() {
    var result = validator.validate(1,
        user().attributes(Map.of("A",
            List.of("B".repeat(100), "B".repeat(100), "B".repeat(100)))).build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0))
        .contains("a value longer than 255 symbols, but it is '304' symbols long");
  }
}

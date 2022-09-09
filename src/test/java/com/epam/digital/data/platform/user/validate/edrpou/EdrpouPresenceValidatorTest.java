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

package com.epam.digital.data.platform.user.validate.edrpou;

import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.user.model.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EdrpouPresenceValidatorTest {

  private EdrpouPresenceValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new EdrpouPresenceValidator();
  }

  @Test
  void happyPathDoesNotWriteAnyErrors() {
    var result = validator.validate(1, user().edrpou("11111111").build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void writeErrorWhenEdrpouIsNull() {
    var result = validator.validate(1, user().edrpou(null).build(), new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0)).isEqualTo("User does not contain EDRPOU");
  }

  @Test
  void writeErrorWhenEdrpouIsEmptyString() {
    List<String> edrpou = new ArrayList<>();
    edrpou.add("");

    var result = validator.validate(1, user().edrpou("").build(), new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0)).isEqualTo("User does not contain EDRPOU");
  }
}

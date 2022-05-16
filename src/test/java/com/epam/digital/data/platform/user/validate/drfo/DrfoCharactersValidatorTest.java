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

package com.epam.digital.data.platform.user.validate.drfo;

import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.digital.data.platform.user.model.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrfoCharactersValidatorTest {

  private DrfoCharactersValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new DrfoCharactersValidator();
  }

  @Test
  void happyPathDoesNotWriteAnyErrors() {
    var result = validator.validate(1, user().drfo("11 Бб ---Qq").build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void errorWhenDrfoContainsInvalidCharacter() {
    var result = validator.validate(1, user().drfo("!@#$%^&*()/|\\\":.,+<>[]{}=").build(),
        new ValidationResult());

    assertEquals(1, result.size());
    assertThat(result.get(1).get(0)).isEqualTo(
        "DRFO contains invalid symbols: {!, @, #, $, %, ^, &, *, (, ), /, |, \\, \", :, ., ,, +, <, >, [, ], {, }, =}");
  }
}
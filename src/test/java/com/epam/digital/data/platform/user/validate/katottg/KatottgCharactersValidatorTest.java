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

package com.epam.digital.data.platform.user.validate.katottg;

import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.user.model.ValidationResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class KatottgCharactersValidatorTest {

  private KatottgCharactersValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new KatottgCharactersValidator();
  }

  @Test
  void notWriteAnyErrorsWhenKatottgsCorrect() {
    var result = validator.validate(1,
        user().katottg(List.of("UA03000000000012345", "UA03020000000012345", "UA")).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {"UA1234567890123456", "UA123456789012345678", "UA 1234567890123456",
      "UB1234567890123456", "UA12345678901r23467", "ua12345678901234567", "UA1"})
  void writeErrorIfIncorrectFormatOfAnyKatottgCodeInList(String arg) {
    var result = validator.validate(1, user().katottg(List.of(arg)).build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0))
        .isEqualTo(
            "Incorrect KATOTTG code. KATOTTG should starts from UA and have 0 or 17 digits, " 
                + "like this: 'UA' or 'UA12345678901234567'. But you are trying to import code '"
                + arg + "' in wrong format");
  }
}
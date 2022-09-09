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

class KatottgCountValidatorTest {

  private KatottgCountValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new KatottgCountValidator();
  }

  @Test
  void notWriteAnyErrorsWhenKatottgsCorrect() {
    var result = validator.validate(1,
        user().katottg(List.of("UA03000000000012345", "UA03020000000012345")).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void notWriteAnyErrorsWhenKatottgIsNull() {
    var result = validator.validate(1, user().build(), new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void notWriteAnyErrorsWhenKatottgIsEmptyList() {
    var result = validator.validate(1,
        user().katottg(List.of()).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void notWriteAnyErrorsWhenKatottgIsListOf16ElementsOrLess() {
    var result = validator.validate(1,
        user().katottg(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "16")).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void errorWhenKatottgIsListWithMoreThen16Elements() {
    var result = validator.validate(1,
        user().katottg(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "16", "17")).build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0)).isEqualTo("The maximum allowable number of values for "
        + "the KATOTTG attribute is '16', but this user has '17' values");
  }
}
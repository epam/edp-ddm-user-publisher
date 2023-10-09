/*
 * Copyright 2023 EPAM Systems.
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

package com.epam.digital.data.platform.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.user.model.CsvUser;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "keycloak.edrCheck=false")
class ValidationSeviceEdrCheckFalseTest extends BaseValidationServiceTest {

  @Test
  void shouldPassValidationWhenEmptyEdrpou() {
    CsvUser user = mockUser();
    user.setEdrpou("");

    var result = validationService.validate(List.of(user));

    assertThat(result).isEmpty();
  }

  @Test
  void shouldPassValidationWhenNullEdrpou() {
    CsvUser user = mockUser();

    var result = validationService.validate(List.of(user));

    assertThat(result).isEmpty();
  }

  @Test
  void shouldPassValidationWhenPresentEdrpou() {
    CsvUser user = mockUser();
    user.setEdrpou("123412");

    var result = validationService.validate(List.of(user));

    assertThat(result).isEmpty();
  }
}

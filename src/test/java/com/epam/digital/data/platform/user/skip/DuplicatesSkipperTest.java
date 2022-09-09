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

package com.epam.digital.data.platform.user.skip;

import static com.epam.digital.data.platform.user.model.CsvUser.DRFO;
import static com.epam.digital.data.platform.utils.MockUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.digital.data.platform.user.model.SkippingResult;
import com.epam.digital.data.platform.user.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class DuplicatesSkipperTest {

  private final DuplicatesSkipper skipper = new DuplicatesSkipper();

  @Test
  void shouldFindAllDuplicates() {
    var user = mockUser();
    var user2 = mockUser();
    user2.getAttributes().put(DRFO, List.of("22222222"));

    var result = skipper.check(List.of(user, user2, user, user2, user2),
        new SkippingResult());

    assertEquals(3, result.size());
    assertThat(result.get(2).get(0)).isEqualTo(
        "The user will be skipped because it's duplicate of user in row '2'");
    assertThat(result.get(3).get(0)).isEqualTo(
        "The user will be skipped because it's duplicate of user in row '3'");
    assertThat(result.get(4).get(0)).isEqualTo(
        "The user will be skipped because it's duplicate of user in row '3'");
  }

  private User mockUser() {
    return
        user()
            .drfo(List.of("11111111"))
            .edrpou(List.of("11111111"))
            .fullName(List.of("some-full-name"))
            .build();
  }
}

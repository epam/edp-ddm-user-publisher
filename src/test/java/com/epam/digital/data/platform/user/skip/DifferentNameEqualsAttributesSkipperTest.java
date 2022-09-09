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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.user.model.SkippingResult;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.provider.ExistingUsersProvider;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DifferentNameEqualsAttributesSkipperTest {

  private DifferentNameEqualsAttributesSkipper skipper;
  private ExistingUsersProvider existingUsersProvider;

  @BeforeEach
  void beforeEach() {
    existingUsersProvider = mock(ExistingUsersProvider.class);
    skipper = new DifferentNameEqualsAttributesSkipper(existingUsersProvider);
  }

  @Test
  void shouldSkipUserIfExistUserWithTheSameAttributesButWithDifferentName() {
    var existingUsers = List.of(mockUser());
    when(existingUsersProvider.getExistingUsers()).thenReturn(existingUsers);

    var user2 = mockUser();
    user2.getAttributes().put(DRFO, List.of("22222222"));

    var user3 = mockUser();
    user3.setUsername("another-user-name");

    var usersForImport = List.of(user2, mockUser(), user3);

    var result = skipper.check(usersForImport, new SkippingResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(2).get(0)).isEqualTo(
        "The user will be skipped because there is already user with the same attributes "
            + "(drfo, edrpou, fullName) but different name. Name of existing user: [some-user-name]");
  }

  private User mockUser() {
    return
        user()
            .username("some-user-name")
            .drfo(List.of("11111111"))
            .edrpou(List.of("11111111"))
            .fullName(List.of("some-full-name"))
            .build();
  }
}

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

package com.epam.digital.data.platform.user.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.user.model.KeycloakRole;
import com.epam.digital.data.platform.user.service.KeycloakService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExistingRolesProviderTest {

  @Mock
  private KeycloakService keycloakService;
  private ExistingRolesProvider existingRolesProvider;

  @BeforeEach
  void beforeEach() {
    when(keycloakService.getAllRoles()).thenReturn(
        Set.of(new KeycloakRole("officer"), new KeycloakRole("head-officer")));
    existingRolesProvider = new ExistingRolesProvider(keycloakService);
  }

  @Test
  void shouldReturnIdAndClientId() {
    existingRolesProvider.postConstruct();

    assertThat(existingRolesProvider.getExistingRoles()).isEqualTo(
        Set.of("officer", "head-officer"));
  }
}

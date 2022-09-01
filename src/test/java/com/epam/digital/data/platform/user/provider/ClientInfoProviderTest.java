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

import com.epam.digital.data.platform.user.model.ClientInfo;
import com.epam.digital.data.platform.user.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientInfoProviderTest {
  
  private static final String ID = "11111111-1111-1111-1111-111111111111";
  private static final String CLIENT_ID = "ClientId";

  @Mock
  private KeycloakService keycloakService;
  private ClientInfoProvider clientInfoProvider;

  @BeforeEach
  void beforeEach() {
    when(keycloakService.getClientInfo()).thenReturn(mockClientInfo());
    clientInfoProvider = new ClientInfoProvider("ClientId", keycloakService);
  }

  @Test
  void shouldReturnIdAndClientId() {
    clientInfoProvider.postConstruct();
    
    assertThat(clientInfoProvider.getClientId()).isEqualTo(CLIENT_ID);
    assertThat(clientInfoProvider.getKeycloakClientId()).isEqualTo(ID);
  }
  
  private ClientInfo mockClientInfo() {
    var clientInfo = new ClientInfo();
    clientInfo.setClientId(CLIENT_ID);
    clientInfo.setId(ID);
    return clientInfo;
  }
}

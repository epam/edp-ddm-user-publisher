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

import com.epam.digital.data.platform.user.service.KeycloakService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@KeycloakDependentProvider
public class ClientInfoProvider {

  private final String clientId;
  private final KeycloakService keycloakService;
  private String keycloakClientId;

  public ClientInfoProvider(
      @Value("${keycloak.clientId}") String clientId,
      KeycloakService keycloakService) {
    this.clientId = clientId;
    this.keycloakService = keycloakService;
  }

  @PostConstruct
  void postConstruct() {
    keycloakClientId = keycloakService.getClientInfo().getId();
  }

  public String getClientId() {
    return clientId;
  }

  public String getKeycloakClientId() {
    return keycloakClientId;
  }
}

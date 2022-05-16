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

package com.epam.digital.data.platform.user.feign;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

import com.epam.digital.data.platform.user.config.FeignConfig;
import com.epam.digital.data.platform.user.model.ClientInfo;
import com.epam.digital.data.platform.user.model.KeycloakAccessToken;
import com.epam.digital.data.platform.user.model.KeycloakRole;
import com.epam.digital.data.platform.user.model.PartialImportKeycloakResponse;
import com.epam.digital.data.platform.user.model.RealmInfo;
import com.epam.digital.data.platform.user.model.User;
import java.util.List;
import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "keycloak-client", url = "${keycloak.url}", configuration = FeignConfig.class)
public interface FeignKeycloakClient {

  @PostMapping(
      path = "/auth/realms/{realm}/protocol/openid-connect/token",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  KeycloakAccessToken getAccessToken(
      @PathVariable("realm") String realm,
      @RequestHeader(value = AUTHORIZATION) String authorizationHeader,
      @RequestBody String grantType
  );

  @PostMapping(
      path = "/auth/admin/realms/{realm}/partialImport",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  PartialImportKeycloakResponse importUsers(
      @PathVariable("realm") String realm,
      @RequestBody String batchOfUsers
  );

  @GetMapping("/auth/admin/realms/{realm}/users")
  List<User> getAllUsers(
      @PathVariable("realm") String realm, @RequestParam("max") int max
  );

  @GetMapping("/auth/admin/realms/{realm}/roles")
  Set<KeycloakRole> getAllRoles(
      @PathVariable("realm") String realm, @RequestParam("max") int max
  );

  @GetMapping("/auth/admin/realms/{realm}")
  RealmInfo getRealmInfo(
      @PathVariable("realm") String realm
  );

  @GetMapping("/auth/admin/realms/{realm}/clients")
  List<ClientInfo> getClientInfo(
      @PathVariable("realm") String realm, @RequestParam("clientId") String clientId
  );
}

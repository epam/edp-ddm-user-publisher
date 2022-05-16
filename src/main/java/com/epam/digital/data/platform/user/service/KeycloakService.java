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

package com.epam.digital.data.platform.user.service;

import com.epam.digital.data.platform.user.exception.ClientInfoValidationException;
import com.epam.digital.data.platform.user.exception.JwtObtainingException;
import com.epam.digital.data.platform.user.exception.JwtParsingException;
import com.epam.digital.data.platform.user.feign.FeignKeycloakClient;
import com.epam.digital.data.platform.user.model.ClientInfo;
import com.epam.digital.data.platform.user.model.KeycloakAccessToken;
import com.epam.digital.data.platform.user.model.KeycloakRole;
import com.epam.digital.data.platform.user.model.PartialImportKeycloakResponse;
import com.epam.digital.data.platform.user.model.RealmInfo;
import com.epam.digital.data.platform.user.model.User;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Clock;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakService {

  private static final String GRANT_TYPE = "grant_type=client_credentials";

  private final FeignKeycloakClient feignKeycloakClient;
  private final String realm;
  private final String clientId;
  private final String clientSecret;
  private final Clock clock;
  private final ThreadLocal<Boolean> isJwtAlreadyRequested = ThreadLocal.withInitial(() -> false);

  private KeycloakAccessToken accessToken;

  public KeycloakService(
      FeignKeycloakClient feignKeycloakClient,
      @Value("${keycloak.realm}") String realm,
      @Value("${keycloak.clientId}") String clientId,
      @Value("${keycloak.clientSecret}") String clientSecret,
      Clock clock) {
    this.feignKeycloakClient = feignKeycloakClient;
    this.realm = realm;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.clock = clock;
  }

  public PartialImportKeycloakResponse importBatchOfUsers(String users) {
    return feignKeycloakClient.importUsers(realm, users);
  }

  public List<User> getAllUsers() {
    return feignKeycloakClient.getAllUsers(realm, Integer.MAX_VALUE);
  }

  public Set<KeycloakRole> getAllRoles() {
    return feignKeycloakClient.getAllRoles(realm, Integer.MAX_VALUE);
  }

  public RealmInfo getRealmInfo() {
    return feignKeycloakClient.getRealmInfo(realm);
  }

  public ClientInfo getClientInfo() {
    var clientInfoList = feignKeycloakClient.getClientInfo(realm, clientId);
    if (clientInfoList.size() != 1) {
      throw new ClientInfoValidationException("Failed to get ClintInfo from Keycloak");
    }
    return clientInfoList.get(0);
  }

  public String getAuthorizationHeader() {
    return tokenToHeader(getAccessToken());
  }

  private String credentialsToBase64Header(String credentials) {
    return "Basic " + Base64.getEncoder()
        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
  }

  private KeycloakAccessToken getAccessToken() {
    if (Boolean.FALSE.equals(isJwtAlreadyRequested.get())) {
      isJwtAlreadyRequested.set(true);
      var credentials = clientId + ":" + clientSecret;
      if (accessToken == null || isTokenExpired(accessToken.getAccessToken())) {
        accessToken = feignKeycloakClient
            .getAccessToken(realm, credentialsToBase64Header(credentials), GRANT_TYPE);
      }
      isJwtAlreadyRequested.set(false);
    } else {
      throw new JwtObtainingException("Failed to get JWT token");
    }
    return accessToken;
  }

  private String tokenToHeader(KeycloakAccessToken token) {
    return "Bearer " + token.getAccessToken();
  }

  private boolean isTokenExpired(String accessToken) {
    JWTClaimsSet jwtClaimsSet = getClaimsFromToken(accessToken);
    Date now = new Date(clock.millis());
    return Optional.of(jwtClaimsSet.getExpirationTime())
        .map(now::after)
        .orElse(true);
  }

  private JWTClaimsSet getClaimsFromToken(String accessToken) {
    try {
      return JWTParser.parse(accessToken).getJWTClaimsSet();
    } catch (ParseException e) {
      throw new JwtParsingException("Error while JWT parsing", e);
    }
  }
}

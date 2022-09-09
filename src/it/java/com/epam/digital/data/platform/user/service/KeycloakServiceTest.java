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

import static com.epam.digital.data.platform.user.util.WireMockCustomizer.customizeGetAccessToken;
import static com.epam.digital.data.platform.user.util.WireMockCustomizer.customizeGetAllRoles;
import static com.epam.digital.data.platform.user.util.WireMockCustomizer.customizeGetAllUser;
import static com.epam.digital.data.platform.user.util.WireMockCustomizer.customizeImportUsers;
import static com.epam.digital.data.platform.user.util.WireMockCustomizer.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.user.UserPublisherApplication;
import com.epam.digital.data.platform.user.config.WireMockConfig;
import com.epam.digital.data.platform.user.exception.JwtObtainingException;
import com.epam.digital.data.platform.user.model.FileObject;
import com.epam.digital.data.platform.user.model.KeycloakRole;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.provider.ClientInfoProvider;
import com.epam.digital.data.platform.user.provider.ExistingRolesProvider;
import com.epam.digital.data.platform.user.provider.ExistingUsersProvider;
import com.epam.digital.data.platform.user.provider.RealmInfoProvider;
import com.epam.digital.data.platform.user.util.TestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import feign.RetryableException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@TestPropertySource(properties =
    {"spring.autoconfigure.exclude=org.springframework.cloud.vault.config.VaultAutoConfiguration"})
@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WireMockConfig.class})
@MockBean(UserPublisherApplication.class)
@MockBean(RealmInfoProvider.class)
@MockBean(ClientInfoProvider.class)
@MockBean(ExistingUsersProvider.class)
class KeycloakServiceTest {

  @Autowired
  KeycloakService keycloakService;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  UserPublishingService userPublishingService;
  @Autowired
  WireMockServer mockKeycloakServer;
  @MockBean
  FileService fileService;
  @MockBean
  ExistingRolesProvider existingRolesProvider;
  @MockBean
  VaultService vaultService;

  @BeforeEach
  void beforeEach() throws IOException {
    when(existingRolesProvider.getExistingRoles()).thenReturn(Set.of("officer", "head-officer"));
    reset(mockKeycloakServer);
    ReflectionTestUtils.setField(keycloakService, "accessToken", null);
    customizeGetAccessToken(mockKeycloakServer, 200, "json/keycloak-access-token.json");
  }

  @Test
  void shouldGetAccessTokenAndThenGetAllUsers() throws IOException {
    // given
    customizeGetAllUser(mockKeycloakServer, 200, "json/all-users.json");

    // when
    assertThat(keycloakService.getAllUsers()).isNotEmpty();

    // then
    mockKeycloakServer.verify(1,
        postRequestedFor(urlEqualTo("/auth/realms/some-realm/protocol/openid-connect/token")));
    mockKeycloakServer.verify(1,
        getRequestedFor(urlEqualTo("/auth/admin/realms/some-realm/users?max=2147483647")));
  }

  @Test
  void shouldGetCorrectListOfUsers() throws IOException {
    // given
    customizeGetAllUser(mockKeycloakServer, 200, "json/all-users.json");

    // when
    List<User> actual = keycloakService.getAllUsers();

    var expected = objectMapper.readValue(
        TestUtils.getContent("json/all-users.json").getBytes(StandardCharsets.UTF_8),
        new TypeReference<List<User>>() {
        }
    );

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldGetCorrectListOfRoles() throws IOException {
    // given
    customizeGetAllRoles(mockKeycloakServer, 200, "json/all-roles.json");

    // when
    Set<KeycloakRole> actual = keycloakService.getAllRoles();

    var expected = objectMapper.readValue(
        TestUtils.getContent("json/all-roles.json").getBytes(StandardCharsets.UTF_8),
        new TypeReference<Set<KeycloakRole>>() {
        }
    );

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldMake5RetriesWhenUnauthorized() throws IOException {
    // given
    customizeImportUsers(mockKeycloakServer, 401, "json/partial-import-response.json");

    var fileObject = getFileObject("json/users-katottg.csv");
    when(fileService.getFile(fileObject.getId())).thenReturn(fileObject);
    var contentStr = new String(fileObject.getContent(), StandardCharsets.UTF_8);
    when(vaultService.decrypt(contentStr)).thenReturn(contentStr);

    // when
    assertThrows(RetryableException.class, () -> userPublishingService.publish(fileObject.getId()));

    // then
    mockKeycloakServer.verify(5,
        postRequestedFor(urlEqualTo("/auth/admin/realms/some-realm/partialImport")));
  }

  @Test
  void shouldThrowJwtObtainingExceptionWhenTryToGetAccessTokenButReturns401() throws IOException {
    // given
    customizeGetAccessToken(mockKeycloakServer, 401, "json/keycloak-access-token.json");

    var fileObject = getFileObject("json/users.csv");
    when(fileService.getFile(fileObject.getId())).thenReturn(fileObject);
    var contentStr = new String(fileObject.getContent(), StandardCharsets.UTF_8);
    when(vaultService.decrypt(contentStr)).thenReturn(contentStr);

    // when, then
    assertThrows(JwtObtainingException.class,
        () -> userPublishingService.publish(fileObject.getId()));
  }

  @Test
  void shouldMakeAttemptForEachBatchAndEachRowWhen5xx() throws IOException {
    // given
    customizeImportUsers(mockKeycloakServer, 500, "json/partial-import-response.json");

    var fileObject = getFileObject("json/users.csv");
    when(fileService.getFile(fileObject.getId())).thenReturn(fileObject);
    var contentStr = new String(fileObject.getContent(), StandardCharsets.UTF_8);
    when(vaultService.decrypt(contentStr)).thenReturn(contentStr);

    // when
    userPublishingService.publish(fileObject.getId());

    // then
    mockKeycloakServer.verify(8,
        postRequestedFor(urlEqualTo("/auth/admin/realms/some-realm/partialImport")));
  }

  @Test
  void shouldSend5UsersInBatchesOf2Users() throws IOException {
    // given
    customizeImportUsers(mockKeycloakServer, 200, "json/partial-import-response.json");

    var fileObject = getFileObject("json/users.csv");
    when(fileService.getFile(fileObject.getId())).thenReturn(fileObject);
    var contentStr = new String(fileObject.getContent(), StandardCharsets.UTF_8);
    when(vaultService.decrypt(contentStr)).thenReturn(contentStr);

    // when
    userPublishingService.publish(fileObject.getId());

    // then
    mockKeycloakServer.verify(3,
        postRequestedFor(urlEqualTo("/auth/admin/realms/some-realm/partialImport")));
  }

  private FileObject getFileObject(String filePath) {
    return new FileObject("id", "someFileName",
        TestUtils.getContent(filePath).getBytes(StandardCharsets.UTF_8));
  }
}

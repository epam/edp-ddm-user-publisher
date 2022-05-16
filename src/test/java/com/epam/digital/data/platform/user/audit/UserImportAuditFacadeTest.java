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

package com.epam.digital.data.platform.user.audit;

import static com.epam.digital.data.platform.starter.audit.model.EventType.SYSTEM_EVENT;
import static com.epam.digital.data.platform.user.util.TestUtils.getContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.starter.audit.model.AuditEvent;
import com.epam.digital.data.platform.starter.audit.model.AuditUserInfo;
import com.epam.digital.data.platform.starter.audit.service.AuditService;
import com.epam.digital.data.platform.user.model.FileObject;
import com.epam.digital.data.platform.user.model.PartialImportKeycloakResponse;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.provider.ClientInfoProvider;
import com.epam.digital.data.platform.user.provider.RealmInfoProvider;
import com.epam.digital.data.platform.user.provider.UserInfoProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserImportAuditFacadeTest {

  private static final String REQUEST_ID = "requestId";
  private static final String HOSTNAME = "hostname";

  private static final String REALM_NAME = "realm-name";
  private static final String REALM_ID = "realm-id";

  private static final String CLIENT_ID = "client-id";
  private static final String KEYCLOAK_CLIENT_ID = "keycloak-client-id";

  private static final String USER_KEYCLOAK_ID = "user-keycloak-id";
  private static final String USER_NAME = "user-name";
  private static final String USER_DRFO = "user-drfo";

  private UserImportAuditFacade auditFacade;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  AuditService auditService;
  @Mock
  RealmInfoProvider realmInfoProvider;
  @Mock
  ClientInfoProvider clientInfoProvider;
  @Mock
  UserInfoProvider userInfoProvider;

  @Captor
  ArgumentCaptor<AuditEvent> auditEventCaptor;

  @BeforeEach
  void beforeEach() {
    auditFacade = new UserImportAuditFacade(
        auditService, REQUEST_ID, HOSTNAME, Clock.systemDefaultZone(),
        realmInfoProvider, clientInfoProvider, userInfoProvider);
    initMocks();
  }

  @Test
  void happyPath() throws IOException {
    var keycloakResponse = getKeycloakResponse("json/partial-import-response.json");
    auditFacade.sendAudit(keycloakResponse, mockUsers(),
        new FileObject("fileId", "fileName", "content".getBytes()));

    verify(auditService).sendAudit(auditEventCaptor.capture());
    var resultAuditEvent = auditEventCaptor.getValue();

    assertThat(resultAuditEvent.getRequestId()).isEqualTo(REQUEST_ID);
    assertThat(resultAuditEvent.getApplication()).isEqualTo("Keycloak");
    assertThat(resultAuditEvent.getName()).isEqualTo("USER_CREATE");
    assertThat(resultAuditEvent.getSourceInfo().getApplication()).isNotBlank();
    assertThat(resultAuditEvent.getEventType()).isEqualTo(SYSTEM_EVENT);
    assertThat(resultAuditEvent.getCurrentTime()).isNotNull();

    assertThat(resultAuditEvent.getUserInfo().getUserName()).isEqualTo(USER_NAME);
    assertThat(resultAuditEvent.getUserInfo().getUserDrfo()).isEqualTo(USER_DRFO);
    assertThat(resultAuditEvent.getUserInfo().getUserKeycloakId()).isEqualTo(USER_KEYCLOAK_ID);
    assertThat(resultAuditEvent.getContext()).isEqualTo(expectedContext());
  }

  private Map<String, Object> expectedContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("sourceFileName", "fileName");
    context.put("realmId", REALM_ID);
    context.put("clientId", CLIENT_ID);
    context.put("realmName", REALM_NAME);
    context.put("keycloakClientId", KEYCLOAK_CLIENT_ID);
    context.put("roles", List.of("officer", "head-officer"));
    context.put("sourceFileId", "fileId");
    context.put("sourceFileSHA256Checksum",
        "ed7002b439e9ac845f22357d822bac1444730fbdb6016d3ec9432297b9ec9f73");
    context.put("userId", "5d107a36-b9e9-406e-b4c3-d1c48933222e");
    context.put("enabled", true);
    context.put("username", "9d57e74a81d1220a6fbb8bbafa3fa4b4236ac64cd42b165d473ab565d9132db3");
    return context;
  }

  void initMocks() {
    when(realmInfoProvider.getRealmId()).thenReturn(REALM_ID);
    when(realmInfoProvider.getRealmName()).thenReturn(REALM_NAME);
    when(clientInfoProvider.getClientId()).thenReturn(CLIENT_ID);
    when(clientInfoProvider.getKeycloakClientId()).thenReturn(KEYCLOAK_CLIENT_ID);
    when(userInfoProvider.getUserInfo()).thenReturn(mockAuditUserInfo());
  }

  private AuditUserInfo mockAuditUserInfo() {
    var userInfo = new AuditUserInfo();
    userInfo.setUserDrfo(USER_DRFO);
    userInfo.setUserName(USER_NAME);
    userInfo.setUserKeycloakId(USER_KEYCLOAK_ID);
    return userInfo;
  }

  private PartialImportKeycloakResponse getKeycloakResponse(String filePath) throws IOException {
    return objectMapper.readValue(getContent(filePath), PartialImportKeycloakResponse.class);
  }

  private List<User> mockUsers() {
    var user1 = new User();
    user1.setRealmRoles(List.of("officer"));
    user1.setUsername("c0ab3771f5cd55655855c6b13d919e1bca19d08e6b345064025c05bbd574f802");

    var user2 = new User();
    user2.setRealmRoles(List.of("officer", "head-officer"));
    user2.setUsername("9d57e74a81d1220a6fbb8bbafa3fa4b4236ac64cd42b165d473ab565d9132db3");

    return List.of(user1, user2);
  }
}

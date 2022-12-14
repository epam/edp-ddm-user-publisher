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
import static com.epam.digital.data.platform.user.model.CsvUser.DRFO;
import static com.epam.digital.data.platform.user.model.CsvUser.EDRPOU;
import static com.epam.digital.data.platform.user.model.CsvUser.FULL_NAME;
import static com.epam.digital.data.platform.user.model.CsvUser.KATOTTG;
import static com.epam.digital.data.platform.user.util.Constants.USER_CREATE_EVENT_NAME;
import static com.epam.digital.data.platform.user.util.Util.trimToNull;

import com.epam.digital.data.platform.starter.audit.model.AuditEvent;
import com.epam.digital.data.platform.starter.audit.model.AuditSourceInfo;
import com.epam.digital.data.platform.starter.audit.service.AbstractAuditFacade;
import com.epam.digital.data.platform.starter.audit.service.AuditService;
import com.epam.digital.data.platform.user.model.FileObject;
import com.epam.digital.data.platform.user.model.PartialImportKeycloakResponse;
import com.epam.digital.data.platform.user.model.PartialImportKeycloakResponse.Result;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.provider.ClientInfoProvider;
import com.epam.digital.data.platform.user.provider.RealmInfoProvider;
import com.epam.digital.data.platform.user.provider.UserInfoProvider;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserImportAuditFacade extends AbstractAuditFacade {

  private final String requestId;
  private final String hostname;
  private final RealmInfoProvider realmInfoProvider;
  private final ClientInfoProvider clientInfoProvider;
  private final UserInfoProvider userInfoProvider;

  public UserImportAuditFacade(
      AuditService auditService,
      @Value("${jobinfo.requestId}") String requestId,
      @Value("${jobinfo.hostname}") String hostname,
      Clock clock,
      RealmInfoProvider realmInfoProvider,
      ClientInfoProvider clientInfoProvider,
      UserInfoProvider userInfoProvider) {
    super(auditService, "Keycloak", clock);
    this.requestId = requestId;
    this.realmInfoProvider = realmInfoProvider;
    this.clientInfoProvider = clientInfoProvider;
    this.userInfoProvider = userInfoProvider;
    String podName = System.getenv().get("HOSTNAME");
    this.hostname = podName == null ? hostname : podName;
  }

  public void sendAudit(PartialImportKeycloakResponse response, List<? extends User> users,
      FileObject fileObject) {
    var usersMap = users.stream()
        .collect(Collectors.toMap(User::getUsername, Function.identity()));

    for (Result result : response.getResults()) {
      if (result.getAction().equals("ADDED")) {
        var user = usersMap.get(result.getResourceName());
        auditService.sendAudit(createAuditEvent(result, user, fileObject));
      }
    }
  }

  private AuditEvent createAuditEvent(Result result, User user, FileObject fileObject) {
    return createBaseAuditEvent(SYSTEM_EVENT, USER_CREATE_EVENT_NAME, requestId)
        .setSourceInfo(getAuditSourceInfo())
        .setContext(createContext(result, user, fileObject))
        .setUserInfo(userInfoProvider.getUserInfo())
        .build();
  }

  private AuditSourceInfo getAuditSourceInfo() {
    return AuditSourceInfo.AuditSourceInfoBuilder
        .anAuditSourceInfo()
        .application(hostname)
        .build();
  }

  private Map<String, Object> createContext(Result result, User user, FileObject fileObject) {
    Map<String, Object> context = new HashMap<>();
    putIfNotNull(context, "userId", result.getId());
    putIfNotNull(context, "username", result.getResourceName());
    
    var customAttributes = new HashMap<> (user.getAttributes());
    putIfNotNull(context, "katottg", customAttributes.remove(KATOTTG)); // well known custom attribute
    customAttributes.remove(DRFO); // mandatory custom attribute has sensitive info
    customAttributes.remove(EDRPOU); // mandatory custom attribute has sensitive info
    customAttributes.remove(FULL_NAME); // mandatory custom attribute has sensitive info
    putIfNotNull(context, "customAttributes", trimToNull(customAttributes)); // other custom attributes
    
    putIfNotNull(context, "enabled", true);
    putIfNotNull(context, "realmId", realmInfoProvider.getRealmId());
    putIfNotNull(context, "realmName", realmInfoProvider.getRealmName());
    putIfNotNull(context, "clientId", clientInfoProvider.getClientId());
    putIfNotNull(context, "keycloakClientId", clientInfoProvider.getKeycloakClientId());
    putIfNotNull(context, "roles", user.getRealmRoles());
    putIfNotNull(context, "sourceFileId", fileObject.getId());
    putIfNotNull(context, "sourceFileName", fileObject.getFileName());
    putIfNotNull(context, "sourceFileSHA256Checksum", fileObject.getChecksum());
    return context;
  }

  private void putIfNotNull(Map<String, Object> context, String key, Object value) {
    if (value != null) {
      context.put(key, value);
    }
  }
}

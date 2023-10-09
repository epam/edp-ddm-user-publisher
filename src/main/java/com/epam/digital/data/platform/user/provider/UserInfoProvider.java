/*
 * Copyright 2023 EPAM Systems.
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

import com.epam.digital.data.platform.starter.audit.model.AuditUserInfo;
import com.epam.digital.data.platform.user.exception.JwtValidationException;
import com.epam.digital.data.platform.user.service.TokenParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserInfoProvider {

  private AuditUserInfo userInfo;

  private final String userAccessToken;
  private final TokenParser tokenParser;

  public UserInfoProvider(@Value("${user.access-token:user_access_token}") String userAccessToken,
      TokenParser tokenParser) {
    this.userAccessToken = userAccessToken;
    this.tokenParser = tokenParser;
  }

  public AuditUserInfo getUserInfo() {
    if (userInfo == null) {
      updateUserInfo();
    }
    return userInfo;
  }

  void updateUserInfo() {
    var jwtClaimsDto = tokenParser.parseClaims(userAccessToken);
    userInfo = AuditUserInfo.AuditUserInfoBuilder.anAuditUserInfo()
        .userName(jwtClaimsDto.getFullName())
        .userKeycloakId(jwtClaimsDto.getSubject())
        .userDrfo(jwtClaimsDto.getDrfo())
        .build();

    validateUserInfo();
  }

  private void validateUserInfo() {
    if (userInfo.getUserDrfo() == null
        || userInfo.getUserName() == null
        || userInfo.getUserKeycloakId() == null) {
      throw new JwtValidationException(
          "Users access token does not have all required parameters (drfo, fullName, subject)");
    }
  }
}

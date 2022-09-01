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

package com.epam.digital.data.platform.user.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.util.StreamUtils.copyToString;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;

public class WireMockCustomizer {

  public static void reset(WireMockServer mockServer) {
    mockServer.resetAll();
  }

  public static void customizeGetAccessToken(WireMockServer mockServer, int status,
      String filePath) throws IOException {
    mockServer.
        stubFor(post(urlEqualTo("/auth/realms/some-realm/protocol/openid-connect/token"))
            .withRequestBody(equalTo("grant_type=client_credentials"))
            .willReturn(aResponse().withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(
                    copyToString(
                        WireMockCustomizer.class.getClassLoader().getResourceAsStream(filePath),
                        StandardCharsets.UTF_8))));
  }

  public static void customizeGetAllUser(WireMockServer mockServer, int status,
      String filePath) throws IOException {
    mockServer.stubFor(
        get(urlEqualTo("/auth/admin/realms/some-realm/users?max=2147483647"))
            .willReturn(aResponse()
                .withStatus(status)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(
                    copyToString(
                        WireMockCustomizer.class.getClassLoader()
                            .getResourceAsStream(filePath), StandardCharsets.UTF_8))));
  }


  public static void customizeGetAllRoles(WireMockServer mockServer, int status,
      String filePath) throws IOException {
    mockServer.stubFor(
        get(urlEqualTo("/auth/admin/realms/some-realm/roles?max=2147483647"))
            .willReturn(aResponse()
                .withStatus(status)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(
                    copyToString(
                        WireMockCustomizer.class.getClassLoader()
                            .getResourceAsStream(filePath), StandardCharsets.UTF_8))));
  }
  
  public static void customizeImportUsers(WireMockServer mockServer, int status,
      String filePath) throws IOException {
    mockServer.
        stubFor(post(urlEqualTo("/auth/admin/realms/some-realm/partialImport"))
            .willReturn(aResponse().withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(
                    copyToString(
                        WireMockCustomizer.class.getClassLoader().getResourceAsStream(filePath),
                        StandardCharsets.UTF_8))));
  }
}

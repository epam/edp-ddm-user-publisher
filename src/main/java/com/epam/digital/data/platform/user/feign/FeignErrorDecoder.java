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

import com.epam.digital.data.platform.user.exception.BatchImportException;
import com.epam.digital.data.platform.user.service.KeycloakService;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.util.HashMap;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

  private final Logger log = LoggerFactory.getLogger(FeignErrorDecoder.class);
  private final ErrorDecoder defaultErrorDecoder = new Default();

  private KeycloakService keycloakService;

  public void setKeycloakService(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
  }

  @Override
  public Exception decode(String methodKey, Response response) {
    var status = HttpStatus.valueOf(response.status());
    log.error("Response status: {}", status);

    if (response.status() == 401) {
      return new RetryableException(response.status(), "Unauthorized request",
          response.request().httpMethod(), null,
          cloneRequest(response.request(), keycloakService.getAuthorizationHeader()));
    }

    if (response.status() == 500) {
      return new BatchImportException("Batch import exception");
    }

    return defaultErrorDecoder.decode(methodKey, response);
  }

  private Request cloneRequest(Request request, String authHeader) {
    var headers = new HashMap<>(request.headers());
    headers.put(AUTHORIZATION, Set.of(authHeader));

    var template = request.requestTemplate()
        .removeHeader(AUTHORIZATION)
        .header(AUTHORIZATION, Set.of(authHeader));

    return
        Request.create(
            request.httpMethod(),
            request.url(),
            headers,
            request.body(),
            request.charset(),
            template
        );
  }
}

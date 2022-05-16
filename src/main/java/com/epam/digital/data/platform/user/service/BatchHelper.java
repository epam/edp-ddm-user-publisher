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

import com.epam.digital.data.platform.user.exception.MappingException;
import com.epam.digital.data.platform.user.model.BatchOfUsers;
import com.epam.digital.data.platform.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchHelper {

  private final ObjectMapper objectMapper;
  private final int batchSize;

  public BatchHelper(ObjectMapper objectMapper, @Value("${keycloak.batchSize}") int batchSize) {
    this.objectMapper = objectMapper;
    this.batchSize = batchSize;
  }

  public <T extends User> BatchOfUsers<T> getBatchOfUsers(List<T> users) {
    if (users.size() <= batchSize) {
      BatchOfUsers<T> result = new BatchOfUsers<>(List.copyOf(users));
      users.clear();
      return result;
    }
    BatchOfUsers<T> result = new BatchOfUsers<>(List.copyOf(users.subList(0, batchSize)));
    users.subList(0, batchSize).clear();
    return result;
  }

  public <T extends User> BatchOfUsers<T> getBatchOfUsers(T user) {
    return new BatchOfUsers<>(List.of(user));
  }

  public String batchToString(BatchOfUsers<? extends User> batch) {
    String batchString;
    try {
      batchString = objectMapper.writeValueAsString(batch);
    } catch (JsonProcessingException e) {
      throw new MappingException("Unable to map BatchOfUsers to String", e);
    }
    return batchString;
  }
}

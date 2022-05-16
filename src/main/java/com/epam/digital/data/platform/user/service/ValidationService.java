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

import static com.epam.digital.data.platform.user.util.Constants.FIRST_USER_OFFSET;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.validate.Validator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidationService {

  private final Logger log = LoggerFactory.getLogger(ValidationService.class);
  private final List<Validator> validators;

  public ValidationService(List<Validator> validators) {
    this.validators = validators;
  }

  public ValidationResult validate(List<CsvUser> users) {
    var results = new ValidationResult();
    for (int i = 0; i < users.size(); i++) {
      for (var validator : validators) {
        validator.validate(i, users.get(i), results);
      }
    }
    return results;
  }

  public void printErrors(ValidationResult validationResult) {
    for (var entry : validationResult.entrySet()) {
      for (var errorMessage : entry.getValue()) {
        log.error("User in row '{}'. Message: {}", entry.getKey() + FIRST_USER_OFFSET,
            errorMessage);
      }
    }
  }
}

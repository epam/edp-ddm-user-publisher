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

package com.epam.digital.data.platform.user.validate.custom;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.validate.CharactersValidator;
import java.util.List;

public class CustomAttributeCharactersValidator extends CharactersValidator {

  private static final String VALID_CHARACTERS_PATTERN = "[^\\[\\]{}\"\\\\]+";

  @Override
  public ValidationResult validate(int userSequenceNumber, CsvUser user, ValidationResult results) {
    for (var entry : user.getCustomAttributes().entrySet()) {
      if (!entry.getKey().matches(VALID_CHARACTERS_PATTERN)) {
        List<String> invalidCharacters =
            getInvalidCharacters(entry.getKey(), VALID_CHARACTERS_PATTERN);
        results.add(userSequenceNumber, "The attribute name '" + entry.getKey()
            + "' contains invalid symbols : {" + String.join(", ", invalidCharacters) + "}");
      }
      var valueString = String.join(",", entry.getValue());
      if (!valueString.matches(VALID_CHARACTERS_PATTERN)) {
        List<String> invalidCharacters =
            getInvalidCharacters(valueString, VALID_CHARACTERS_PATTERN);
        results.add(userSequenceNumber, "The attribute with name '" + entry.getKey()
            + "' contains value with invalid symbols : {"
            + String.join(", ", invalidCharacters) + "}");
      }
    }
    return validateNext(userSequenceNumber, user, results);
  }
}

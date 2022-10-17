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

package com.epam.digital.data.platform.user.validate.fullname;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.validate.Validator;
import java.util.ArrayList;
import java.util.List;

public class FullNameCharactersValidator extends Validator {

  private static final String VALID_CHARACTERS_PATTERN = "[ '`’\\p{IsCyrillic}\\p{IsLatin}\\p{Digit}\\-]+";

  @Override
  public ValidationResult validate(int userSequenceNumber, CsvUser user, ValidationResult results) {
    if (!user.getFullName().matches(VALID_CHARACTERS_PATTERN)) {
      List<String> invalidCharacters = getInvalidCharacters(user.getFullName());
      results.add(userSequenceNumber, "FullName contains invalid symbols: {" + String.join(", ", invalidCharacters) + "}");
    }
    return validateNext(userSequenceNumber, user, results);
  }
  
  private List<String> getInvalidCharacters(String str) {
    List<String> invalidCharacters = new ArrayList<>();
    for(int i = 0; i < str.length(); i++) {
      var character = Character.toString(str.charAt(i));
      if(!character.matches(VALID_CHARACTERS_PATTERN)) {
        invalidCharacters.add(character);
      }
    }
    return invalidCharacters;
  }
}

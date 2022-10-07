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

package com.epam.digital.data.platform.user.validate.drfo;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.validate.CharactersValidator;
import java.util.List;

public class DrfoCharactersValidator extends CharactersValidator {

  private static final String VALID_CHARACTERS_PATTERN = "[ \\p{IsCyrillic}\\p{IsLatin}\\p{Digit}\\-]+";

  @Override
  public ValidationResult validate(int userSequenceNumber, CsvUser user, ValidationResult results) {
    if (!user.getDrfo().matches(VALID_CHARACTERS_PATTERN)) {
      List<String> invalidCharacters = 
          getInvalidCharacters(user.getDrfo(), VALID_CHARACTERS_PATTERN);
      results.add(userSequenceNumber,
          "DRFO contains invalid symbols: {" + String.join(", ", invalidCharacters) + "}");
    }
    return validateNext(userSequenceNumber, user, results);
  }
}

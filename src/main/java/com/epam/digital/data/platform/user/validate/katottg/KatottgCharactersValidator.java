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

package com.epam.digital.data.platform.user.validate.katottg;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.validate.Validator;

public class KatottgCharactersValidator extends Validator {

  private static final String KATOTTG_PATTERN = "^UA\\d{17}$";
  private static final String COUNTRY_KATOTTG_PATTERN = "^UA$";

  @Override
  public ValidationResult validate(int userSequenceNumber, CsvUser user, ValidationResult results) {
    var katottgs = user.getKatottg();
    for(String katottg : katottgs) {
      if (!katottg.matches(KATOTTG_PATTERN) && !katottg.matches(COUNTRY_KATOTTG_PATTERN)) {
        results.add(userSequenceNumber, 
            "Incorrect KATOTTG code. KATOTTG should starts from UA and have 0 or 17 digits, like this: " 
                + "'UA' or 'UA12345678901234567'. But you are trying to import code '" + katottg
                + "' in wrong format");
      }
    }
    return validateNext(userSequenceNumber, user, results);
  }
}

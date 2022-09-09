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
import com.epam.digital.data.platform.user.validate.Validator;
import java.util.List;

public class CustomAttributeLengthValidator extends Validator {

  protected static final int MAX_LENGTH = 255;

  @Override
  public ValidationResult validate(int userSequenceNumber, CsvUser user, ValidationResult results) {
    for (var entry : user.getCustomAttributes().entrySet()) {
      if (entry.getKey().length() > MAX_LENGTH) {
        results.add(userSequenceNumber, "The '" + entry.getKey()
            + "' attribute must not have a name longer than 255 symbols,"
            + " but it is '" + entry.getKey().length() + "' symbols long");
      }
      int totalLength = countTotalLength(entry.getValue());
      if (totalLength > MAX_LENGTH) {
        results.add(userSequenceNumber,
            "The '" + entry.getKey()
                + "' attribute must not have a value longer than 255 symbols,"
                + " but it is '" + totalLength + "' symbols long");
      }
    }
    return validateNext(userSequenceNumber, user, results);
  }

  private int countTotalLength(List<String> list) {
    if (list.isEmpty()) {
      return 0;
    } else if (list.size() == 1) {
      return list.get(0).length();
    }
    int separators = (list.size() - 1) * 2; // abc,cde,efg -> abc##cde##efg
    return separators + list.stream().mapToInt(String::length).sum();
  }
}

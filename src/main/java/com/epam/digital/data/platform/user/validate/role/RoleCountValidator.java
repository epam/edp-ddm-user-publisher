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

package com.epam.digital.data.platform.user.validate.role;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.validate.Validator;

public class RoleCountValidator extends Validator {

  @Override
  public ValidationResult validate(int userSequenceNumber, CsvUser user, ValidationResult results) {
    var roles = user.getRealmRoles();
    if (roles == null) {
      return results.add(userSequenceNumber, "List of Roles is null");
    }
    if (roles.isEmpty()) {
      return results.add(userSequenceNumber,
          "User must contain at least one role, but contains zero");
    }
    return validateNext(userSequenceNumber, user, results);
  }
}

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

package com.epam.digital.data.platform.user.config;

import com.epam.digital.data.platform.user.provider.ExistingRolesProvider;
import com.epam.digital.data.platform.user.validate.Validator;
import com.epam.digital.data.platform.user.validate.custom.CustomAttributeCharactersValidator;
import com.epam.digital.data.platform.user.validate.custom.CustomAttributeLengthValidator;
import com.epam.digital.data.platform.user.validate.custom.CustomAttributePresenceValidator;
import com.epam.digital.data.platform.user.validate.drfo.DrfoCharactersValidator;
import com.epam.digital.data.platform.user.validate.drfo.DrfoPresenceValidator;
import com.epam.digital.data.platform.user.validate.edrpou.EdrpouCharactersValidator;
import com.epam.digital.data.platform.user.validate.edrpou.EdrpouPresenceValidator;
import com.epam.digital.data.platform.user.validate.fullname.FullNameCharactersValidator;
import com.epam.digital.data.platform.user.validate.fullname.FullNamePresenceValidator;
import com.epam.digital.data.platform.user.validate.katottg.KatottgCharactersValidator;
import com.epam.digital.data.platform.user.validate.katottg.KatottgCountValidator;
import com.epam.digital.data.platform.user.validate.role.RoleCountValidator;
import com.epam.digital.data.platform.user.validate.role.RolePresenceInRealmValidator;
import com.epam.digital.data.platform.user.validate.role.RolePresenceValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

  @Bean
  public Validator drfoValidator() {
    var validator = new DrfoPresenceValidator();
    validator.linkWith(new DrfoCharactersValidator());
    return validator;
  }

  @Bean
  public Validator edrpouValidator() {
    var validator = new EdrpouPresenceValidator();
    validator.linkWith(new EdrpouCharactersValidator());
    return validator;
  }

  @Bean
  public Validator fullNameValidator() {
    var validator = new FullNamePresenceValidator();
    validator.linkWith(new FullNameCharactersValidator());
    return validator;
  }

  @Bean
  public Validator roleValidator(ExistingRolesProvider existingRolesProvider) {
    var validator = new RoleCountValidator();
    validator
        .linkWith(new RolePresenceValidator())
        .linkWith(new RolePresenceInRealmValidator(existingRolesProvider));
    return validator;
  }

  @Bean
  public Validator katottgValidator() {
    var validator = new KatottgCountValidator();
    validator.linkWith(new KatottgCharactersValidator());
    return validator;
  }

  @Bean
  public Validator customAttributeValidator() {
    var validator = new CustomAttributePresenceValidator();
    validator
        .linkWith(new CustomAttributeLengthValidator())
        .linkWith(new CustomAttributeCharactersValidator());
    return validator;
  }
}

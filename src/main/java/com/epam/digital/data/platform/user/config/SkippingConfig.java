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

import com.epam.digital.data.platform.user.provider.ExistingUsersProvider;
import com.epam.digital.data.platform.user.skip.DifferentNameEqualsAttributesSkipper;
import com.epam.digital.data.platform.user.skip.DuplicatesSkipper;
import com.epam.digital.data.platform.user.skip.EqualsNameDifferentAttributesSkipper;
import com.epam.digital.data.platform.user.skip.EqualsNameEqualsAttributesSkipper;
import com.epam.digital.data.platform.user.skip.Skipper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkippingConfig {

  @Bean
  public Skipper userSkipper(ExistingUsersProvider existingUsersProvider) {
    var skipper = new DuplicatesSkipper();
    skipper
        .linkWith(new EqualsNameEqualsAttributesSkipper(existingUsersProvider))
        .linkWith(new EqualsNameDifferentAttributesSkipper(existingUsersProvider))
        .linkWith(new DifferentNameEqualsAttributesSkipper(existingUsersProvider));
    return skipper;
  }
}

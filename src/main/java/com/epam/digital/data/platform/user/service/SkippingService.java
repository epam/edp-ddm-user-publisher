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

import com.epam.digital.data.platform.user.model.SkippingResult;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.skip.Skipper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SkippingService {

  private final Logger log = LoggerFactory.getLogger(SkippingService.class);
  private final List<Skipper> skippers;

  public SkippingService(List<Skipper> skippers) {
    this.skippers = skippers;
  }

  public SkippingResult check(List<User> users) {
    var results = new SkippingResult();
    for (var skipper : skippers) {
      skipper.check(users, results);
    }
    return results;
  }

  public void printResults(SkippingResult skippingResult) {
    for (var entry : skippingResult.entrySet()) {
      for (var message : entry.getValue()) {
        log.warn("User in row '{}'. Message: {}", entry.getKey() + FIRST_USER_OFFSET, message);
      }
    }
  }
}

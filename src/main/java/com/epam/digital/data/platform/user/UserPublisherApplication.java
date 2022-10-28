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

package com.epam.digital.data.platform.user;

import com.epam.digital.data.platform.user.service.UserPublishingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserPublisherApplication implements ApplicationRunner {

  private final Logger log = LoggerFactory.getLogger("UserPublisherApplication");

  private final UserPublishingService userPublishingService;

  public UserPublisherApplication(UserPublishingService userPublishingService) {
    this.userPublishingService = userPublishingService;
  }

  public static void main(String[] args) {
    System.setProperty("log4j.shutdownHookEnabled", Boolean.toString(false));
    SpringApplication.run(UserPublisherApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    var fileId = getFileId(args);
    log.info("Start importing users to keycloak from file with id '{}'", fileId);
    userPublishingService.publish(fileId);
  }

  private String getFileId(ApplicationArguments args) {
    return Optional.ofNullable(args.getOptionValues("id"))
        .filter(fileIdList -> fileIdList.size() == 1)
        .map(fileIdList -> fileIdList.get(0))
        .orElseThrow(() -> new IllegalArgumentException("Invalid file id specification"));
  }
}

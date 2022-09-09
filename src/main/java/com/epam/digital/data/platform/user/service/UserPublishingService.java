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

import com.epam.digital.data.platform.user.audit.UserImportAuditFacade;
import com.epam.digital.data.platform.user.exception.BatchImportException;
import com.epam.digital.data.platform.user.exception.MappingException;
import com.epam.digital.data.platform.user.model.BatchOfUsers;
import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.EnumerableUser;
import com.epam.digital.data.platform.user.model.FileObject;
import com.epam.digital.data.platform.user.model.PartialImportKeycloakResponse;
import com.epam.digital.data.platform.user.model.Statistics;
import com.epam.digital.data.platform.user.model.User;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserPublishingService {

  private final Logger log = LoggerFactory.getLogger(UserPublishingService.class);

  private final FileService fileService;
  private final KeycloakService keycloakService;
  private final UserService userService;
  private final BatchHelper batchHelper;
  private final ValidationService validationService;
  private final SkippingService skippingService;
  private final UserImportAuditFacade userImportAuditFacade;
  private final VaultService vaultService;

  public UserPublishingService(
      FileService fileService,
      KeycloakService keycloakService,
      UserService userService,
      BatchHelper batchHelper,
      ValidationService validationService,
      SkippingService skippingService,
      UserImportAuditFacade userImportAuditFacade,
      VaultService vaultService) {
    this.fileService = fileService;
    this.keycloakService = keycloakService;
    this.userService = userService;
    this.batchHelper = batchHelper;
    this.validationService = validationService;
    this.skippingService = skippingService;
    this.userImportAuditFacade = userImportAuditFacade;
    this.vaultService = vaultService;
  }

  public void publish(String id) {
    if(id.isEmpty()) {
      log.info("Nothing to do");
      return;
    }
    // get file 
    var fileObject = fileService.getFile(id);
    
    // decryptFile
    var csv = vaultService.decrypt(new String(fileObject.getContent(), StandardCharsets.UTF_8));

    // convert to csvUsers
    List<CsvUser> csvUsers; 
    try {
      csvUsers = userService.getCsvUsers(csv);
    } catch (MappingException e) {
      fileService.deleteFile(fileObject);
      log.error("Error while parsing csv file", e);
      return;
    }
    var statistics = new Statistics(csvUsers.size());

    // validate users
    boolean isValid = validate(fileObject, csvUsers);
    if(!isValid) {
      return;
    }

    List<User> users = userService.convertToKeycloakUsers(csvUsers);

    // find users to be skipped
    var usersToBeSkipped = skippingService.check(users);
    statistics.addSkippedUsers(usersToBeSkipped.size());

    // print users to be skipped to log.warn
    skippingService.printResults(usersToBeSkipped);

    // remove users to be skipped
    List<EnumerableUser> enumerableUsers = userService.convertToEnumerableUsers(users);
    var filteredUsers = removeUsersToBeSkipped(enumerableUsers, usersToBeSkipped.keySet());
    
    // collapse KATOTTG codes to prefixes
    statistics.addCollapsedKatottg(userService.collapseKatottg(filteredUsers));

    // import users
    statistics.addStatistics(importUsers(fileObject, filteredUsers));

    // move file to archive 
    if (statistics.getImportedUsers() != 0) {
      fileService.moveToArchive(fileObject);
    } else {
      fileService.deleteFile(fileObject);
    }
    
    printStatistics(statistics);
  }

  private boolean validate(FileObject fileObject, List<CsvUser> users) {
    var validationResult = validationService.validate(users);
    if (validationResult.isValid()) {
      log.info("File has been validated. File name: '{}', id: '{}'", 
          fileObject.getFileName(), fileObject.getId());
    } else {
      log.error("File validation failed. File name: '{}', id: '{}'",
          fileObject.getFileName(), fileObject.getId());
      validationService.printErrors(validationResult);
      fileService.deleteFile(fileObject);
    }
    return validationResult.isValid();
  }

  private <T extends User> List<T> removeUsersToBeSkipped(List<T> users, Set<Integer> numbersOfSkippedUsers) {
    List<T> filteredUsers = new ArrayList<>();
    for (int i = 0; i < users.size(); i++) {
      if (!numbersOfSkippedUsers.contains(i)) {
        filteredUsers.add(users.get(i));
      }
    }
    return filteredUsers;
  }

  private Statistics importUsers(FileObject fileObject, final List<EnumerableUser> users) {
    var usersClone = new LinkedList<>(users);

    var statistics = new Statistics();
    while (!usersClone.isEmpty()) {
      var batch = batchHelper.getBatchOfUsers(usersClone);
      try {
        updateStatistics(statistics, processBatch(batch, users, fileObject));
      } catch (BatchImportException e) {
        log.error("Error while importing batch of users. Start importing users one by one");
        oneByOneImport(batch.getUsers(), fileObject, statistics);
      }
    }
    return statistics;
  }
  
  private void oneByOneImport(List<EnumerableUser> users, FileObject fileObject, Statistics statistics) {
    for(var user : users) {
      var batch = batchHelper.getBatchOfUsers(user);
      try {
        updateStatistics(statistics, processBatch(batch, users, fileObject));
      } catch (BatchImportException e) {
        log.error("Error while importing user from row {}", user.getSerialNumber() + FIRST_USER_OFFSET);
        statistics.addNotImportedUsers(1);
      }
    }
  }

  private PartialImportKeycloakResponse processBatch(
      BatchOfUsers<EnumerableUser> batch, 
      List<EnumerableUser> users,
      FileObject fileObject) {
    var response = keycloakService.importBatchOfUsers(batchHelper.batchToString(batch));
    logResponse(response);
    userImportAuditFacade.sendAudit(response, users, fileObject);
    return response;
  }

  private void logResponse(PartialImportKeycloakResponse response) {
    if (response.getSkipped() == 0) {
      return;
    }
    for (var result : response.getResults()) {
      if (result.getAction().equals("SKIPPED")) {
        log.info("User skipped by keycloak because there is already a user with the same name. "
            + "Username: '{}', keycloakId: '{}'", result.getResourceName(), result.getId());
      }
    }
  }
  
  private void updateStatistics(Statistics statistics, PartialImportKeycloakResponse response) {
    statistics.addImportedUsers(response.getAdded());
    statistics.addSkippedUsers(response.getSkipped());
  }

  private void printStatistics(Statistics statistics) {
    log.info(
        "The file has been processed. Total users in file: {}. Successfully imported: {}. " 
            + "Skipped: {}. Failed to import: {}. KATOTTG codes which are already included in the " 
            + "higher levels of code were detected and removed in the following rows: {}", 
        statistics.getTotalNumberOfUsers(), statistics.getImportedUsers(), 
        statistics.getSkippedUsers(), statistics.getNotImportedUsers(), 
        statistics.getCollapsedKatottg());
  }
}

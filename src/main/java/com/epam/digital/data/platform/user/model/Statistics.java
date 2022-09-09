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

package com.epam.digital.data.platform.user.model;

import java.util.LinkedList;
import java.util.List;

public class Statistics {

  private int totalNumberOfUsers;
  private int importedUsers;
  private int notImportedUsers;
  private int skippedUsers;
  private List<Integer> collapsedKatottg = new LinkedList<>();

  public Statistics() {
  }

  public Statistics(int totalNumberOfUsers) {
    this.totalNumberOfUsers = totalNumberOfUsers;
  }

  public Statistics addStatistics(Statistics statistics) {
    this.totalNumberOfUsers += statistics.totalNumberOfUsers;
    this.importedUsers += statistics.importedUsers;
    this.notImportedUsers += statistics.notImportedUsers;
    this.skippedUsers += statistics.skippedUsers;
    this.collapsedKatottg.addAll(statistics.collapsedKatottg);
    return this;
  }

  public int getTotalNumberOfUsers() {
    return totalNumberOfUsers;
  }

  public void setTotalNumberOfUsers(int totalNumberOfUsers) {
    this.totalNumberOfUsers = totalNumberOfUsers;
  }

  public void addTotalNumberOfUsers(int totalNumberOfUsers) {
    this.totalNumberOfUsers += totalNumberOfUsers;
  }

  public int getImportedUsers() {
    return importedUsers;
  }

  public void setImportedUsers(int importedUsers) {
    this.importedUsers = importedUsers;
  }

  public void addImportedUsers(int importedUsers) {
    this.importedUsers += importedUsers;
  }

  public int getNotImportedUsers() {
    return notImportedUsers;
  }

  public void setNotImportedUsers(int notImportedUsers) {
    this.notImportedUsers = notImportedUsers;
  }

  public void addNotImportedUsers(int notImportedUsers) {
    this.notImportedUsers += notImportedUsers;
  }

  public int getSkippedUsers() {
    return skippedUsers;
  }

  public void setSkippedUsers(int skippedUsers) {
    this.skippedUsers = skippedUsers;
  }

  public void addSkippedUsers(int skippedUsers) {
    this.skippedUsers += skippedUsers;
  }

  public List<Integer> getCollapsedKatottg() {
    return collapsedKatottg;
  }

  public void setCollapsedKatottg(List<Integer> collapsedKatottg) {
    this.collapsedKatottg = collapsedKatottg;
  }

  public void addCollapsedKatottg(List<Integer> collapsedKatottg) {
    this.collapsedKatottg.addAll(collapsedKatottg);
  }
}

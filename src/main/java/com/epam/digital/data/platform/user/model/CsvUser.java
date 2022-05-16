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

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

public class CsvUser {

  private String drfo;
  private String edrpou;
  private String fullName;
  @JsonAlias({"Realm Roles"})
  private List<String> realmRoles;

  public String getDrfo() {
    return drfo;
  }

  public void setDrfo(String drfo) {
    this.drfo = drfo;
  }

  public String getEdrpou() {
    return edrpou;
  }

  public void setEdrpou(String edrpou) {
    this.edrpou = edrpou;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public List<String> getRealmRoles() {
    return realmRoles;
  }

  public void setRealmRoles(List<String> realmRoles) {
    this.realmRoles = realmRoles;
  }
}

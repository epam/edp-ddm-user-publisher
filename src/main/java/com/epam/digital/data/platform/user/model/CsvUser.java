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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CsvUser {

  // mandatory fields
  public static final String FULL_NAME = "fullName";
  public static final String EDRPOU = "edrpou";
  public static final String DRFO = "drfo";
  public static final String REALM_ROLES = "Realm Roles";
  public static final String KATOTTG = "KATOTTG";

  public final Map<String, Consumer<String>> mandatoryFieldsString = Map.of(
      FULL_NAME, this::setFullName,
      EDRPOU, this::setEdrpou,
      DRFO, this::setDrfo
  );

  public final Map<String, Consumer<List<String>>> mandatoryFieldsList = Map.of(
      REALM_ROLES, this::setRealmRoles,
      KATOTTG, this::setKatottg
  );

  private String drfo;
  private String edrpou;
  private String fullName;
  private List<String> realmRoles;
  private List<String> katottg;
  private Map<String, List<String>> customAttributes;

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

  public List<String> getKatottg() {
    return katottg;
  }

  public void setKatottg(List<String> katottg) {
    this.katottg = katottg;
  }

  public Map<String, List<String>> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, List<String>> customAttributes) {
    this.customAttributes = customAttributes;
  }
}

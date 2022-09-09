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

import static com.epam.digital.data.platform.user.model.CsvUser.DRFO;
import static com.epam.digital.data.platform.user.model.CsvUser.EDRPOU;
import static com.epam.digital.data.platform.user.model.CsvUser.FULL_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User {

  private String username;
  private final Map<String, List<String>> attributes;
  private List<String> realmRoles;
  private String lastName;
  boolean enabled = true;

  public User() {
    attributes = new HashMap<>();
  }

  public User(User other) {
    this.username = other.username;
    this.attributes = new HashMap<>(other.attributes);
    realmRoles = other.realmRoles == null ? null : new ArrayList<>(other.realmRoles);
    this.setLastName(other.getLastName());
    this.enabled = other.enabled;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Map<String, List<String>> getAttributes() {
    return attributes;
  }

  public List<String> getRealmRoles() {
    return realmRoles;
  }

  public void setRealmRoles(List<String> realmRoles) {
    this.realmRoles = realmRoles;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(attributes.get(FULL_NAME), user.attributes.get(FULL_NAME))
        && Objects.equals(attributes.get(EDRPOU), user.attributes.get(EDRPOU))
        && Objects.equals(attributes.get(DRFO), user.attributes.get(DRFO));
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributes.get(FULL_NAME), attributes.get(EDRPOU), attributes.get(DRFO));
  }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

  private String username;
  private Attributes attributes;
  private List<String> realmRoles;
  private String lastName;
  boolean enabled = true;

  public User() {
    attributes = new Attributes();
  }

  public User(User other) {
    this.username = other.username;
    this.attributes = new Attributes(other.attributes);
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

  public void setDrfo(List<String> drfo) {
    this.attributes.drfo = drfo;
  }

  public void setEdrpou(List<String> edrpou) {
    this.attributes.edrpou = edrpou;
  }

  public void setFullName(List<String> fullName) {
    this.attributes.fullName = fullName;
  }

  public Attributes getAttributes() {
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
    return Objects.equals(attributes, user.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributes);
  }

  public class Attributes {

    private List<String> drfo;
    private List<String> edrpou;
    private List<String> fullName;

    public Attributes() {
    }

    public Attributes(Attributes other) {
      drfo = other.drfo == null ? null : new ArrayList<>(other.drfo);
      edrpou = other.edrpou == null ? null : new ArrayList<>(other.edrpou);
      fullName = other.fullName == null ? null : new ArrayList<>(other.fullName);
    }

    public List<String> getDrfo() {
      return drfo;
    }

    public List<String> getEdrpou() {
      return edrpou;
    }

    public List<String> getFullName() {
      return fullName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Attributes that = (Attributes) o;
      return Objects.equals(drfo, that.drfo) && Objects.equals(edrpou, that.edrpou)
          && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(drfo, edrpou, fullName);
    }
  }
}

package com.epam.digital.data.platform.utils;

import com.epam.digital.data.platform.user.model.User;
import java.util.List;

public class MockUser {

  public static UserBuilder user() {
    return new UserBuilder();
  }

  public static final class UserBuilder {

    private final User user = new User();

    public UserBuilder username(String username) {
      user.setUsername(username);
      return this;
    }

    public UserBuilder drfo(List<String> drfo) {
      user.setDrfo(drfo);
      return this;
    }

    public UserBuilder edrpou(List<String> edrpou) {
      user.setEdrpou(edrpou);
      return this;
    }

    public UserBuilder fullName(List<String> fullName) {
      user.setFullName(fullName);
      return this;
    }

    public UserBuilder realmRoles(List<String> realmRoles) {
      user.setRealmRoles(realmRoles);
      return this;
    }

    public UserBuilder enabled(boolean enabled) {
      user.setEnabled(enabled);
      return this;
    }

    public User build() {
      return user;
    }
  }
}

package com.epam.digital.data.platform.utils;

import com.epam.digital.data.platform.user.model.CsvUser;
import java.util.List;

public class MockCsvUser {

  public static UserBuilder user() {
    return new UserBuilder();
  }

  public static final class UserBuilder {

    private final CsvUser user = new CsvUser();

    public UserBuilder drfo(String drfo) {
      user.setDrfo(drfo);
      return this;
    }

    public UserBuilder edrpou(String edrpou) {
      user.setEdrpou(edrpou);
      return this;
    }

    public UserBuilder fullName(String fullName) {
      user.setFullName(fullName);
      return this;
    }

    public UserBuilder realmRoles(List<String> realmRoles) {
      user.setRealmRoles(realmRoles);
      return this;
    }

    public CsvUser build() {
      return user;
    }
  }
}

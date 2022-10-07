package com.epam.digital.data.platform.user.validate;

import java.util.ArrayList;
import java.util.List;

public abstract class CharactersValidator extends Validator {

  protected List<String> getInvalidCharacters(String str, String validCharactersPattern) {
    List<String> invalidCharacters = new ArrayList<>();
    for(int i = 0; i < str.length(); i++) {
      var character = Character.toString(str.charAt(i));
      if(!character.matches(validCharactersPattern)) {
        invalidCharacters.add(character);
      }
    }
    return invalidCharacters;
  }
}

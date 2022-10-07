package com.epam.digital.data.platform.user.validate.custom;

import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.user.model.ValidationResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomAttributeCharactersValidatorTest {

  private CustomAttributeCharactersValidator validator;

  @BeforeEach
  void beforeEach() {
    validator = new CustomAttributeCharactersValidator();
  }

  @Test
  void happyPathDoesNotWriteAnyErrors() {
    var result = validator.validate(1,
        user().attributes(Map.of("some correct name", List.of("some correct value"))).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void errorWhenCustomAttributeHasNameWithInvalidCharacter() {
    var result = validator.validate(1,
        user().attributes(Map.of("so[me] \\ {incorrect} \"name\"", List.of("correct value")))
            .build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0)).isEqualTo(
        "The attribute name 'so[me] \\ {incorrect} \"name\"' contains invalid symbols : {[, ], \\, {, }, \", \"}");
  }


  @Test
  void errorWhenCustomAttributeHasValueWithInvalidCharacter() {
    var result = validator.validate(1,
        user().attributes(
                Map.of("some correct name", List.of("incorr[ect] value1 \\", "inc{orrect} \"value2\"")))
            .build(),
        new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0)).isEqualTo(
        "The attribute with name 'some correct name' contains value with invalid symbols : {[, ], \\, {, }, \", \"}");
  }
}
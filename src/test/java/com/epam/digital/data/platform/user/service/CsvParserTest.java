package com.epam.digital.data.platform.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.digital.data.platform.user.config.GenericConfig;
import com.epam.digital.data.platform.user.exception.FileHasDuplicateColumnsException;
import com.epam.digital.data.platform.user.util.TestUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenericConfig.class, CsvParser.class})
class CsvParserTest {

  @Autowired
  CsvParser csvParser;

  @Test
  void exceptionWhenFileHaveDuplicateColumns() {
    byte[] content = TestUtils.getContent("json/users-duplicate-columns.csv")
        .getBytes(StandardCharsets.UTF_8);
    var csv = new String(content, StandardCharsets.UTF_8);

    assertThat(
        assertThrows(FileHasDuplicateColumnsException.class, () -> csvParser.getCsvUsers(csv))
            .getMessage()).isEqualTo("The following columns have duplicates: [drfo, Realm Roles]");
  }
}
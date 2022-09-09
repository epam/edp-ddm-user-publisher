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

package com.epam.digital.data.platform.user.service;

import static com.epam.digital.data.platform.user.util.Util.listToString;
import static com.epam.digital.data.platform.user.util.Util.trimToNull;

import com.epam.digital.data.platform.user.exception.MappingException;
import com.epam.digital.data.platform.user.model.CsvUser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

  public static final String UTF8_BOM = "\uFEFF";

  private final ObjectReader csvMapper;

  public CsvParser(ObjectReader csvMapper) {
    this.csvMapper = csvMapper;
  }

  public List<CsvUser> getCsvUsers(String csv) {
    csv = removeUtf8BomIfExist(csv);
    try {
      MappingIterator<CsvMap> iterator = csvMapper.readValues(csv);
      return iterator.readAll().stream()
          .map(this::mapToCsvUser)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new MappingException("Unable to map csv file to List<User>", e);
    }
  }

  private CsvUser mapToCsvUser(Map<String, List<String>> csvMap) {
    var csvUser = new CsvUser();
    csvUser.mandatoryFieldsString.forEach(
        (key, value) -> value.accept(listToString(csvMap.remove(key))));
    csvUser.mandatoryFieldsList.forEach(
        (key, value) -> value.accept(trimToNull(csvMap.remove(key))));
    csvUser.setCustomAttributes(trimToNull(csvMap));
    return csvUser;
  }

  private String removeUtf8BomIfExist(String s) {
    while (s.startsWith(UTF8_BOM)) {
      s = s.substring(1);
    }
    return s;
  }

  public static class CsvMap extends HashMap<String, List<String>> {

  }
}

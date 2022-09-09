package com.epam.digital.data.platform.user.util;

import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class KatottgUtilTest {

  // UA03 02 001 001 01 12345 - divided into groups
  
  private static final String KATOTTG_01 = "UA";
  private static final String KATOTTG_02 = "UA03020000000012345";
  private static final String KATOTTG_03 = "UA03020010000012345";
  private static final String KATOTTG_04 = "UA04020010010012345";
  private static final String KATOTTG_05 = "UA05020010010112345";
  
  private static final String KATOTTG_06 = "UA04000000000012345";
  private static final String KATOTTG_07 = "UA05020000000012345";
  private static final String KATOTTG_08 = "UA06020010000012345";
  private static final String KATOTTG_09 = "UA07020010010012345";
  private static final String KATOTTG_10 = "UA08020010010112345";
  
  @Test
  void shouldReturnPrefixesSortedByStringLength() {
    var result = KatottgUtil.retainPrefixes(
        List.of(KATOTTG_08, KATOTTG_07, KATOTTG_09, KATOTTG_06, KATOTTG_10));
    
    assertThat(result)
        .isSortedAccordingTo(Comparator.comparing(String::length))
        .containsExactly("UA04", "UA0502", "UA0602001", "UA0702001001", "UA080200100101");
  }

  @Test
  void shouldReturnOnlyOnePrefix() {
    var result = KatottgUtil.retainPrefixes(
        List.of(KATOTTG_01, KATOTTG_08, KATOTTG_07, KATOTTG_09, KATOTTG_06, KATOTTG_10));

    assertThat(result)
        .isSortedAccordingTo(Comparator.comparing(String::length))
        .containsExactly("UA");
  }
  
  @Test
  void shouldCollapseSubgroupPrefixes() {
    var result = KatottgUtil.retainPrefixes(
        List.of(KATOTTG_02, KATOTTG_03, KATOTTG_04, KATOTTG_05, KATOTTG_06));

    assertThat(result)
        .containsExactly("UA04", "UA0302", "UA050200100101");
  }
}


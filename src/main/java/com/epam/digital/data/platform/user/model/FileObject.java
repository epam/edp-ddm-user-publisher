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

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;

public class FileObject {

  private String id;
  private String fileName;
  private byte[] content;
  private String checksum;

  public FileObject(String id, String fileName, byte[] content) {
    this.id = id;
    this.fileName = fileName;
    this.content = content;
    this.checksum = DigestUtils.sha256Hex(content);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileObject that = (FileObject) o;
    return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName)
        && Arrays.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, fileName);
    result = 31 * result + Arrays.hashCode(content);
    return result;
  }
}

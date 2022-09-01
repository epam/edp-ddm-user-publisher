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

import static com.epam.digital.data.platform.user.util.Constants.USER_METADATA_KEY_FILENAME;
import static com.epam.digital.data.platform.user.util.Constants.CONTENT_TYPE;

import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.user.exception.FileContentNotReadableException;
import com.epam.digital.data.platform.user.exception.Base64DecodingException;
import com.epam.digital.data.platform.user.exception.FileNameNotFoundException;
import com.epam.digital.data.platform.user.exception.FileNotFoundException;
import com.epam.digital.data.platform.user.model.FileObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileService {

  private final String userImportBucket;
  private final String userImportArchiveBucket;
  private final CephService userImportCephService;
  private final CephService userImportArchiveCephService;

  public FileService(
      @Value("${user-import-ceph.bucket}") String userImportBucket,
      @Value("${user-import-archive-ceph.bucket}") String userImportArchiveBucket,
      CephService userImportCephService,
      CephService userImportArchiveCephService) {
    this.userImportBucket = userImportBucket;
    this.userImportArchiveBucket = userImportArchiveBucket;
    this.userImportCephService = userImportCephService;
    this.userImportArchiveCephService = userImportArchiveCephService;
  }

  public FileObject getFile(String id) {
    var cephObject = userImportCephService.get(userImportBucket, id)
        .orElseThrow(() -> new FileNotFoundException("File " + id + " not found!"));

    var base64FileName = cephObject.getMetadata().getUserMetadata().get(USER_METADATA_KEY_FILENAME);
    if (base64FileName == null) {
      throw new FileNameNotFoundException(
          "UserMetadata doesn't contain '" + USER_METADATA_KEY_FILENAME + "'. Ceph id:" + id);
    }
    
    String fileName;
    try {
      fileName = new String(Base64.getDecoder().decode(base64FileName), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException ex) {
      throw new Base64DecodingException("Cannot read file name", ex);
    }
    
    byte[] content;
    try {
      content = cephObject.getContent().readAllBytes();
    } catch (IOException e) {
      throw new FileContentNotReadableException("Cannot read file content", e);
    }
    return new FileObject(id, fileName, content);
  }

  public void moveToArchive(FileObject fileObject) {
    var base64FileName = Base64.getEncoder()
        .encodeToString(fileObject.getFileName().getBytes(StandardCharsets.UTF_8));
    
    var userMetadata = Map.of(USER_METADATA_KEY_FILENAME, base64FileName);
    userImportArchiveCephService.put(
        userImportArchiveBucket,
        fileObject.getId(),
        CONTENT_TYPE,
        userMetadata,
        new ByteArrayInputStream(fileObject.getContent()));

    deleteFile(fileObject);
  }

  public void deleteFile(FileObject fileObject) {
    userImportCephService.delete(userImportBucket, Set.of(fileObject.getId()));
  }
}

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

import static com.epam.digital.data.platform.user.util.Constants.CONTENT_TYPE;
import static com.epam.digital.data.platform.user.util.Constants.USER_METADATA_KEY_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.integration.ceph.model.CephObject;
import com.epam.digital.data.platform.integration.ceph.model.CephObjectMetadata;
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.user.exception.FileNameNotFoundException;
import com.epam.digital.data.platform.user.exception.FileNotFoundException;
import com.epam.digital.data.platform.user.model.FileObject;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

  private static final String USER_IMPORT_BUCKET = "user-import";
  private static final String USER_IMPORT_ARCHIVE_BUCKET = "user-import-archive";
  private static final String FILE_ID = "someId";
  private static final String FILE_NAME = "someFileName";
  private static final String BASE64_FILE_NAME = "c29tZUZpbGVOYW1l";
  private static final byte[] FILE_CONTENT = new byte[]{1, 2, 3, 4};

  @Mock
  CephService userImportCephService;
  @Mock
  CephService userImportArchiveCephService;
  private FileService fileService;

  @BeforeEach
  void beforeEach() {
    fileService = new FileService(USER_IMPORT_BUCKET, USER_IMPORT_ARCHIVE_BUCKET,
        userImportCephService, userImportArchiveCephService);
  }


  @Test
  void shouldGetCorrectFileObject() {
    when(userImportCephService.get(USER_IMPORT_BUCKET, FILE_ID))
        .thenReturn(Optional.of(mockCephObject()));

    var actual = fileService.getFile(FILE_ID);

    assertThat(actual).isEqualTo(mockFileObject());
  }

  @Test
  void throwsExceptionWhenFileNotFound() {
    when(userImportCephService.get(USER_IMPORT_BUCKET, FILE_ID)).thenReturn(Optional.empty());

    assertThrows(FileNotFoundException.class, () -> fileService.getFile(FILE_ID));
  }

  @Test
  void throwsExceptionWhenUserMetadataDoesNotContainFilename() {
    CephObject mock = mockCephObject();
    mock.getMetadata().setUserMetadata(new HashMap<>());

    when(userImportCephService.get(USER_IMPORT_BUCKET, FILE_ID))
        .thenReturn(Optional.of(mock));

    assertThrows(FileNameNotFoundException.class, () -> fileService.getFile(FILE_ID));
  }

  @Test
  void shouldCopyFileToArchiveBucketAndDeleteFromFirstBucket() {
    fileService.moveToArchive(mockFileObject());

    verify(userImportArchiveCephService)
        .put(
            eq(USER_IMPORT_ARCHIVE_BUCKET),
            eq(FILE_ID),
            eq(CONTENT_TYPE),
            eq(Map.of(USER_METADATA_KEY_FILENAME, BASE64_FILE_NAME)),
            any(ByteArrayInputStream.class)
        );

    verify(userImportCephService).delete(USER_IMPORT_BUCKET, Set.of(FILE_ID));
  }

  private FileObject mockFileObject() {
    return new FileObject(FILE_ID, FILE_NAME, FILE_CONTENT);
  }

  private CephObject mockCephObject() {
    return CephObject.builder()
        .content(new ByteArrayInputStream(FILE_CONTENT))
        .metadata(CephObjectMetadata.builder()
            .userMetadata(Map.of(USER_METADATA_KEY_FILENAME, BASE64_FILE_NAME))
            .build())
        .build();
  }
}

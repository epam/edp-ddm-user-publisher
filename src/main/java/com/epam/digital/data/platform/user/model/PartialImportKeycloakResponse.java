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

import java.util.List;

public class PartialImportKeycloakResponse {

  private int overwritten;
  private int added;
  private int skipped;
  private List<Result> results;

  public int getOverwritten() {
    return overwritten;
  }

  public void setOverwritten(int overwritten) {
    this.overwritten = overwritten;
  }

  public int getAdded() {
    return added;
  }

  public void setAdded(int added) {
    this.added = added;
  }

  public int getSkipped() {
    return skipped;
  }

  public void setSkipped(int skipped) {
    this.skipped = skipped;
  }

  public List<Result> getResults() {
    return results;
  }

  public void setResults(List<Result> results) {
    this.results = results;
  }

  public static class Result {

    private String action;
    private String resourceType;
    private String resourceName;
    private String id;

    public String getAction() {
      return action;
    }

    public void setAction(String action) {
      this.action = action;
    }

    public String getResourceType() {
      return resourceType;
    }

    public void setResourceType(String resourceType) {
      this.resourceType = resourceType;
    }

    public String getResourceName() {
      return resourceName;
    }

    public void setResourceName(String resourceName) {
      this.resourceName = resourceName;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }
}

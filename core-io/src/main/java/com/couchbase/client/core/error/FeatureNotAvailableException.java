/*
 * Copyright (c) 2016 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.core.error;

import com.couchbase.client.core.service.ServiceType;

/**
 * Exception which states that the feature is not available for the bucket.
 */
public class FeatureNotAvailableException extends CouchbaseException {

  public FeatureNotAvailableException(String message) {
    super(message);
  }

  public FeatureNotAvailableException(String message, Throwable cause) {
    super(message, cause);
  }

  public FeatureNotAvailableException(Throwable cause) {
    super(cause);
  }

  public static FeatureNotAvailableException clusterLevelQuery(final ServiceType serviceType) {
    return new FeatureNotAvailableException("The cluster does not support cluster-level queries " +
      "(only Couchbase Server 6.5 and later) and no bucket is open. If an older Couchbase Server version " +
      "is used, at least one bucket needs to be opened to dispatch this " + serviceType.ident() + " request!");
  }
}
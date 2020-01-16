/*
 * Copyright (c) 2019 Couchbase, Inc.
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

package com.couchbase.client.scala.kv.handlers

import com.couchbase.client.core.cnc.RequestSpan
import com.couchbase.client.core.msg.ResponseStatus
import com.couchbase.client.core.msg.kv.{GetMetaRequest, GetMetaResponse, KeyValueRequest}
import com.couchbase.client.core.retry.RetryStrategy
import com.couchbase.client.scala.HandlerParams
import com.couchbase.client.scala.kv.{DefaultErrors, ExistsResult}
import com.couchbase.client.scala.util.Validate

import scala.util.{Success, Try}

/**
  * Handles requests and responses for KV exists operations.
  *
  * @author Graham Pople
  */
private[scala] class ExistsHandler(hp: HandlerParams)
    extends KeyValueRequestHandler[GetMetaResponse, ExistsResult] {
  def request(
      id: String,
      timeout: java.time.Duration,
      retryStrategy: RetryStrategy,
      parentSpan: Option[RequestSpan]
  ): Try[GetMetaRequest] = {
    val validations: Try[GetMetaRequest] = for {
      _ <- Validate.notNullOrEmpty(id, "id")
      _ <- Validate.notNull(timeout, "timeout")
      _ <- Validate.notNull(retryStrategy, "retryStrategy")
      _ <- Validate.notNull(parentSpan, "parentSpan")
    } yield null

    if (validations.isFailure) {
      validations
    } else
      Success(
        new GetMetaRequest(
          id,
          timeout,
          hp.core.context(),
          hp.collectionIdentifier,
          retryStrategy,
          hp.tracer.internalSpan(GetMetaRequest.OPERATION_NAME_EXISTS, parentSpan.orNull)
        )
      )
  }

  override def response(
      request: KeyValueRequest[GetMetaResponse],
      id: String,
      response: GetMetaResponse
  ): ExistsResult = {
    val deleted = response.deleted()
    response.status() match {
      case ResponseStatus.SUCCESS if !deleted                => ExistsResult(true, response.cas())
      case ResponseStatus.SUCCESS | ResponseStatus.NOT_FOUND => ExistsResult(false, 0)
      case _                                                 => throw DefaultErrors.throwOnBadResult(request, response)
    }
  }
}

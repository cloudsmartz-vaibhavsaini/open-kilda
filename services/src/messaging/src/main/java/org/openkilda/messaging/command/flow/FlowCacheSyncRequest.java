/* Copyright 2018 Telstra Open Source
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.openkilda.messaging.command.flow;

import static com.google.common.base.MoreObjects.toStringHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openkilda.messaging.command.CommandData;

import java.util.Objects;

/**
 * Sync the FlowCache request.
 */
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowCacheSyncRequest extends CommandData {
    /**
     * Serialization version number constant.
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty("sync_cache")
    private SynchronizeCacheAction syncCacheAction;

    /**
     * Instance constructor.
     *
     * @throws IllegalArgumentException if payload is null
     */
    @JsonCreator
    public FlowCacheSyncRequest(
            @JsonProperty("sync_cache") SynchronizeCacheAction syncCacheAction) {
        this.syncCacheAction = Objects.requireNonNull(syncCacheAction, "sync_cache must not be null");
    }

    public SynchronizeCacheAction getSyncCacheAction() {
        return syncCacheAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        return true;
    }
}

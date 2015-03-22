/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.model;

/**
 * Created by steve on 22/03/15.
 *
 * A new object for cache in order to support deep etag.
 *
 */
public class CacheObject {
    String etag;
    Object data;

    public CacheObject(String etag, Object data) {
        this.etag = etag;
        this.data = data;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheObject)) return false;

        CacheObject that = (CacheObject) o;

        if (!etag.equals(that.etag)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return etag.hashCode();
    }

    @Override
    public String toString() {
        return "CacheObject{" +
                "etag=" + etag +
                ", data=" + data +
                '}';
    }
}

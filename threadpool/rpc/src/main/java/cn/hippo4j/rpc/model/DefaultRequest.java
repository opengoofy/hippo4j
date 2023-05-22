/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.rpc.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

/**
 * default request<br>
 * Use the fully qualified name key of the interface and override equals and hashCode
 *
 * @since 2.0.0
 */
public final class DefaultRequest implements Request {

    String RID;
    String key;
    int length;
    transient Object[] parameters;

    public DefaultRequest(String RID, String key, Object[] parameters) {
        this.RID = RID;
        this.key = key;
        this.parameters = parameters;
        this.length = parameters.length;
    }

    public DefaultRequest(String RID, String key) {
        this(RID, key, new Object[]{});
    }

    @Override
    public String getRID() {
        return RID;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultRequest that = (DefaultRequest) o;
        return Objects.equals(key, that.key) && Objects.equals(RID, that.RID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, RID);
    }

    /**
     * Redefine the behavior of serialization, that is, re-acquire the initially serialized
     * data from the stream and re-serialize it. Simple serialization will result in the
     * loss of the field identified by transient.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (parameters == null) {
            return;
        }
        // Serialization parameters
        for (Object parameter : parameters) {
            s.writeObject(parameter);
        }
    }

    /**
     * Redefine the deserialization behavior, and sequentially deserialize the data specified during
     * serialization, because there is data that is not deserialized during initial deserialization,
     * such as fields defined by transient
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        // Deserialization parameters
        Object[] a = new Object[length];
        for (int i = 0; i < length; i++) {
            a[i] = s.readObject();
        }
        this.parameters = a;
    }
}

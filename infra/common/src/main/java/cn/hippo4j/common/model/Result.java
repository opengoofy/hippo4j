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

package cn.hippo4j.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Result.
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -4408341719434417427L;

    /**
     * Correct return code.
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * Return code.
     */
    private String code;

    /**
     * Message.
     */
    private String message;

    /**
     * Response data.
     */
    private transient T data;

    /**
     * Is success.
     *
     * @return
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    /**
     * Redefine the behavior of serialization, that is, re-acquire the initially serialized
     * data from the stream and re-serialize it. Simple serialization will result in the
     * loss of the field identified by transient.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (data == null) {
            return;
        }
        // Serialization obj
        s.writeObject(this.data);
    }

    /**
     * Redefine the deserialization behavior, and sequentially deserialize the data specified during
     * serialization, because there is data that is not deserialized during initial deserialization,
     * such as fields defined by transient
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        try {
            // Deserialization obj
            if (isSuccess()) {
                this.data = (T) s.readObject();
            }
        } catch (IOException e) {
            // data may also be null when successful
        }

    }
}

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

package cn.hippo4j.rpc.coder;

import cn.hippo4j.rpc.exception.CoderException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * this is a encoder, For custom gluing and unpacking<br>
 * {@link io.netty.handler.codec.serialization.ObjectEncoder}
 *
 * @since 2.0.0
 */
public class ObjectEncoder extends MessageToByteEncoder<Serializable> {

    private static final int BYTE_LENGTH = 4;
    private static final byte[] BYTE = new byte[BYTE_LENGTH];

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) {
        int startIndex = out.writerIndex();
        try (ByteBufOutputStream outPut = new ByteBufOutputStream(out)) {
            outPut.write(BYTE);
            try (ObjectOutputStream outputStream = new CompactObjectOutputStream(outPut)) {
                outputStream.writeObject(msg);
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new CoderException("The encoding is abnormal, which may be caused by the transfer object being unable to be serialized");
        }
        int endIndex = out.writerIndex();
        out.setInt(startIndex, endIndex - startIndex - BYTE_LENGTH);
    }
}

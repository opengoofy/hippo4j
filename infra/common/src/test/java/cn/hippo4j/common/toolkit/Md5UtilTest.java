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

package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mockStatic;

public class Md5UtilTest {

    @Test
    public void assertMd5Hex() throws NoSuchAlgorithmException {
        String md5 = "3cdefff74dcf7a5d60893865b84b62c8";
        String message = "message-consume";
        Assert.isTrue(md5.equals(Md5Util.md5Hex(message.getBytes())));
    }

    @Test
    public void assertMd5Hex2() {
        String md5 = "503840dc3af3cdb39749cd099e4dfeff";
        String message = "dynamic-threadpool-example";
        Assert.isTrue(md5.equals(Md5Util.md5Hex(message, "UTF-8")));
    }

    @Test
    public void assetEncodeHexString() {
        String encodeHexString = "00010f107f80203040506070";
        byte[] bytes = {0, 1, 15, 16, 127, -128, 32, 48, 64, 80, 96, 112};
        Assert.isTrue(encodeHexString.equals(Md5Util.encodeHexString(bytes)));
    }

    @Test
    public void assetGetTpContentMd5() {
        final ThreadPoolParameterInfo threadPoolParameterInfo = new ThreadPoolParameterInfo();
        final String mockContent = "mockContent";
        final String mockContentMd5 = "34cf17bc632ece6e4c81a4ce8aa97d5e";
        try (final MockedStatic<ContentUtil> mockedContentUtil = mockStatic(ContentUtil.class)) {
            mockedContentUtil.when(() -> ContentUtil.getPoolContent(threadPoolParameterInfo)).thenReturn(mockContent);
            final String result = Md5Util.getTpContentMd5(threadPoolParameterInfo);
            Assert.isTrue(result.equals(mockContentMd5));
            mockedContentUtil.verify(() -> ContentUtil.getPoolContent(threadPoolParameterInfo), times(1));
        }
    }

    @Test
    public void assetCompareMd5ResultString() throws IOException {
        Assert.isTrue("".equals(Md5Util.compareMd5ResultString(null)));
        String result = "prescription%02dynamic-threadpool-example%02message-consume%01" +
                "prescription%02dynamic-threadpool-example%02message-produce%01";
        List<String> changedGroupKeys = new ArrayList<>(2);
        changedGroupKeys.add("prescription+dynamic-threadpool-example+message-consume+12");
        changedGroupKeys.add("prescription+dynamic-threadpool-example+message-produce+11");
        Assert.isTrue(result.equals(Md5Util.compareMd5ResultString(changedGroupKeys)));
    }
}

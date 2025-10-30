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

package cn.hippo4j.config.toolkit;

import cn.hippo4j.common.constant.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

/**
 * Md5ConfigUtil Version Detection Test
 * Tests the client protocol version detection logic for cross-version compatibility
 */
@RunWith(MockitoJUnitRunner.class)
public class Md5ConfigUtilVersionTest {

    @Mock
    private HttpServletRequest request;

    private static final String PROTOCOL_VERSION_HEADER = "X-Hippo4j-Protocol-Version";

    /**
     * Test: Client sends v2 protocol version header
     * Expected: Server correctly identifies client as v2
     */
    @Test
    public void testGetClientVersion_WithV2Header() throws Exception {
        System.out.println("========== Test 1: Client sends v2 version header ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn("2");

        int version = invokeGetClientVersion(request);

        System.out.println("Request header: X-Hippo4j-Protocol-Version = 2");
        System.out.println("Detected client version: " + version);

        Assert.assertEquals("Should detect v2 client", 2, version);
        System.out.println("Test passed: Server correctly identifies v2 client");
    }

    /**
     * Test: Old client doesn't send version header
     * Expected: Server defaults to v1 for backward compatibility
     */
    @Test
    public void testGetClientVersion_WithoutHeader() throws Exception {
        System.out.println("\n========== Test 2: Old client without version header ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn(null);

        int version = invokeGetClientVersion(request);

        System.out.println("Request header: X-Hippo4j-Protocol-Version = null");
        System.out.println("Detected client version: " + version);

        Assert.assertEquals("Should default to v1", 1, version);
        System.out.println("Test passed: Server defaults to v1 for backward compatibility");
    }

    /**
     * Test: Version header is empty string
     * Expected: Server defaults to v1
     */
    @Test
    public void testGetClientVersion_WithEmptyHeader() throws Exception {
        System.out.println("\n========== Test 3: Version header is empty string ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn("");

        int version = invokeGetClientVersion(request);

        System.out.println("Request header: X-Hippo4j-Protocol-Version = \"\"");
        System.out.println("Detected client version: " + version);

        Assert.assertEquals("Should default to v1 for empty header", 1, version);
        System.out.println("Test passed: Empty header defaults to v1");
    }

    /**
     * Test: Version header has invalid format (non-numeric)
     * Expected: Server gracefully handles error and defaults to v1
     */
    @Test
    public void testGetClientVersion_WithInvalidFormat() throws Exception {
        System.out.println("\n========== Test 4: Version header has invalid format ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn("invalid");

        int version = invokeGetClientVersion(request);

        System.out.println("Request header: X-Hippo4j-Protocol-Version = \"invalid\"");
        System.out.println("Detected client version: " + version);

        Assert.assertEquals("Should default to v1 for invalid format", 1, version);
        System.out.println("Test passed: Invalid format gracefully handled");
    }

    /**
     * Test: Version header is v1 explicitly
     * Expected: Server correctly identifies as v1
     */
    @Test
    public void testGetClientVersion_WithV1Header() throws Exception {
        System.out.println("\n========== Test 5: Client explicitly sends v1 ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn("1");

        int version = invokeGetClientVersion(request);

        System.out.println("Request header: X-Hippo4j-Protocol-Version = 1");
        System.out.println("Detected client version: " + version);

        Assert.assertEquals("Should detect v1 client", 1, version);
        System.out.println("Test passed: Server correctly identifies v1 client");
    }

    /**
     * Test: Version header is future version (v3)
     * Expected: Server correctly parses and returns the version
     */
    @Test
    public void testGetClientVersion_WithFutureVersion() throws Exception {
        System.out.println("\n========== Test 6: Client sends future version (v3) ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn("3");

        int version = invokeGetClientVersion(request);

        System.out.println("Request header: X-Hippo4j-Protocol-Version = 3");
        System.out.println("Detected client version: " + version);

        Assert.assertEquals("Should parse future version", 3, version);
        System.out.println("Test passed: Future version handled correctly");
    }

    /**
     * Test: compareMd5 method can be called with v1 client
     * Note: This is a lightweight test focusing on version detection
     * Full integration tests are covered separately
     */
    @Test
    public void testCompareMd5_VersionDetection_V1Client() throws Exception {
        System.out.println("\n========== Test 7: Version detection in compareMd5 (v1) ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn(null);

        // Verify version detection works
        int version = invokeGetClientVersion(request);

        System.out.println("Client version detected: v" + version);
        Assert.assertEquals("Should detect v1 client", 1, version);
        System.out.println("Test passed: compareMd5 will use v1 protocol for this client");
    }

    /**
     * Test: compareMd5 method can be called with v2 client
     * Note: This is a lightweight test focusing on version detection
     * Full integration tests are covered separately
     */
    @Test
    public void testCompareMd5_VersionDetection_V2Client() throws Exception {
        System.out.println("\n========== Test 8: Version detection in compareMd5 (v2) ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn("2");

        // Verify version detection works
        int version = invokeGetClientVersion(request);

        System.out.println("Client version detected: v" + version);
        Assert.assertEquals("Should detect v2 client", 2, version);
        System.out.println("Test passed: compareMd5 will use v2 protocol for this client");
    }

    /**
     * Test: Fallback to semantic client version when protocol header is missing.
     */
    @Test
    public void testGetClientVersion_FromClientVersionHeader() throws Exception {
        System.out.println("\n========== Test 9: Fallback to client version header ==========");

        when(request.getHeader(PROTOCOL_VERSION_HEADER)).thenReturn(null);
        when(request.getHeader(Constants.CLIENT_VERSION)).thenReturn("2.0.1");

        int version = invokeGetClientVersion(request);

        System.out.println("Client-Version header: 2.0.1");
        System.out.println("Detected protocol version: v" + version);

        Assert.assertEquals("Semantic version should map to protocol v2", 2, version);
        System.out.println("Test passed: Fallback resolved protocol from client version header");
    }

    /**
     * Helper method to invoke private getClientVersion method via reflection
     */
    private int invokeGetClientVersion(HttpServletRequest request) throws Exception {
        Method method = Md5ConfigUtil.class.getDeclaredMethod("getClientProtocolVersion", HttpServletRequest.class, String.class);
        method.setAccessible(true);
        return (int) method.invoke(null, request, request.getHeader(Constants.CLIENT_VERSION));
    }
}

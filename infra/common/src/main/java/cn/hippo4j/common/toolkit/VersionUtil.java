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

import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version related utility methods.
 *
 * <p>This utility centralises how Hippo4j resolves client versions from
 * different sources (pom, manifest) and how those versions map to the
 * incremental protocol version used for MD5 comparison.</p>
 */
public final class VersionUtil {
    public static final String UNKNOWN_VERSION = "0.0.0";

    public static final int LEGACY_PROTOCOL_VERSION = 1;

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?.*");

    private static final NavigableMap<SemanticVersion, Integer> PROTOCOL_VERSION_MAPPINGS = new TreeMap<>();

    static {
        registerProtocolVersion(UNKNOWN_VERSION, LEGACY_PROTOCOL_VERSION);
        registerProtocolVersion("1.0.0", LEGACY_PROTOCOL_VERSION);
        registerProtocolVersion("2.0.0", IncrementalContentUtil.PROTOCOL_VERSION);
    }

    private VersionUtil() {
    }

    /**
     * Resolve the client version string. First non blank candidate will be returned;
     * if all candidates are blank the method falls back to the Implementation-Version
     * from the provided class' package, and finally to {@code 0.0.0}.
     *
     * @param explicitVersion explicit version string provided by caller (can be null)
     * @param fallbackClass   class whose package can provide an Implementation-Version
     * @return resolved version string, never {@code null}
     */
    public static String resolveClientVersion(String explicitVersion, Class<?> fallbackClass) {
        String candidate = firstNonBlank(explicitVersion);
        if (StringUtil.isBlank(candidate) && fallbackClass != null) {
            Package pkg = fallbackClass.getPackage();
            if (pkg != null) {
                candidate = pkg.getImplementationVersion();
            }
        }
        if (StringUtil.isBlank(candidate)) {
            return UNKNOWN_VERSION;
        }
        return candidate.trim();
    }

    /**
     * Resolve protocol version from a semantic version string. If the provided version is blank
     * or cannot be parsed, {@code defaultVersion} will be returned.
     *
     * @param version        semantic version string
     * @param defaultVersion default protocol version fallback
     * @return resolved protocol version number
     */
    public static int resolveProtocolVersion(String version, int defaultVersion) {
        if (StringUtil.isBlank(version)) {
            return defaultVersion;
        }
        SemanticVersion semanticVersion = SemanticVersion.parse(version);
        if (semanticVersion == null) {
            return defaultVersion;
        }
        Map.Entry<SemanticVersion, Integer> entry = PROTOCOL_VERSION_MAPPINGS.floorEntry(semanticVersion);
        if (entry == null) {
            return defaultVersion;
        }
        Integer mapped = entry.getValue();
        return mapped != null ? mapped : defaultVersion;
    }

    /**
     * Resolve protocol version using default value {@link IncrementalContentUtil#PROTOCOL_VERSION}.
     *
     * @param version semantic version string
     * @return resolved protocol version number
     */
    public static int resolveProtocolVersion(String version) {
        return resolveProtocolVersion(version, IncrementalContentUtil.PROTOCOL_VERSION);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static void registerProtocolVersion(String version, int protocolVersion) {
        SemanticVersion semanticVersion = SemanticVersion.parse(version);
        if (semanticVersion != null) {
            PROTOCOL_VERSION_MAPPINGS.put(semanticVersion, protocolVersion);
        }
    }

    /**
     * Lightweight immutable semantic version implementation (major.minor.patch).
     */
    private static final class SemanticVersion implements Comparable<SemanticVersion> {

        private final int major;
        private final int minor;
        private final int patch;

        private SemanticVersion(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        private static SemanticVersion parse(String version) {
            Matcher matcher = VERSION_PATTERN.matcher(version.trim());
            if (!matcher.matches()) {
                return null;
            }
            int major = parseOrDefault(matcher.group(1));
            int minor = parseOrDefault(matcher.group(2));
            int patch = parseOrDefault(matcher.group(3));
            return new SemanticVersion(major, minor, patch);
        }

        private static int parseOrDefault(String value) {
            if (StringUtil.isBlank(value)) {
                return 0;
            }
            return Integer.parseInt(value);
        }

        @Override
        public int compareTo(SemanticVersion other) {
            if (other == null) {
                return 1;
            }
            if (major != other.major) {
                return Integer.compare(major, other.major);
            }
            if (minor != other.minor) {
                return Integer.compare(minor, other.minor);
            }
            return Integer.compare(patch, other.patch);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SemanticVersion)) {
                return false;
            }
            SemanticVersion that = (SemanticVersion) obj;
            return major == that.major && minor == that.minor && patch == that.patch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(major, minor, patch);
        }
    }
}

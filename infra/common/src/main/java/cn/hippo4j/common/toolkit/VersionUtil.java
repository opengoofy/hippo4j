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

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?.*");

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
     * Compare two semantic versions using {@link SemanticVersion}. Returns {@code true} if
     * {@code version1} is greater than or equal to {@code version2}. Blank or unparsable versions
     * are treated conservatively and will return {@code false}.
     *
     * @param version1 the client version
     * @param version2 the minimum version requirement
     * @return {@code true} if version1 >= version2
     */
    public static boolean isVersionGreaterOrEqual(String version1, String version2) {
        if (StringUtil.isBlank(version1) || StringUtil.isBlank(version2)) {
            return false;
        }
        SemanticVersion v1 = SemanticVersion.parse(version1);
        SemanticVersion v2 = SemanticVersion.parse(version2);
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) >= 0;
    }

    /**
     * Return the first non-blank value from the provided arguments.
     *
     * @param values variable arguments to check
     * @return first non-blank value, or {@code null} if all are blank
     */
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

        /**
         * Parse a semantic version string into a SemanticVersion instance.
         *
         * @param version version string (e.g., "2.1.5", "2.0.0-SNAPSHOT")
         * @return parsed SemanticVersion, or {@code null} if format is invalid
         */
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

        /**
         * Parse a version component string to integer, defaulting to 0 if blank.
         *
         * @param value version component string
         * @return parsed integer, or 0 if blank
         */
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

        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }
}

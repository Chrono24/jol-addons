/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jol.addons;

import static java.util.stream.Collectors.toUnmodifiableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;


public class HistogramDeduplicator {

    private static final HistogramDeduplicator INSTANCE = builder().withTerminalClasses(Set.of( //
            "sun.nio.ch.FileChannelImpl", //
            "java.io.FileDescriptor", //
            "java.nio.channels.FileChannel" //
    )).build();

    private static final Set<String> MERGE_NESTED_INSTANCES_OF_CLASSES = Set.of( //
            // java.util
            "java.util.LinkedList$Node", //
            "java.util.TreeMap$Entry", //
            "java.util.LinkedHashMap$Entry", //
            "java.util.HashMap$Node", //
            "java.util.HashMap$TreeNode",

            // java.util.concurrent
            "java.util.concurrent.ConcurrentSkipListMap$Node", //
            "java.util.concurrent.ConcurrentSkipListMap$Index", //
            "java.util.concurrent.ConcurrentHashMap$Node",  //
            "java.util.concurrent.ConcurrentHashMap$TreeBin",  //
            "java.util.concurrent.LinkedBlockingDeque$Node", //
            "java.util.concurrent.LinkedBlockingQueue$Node", //
            "java.util.concurrent.ConcurrentLinkedQueue$Node", //
            "java.util.concurrent.SynchronousQueue$TransferQueue$QNode", //
            "java.util.concurrent.SynchronousQueue$TransferStack$SNode", //

            // guava
            "com.google.common.cache.LocalCache$WriteThroughEntry", //
            "com.google.common.cache.LocalCache$WeakAccessWriteEntry", //
            "com.google.common.cache.LocalCache$WeakWriteEntry", //
            "com.google.common.cache.LocalCache$WeakAccessEntry", //
            "com.google.common.cache.LocalCache$WeakEntry", //
            "com.google.common.cache.LocalCache$StrongAccessWriteEntry", //
            "com.google.common.cache.LocalCache$StrongWriteEntry", //
            "com.google.common.cache.LocalCache$StrongAccessEntry", //
            "com.google.common.cache.LocalCache$StrongEntry" //
    );

    private static final Map<String, Map<String, String>> MERGE_FIELDS_PER_CLASS;

    static {
        Map<String, String> firstAndLast = mergeFields("first", "last");
        Map<String, String> headAndTail = mergeFields("head", "tail");

        MERGE_FIELDS_PER_CLASS = Map.of( //
                // java.util
                "java.util.LinkedList", firstAndLast, //
                "java.util.LinkedHashMap", headAndTail, //

                // java.util.concurrent
                "java.util.concurrent.ConcurrentHashMap$Node", mergeFields("table", "nextTable"), //
                "java.util.concurrent.LinkedBlockingDeque", firstAndLast, //
                "java.util.concurrent.LinkedBlockingQueue", mergeFields("head", "last"), //
                "java.util.concurrent.ConcurrentLinkedQueue", headAndTail //
        );
    }

    private static Map<String, String> mergeFields(String... fieldNames) {
        String mergedFieldName = String.join("/", fieldNames);
        return Stream.concat(Arrays.stream(fieldNames), Stream.of(mergedFieldName))
                .distinct()
                .collect(toUnmodifiableMap(Function.identity(), ignored -> mergedFieldName));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String getMergedField(Class<?> clazz, String fieldName) {
        return MERGE_FIELDS_PER_CLASS.getOrDefault(clazz.getName(), Collections.emptyMap()).getOrDefault(fieldName, fieldName);
    }

    public static HistogramDeduplicator instance() {
        return INSTANCE;
    }

    public static boolean isNestedInstanceMerged(Class<?> parentInstanceClass, Class<?> childInstanceClass) {
        // TODO define tuples of classes to be merged, and the means to reduce class names to the merger, just like sibling field names
        return parentInstanceClass.equals(childInstanceClass) && MERGE_NESTED_INSTANCES_OF_CLASSES.contains(childInstanceClass.getName());
    }

    private final Set<String> terminalClasses;

    public HistogramDeduplicator(Set<String> terminalClasses) {
        this.terminalClasses = terminalClasses;
    }

    public boolean isTerminal(Class<?> clazz) {
        return terminalClasses.contains(clazz.getName());
    }

    public static class Builder {

        private Set<String> terminalClasses = Collections.emptySet();

        public HistogramDeduplicator build() {
            return new HistogramDeduplicator(terminalClasses);
        }

        public Builder withTerminalClasses(Set<String> terminalClasses) {
            this.terminalClasses = terminalClasses;
            return this;
        }
    }
}
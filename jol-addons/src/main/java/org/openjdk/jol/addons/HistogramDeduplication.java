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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HistogramDeduplication {

    private static final Map<String, Map<String, String>> MERGED_FIELDS = Map.of( //
            "java.util.LinkedList", //
            Map.of( //
                    "first", "first/last", //
                    "last", "first/last" //
            ), //
            "java.util.LinkedList$Node", //
            Map.of( //
                    "prev", "prev/next", //
                    "next", "prev/next" //
            ), //
            "java.util.TreeMap$Entry", //
            Map.of( //
                    "parent", "parent/left/right", //
                    "left", "parent/left/right", //
                    "right", "parent/left/right" //
            ), //
            "java.util.LinkedHashMap", //
            Map.of( //
                    "head", "head/tail", //
                    "tail", "head/tail" //
            ), //
            "java.util.LinkedHashMap$Entry", //
            Map.of( //
                    "next", "next/before/after", //
                    "before", "next/before/after", //
                    "after", "next/before/after" //
            ), //
            "ConcurrentSkipListMap$Index", //
            Map.of( //
                    "down", "down/right", //
                    "right", "down/right" //
            ) //
    );

    private static final Set<String> MERGED_FIELD_NAMES = Stream.concat(
            HistogramDeduplication.MERGED_FIELDS.values().stream().flatMap(m -> m.keySet().stream()).distinct(),
            HistogramDeduplication.MERGED_FIELDS.values().stream().flatMap(m -> m.values().stream()).distinct()).collect(Collectors.toSet());

    private static final Set<String> TERMINAL_CLASSES = new HashSet<>(Set.of( //
            "sun.nio.ch.FileChannelImpl", //
            "java.io.FileDescriptor", //
            "java.nio.channels.FileChannel", //
            "org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter", //
            "org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader", //
            "org.apache.lucene.queryparser.classic.QueryParser", //
            "org.apache.lucene.search.SearcherManager", //
            "org.apache.lucene.index.IndexWriter", //
            "org.apache.lucene.index.IndexWriterConfig", //
            "org.slf4j.impl.Log4jLoggerAdapter", //
            "org.slf4j.Logger" //
    ));

    public static String getMergedField(Class<?> clazz, String fieldName) {
        return MERGED_FIELDS.getOrDefault(clazz.getName(), Collections.emptyMap()).getOrDefault(fieldName, fieldName);
    }

    public static boolean isFieldMerged(String fieldName) {
        return MERGED_FIELD_NAMES.contains(fieldName);
    }

    public static boolean isTerminal(Class<?> clazz) {
        return TERMINAL_CLASSES.contains(clazz.getName());
    }
}
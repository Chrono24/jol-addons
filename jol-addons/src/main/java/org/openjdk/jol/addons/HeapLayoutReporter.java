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

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.PrintWriter;
import java.util.function.LongFunction;


abstract class HeapLayoutReporter {

    private static final int INDENT_CHARS = 3;

    /* TODO reconsider this.
     * There should never go too much in here, and it's so damned repetitive... Should something ever
     * implode though, and fill this up wholesale, the fact it will never go away won't exactly help.
     */
    private static final TIntObjectMap<String> CACHED_INDENTS = new TSynchronizedIntObjectMap<>(new TIntObjectHashMap<>());

    private static final String MARGIN = " ".repeat(INDENT_CHARS);
    private static final String INDENT_VERTICAL = "|" + " ".repeat(INDENT_CHARS - 1);
    private static final String INDENT_HORIZONTAL = "+" + "-".repeat(INDENT_CHARS - 1);
    protected final PermNode root;
    protected final int stackDepth;
    public HeapLayoutReporter(PermNode root, int stackDepth) {
        this.root = root;
        this.stackDepth = stackDepth;
    }

    private static <V> V computeIfAbsent(TIntObjectMap<V> map, int key, LongFunction<V> keyMapper) {
        V value = map.get(key);
        if (value == null) {
            value = keyMapper.apply(key);
            map.put(key, value);
        }
        return value;
    }

    public void toDrillDown(PrintWriter pw) {
        printHeadline(pw);
        NodeWithChildren.walk(root, 0, NodeWithChildren.UNLIMITED, 0, (n, d) -> printRow(n, d, pw), stackDepth);
    }

    protected String getIndentFor(int depth) {
        String indent = computeIfAbsent(CACHED_INDENTS, depth, d -> {
            String margin = depth > 0 ? MARGIN : "";
            String indentVertical = depth > 1 ? INDENT_VERTICAL.repeat(depth - 1) : "";
            String indentHorizontal = depth > 0 ? INDENT_HORIZONTAL : "";
            String combinedIndent = margin + indentVertical + indentHorizontal;
            return combinedIndent;
        });
        return indent;
    }

    protected abstract void printHeadline(PrintWriter pw);

    protected abstract void printRow(PermNode node, int depth, PrintWriter pw);
}

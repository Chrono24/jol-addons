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

import org.openjdk.jol.util.SimpleStack;

import javax.annotation.Nonnull;
import java.util.List;


public final class InitialNodeFactory {

    private final HistogramDeduplicator histogramDeduplicator;
    private final SimpleStack<InitialNode> recyclerForObjects;
    private final SimpleStack<InitialNode> recyclerForArrays;
    private final List<Object> tailCache;

    public InitialNodeFactory(HistogramDeduplicator histogramDeduplicator, int expectedStackDepth) {
        this.histogramDeduplicator = histogramDeduplicator;
        recyclerForObjects = new SimpleStack<>(expectedStackDepth);
        recyclerForArrays = new SimpleStack<>(Math.max(1 << 5, expectedStackDepth >>> 4));
        tailCache = new TwoItemArrayList<>();
    }

    InitialNode createArrayIndexNode(InitialNode parent, int idx, int depth, Object o) {
        return getNode(parent, "[i]", o, true);
    }

    InitialNode createFieldNode(InitialNode parent, String label, int depth, Object o) {
        return getNode(parent, label, o, false);
    }

    private SimpleStack<InitialNode> getRecyclerFor(InitialNode node) {
        return getRecyclerFor(node.isArrayInfo());
    }

    private SimpleStack<InitialNode> getRecyclerFor(boolean isArray) {
        return isArray ? recyclerForArrays : recyclerForObjects;
    }

    void recycleNode(InitialNode node) {
        getRecyclerFor(node).push(node.reset());
    }

    @Nonnull
    private InitialNode getNode(InitialNode parent, String label, Object o, boolean arrayIndexed) {
        ClassPath path = getPath(parent, o, label, arrayIndexed);

        return tryReuseNode(path, o, o.getClass().isArray());
    }

    private ClassPath getPath(InitialNode parent, Object o, String label, boolean arrayIndexed) {
        String mergedLabel = arrayIndexed || parent == null ? label : HistogramDeduplicator.getMergedField(parent.getObjectClass(), label);

        if (parent != null) {
            tailCache.set(0, mergedLabel);
            tailCache.set(1, o.getClass());
            return parent.getPath().computeIfAbsent(mergedLabel, o.getClass(), tailCache, histogramDeduplicator, arrayIndexed);
        } else {
            ClassPathImpl root = new ClassPathImpl(1);
            root.add(o.getClass());
            return root;
        }
    }

    private InitialNode tryReuseNode(ClassPath path, Object o, boolean isArray) {
        SimpleStack<InitialNode> recycler = getRecyclerFor(isArray);
        InitialNode node = recycler.isEmpty() ? isArray ? new InitialNodeForArray() : new InitialNode() : recycler.pop();

        node.setPath(path);
        node.setObject(o);

        return node;
    }

}

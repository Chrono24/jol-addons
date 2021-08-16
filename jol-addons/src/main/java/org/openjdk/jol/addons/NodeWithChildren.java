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

import org.apache.commons.lang3.tuple.Pair;
import org.openjdk.jol.util.SimpleStack;

import java.util.function.BiConsumer;


interface NodeWithChildren<N> {

    int UNLIMITED = -1;

    /**
     * Reverses traversal order of children in nodes due to LIFO properties of Stack.
     *
     * @param root               the tree to walk
     * @param minDepth           perform operations starting at this depth (inclusive)
     * @param maxDepth           walk tree only so many levels deep (inclusive)
     * @param depthOffset        offset to actual in-tree depth passed to operations
     * @param preOrder           operation performed pre-order
     * @param expectedStackDepth expected depth of the stack
     */
    static <N extends NodeWithChildren<N>> void walk(N root, int minDepth, int maxDepth, int depthOffset, BiConsumer<N, Integer> preOrder,
                                                     int expectedStackDepth) {

        final SimpleStack<Pair<Integer, N>> stack = new SimpleStack<>(expectedStackDepth);
        stack.push(Pair.of(0, root));

        while (!stack.isEmpty()) {
            final Pair<Integer, N> top = stack.pop();
            final int depth = top.getLeft();
            final N node = top.getRight();

            if (depth >= minDepth) {
                preOrder.accept(node, depth + depthOffset);
            }

            if (UNLIMITED == maxDepth || depth < maxDepth) {
                final N[] children = node.getChildren();
                if (children != null) {
                    for (N child : children) {
                        stack.push(Pair.of(depth + 1, child));
                    }
                }
            }
        }
    }

    N[] getChildren();
}

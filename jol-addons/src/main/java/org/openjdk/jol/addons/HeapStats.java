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

import org.openjdk.jol.info.ArraySizeCache;
import org.openjdk.jol.info.HeapWalker;
import org.openjdk.jol.info.VisitedIdentities;
import org.openjdk.jol.util.SimpleStack;


public class HeapStats implements HeapWalker.Stats {

    private long totalCount;
    private long totalSize;
    private int stackCapacity;
    private int identitySetCapacity;
    private int sizeCacheCapacity;

    public HeapStats(HeapStats original) {
        totalCount = original.totalCount();
        totalSize = original.totalSize();
        stackCapacity = original.stackCapacity();
        identitySetCapacity = original.identitySetCapacity();
        sizeCacheCapacity = original.sizeCacheCapacity();
    }

    public HeapStats(Object... roots) {
    }

    /**
     * Parse the object stats starting from the given instance(s).
     *
     * @param roots root instance(s) to start from
     * @return object stats
     */
    public static HeapStats parseSimpleInstance(Object... roots) {
        return new HeapWalker().getStats(HeapStats::new, roots);
    }

    /**
     * Parse the object stats starting from the given instance(s).
     *
     * @param tc                      TraversalControl employed to restrict heap parsing to an imaginary directed acyclic graph of interest
     * @param identitySet             prepared identity set
     * @param stackCapacity           pass the value from the previous run or a guesstimate to reduce incremental growth costs
     * @param objectSizeCacheCapacity pass the value from the previous run or a guesstimate to reduce incremental growth costs
     * @param roots                   root instance(s) to start from
     * @return object stats
     */
    public static HeapStats parseInstance(TraversalControl tc, VisitedIdentities identitySet, int stackCapacity, int objectSizeCacheCapacity, Object... roots) {

        final ObjectSizeCache objectSizeCache = new ObjectSizeCache.WithObject2LongMap(objectSizeCacheCapacity);
        final SimpleStack<Object> stack = new SimpleStack<>(stackCapacity);

        return parseInstance(tc, identitySet, objectSizeCache, stack, roots);
    }


    /**
     * Parse the object stats starting from the given instance(s).
     *
     * @param tc              TraversalControl employed to restrict heap parsing to an imaginary directed acyclic graph of interest
     * @param identitySet     prepared identity set
     * @param objectSizeCache prepared object size cache
     * @param stack           prepared node stack
     * @param roots           root instance(s) to start from
     * @return object graph
     */
    public static HeapStats parseInstance(TraversalControl tc,
          VisitedIdentities identitySet, org.openjdk.jol.info.ObjectSizeCache objectSizeCache,
          SimpleStack<Object> stack,
          Object... roots) {

        return new HeapWalker() //
              .withConditionalRecursion(tc::isChildToBeTraversed) //
              .withIdentitySet(identitySet) //
              .withObjectSizeCache(objectSizeCache) //
              .withArraySizeCache(new ArraySizeCache.Precalculated()) //
              .withStack(stack) //
              .getStats(HeapStats::new, roots);
    }


    public void addRecord(long size) {
        totalCount++;
        totalSize += size;
    }

    public int identitySetCapacity() {
        return identitySetCapacity;
    }

    public void setContainerCapacities(int stackCapacity, int identitySetCapacity, int sizeCacheCapacity) {
        this.stackCapacity = Math.max(this.stackCapacity, stackCapacity);
        this.identitySetCapacity = identitySetCapacity;
        this.sizeCacheCapacity = sizeCacheCapacity;
    }

    public int sizeCacheCapacity() {
        return sizeCacheCapacity;
    }

    public int stackCapacity() {
        return stackCapacity;
    }

    public long totalCount() {
        return totalCount;
    }

    public long totalSize() {
        return totalSize;
    }
}

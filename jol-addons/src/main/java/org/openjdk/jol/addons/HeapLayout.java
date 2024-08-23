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

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;

import static java.util.Comparator.comparingLong;


public class HeapLayout extends HeapStats {

    private final PermNode classHistogramRoot;
    private final int classHistogramDepth;

    private final PermNode heapTreeRoot;
    private final int heapTreeDepth;

    private final String description;


    HeapLayout(PermNode classHistogramRoot, int classHistogramDepth, String description, int heapTreeDepth, PermNode heapTreeRoot, HeapStats stats) {

        this.classHistogramRoot = classHistogramRoot;
        this.classHistogramDepth = classHistogramDepth;

        this.heapTreeRoot = heapTreeRoot;
        this.heapTreeDepth = heapTreeDepth;

        this.description = description;

        setContainerCapacities(stats.stackCapacity(), stats.identitySetCapacity(), stats.sizeCacheCapacity());
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    /**
     * Parse the object graph starting from the given instance(s).
     *
     * @param roots root instances to start from
     * @return object graph
     */
    public static HeapLayout parseSimpleInstance(Object... roots) {

        InitialNodeFactory nodeFactory = new InitialNodeFactory(HistogramDeduplicator.instance(), 0);
        HeapLayout.Builder builder = new HeapWalker().getTree(HeapLayout.Builder::new,
                nodeFactory::createFieldNode, nodeFactory::createArrayIndexNode, nodeFactory::recycleNode, roots);

        return builder.build();
    }

    /**
     * Parse the object graph starting from the given instance(s).
     *
     * @param tc                      TraversalControl employed to restrict heap parsing to an imaginary directed acyclic graph of interest
     * @param hd                      HistogramDeduplicator to make sure the output is a lot less redundant but instead far more expressive
     * @param identitySet             prepared identity set
     * @param stackCapacity           pass the value from the previous run or a guesstimate to reduce incremental growth costs
     * @param objectSizeCacheCapacity pass the value from the previous run or a guesstimate to reduce incremental growth costs
     * @param roots                   root instance(s) to start from
     * @return object graph
     */
    public static HeapLayout parseInstance(TraversalControl tc, HistogramDeduplicator hd,
                                           VisitedIdentities identitySet, int stackCapacity,
                                           int objectSizeCacheCapacity, Object... roots) {

        ObjectSizeCache objectSizeCache = new ObjectSizeCache.WithObject2LongMap(objectSizeCacheCapacity);
        SimpleStack<Object> stack = new SimpleStack<>(stackCapacity);
        InitialNodeFactory nodeFactory = new InitialNodeFactory(hd, stackCapacity);

        return parseInstance(tc, identitySet, objectSizeCache, stack, nodeFactory, roots);
    }

    /**
     * Parse the object graph starting from the given instance(s).
     *
     * @param tc              TraversalControl employed to restrict heap parsing to an imaginary directed acyclic graph of interest
     * @param identitySet     prepared identity set
     * @param objectSizeCache prepared object size cache
     * @param stack           prepared node stack
     * @param nodeFactory     prepared node factory
     * @param roots           root instance(s) to start from
     * @return object graph
     */
    public static HeapLayout parseInstance(TraversalControl tc,
                                           VisitedIdentities identitySet, ObjectSizeCache objectSizeCache,
                                           SimpleStack<Object> stack, InitialNodeFactory nodeFactory,
                                           Object... roots) {

        HeapLayout.Builder builder = new HeapWalker() //
                .withConditionalRecursion(tc::isChildToBeTraversed) //
                .withIdentitySet(identitySet) //
                .withObjectSizeCache(objectSizeCache) //
                .withArraySizeCache(new ArraySizeCache.Precalculated()) //
                .withStack(stack) //
                .getTree(HeapLayout.Builder::new, nodeFactory::createFieldNode, nodeFactory::createArrayIndexNode, nodeFactory::recycleNode, roots);

        return builder.build();
    }

    public HeapStats toStats() {
        return new HeapStats(this);
    }

    public void toClassHistogramDrillDown(PrintWriter pw) {
        classHistogram().toDrillDown(pw);
    }

    public void toFootprint(PrintWriter pw) {
        classHistogram().toFootprint(pw);
    }

    public void toHeapTreeDrillDown(PrintWriter pw) {
        heapTree().toDrillDown(pw);
    }

    @Override
    public long totalCount() {
        return heapTreeRoot.getTotalCount();
    }

    @Override
    public long totalSize() {
        return heapTreeRoot.getTotalSize();
    }

    private ClassHistogramReporter classHistogram() {
        return new ClassHistogramReporter(classHistogramRoot, classHistogramDepth, description);
    }

    private HeapTreeReporter heapTree() {
        return new HeapTreeReporter(heapTreeRoot, heapTreeDepth);
    }

    private static final class Builder implements HeapWalker.Graph<InitialNode> {

        private static final int SHORTCUT_INIT_CAP = 1 << 9;  // avoid resizing in most cases; TODO: render configurable

        private final String description;

        private final HeapStats stats = new HeapStats();

        private final DiyTrie<Object, ClassPath, BaseNode> classHistogramDrillDown = new DiyTrie<>();
        private final DiyTrie<Object, ClassPath, BaseNode> heapTreeDrillDown = new DiyTrie<>();

        // this greatly reduces number of hash lookups, and yields an opportunity to cache ClassPath hashCode,
        // unlike Trie lookup which always looks at the contents one symbol at a time anyway
        private final Map<ClassPath, BaseNode> classShortcut = new HashMap<>(SHORTCUT_INIT_CAP);
        private final Map<ClassPath, BaseNode> heapShortcut = new HashMap<>(SHORTCUT_INIT_CAP);

        private boolean isArrayInfo;

        public Builder(Object... roots) {

            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for (Object root : roots) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append(String.format("%s@%xd", root.getClass().getName(), System.identityHashCode(root)));
            }
            description = sb.toString();
        }

        @Override
        public void addNode(InitialNode node) {
            isArrayInfo = node.isArrayInfo();

            classShortcut.computeIfAbsent(node.getPath().getClassBasedOrder(), this::addToClassHistogramDrilldown).add(node);
            heapShortcut.computeIfAbsent(node.getPath().getTreeBasedOrder(), this::addToHeapTreeDrilldown).add(node);
        }

        private BaseNode addToClassHistogramDrilldown(ClassPath path) {
            return classHistogramDrillDown.computeIfAbsent(path, this::newGatheringNode);
        }

        private BaseNode addToHeapTreeDrilldown(ClassPath path) {
            return heapTreeDrillDown.computeIfAbsent(path, this::newGatheringNode);
        }

        private BaseNode newGatheringNode(ClassPath ignored) {
            return isArrayInfo ? new GatheringNodeForArray() : new GatheringNode();
        }

        @Override
        public void addRecord(long size) {
            stats.addRecord(size);
        }

        public HeapLayout build() {
            AtomicInteger classHistogramDepth = new AtomicInteger();
            PermNode classHistogramRoot = convertTrie(classHistogramDrillDown, true, false, this::getParentClassNameForClassHistogram, this::takeParentClassName,
                    classHistogramDepth);

            AtomicInteger heapTreeDepth = new AtomicInteger();
            PermNode heapTreeRoot = convertTrie(heapTreeDrillDown, true, true, this::getParentClassNameForHeapTree, this::takeLabel, heapTreeDepth);

            return new HeapLayout(classHistogramRoot, classHistogramDepth.get(), description, heapTreeDepth.get(), heapTreeRoot, stats);
        }

        @Override
        public void setContainerCapacities(int stackCapacity, int identitySetCapacity, int sizeCacheCapacity) {
            stats.setContainerCapacities(stackCapacity, identitySetCapacity, sizeCacheCapacity);
        }

        private PermNode convertTrie(DiyTrie<Object, ClassPath, BaseNode> trie, boolean mergeSingleFieldRowIntoClass, boolean aggregate,
                                     ParentClassNameRetrievalFunction getClassPath,
                                     BiFunction<String, String, String> choosePrefix, AtomicInteger maxStackDepth) {

            AtomicReference<PermNode> root = new AtomicReference<>();

            trie.iterator().withPreOrder(c -> {

            }).withPostOrder(nodeContext -> {

                DiyTrie.Node<Object, ClassPath, BaseNode> trieNode = nodeContext.getNode();

                final Object key = trieNode.getKeyElement();
                final boolean isRoot = key == null;
                final boolean isClass = key instanceof Class<?>;

                final ClassPath classPath = trieNode.getKey() == null ? null : trieNode.getKey().getOriginal();
                final boolean isTerminalSymbol = classPath != null && classPath.isTerminal();
                final String parentClassName = getClassPath.apply(nodeContext, isRoot, isClass, classPath);

                final String label = isRoot ? "(total)" : isClass ? ((Class<?>) key).getName() : (String) key;

                final BaseNode gatheringNode = trieNode.getValue();
                final boolean isEmptyRow = gatheringNode == null || gatheringNode.getCount() == 0;
                final boolean relabelChildRow = mergeSingleFieldRowIntoClass && nodeContext.getDepth() > 0 && isClass && isEmptyRow && trieNode.size() == 1;

                final PermNode permNode;
                if (relabelChildRow) {
                    permNode = trieNode.values().stream().map(DiyTrie.Node::getValue).map(PermNode.class::cast).findFirst().get();
                    permNode.setPrefix(choosePrefix.apply(label, parentClassName));
                } else {
                    permNode = createRegularPermNode(trieNode, gatheringNode, parentClassName, label, isRoot, isClass, isTerminalSymbol, isEmptyRow, aggregate);
                }

                trieNode.setValue(permNode);
                if (isRoot) {
                    root.set(permNode); // extract
                }

            }).forEachRemaining(c -> maxStackDepth.getAndUpdate(depth -> Math.max(depth, c.getDepth())));

            return root.get();
        }

        @Nonnull
        private PermNode createRegularPermNode(DiyTrie.Node<Object, ClassPath, BaseNode> trieNode, BaseNode gatheringNode, String parentClassName, String label,
                                               boolean isRoot, boolean isClass, boolean isTerminalSymbol, boolean isEmptyRow, boolean aggregate) {

            boolean isArray = gatheringNode != null && gatheringNode.isArrayInfo();
            PermNode permNode = isTerminalSymbol ? new Ellipsis(label) : isArray ? new PermNodeForArray(label) : new PermNode(label);

            if (isEmptyRow) {  // row has no own values, substitute aggregation of children instead
                trieNode.values().stream().map(DiyTrie.Node::getValue).forEach(permNode::add);
                if (isRoot) {
                    permNode.clearArrayInfo();
                }
            } else {
                permNode.add(gatheringNode);
            }

            if (!trieNode.isEmpty()) {
                permNode.setChildren(getChildren(trieNode, permNode, aggregate, isEmptyRow));
            }

            if (!isClass) {
                permNode.setParentClassName(parentClassName);
            }
            return permNode;
        }

        @Nonnull
        private PermNode[] getChildren(DiyTrie.Node<Object, ClassPath, BaseNode> trieNode, PermNode permNode, boolean aggregate, boolean emptyRow) {

            PermNode[] children = trieNode.values()
                    .stream()
                    .map(DiyTrie.Node::getValue)
                    .map(PermNode.class::cast)
                    .sorted(comparingLong(PermNode::getTotalSize)
                            .thenComparing(PermNode::getSize)
                            .thenComparing(PermNode::getCount)
                            .thenComparing(PermNode::getLabel))
                    .toArray(PermNode[]::new);

            if (aggregate) {
                ToLongFunction<PermNode> getSize;
                ToLongFunction<PermNode> getCount;
                if (emptyRow) {
                    getSize = PermNode::getRetainedChildSize;
                    getCount = PermNode::getRetainedChildCount;
                } else {
                    getSize = PermNode::getTotalSize;
                    getCount = PermNode::getTotalCount;
                }
                long childrenTotalSize = Arrays.stream(children).mapToLong(getSize).sum();
                permNode.setRetainedChildSize(childrenTotalSize);
                long childrenTotalCount = Arrays.stream(children).mapToLong(getCount).sum();
                permNode.setRetainedChildCount(childrenTotalCount);
            }

            return children;
        }

        @Nonnull
        private String getParentClassNameForClassHistogram(DiyTrie.NodeContext<DiyTrie.Node<Object, ClassPath, BaseNode>> nodeContext, boolean isRoot,
                                                           boolean isClass, ClassPath classPath) {

            if (nodeContext.getDepth() > 0) {
                Class<?> parentClass = (Class<?>) (nodeContext.getParent().getNode().getKeyElement() instanceof Class ?
                        nodeContext.getParent().getNode().getKeyElement() :
                        nodeContext.getParent().getParent().getNode().getKeyElement());
                return isClass ? parentClass.getName() : parentClass.isArray() ? "" : parentClass.getSimpleName();
            } else {
                return "";
            }
        }

        @Nonnull
        private String getParentClassNameForHeapTree(DiyTrie.NodeContext<DiyTrie.Node<Object, ClassPath, BaseNode>> nodeContext, boolean isRoot, boolean isClass,
                                                     ClassPath classPath) {

            if (!isRoot && !isClass) {
                Class<?> parentClass = (Class<?>) classPath.get(classPath.size() - 3);
                return parentClass.isArray() ? "" : parentClass.getSimpleName();
            } else {
                return "";
            }
        }

        private String takeLabel(String label, String parentClassName) {
            return label;
        }

        private String takeParentClassName(String label, String parentClassName) {
            return parentClassName;
        }


        @FunctionalInterface
        private interface ParentClassNameRetrievalFunction {
            String apply(DiyTrie.NodeContext<DiyTrie.Node<Object, ClassPath, BaseNode>> nodeContext, boolean isRoot, boolean isClass, ClassPath classPath);
        }

    }

}

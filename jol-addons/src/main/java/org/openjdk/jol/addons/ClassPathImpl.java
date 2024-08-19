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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;


public class ClassPathImpl extends ArrayList<Object> implements ClassPath {

    private static final Logger LOG = LoggerFactory.getLogger(ClassPathImpl.class);

    private static final long serialVersionUID = -249241882148716230L;
    private int hashCode;
    private boolean hashInit;
    private ClassPathImpl classBasedOrder;
    private ClassPathImpl treeBasedOrder;
    private ClassPathImpl parent;
    private Map<List<Object>, ClassPathImpl> children;
    private boolean terminal;
    private ClassPathImpl original;
    public ClassPathImpl(int initialCapacity) {
        super(initialCapacity);
    }

    public ClassPathImpl(@Nonnull Collection<?> c) {
        super(c);
    }

    private static void juxtapose(ClassPath reorder) {
        // Class / field / class ... is easier to deduplicate and to handle inversion from heap to class order.
        // We want stats about a class, then its composition split by class, then field.
        // So, the order is class, class, field, class, field in both cases; i.e., the first object remains in place, the remainder will be exchanged pairwise.
        assert (reorder.size() % 2 == 1); // size() is odd at all times
        List<Object> twiddle = reorder.subList(1, reorder.size());
        for (int i = 0, n = twiddle.size(); i < n; i += 2) {
            Object tmp = twiddle.get(i);
            twiddle.set(i, twiddle.get(i + 1));
            twiddle.set(i + 1, tmp);
        }
    }

    @Override
    public ClassPathImpl computeIfAbsent(String label, Class<?> clazz, HistogramDeduplicator hd, boolean arrayIndexed) {
        if (this.isTerminal()) {
            return this;
        }

        if (children == null) {
            children = new HashMap<>();
        }

        final List<Object> tail = List.of(label, clazz);
        return children.computeIfAbsent(tail, t -> {
            boolean terminal = hd.isTerminal(clazz);
            if (terminal) {
                LOG.debug("terminal symbol {}", clazz);
            }

            // trivial case: List / Map / whatever nodes pointing to instances of same class within ADT
            if (isMerged(label, clazz)) {
                LOG.debug("merging tail {}", tail);
                return this;
            }

            ClassPathImpl child = new ClassPathImpl(this.size() + t.size());
            this.forEach(child::add);
            t.forEach(child::add);

            if (!terminal && !arrayIndexed && this.size() > 3) {
                for (int end = child.size(), testLength = (end - 1) / 4 * 2; testLength > 0; testLength -= 2) {
                    final int split = end - testLength;
                    final int start = split - testLength;
                    if (child.get(split - 2).equals(label) && clazz.equals(child.get(split - 1))) {
                        List<Object> test1 = child.subList(start, split);
                        List<Object> test2 = child.subList(split, end);
                        if (test1.equals(test2)) {
                            ClassPathImpl parent = this;
                            do {
                                if (parent.size() == split) {
                                    LOG.debug("deduplicating {}", test2);
                                    return parent;
                                }
                                parent = parent.getParent();
                            }
                            while (parent != null);
                        }
                    }
                }
            }

            child.setParent(this);
            child.setTerminal(terminal);
            return child;
        });
    }

    @Override
    public ClassPathImpl getClassBasedOrder() {
        if (classBasedOrder == null) {
            classBasedOrder = new ClassPathImpl(Lists.reverse(this));
            juxtapose(classBasedOrder);
            classBasedOrder.setOriginal(this);
        }
        return classBasedOrder;
    }

    @Override
    public ClassPathImpl getOriginal() {
        return original;
    }

    public void setOriginal(ClassPathImpl original) {
        this.original = original;
    }

    public ClassPathImpl getParent() {
        return parent;
    }

    public void setParent(ClassPathImpl parent) {
        this.parent = parent;
    }

    @Override
    public ClassPathImpl getTreeBasedOrder() {
        if (treeBasedOrder == null) {
            treeBasedOrder = new ClassPathImpl(this);
            juxtapose(treeBasedOrder);
            treeBasedOrder.setOriginal(this);
        }
        return treeBasedOrder;
    }

    @Override
    public int hashCode() {
        // although the class is not strictly immutable, its use case does not suit itself to mutation
        // with Trie lookups working on the list contents anyway, this was not much use, but with the node lookup cache upfront, that's a different story
        if (!hashInit) {
            hashCode = super.hashCode();
            hashInit = true;
        }
        return hashCode;
    }

    @Override
    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    private boolean isMerged(String label, Class<?> clazz) {
        if (this.isEmpty()) {
            return false;
        }

        // Same class and member with suspicious name? Display on same level, please - don't build an infinite slide to the right
        // First and last in original ClassPath are always Class<?>
        if (this.get(this.size() - 1).equals(clazz)) {
            return HistogramDeduplicator.isFieldMerged(label);
        }

        return false;
    }
}

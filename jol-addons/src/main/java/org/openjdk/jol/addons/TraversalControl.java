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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.FileDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class TraversalControl {

    private static final Logger LOG = LoggerFactory.getLogger(TraversalControl.class);

    private static final TraversalControl INSTANCE = builder()
            .withIncludedAnnotations(List.of(IncludeInHeapTraversal.class))

            .withIncludedChildrenExactClasses(Collections.emptyList())
            .withIncludedChildrenInstanceOf(Collections.emptySet())

            .withExcludedParentsExactClasses(Set.of(Class.class, Field.class))
            .withExcludedParentsInstanceOf(List.of(Thread.class, EnumSet.class, ClassLoader.class))

            .withExcludedAnnotations(List.of(ExcludeFromHeapTraversal.class))

            .withExcludedChildrenExactClasses(Set.of(FileDescriptor.class))
            .withExcludedChildrenInstanceOf(List.of(FileChannel.class))

            .build(false);


    private final List<Class<? extends Annotation>> includedAnnotations;

    private final Set<Class<?>> includedChildrenExactClasses;
    private final List<Class<?>> includedChildrenInstanceOf;

    private final Set<Class<?>> excludedParentsExactClasses;
    private final List<Class<?>> excludedParentsInstanceOf;

    private final List<Class<? extends Annotation>> excludedAnnotations;

    private final Set<Class<?>> excludedChildrenExactClasses;
    private final List<Class<?>> excludedChildrenInstanceOf;

    private final Map<String, Set<String>> firstDescents;


    public TraversalControl(Collection<Class<? extends Annotation>> includedAnnotations,

                            Collection<Class<?>> includedChildrenExactClasses,
                            Collection<Class<?>> includedChildrenInstanceOf,

                            Collection<Class<?>> excludedParentsExactClasses,
                            Collection<Class<?>> excludedParentsInstanceOf,

                            Collection<Class<? extends Annotation>> excludedAnnotations,

                            Collection<Class<?>> excludedChildrenExactClasses,
                            Collection<Class<?>> excludedChildrenInstanceOf,

                            boolean takeNote) {

        this.includedAnnotations = List.copyOf(includedAnnotations);

        this.includedChildrenExactClasses = Set.copyOf(includedChildrenExactClasses);
        this.includedChildrenInstanceOf = List.copyOf(includedChildrenInstanceOf);

        this.excludedParentsExactClasses = Set.copyOf(excludedParentsExactClasses);
        this.excludedParentsInstanceOf = List.copyOf(excludedParentsInstanceOf);

        this.excludedAnnotations = List.copyOf(excludedAnnotations);

        this.excludedChildrenExactClasses = Set.copyOf(excludedChildrenExactClasses);
        this.excludedChildrenInstanceOf = List.copyOf(excludedChildrenInstanceOf);

        this.firstDescents = takeNote ? new HashMap<>() : null;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static TraversalControl instance() {
        return INSTANCE;
    }

    public boolean isChildToBeTraversed(@Nullable Object parent, @Nullable Field field, Object child) {
        takeNote(parent, child);

        if (isFieldIncluded(field)) {
            return true;
        }

        if (isChildIncluded(child.getClass())) {
            return true;
        }


        if (parent != null && isParentExcluded(parent.getClass())) {
            return false;
        }


        if (isFieldExcluded(field)) {
            return false;
        }

        if (isChildExcluded(child.getClass())) {
            return false;
        }

        return true;
    }

    private boolean isFieldExcluded(@Nullable Field field) {
        return isFieldAffectedByAnnotations(field, excludedAnnotations);
    }

    private boolean isFieldIncluded(@Nullable Field field) {
        return isFieldAffectedByAnnotations(field, includedAnnotations);
    }

    private boolean isChildIncluded(Class<?> cl) {
        return isAffected(cl, includedChildrenExactClasses, includedChildrenInstanceOf);
    }

    private boolean isChildExcluded(Class<?> cl) {
        return isAffected(cl, excludedChildrenExactClasses, excludedChildrenInstanceOf);
    }

    private boolean isAffected(Class<?> cl, Set<Class<?>> affectedExactClasses, List<Class<?>> affectedInstanceOf) {
        if (affectedExactClasses.contains(cl)) {
            return true;
        }
        for (int i = 0, n = affectedInstanceOf.size(); i < n; ++i) {
            if (affectedInstanceOf.get(i).isAssignableFrom(cl)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFieldAffectedByAnnotations(@Nullable Field field, List<Class<? extends Annotation>> affectedAnnotations) {
        if (field == null) {
            return false;
        }

        for (int i = 0, n = affectedAnnotations.size(); i < n; ++i) {
            if (field.isAnnotationPresent(affectedAnnotations.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean isParentExcluded(Class<?> cl) {
        return isAffected(cl, excludedParentsExactClasses, excludedParentsInstanceOf);
    }

    private void takeNote(Object parent, Object child) {
        if (firstDescents != null) {
            final String parentName = (parent == null ? Root.class : parent.getClass()).getName();
            final String childName = child.getClass().getName();
            if (firstDescents.computeIfAbsent(parentName, p -> ConcurrentHashMap.newKeySet()).add(childName)) {
                LOG.debug("{} -> {}", parentName, childName);
            }
        }
    }

    public static class Builder implements Cloneable {

        private List<Class<? extends Annotation>> includedAnnotations = Collections.emptyList();

        private Set<Class<?>> includedChildrenExactClasses = Collections.emptySet();
        private List<Class<?>> includedChildrenInstanceOf = Collections.emptyList();

        private Set<Class<?>> excludedParentsExactClasses = Collections.emptySet();
        private List<Class<?>> excludedParentsInstanceOf = Collections.emptyList();

        private List<Class<? extends Annotation>> excludedAnnotations = Collections.emptyList();

        private Set<Class<?>> excludedChildrenExactClasses = Collections.emptySet();
        private List<Class<?>> excludedChildrenInstanceOf = Collections.emptyList();


        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public TraversalControl build(boolean takeNote) {
            return new TraversalControl(includedAnnotations,
                    includedChildrenExactClasses, includedChildrenInstanceOf,
                    excludedParentsExactClasses, excludedParentsInstanceOf,
                    excludedAnnotations,
                    excludedChildrenExactClasses, excludedChildrenInstanceOf,
                    takeNote);
        }

        public Builder withIncludedAnnotations(Collection<Class<? extends Annotation>> annotations) {
            this.includedAnnotations = List.copyOf(annotations);
            return this;
        }

        public Builder withIncludedChildrenExactClasses(Collection<Class<?>> classes) {
            includedChildrenExactClasses = Set.copyOf(classes);
            return this;
        }

        public Builder withIncludedChildrenInstanceOf(Collection<Class<?>> classes) {
            includedChildrenInstanceOf = List.copyOf(classes);
            return this;
        }


        public Builder withExcludedAnnotations(Collection<Class<? extends Annotation>> annotations) {
            this.excludedAnnotations = List.copyOf(annotations);
            return this;
        }

        public Builder withExcludedChildrenExactClasses(Collection<Class<?>> classes) {
            excludedChildrenExactClasses = Set.copyOf(classes);
            return this;
        }

        public Builder withExcludedChildrenInstanceOf(Collection<Class<?>> classes) {
            excludedChildrenInstanceOf = List.copyOf(classes);
            return this;
        }

        public Builder withExcludedParentsExactClasses(Collection<Class<?>> classes) {
            excludedParentsExactClasses = Set.copyOf(classes);
            return this;
        }

        public Builder withExcludedParentsInstanceOf(Collection<Class<?>> classes) {
            excludedParentsInstanceOf = List.copyOf(classes);
            return this;
        }
    }


    /**
     * dummy class to use as key in HashMap
     */
    private static final class Root {
    }
}

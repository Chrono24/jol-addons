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

    private static final TraversalControl INSTANCE = builder().withParentBlacklistDirectClasses(Set.of(Class.class, Field.class))
            .withParentBlacklist(List.of(Thread.class, EnumSet.class, ClassLoader.class))
            .withAnnotations(List.of(Exclude.class))
            .withChildBlacklistDirectClasses(Set.of(FileDescriptor.class))
            .withChildBlacklist(List.of(FileChannel.class))
            .build(false);
    private final Set<Class<?>> parentBlacklistDirectClasses;
    private final List<Class<?>> parentBlacklist;
    private final List<Class<? extends Annotation>> annotations;
    private final Set<Class<?>> childBlacklistDirectClasses;
    private final List<Class<?>> childBlacklist;
    private final Map<String, Set<String>> firstDescents;
    public TraversalControl(Set<Class<?>> parentBlacklistDirectClasses, List<Class<?>> parentBlacklist, List<Class<? extends Annotation>> annotations,
                            Set<Class<?>> childBlacklistDirectClasses, List<Class<?>> childBlacklist, boolean takeNote) {

        this.parentBlacklistDirectClasses = parentBlacklistDirectClasses;
        this.parentBlacklist = parentBlacklist;
        this.annotations = annotations;
        this.childBlacklistDirectClasses = childBlacklistDirectClasses;
        this.childBlacklist = childBlacklist;

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

        if (parent != null && isParentBlacklisted(parent.getClass())) {
            return false;
        }
        // TODO introduce and handle annotations @ExcludeKeys / @ExcludeValues that allow for ADTs to be counted without contents

        if (isChildBlacklisted(child.getClass())) {
            return false;
        }

        if (isFieldExcludedByAnnotations(field)) {
            return false;
        }
        // TODO find setters with @Autowired annotation matching fields ;) Perhaps then the child blacklist on DAOs can disappear...

        return true;
    }

    private boolean isBlacklisted(Class<?> cl, Set<Class<?>> blackset, List<Class<?>> blacklist) {
        if (blackset.contains(cl)) {
            return true;
        }
        for (int i = 0, n = blacklist.size(); i < n; ++i) {
            if (blacklist.get(i).isAssignableFrom(cl)) {
                return true;
            }
        }
        return false;
    }

    private boolean isChildBlacklisted(Class<?> cl) {
        return isBlacklisted(cl, childBlacklistDirectClasses, childBlacklist);
    }

    private boolean isFieldExcludedByAnnotations(@Nullable Field field) {
        if (field == null) {
            return false;
        }

        for (int i = 0, n = annotations.size(); i < n; ++i) {
            if (field.isAnnotationPresent(annotations.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean isParentBlacklisted(Class<?> cl) {
        return isBlacklisted(cl, parentBlacklistDirectClasses, parentBlacklist);
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

    public static class Builder {

        private Set<Class<?>> parentBlacklistDirectClasses = Collections.emptySet();
        private List<Class<?>> parentBlacklist = Collections.emptyList();

        private List<Class<? extends Annotation>> annotations = Collections.emptyList();

        private Set<Class<?>> childBlacklistDirectClasses = Collections.emptySet();
        private List<Class<?>> childBlacklist = Collections.emptyList();

        public TraversalControl build(boolean takeNote) {
            return new TraversalControl(parentBlacklistDirectClasses, parentBlacklist, annotations, childBlacklistDirectClasses, childBlacklist, takeNote);
        }

        public Builder withAnnotations(List<Class<? extends Annotation>> annotations) {
            this.annotations = annotations;
            return this;
        }

        public Builder withChildBlacklist(List<Class<?>> classes) {
            childBlacklist = classes;
            return this;
        }

        public Builder withChildBlacklistDirectClasses(Set<Class<?>> classes) {
            childBlacklistDirectClasses = classes;
            return this;
        }

        public Builder withParentBlacklist(List<Class<?>> classes) {
            parentBlacklist = classes;
            return this;
        }

        public Builder withParentBlacklistDirectClasses(Set<Class<?>> classes) {
            parentBlacklistDirectClasses = classes;
            return this;
        }
    }


    /**
     * dummy class to use as key in HashMap
     */
    private static final class Root {
    }
}

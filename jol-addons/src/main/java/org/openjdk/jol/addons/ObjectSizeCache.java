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

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;

import java.util.function.ToLongFunction;


public interface ObjectSizeCache extends org.openjdk.jol.info.ObjectSizeCache {

    class WithObject2LongMap implements ObjectSizeCache {

        private final VirtualMachine           vm;
        private final Object2LongMap<Class<?>> map;

        public WithObject2LongMap(int capacity) {
            vm = VM.current();
            map = new Object2LongOpenHashMap<>(capacity);
        }

        @Override
        public long get(Class<?> cl, Object e) {
            long size = map.getLong(cl);
            if (size == map.defaultReturnValue()) {
                size = vm.sizeOf(e);
                map.put(cl, size);
            }
            return size;
        }

        @Override
        public int size() {
            return map.size();
        }
    }

    class WithTObjectLongMap implements ObjectSizeCache {

        private final VirtualMachine vm;
        private final TObjectLongMap<Class<?>> map;

        public WithTObjectLongMap(int capacity) {
            vm = VM.current();
            map = new TObjectLongHashMap<>(capacity);
        }

        @Override
        public long get(Class<?> cl, Object e) {
            return computeIfAbsent(map, cl, c -> vm.sizeOf(e));
        }

        @Override
        public int size() {
            return map.size();
        }

        private <K> long computeIfAbsent(TObjectLongMap<K> map, K key, ToLongFunction<K> keyMapper) {
            long value = map.get(key);
            if (value == 0) {
                value = keyMapper.applyAsLong(key);
                map.put(key, value);
            }
            return value;
        }
    }
}
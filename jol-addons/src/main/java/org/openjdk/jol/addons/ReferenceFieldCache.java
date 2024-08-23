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

import java.lang.reflect.Field;
import java.util.function.ToLongFunction;

public interface ReferenceFieldCache extends org.openjdk.jol.info.ReferenceFieldCache {

    class WithObject2LongMap implements ReferenceFieldCache {

        private final VirtualMachine vm;
        private final Object2LongMap<Field> map;

        public WithObject2LongMap(int capacity) {
            vm = VM.current();
            map = new Object2LongOpenHashMap<>(capacity);
        }

        @Override
        public Object get(Field f, Object e) {
            long offset = map.getLong(f);
            if (offset == map.defaultReturnValue()) {
                offset = vm.fieldOffset(f);
                map.put(f, offset);
            }
            return vm.getObject(e, offset);
        }

        @Override
        public int size() {
            return map.size();
        }
    }

    class WithTObjectLongMap implements ReferenceFieldCache {

        private final VirtualMachine vm;
        private final TObjectLongMap<Field> map;

        public WithTObjectLongMap(int capacity) {
            vm = VM.current();
            map = new TObjectLongHashMap<>(capacity);
        }

        @Override
        public Object get(Field f, Object e) {
            long offset = computeIfAbsent(map, f, c -> vm.fieldOffset(f));
            return vm.getObject(e, offset);
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

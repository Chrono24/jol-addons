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

import org.openjdk.jol.vm.VM;


abstract class BaseNode {

    protected static final int SIZE_SHIFT = 31 - Integer.numberOfLeadingZeros(VM.current().objectAlignment());
    private int _count;
    private int _size;
    private long _length;
    private long _used;

    public static double percent(long part, long whole) {
        return whole == 0 ? Double.POSITIVE_INFINITY : (double) part * 100.0 / (double) whole;
    }

    public void add(BaseNode other) {
        _count += other.getCount();
        _size += other._size;

        _length += other._length;
        _used += other._used;
    }

    public void clearArrayInfo() {
        _length = 0;
        _used = 0;
    }

    public int getAverage() {
        return _count == 0 ? 0 : (int) (getSize() / (long) getCount());
    }

    public int getCount() {
        return _count;
    }

    public long getLength() {
        return _length;
    }

    public void setLength(long length) {
        _length = length;
    }

    public long getSize() {
        return ((long) _size) << SIZE_SHIFT;
    }

    public void setSize(long size) {
        _size = (int) (size >> SIZE_SHIFT);
    }

    public double getUsePercentage() {
        return percent(_used, _length);
    }

    public long getUsed() {
        return _used;
    }

    public void setUsed(long used) {
        _used = used;
    }

    public boolean hasArrayInfo() {
        return _length > 0;
    }

    public BaseNode reset() {
        _count = 0;
        _size = 0;

        _length = 0;
        _used = 0;

        return this;
    }
}
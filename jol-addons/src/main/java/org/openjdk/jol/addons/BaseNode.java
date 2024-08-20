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

    private static final int SIZE_SHIFT = 31 - Integer.numberOfLeadingZeros(VM.current().objectAlignment());

    private static final long UINT_MASK = 0x00000000ffffffffL;

    public static long shiftOut(long size) {
        return size >>> SIZE_SHIFT;
    }

    public static long shiftIn(long size) {
        return size << SIZE_SHIFT;
    }

    public static void checkOverflow(long unsignedInteger) {
        if ( (unsignedInteger & UINT_MASK) != unsignedInteger ) {
            throw new ArithmeticException("unsigned integer overflow");
        }
    }

    public static double percent(long part, long whole) {
        return whole == 0 ? Double.POSITIVE_INFINITY : (double)part * 100.0 / (double)whole;
    }

    private int _count;
    private int _size;

    private long _length;
    private long _used;

    public void add(BaseNode other) {
        setCount(getCount() + other.getCount());
        setSize(getSize() + other.getSize());
        setLength(Math.addExact(getLength(), other.getLength()));
        setUsed(Math.addExact(getUsed(), other.getUsed()));
    }

    public void clearArrayInfo() {
        _length = 0;
        _used = 0;
    }

    public long getAverage() {
        return _count == 0 ? 0 : Long.divideUnsigned(getSize(), getCount());
    }

    public long getCount() {
        return Integer.toUnsignedLong(_count);
    }

    public long getLength() {
        return _length;
    }

    public long getSize() {
        return Integer.toUnsignedLong(_size) << SIZE_SHIFT;
    }

    public double getUsePercentage() {
        return percent(_used, _length);
    }

    public long getUsed() {
        return _used;
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

    public void setLength(long length) {
        _length = length;
    }

    public void setSize(long size) {
        long shifted = size >>> SIZE_SHIFT;
        checkOverflow(shifted);
        _size = (int) shifted;
    }

    public void setUsed(long used) {
        _used = used;
    }

    void setCount(long count) {
        checkOverflow(count);
        _count = (int) count;
    }
}
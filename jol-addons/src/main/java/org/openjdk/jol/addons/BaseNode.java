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

    public static void checkOverflow( long unsignedInteger ) {
        if ((unsignedInteger & UINT_MASK) != unsignedInteger) {
            throw new ArithmeticException("unsigned integer overflow");
        }
    }

    public static double percent( long part, long whole ) {
        if (part == 0) {
            return 0.0;
        }

        if (whole == 0) {
            return part >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }

        return (double)part * 100.0 / (double)whole;
    }

    private int _count;
    private int _size;

    protected void add( BaseNode other ) {
        setCount(getCount() + other.getCount());
        setSize(getSize() + other.getSize());
    }

    public void clearArrayInfo() {}

    public long getAverage() {
        return _count == 0 ? 0 : Long.divideUnsigned(getSize(), getCount());
    }

    public long getCount() {
        return Integer.toUnsignedLong(_count);
    }

    public long getLength() {
        return 0;
    }

    public long getSize() {
        return Integer.toUnsignedLong(_size) << SIZE_SHIFT;
    }

    public double getUsePercentage() {
        return 0;
    }

    public long getUsed() {
        return 0;
    }

    public boolean isArrayInfo() {
        return false;
    }

    public BaseNode reset() {
        _count = 0;
        _size = 0;

        clearArrayInfo();

        return this;
    }

    public void setLength( long length ) {
        throw new UnsupportedOperationException();
    }

    public void setSize( long size ) {
        long shifted = size >>> SIZE_SHIFT;
        checkOverflow(shifted);
        _size = (int)shifted;
    }

    public void setUsed( long used ) {
        throw new UnsupportedOperationException();
    }

    void setCount( long count ) {
        checkOverflow(count);
        _count = (int)count;
    }
}
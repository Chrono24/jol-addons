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

class InitialNodeForArray extends InitialNode {

    private long _length;
    private long _used;

    @Override
    protected void add(BaseNode other) {
        super.add(other);

        setLength(Math.addExact(this.getLength(), other.getLength()));
        setUsed(Math.addExact(this.getUsed(), other.getUsed()));
    }

    @Override
    public void clearArrayInfo() {
        _length = 0;
        _used = 0;
    }

    @Override
    public long getLength() {
        return _length;
    }

    @Override
    public void setLength(long length) {
        _length = length;
    }

    @Override
    public double getUsePercentage() {
        return percent(_used, _length);
    }

    @Override
    public long getUsed() {
        return _used;
    }

    @Override
    public void setUsed(long used) {
        _used = used;
    }

    @Override
    public boolean isArrayInfo() {
        return true;
    }
}

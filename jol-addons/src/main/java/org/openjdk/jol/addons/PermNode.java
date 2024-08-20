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

import com.google.common.base.Strings;

class PermNode extends BaseNode implements NodeWithChildren<PermNode> {

    private final String _label;

    private String _parentClassName;
    private String _prefix;

    private int _retainedChildSize;
    private int _retainedChildCount;

    private PermNode[] _children;
    private PermNode _parent;

    public PermNode(String label) {
        _label = label;
    }

    public String getArrayLabel() {
        if ( isArrayInfo()) {
            return String.format(" [%d of %d used (%.2f %%)]", getUsed(), getLength(), getUsePercentage());
        } else {
            return "";
        }
    }

    @Override
    public PermNode[] getChildren() {
        return _children;
    }

    public void setChildren(PermNode[] children) {
        _children = children;
        for (PermNode child : children) {
            child._parent = this;
        }
    }

    public String getLabel() {
        return Strings.isNullOrEmpty(_parentClassName) ? _label : _parentClassName + "." + _label;
    }

    public double getParentCountPercentage() {
        return _parent == null ? 100.0 : percent(getTotalCount(), _parent.getTotalCount());
    }

    public double getParentSizePercentage() {
        return _parent == null ? 100.0 : percent(getTotalSize(), _parent.getTotalSize());
    }

    public String getPrefix() {
        return _prefix;
    }

    public void setPrefix(String prefix) {
        _prefix = prefix;
    }

    public long getRetainedChildCount() {
        return Integer.toUnsignedLong(_retainedChildCount);
    }

    public void setRetainedChildCount(long retainedChildCount) {
        checkOverflow(retainedChildCount);
        _retainedChildCount = (int)retainedChildCount;
    }

    public long getRetainedChildSize() {
        return shiftIn(Integer.toUnsignedLong(_retainedChildSize));
    }

    public void setRetainedChildSize(long retainedChildSize) {
        long shifted = shiftOut(retainedChildSize);
        checkOverflow(shifted);
        _retainedChildSize = (int)shifted;
    }

    public double getSizePercentage(PermNode parent) {
        return percent(getTotalSize(), parent.getTotalSize());
    }

    public long getTotalCount() {
        return getCount() + getRetainedChildCount();
    }

    public long getTotalSize() {
        return getSize() + getRetainedChildSize();
    }

    public void setParentClassName(String parentClassName) {
        _parentClassName = parentClassName;
    }

}
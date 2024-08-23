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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TwoItemArrayList<E> extends ArrayList<E> {

    public TwoItemArrayList(List<? extends E> source) {
        super(source);
        if (source.size() != 2) {
            throw new IllegalArgumentException();
        }
    }

    public TwoItemArrayList() {
        super(2);
        add(null);
        add(null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TwoItemArrayList)) {
            return false;
        }

        //noinspection unchecked
        TwoItemArrayList<E> other = (TwoItemArrayList<E>) o;
        if (other.size() != 2) {
            return false;
        }

        return Objects.equals(this.get(0), other.get(0)) && Objects.equals(this.get(1), other.get(1));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

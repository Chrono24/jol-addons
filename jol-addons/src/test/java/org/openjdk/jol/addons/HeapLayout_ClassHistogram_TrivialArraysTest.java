/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.api.Test;


public class HeapLayout_ClassHistogram_TrivialArraysTest extends BaseHeapLayoutTest {

    @Test
    void testBooleanArray() {
        givenRoot(new boolean[0]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n" +
                "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--[Z\n");
    }

    @Test
    void testByteArray() {
        givenRoot(new byte[1]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       24 B           24       24 B              24   100.00 %   (total)\n" +
                "              1   100.00 %       24 B           24       24 B              24   100.00 %      +--[B\n");
    }

    @Test
    void testCharArray() {
        givenRoot(new char[2]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       24 B           24       24 B              24   100.00 %   (total)\n" +
                "              1   100.00 %       24 B           24       24 B              24   100.00 %      +--[C\n");
    }

    @Test
    void testShortArray() {
        givenRoot(new short[3]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       24 B           24       24 B              24   100.00 %   (total)\n" +
                "              1   100.00 %       24 B           24       24 B              24   100.00 %      +--[S\n");
    }

    @Test
    void testIntegerArray() {
        givenRoot(new int[4]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       32 B           32       32 B              32   100.00 %   (total)\n" +
                "              1   100.00 %       32 B           32       32 B              32   100.00 %      +--[I\n");
    }

    @Test
    void testLongArray() {
        givenRoot(new long[5]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       56 B           56       56 B              56   100.00 %   (total)\n" +
                "              1   100.00 %       56 B           56       56 B              56   100.00 %      +--[J\n");
    }

    @Test
    void testFloatArray() {
        givenRoot(new float[6]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       40 B           40       40 B              40   100.00 %   (total)\n" +
                "              1   100.00 %       40 B           40       40 B              40   100.00 %      +--[F\n");
    }

    @Test
    void testDoubleArray() {
        givenRoot(new double[7]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       72 B           72       72 B              72   100.00 %   (total)\n" +
                "              1   100.00 %       72 B           72       72 B              72   100.00 %      +--[D\n");
    }

    @Test
    void testObjectArray() {
        givenRoot(new Object[8]);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n" +
                "              1   100.00 %       48 B           48       48 B              48   100.00 %   (total)\n" +
                "              1   100.00 %       48 B           48       48 B              48   100.00 %      +--[Ljava.lang.Object;\n");
    }
}

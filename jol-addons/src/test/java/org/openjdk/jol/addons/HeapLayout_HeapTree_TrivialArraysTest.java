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


public class HeapLayout_HeapTree_TrivialArraysTest extends BaseHeapLayoutTest {

    @Test
    void testBooleanArray() {
        givenRoot(new boolean[0]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       16 B           16       16 B              16               1   100.00 %         16 B              16   100.00 %   (total)\n" +
                "              1       16 B           16       16 B              16               1   100.00 %         16 B              16   100.00 %      +--[Z [0 of 0 used (0.00 %)]\n");
    }

    @Test
    void testByteArray() {
        givenRoot(new byte[1]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       24 B           24       24 B              24               1   100.00 %         24 B              24   100.00 %   (total)\n" +
                "              1       24 B           24       24 B              24               1   100.00 %         24 B              24   100.00 %      +--[B [0 of 1 used (0.00 %)]\n");
    }

    @Test
    void testCharArray() {
        givenRoot(new char[2]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       24 B           24       24 B              24               1   100.00 %         24 B              24   100.00 %   (total)\n" +
                "              1       24 B           24       24 B              24               1   100.00 %         24 B              24   100.00 %      +--[C [0 of 2 used (0.00 %)]\n");
    }

    @Test
    void testShortArray() {
        givenRoot(new short[3]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       24 B           24       24 B              24               1   100.00 %         24 B              24   100.00 %   (total)\n" +
                "              1       24 B           24       24 B              24               1   100.00 %         24 B              24   100.00 %      +--[S [0 of 3 used (0.00 %)]\n");
    }

    @Test
    void testIntegerArray() {
        givenRoot(new int[4]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       32 B           32       32 B              32               1   100.00 %         32 B              32   100.00 %   (total)\n" +
                "              1       32 B           32       32 B              32               1   100.00 %         32 B              32   100.00 %      +--[I [0 of 4 used (0.00 %)]\n");
    }

    @Test
    void testLongArray() {
        givenRoot(new long[5]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       56 B           56       56 B              56               1   100.00 %         56 B              56   100.00 %   (total)\n" +
                "              1       56 B           56       56 B              56               1   100.00 %         56 B              56   100.00 %      +--[J [0 of 5 used (0.00 %)]\n");
    }

    @Test
    void testFloatArray() {
        givenRoot(new float[6]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       40 B           40       40 B              40               1   100.00 %         40 B              40   100.00 %   (total)\n" +
                "              1       40 B           40       40 B              40               1   100.00 %         40 B              40   100.00 %      +--[F [0 of 6 used (0.00 %)]\n");
    }

    @Test
    void testDoubleArray() {
        givenRoot(new double[7]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       72 B           72       72 B              72               1   100.00 %         72 B              72   100.00 %   (total)\n" +
                "              1       72 B           72       72 B              72               1   100.00 %         72 B              72   100.00 %      +--[D [0 of 7 used (0.00 %)]\n");
    }

    @Test
    void testObjectArray() {
        givenRoot(new Object[8]);
        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n" +
                "              1       48 B           48       48 B              48               1   100.00 %         48 B              48   100.00 %   (total)\n" +
                "              1       48 B           48       48 B              48               1   100.00 %         48 B              48   100.00 %      +--[Ljava.lang.Object; [0 of 8 used (0.00 %)]\n");
    }
}

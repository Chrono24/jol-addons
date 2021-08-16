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


public class HeapLayout_ClassHistogram_TrivialObjectsTest extends BaseHeapLayoutTest {

    @Test
    void testBoolean() {
        givenRoot(false);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--java.lang.Boolean\n");
    }

    @Test
    void testByte() {
        givenRoot((byte) 0);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--java.lang.Byte\n");
    }

    @Test
    void testDouble() {
        givenRoot(0.0);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       24 B           24       24 B              24   100.00 %   (total)\n"
                        + "              1   100.00 %       24 B           24       24 B              24   100.00 %      +--java.lang.Double\n");
    }

    @Test
    void testFloat() {
        givenRoot(0.0f);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--java.lang.Float\n");
    }

    @Test
    void testInteger() {
        givenRoot(0);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--java.lang.Integer\n");
    }

    @Test
    void testLong() {
        givenRoot(0L);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       24 B           24       24 B              24   100.00 %   (total)\n"
                        + "              1   100.00 %       24 B           24       24 B              24   100.00 %      +--java.lang.Long\n");
    }

    @Test
    void testObject() {
        givenRoot(new Object());
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--java.lang.Object\n");
    }

    @Test
    void testShort() {
        givenRoot((short) 0);
        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %   (total)\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      +--java.lang.Short\n");
    }
}

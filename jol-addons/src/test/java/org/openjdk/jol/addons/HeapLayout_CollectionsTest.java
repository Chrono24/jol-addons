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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class HeapLayout_CollectionsTest extends BaseHeapLayoutTest {

    /*
     * Footprint, Heap Tree and Class Histogram are kept in one spot for better cross-reference.
     */

    static final Map<String, String> _templateMap = Map.of("a", "A", "b", "B", "c", "C", "d", "D", "e", "E");
    static final Set<String> _templateSet = Set.of("a", "A", "b", "B", "c", "C", "d", "D", "e", "E");
    static final List<String> _templateList = List.of("a", "A", "b", "B", "c", "C", "d", "D", "e", "E");

    @Test
    void arrayList() {
        givenRoot(new ArrayList(_templateList));

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              22   100.00 %           --      560 B             560   100.00 %   (total)\n"
                        + "              10    45.45 %           24      240 B             240    42.86 %   java.lang.String\n"
                        + "              10    45.45 %           24      240 B             240    42.86 %   [B\n"
                        + "               1     4.55 %           56       56 B              56    10.00 %   [Ljava.lang.Object;\n"
                        + "               1     4.55 %           24       24 B              24     4.29 %   java.util.ArrayList\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       24 B           24       24 B              24              22   100.00 %        560 B             560   100.00 %   (total)\n"
                        + "              1       24 B           24       24 B              24              22   100.00 %        560 B             560   100.00 %      +--java.util.ArrayList\n"
                        + "              1       56 B           56       56 B              56              21    95.45 %        536 B             536    95.71 %      |  +--[Ljava.lang.Object; ArrayList.elementData [10 of 10 used (100.00 %)]\n"
                        + "             10       24 B           24      240 B             240              20    95.24 %        480 B             480    89.55 %      |  |  +--java.lang.String [i]\n"
                        + "             10       24 B           24      240 B             240              10    50.00 %        240 B             240    50.00 %      |  |  |  +--[B String.value [10 of 10 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             22   100.00 %       25 B           25      560 B             560   100.00 %   (total)\n"
                        + "             10    45.45 %       24 B           24      240 B             240    42.86 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--[Ljava.lang.Object; ArrayList.elementData\n"
                        + "             10    45.45 %       24 B           24      240 B             240    42.86 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--[Ljava.lang.Object; ArrayList.elementData\n"
                        + "              1     4.55 %       56 B           56       56 B              56    10.00 %      +--[Ljava.lang.Object;\n"
                        + "              1   100.00 %       56 B           56       56 B              56   100.00 %      |  +--[Ljava.lang.Object; ArrayList.elementData\n"
                        + "              1     4.55 %       24 B           24       24 B              24     4.29 %      +--java.util.ArrayList\n");
    }

    @Test
    void concurrentHashMap() {
        givenRoot(new ConcurrentHashMap<>(_templateMap));

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              27   100.00 %           --      784 B             784   100.00 %   (total)\n"
                        + "              10    37.04 %           24      240 B             240    30.61 %   java.lang.String\n"
                        + "              10    37.04 %           24      240 B             240    30.61 %   [B\n"
                        + "               5    18.52 %           32      160 B             160    20.41 %   java.util.concurrent.ConcurrentHashMap$Node\n"
                        + "               1     3.70 %           80       80 B              80    10.20 %   [Ljava.util.concurrent.ConcurrentHashMap$Node;\n"
                        + "               1     3.70 %           64       64 B              64     8.16 %   java.util.concurrent.ConcurrentHashMap\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       64 B           64       64 B              64              27   100.00 %        784 B             784   100.00 %   (total)\n"
                        + "              1       64 B           64       64 B              64              27   100.00 %        784 B             784   100.00 %      +--java.util.concurrent.ConcurrentHashMap\n"
                        + "              1       80 B           80       80 B              80              26    96.30 %        720 B             720    91.84 %      |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table [5 of 16 used (31.25 %)]\n"
                        + "              5       32 B           32      160 B             160              25    96.15 %        640 B             640    88.89 %      |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "             10       24 B           24      240 B             240              20    80.00 %        480 B             480    75.00 %      |  |  |  +--java.lang.String\n"
                        + "              5       24 B           24      120 B             120              10    50.00 %        240 B             240    50.00 %      |  |  |  |  +--Node.val\n"
                        + "              5       24 B           24      120 B             120               5    50.00 %        120 B             120    50.00 %      |  |  |  |  |  +--[B String.value [5 of 5 used (100.00 %)]\n"
                        + "              5       24 B           24      120 B             120              10    50.00 %        240 B             240    50.00 %      |  |  |  |  +--Node.key\n"
                        + "              5       24 B           24      120 B             120               5    50.00 %        120 B             120    50.00 %      |  |  |  |  |  +--[B String.value [5 of 5 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             27   100.00 %       29 B           29      784 B             784   100.00 %   (total)\n"
                        + "             10    37.04 %       24 B           24      240 B             240    30.61 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.util.concurrent.ConcurrentHashMap$Node\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Node.val\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Node.key\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "             10    37.04 %       24 B           24      240 B             240    30.61 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.util.concurrent.ConcurrentHashMap$Node\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Node.val\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Node.key\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "              5    18.52 %       32 B           32      160 B             160    20.41 %      +--java.util.concurrent.ConcurrentHashMap$Node\n"
                        + "              5   100.00 %       32 B           32      160 B             160   100.00 %      |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "              5   100.00 %       32 B           32      160 B             160   100.00 %      |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "              1     3.70 %       80 B           80       80 B              80    10.20 %      +--[Ljava.util.concurrent.ConcurrentHashMap$Node;\n"
                        + "              1   100.00 %       80 B           80       80 B              80   100.00 %      |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "              1     3.70 %       64 B           64       64 B              64     8.16 %      +--java.util.concurrent.ConcurrentHashMap\n");
    }

    @Test
    void concurrentHashMapKeySetView() {
        ConcurrentHashMap.KeySetView<Object, Boolean> set = ConcurrentHashMap.newKeySet();
        set.addAll(_templateSet);
        givenRoot(set);

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              34   100.00 %           --      984 B             984   100.00 %   (total)\n"
                        + "              10    29.41 %           32      320 B             320    32.52 %   java.util.concurrent.ConcurrentHashMap$Node\n"
                        + "              10    29.41 %           24      240 B             240    24.39 %   java.lang.String\n"
                        + "              10    29.41 %           24      240 B             240    24.39 %   [B\n"
                        + "               1     2.94 %           80       80 B              80     8.13 %   [Ljava.util.concurrent.ConcurrentHashMap$Node;\n"
                        + "               1     2.94 %           64       64 B              64     6.50 %   java.util.concurrent.ConcurrentHashMap\n"
                        + "               1     2.94 %           24       24 B              24     2.44 %   java.util.concurrent.ConcurrentHashMap$KeySetView\n"
                        + "               1     2.94 %           16       16 B              16     1.63 %   java.lang.Boolean\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       24 B           24       24 B              24              34   100.00 %        984 B             984   100.00 %   (total)\n"
                        + "              1       24 B           24       24 B              24              34   100.00 %        984 B             984   100.00 %      +--java.util.concurrent.ConcurrentHashMap$KeySetView\n"
                        + "              1       64 B           64       64 B              64              32    94.12 %        944 B             944    95.93 %      |  +--java.util.concurrent.ConcurrentHashMap KeySetView.map\n"
                        + "              1       80 B           80       80 B              80              31    96.88 %        880 B             880    93.22 %      |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table [5 of 16 used (31.25 %)]\n"
                        + "             10       32 B           32      320 B             320              30    96.77 %        800 B             800    90.91 %      |  |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "             10       24 B           24      240 B             240              20    66.67 %        480 B             480    60.00 %      |  |  |  |  +--java.lang.String Node.key\n"
                        + "             10       24 B           24      240 B             240              10    50.00 %        240 B             240    50.00 %      |  |  |  |  |  +--[B String.value [10 of 10 used (100.00 %)]\n"
                        + "              1       16 B           16       16 B              16               1     2.94 %         16 B              16     1.63 %      |  +--java.lang.Boolean KeySetView.value\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             34   100.00 %       28 B           28      984 B             984   100.00 %   (total)\n"
                        + "             10    29.41 %       32 B           32      320 B             320    32.52 %      +--java.util.concurrent.ConcurrentHashMap$Node\n"
                        + "             10   100.00 %       32 B           32      320 B             320   100.00 %      |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "             10   100.00 %       32 B           32      320 B             320   100.00 %      |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "             10   100.00 %       32 B           32      320 B             320   100.00 %      |  |  |  +--java.util.concurrent.ConcurrentHashMap KeySetView.map\n"
                        + "             10    29.41 %       24 B           24      240 B             240    24.39 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.lang.String Node.key\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  |  +--java.util.concurrent.ConcurrentHashMap KeySetView.map\n"
                        + "             10    29.41 %       24 B           24      240 B             240    24.39 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.lang.String Node.key\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--java.util.concurrent.ConcurrentHashMap$Node [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  |  |  +--java.util.concurrent.ConcurrentHashMap KeySetView.map\n"
                        + "              1     2.94 %       80 B           80       80 B              80     8.13 %      +--[Ljava.util.concurrent.ConcurrentHashMap$Node;\n"
                        + "              1   100.00 %       80 B           80       80 B              80   100.00 %      |  +--[Ljava.util.concurrent.ConcurrentHashMap$Node; ConcurrentHashMap.table\n"
                        + "              1   100.00 %       80 B           80       80 B              80   100.00 %      |  |  +--java.util.concurrent.ConcurrentHashMap KeySetView.map\n"
                        + "              1     2.94 %       64 B           64       64 B              64     6.50 %      +--java.util.concurrent.ConcurrentHashMap\n"
                        + "              1   100.00 %       64 B           64       64 B              64   100.00 %      |  +--java.util.concurrent.ConcurrentHashMap KeySetView.map\n"
                        + "              1     2.94 %       24 B           24       24 B              24     2.44 %      +--java.util.concurrent.ConcurrentHashMap$KeySetView\n"
                        + "              1     2.94 %       16 B           16       16 B              16     1.63 %      +--java.lang.Boolean\n"
                        + "              1   100.00 %       16 B           16       16 B              16   100.00 %      |  +--java.lang.Boolean KeySetView.value\n");
    }

    @Test
    void hashMap() {
        givenRoot(new HashMap<>(_templateMap));

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              27   100.00 %           --      736 B             736   100.00 %   (total)\n"
                        + "              10    37.04 %           24      240 B             240    32.61 %   java.lang.String\n"
                        + "              10    37.04 %           24      240 B             240    32.61 %   [B\n"
                        + "               5    18.52 %           32      160 B             160    21.74 %   java.util.HashMap$Node\n"
                        + "               1     3.70 %           48       48 B              48     6.52 %   java.util.HashMap\n"
                        + "               1     3.70 %           48       48 B              48     6.52 %   [Ljava.util.HashMap$Node;\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       48 B           48       48 B              48              27   100.00 %        736 B             736   100.00 %   (total)\n"
                        + "              1       48 B           48       48 B              48              27   100.00 %        736 B             736   100.00 %      +--java.util.HashMap\n"
                        + "              1       48 B           48       48 B              48              26    96.30 %        688 B             688    93.48 %      |  +--[Ljava.util.HashMap$Node; HashMap.table [5 of 8 used (62.50 %)]\n"
                        + "              5       32 B           32      160 B             160              25    96.15 %        640 B             640    93.02 %      |  |  +--java.util.HashMap$Node [i]\n"
                        + "             10       24 B           24      240 B             240              20    80.00 %        480 B             480    75.00 %      |  |  |  +--java.lang.String\n"
                        + "              5       24 B           24      120 B             120              10    50.00 %        240 B             240    50.00 %      |  |  |  |  +--Node.value\n"
                        + "              5       24 B           24      120 B             120               5    50.00 %        120 B             120    50.00 %      |  |  |  |  |  +--[B String.value [5 of 5 used (100.00 %)]\n"
                        + "              5       24 B           24      120 B             120              10    50.00 %        240 B             240    50.00 %      |  |  |  |  +--Node.key\n"
                        + "              5       24 B           24      120 B             120               5    50.00 %        120 B             120    50.00 %      |  |  |  |  |  +--[B String.value [5 of 5 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             27   100.00 %       27 B           27      736 B             736   100.00 %   (total)\n"
                        + "             10    37.04 %       24 B           24      240 B             240    32.61 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.util.HashMap$Node\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Node.value\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  +--java.util.HashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--[Ljava.util.HashMap$Node; HashMap.table\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Node.key\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  +--java.util.HashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--[Ljava.util.HashMap$Node; HashMap.table\n"
                        + "             10    37.04 %       24 B           24      240 B             240    32.61 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.util.HashMap$Node\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Node.value\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--java.util.HashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  |  +--[Ljava.util.HashMap$Node; HashMap.table\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Node.key\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--java.util.HashMap$Node [i]\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  |  +--[Ljava.util.HashMap$Node; HashMap.table\n"
                        + "              5    18.52 %       32 B           32      160 B             160    21.74 %      +--java.util.HashMap$Node\n"
                        + "              5   100.00 %       32 B           32      160 B             160   100.00 %      |  +--java.util.HashMap$Node [i]\n"
                        + "              5   100.00 %       32 B           32      160 B             160   100.00 %      |  |  +--[Ljava.util.HashMap$Node; HashMap.table\n"
                        + "              1     3.70 %       48 B           48       48 B              48     6.52 %      +--java.util.HashMap\n"
                        + "              1     3.70 %       48 B           48       48 B              48     6.52 %      +--[Ljava.util.HashMap$Node;\n"
                        + "              1   100.00 %       48 B           48       48 B              48   100.00 %      |  +--[Ljava.util.HashMap$Node; HashMap.table\n");
    }

    @Test
    void immutableCollectionsList() {
        givenRoot(_templateList);

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              22   100.00 %           --      552 B             552   100.00 %   (total)\n"
                        + "              10    45.45 %           24      240 B             240    43.48 %   java.lang.String\n"
                        + "              10    45.45 %           24      240 B             240    43.48 %   [B\n"
                        + "               1     4.55 %           56       56 B              56    10.14 %   [Ljava.lang.Object;\n"
                        + "               1     4.55 %           16       16 B              16     2.90 %   java.util.ImmutableCollections$ListN\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       16 B           16       16 B              16              22   100.00 %        552 B             552   100.00 %   (total)\n"
                        + "              1       16 B           16       16 B              16              22   100.00 %        552 B             552   100.00 %      +--java.util.ImmutableCollections$ListN\n"
                        + "              1       56 B           56       56 B              56              21    95.45 %        536 B             536    97.10 %      |  +--[Ljava.lang.Object; ListN.elements [10 of 10 used (100.00 %)]\n"
                        + "             10       24 B           24      240 B             240              20    95.24 %        480 B             480    89.55 %      |  |  +--java.lang.String [i]\n"
                        + "             10       24 B           24      240 B             240              10    50.00 %        240 B             240    50.00 %      |  |  |  +--[B String.value [10 of 10 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             22   100.00 %       25 B           25      552 B             552   100.00 %   (total)\n"
                        + "             10    45.45 %       24 B           24      240 B             240    43.48 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--[Ljava.lang.Object; ListN.elements\n"
                        + "             10    45.45 %       24 B           24      240 B             240    43.48 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--[Ljava.lang.Object; ListN.elements\n"
                        + "              1     4.55 %       56 B           56       56 B              56    10.14 %      +--[Ljava.lang.Object;\n"
                        + "              1   100.00 %       56 B           56       56 B              56   100.00 %      |  +--[Ljava.lang.Object; ListN.elements\n"
                        + "              1     4.55 %       16 B           16       16 B              16     2.90 %      +--java.util.ImmutableCollections$ListN\n");
    }

    @Test
    void immutableCollectionsMap() {
        givenRoot(_templateMap);

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              22   100.00 %           --      608 B             608   100.00 %   (total)\n"
                        + "              10    45.45 %           24      240 B             240    39.47 %   java.lang.String\n"
                        + "              10    45.45 %           24      240 B             240    39.47 %   [B\n"
                        + "               1     4.55 %           96       96 B              96    15.79 %   [Ljava.lang.Object;\n"
                        + "               1     4.55 %           32       32 B              32     5.26 %   java.util.ImmutableCollections$MapN\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       32 B           32       32 B              32              22   100.00 %        608 B             608   100.00 %   (total)\n"
                        + "              1       32 B           32       32 B              32              22   100.00 %        608 B             608   100.00 %      +--java.util.ImmutableCollections$MapN\n"
                        + "              1       96 B           96       96 B              96              21    95.45 %        576 B             576    94.74 %      |  +--[Ljava.lang.Object; MapN.table [10 of 20 used (50.00 %)]\n"
                        + "             10       24 B           24      240 B             240              20    95.24 %        480 B             480    83.33 %      |  |  +--java.lang.String [i]\n"
                        + "             10       24 B           24      240 B             240              10    50.00 %        240 B             240    50.00 %      |  |  |  +--[B String.value [10 of 10 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             22   100.00 %       27 B           27      608 B             608   100.00 %   (total)\n"
                        + "             10    45.45 %       24 B           24      240 B             240    39.47 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--[Ljava.lang.Object; MapN.table\n"
                        + "             10    45.45 %       24 B           24      240 B             240    39.47 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--[Ljava.lang.Object; MapN.table\n"
                        + "              1     4.55 %       96 B           96       96 B              96    15.79 %      +--[Ljava.lang.Object;\n"
                        + "              1   100.00 %       96 B           96       96 B              96   100.00 %      |  +--[Ljava.lang.Object; MapN.table\n"
                        + "              1     4.55 %       32 B           32       32 B              32     5.26 %      +--java.util.ImmutableCollections$MapN\n");
    }

    @Test
    void immutableCollectionsSet() {
        givenRoot(_templateSet);

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              22   100.00 %           --      600 B             600   100.00 %   (total)\n"
                        + "              10    45.45 %           24      240 B             240    40.00 %   java.lang.String\n"
                        + "              10    45.45 %           24      240 B             240    40.00 %   [B\n"
                        + "               1     4.55 %           96       96 B              96    16.00 %   [Ljava.lang.Object;\n"
                        + "               1     4.55 %           24       24 B              24     4.00 %   java.util.ImmutableCollections$SetN\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       24 B           24       24 B              24              22   100.00 %        600 B             600   100.00 %   (total)\n"
                        + "              1       24 B           24       24 B              24              22   100.00 %        600 B             600   100.00 %      +--java.util.ImmutableCollections$SetN\n"
                        + "              1       96 B           96       96 B              96              21    95.45 %        576 B             576    96.00 %      |  +--[Ljava.lang.Object; SetN.elements [10 of 20 used (50.00 %)]\n"
                        + "             10       24 B           24      240 B             240              20    95.24 %        480 B             480    83.33 %      |  |  +--java.lang.String [i]\n"
                        + "             10       24 B           24      240 B             240              10    50.00 %        240 B             240    50.00 %      |  |  |  +--[B String.value [10 of 10 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             22   100.00 %       27 B           27      600 B             600   100.00 %   (total)\n"
                        + "             10    45.45 %       24 B           24      240 B             240    40.00 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--[Ljava.lang.Object; SetN.elements\n"
                        + "             10    45.45 %       24 B           24      240 B             240    40.00 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.lang.String [i]\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--[Ljava.lang.Object; SetN.elements\n"
                        + "              1     4.55 %       96 B           96       96 B              96    16.00 %      +--[Ljava.lang.Object;\n"
                        + "              1   100.00 %       96 B           96       96 B              96   100.00 %      |  +--[Ljava.lang.Object; SetN.elements\n"
                        + "              1     4.55 %       24 B           24       24 B              24     4.00 %      +--java.util.ImmutableCollections$SetN\n");
    }

    @Test
    void linkedHashMap() {
        givenRoot(new LinkedHashMap<>(_templateMap));

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              27   100.00 %           --      784 B             784   100.00 %   (total)\n"
                        + "              10    37.04 %           24      240 B             240    30.61 %   java.lang.String\n"
                        + "              10    37.04 %           24      240 B             240    30.61 %   [B\n"
                        + "               5    18.52 %           40      200 B             200    25.51 %   java.util.LinkedHashMap$Entry\n"
                        + "               1     3.70 %           56       56 B              56     7.14 %   java.util.LinkedHashMap\n"
                        + "               1     3.70 %           48       48 B              48     6.12 %   [Ljava.util.HashMap$Node;\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       56 B           56       56 B              56              27   100.00 %        784 B             784   100.00 %   (total)\n"
                        + "              1       56 B           56       56 B              56              27   100.00 %        784 B             784   100.00 %      +--java.util.LinkedHashMap\n"
                        + "              1       48 B           48       48 B              48              16    59.26 %        456 B             456    58.16 %      |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table [5 of 8 used (62.50 %)]\n"
                        + "              3       40 B           40      120 B             120              15    93.75 %        408 B             408    89.47 %      |  |  +--java.util.LinkedHashMap$Entry [i]\n"
                        + "              6       24 B           24      144 B             144              12    80.00 %        288 B             288    70.59 %      |  |  |  +--java.lang.String\n"
                        + "              3       24 B           24       72 B              72               6    50.00 %        144 B             144    50.00 %      |  |  |  |  +--Entry.value\n"
                        + "              3       24 B           24       72 B              72               3    50.00 %         72 B              72    50.00 %      |  |  |  |  |  +--[B String.value [3 of 3 used (100.00 %)]\n"
                        + "              3       24 B           24       72 B              72               6    50.00 %        144 B             144    50.00 %      |  |  |  |  +--Entry.key\n"
                        + "              3       24 B           24       72 B              72               3    50.00 %         72 B              72    50.00 %      |  |  |  |  |  +--[B String.value [3 of 3 used (100.00 %)]\n"
                        + "              2       40 B           40       80 B              80              10    37.04 %        272 B             272    34.69 %      |  +--java.util.LinkedHashMap$Entry LinkedHashMap.head/tail\n"
                        + "              4       24 B           24       96 B              96               8    80.00 %        192 B             192    70.59 %      |  |  +--java.lang.String\n"
                        + "              2       24 B           24       48 B              48               4    50.00 %         96 B              96    50.00 %      |  |  |  +--Entry.value\n"
                        + "              2       24 B           24       48 B              48               2    50.00 %         48 B              48    50.00 %      |  |  |  |  +--[B String.value [2 of 2 used (100.00 %)]\n"
                        + "              2       24 B           24       48 B              48               4    50.00 %         96 B              96    50.00 %      |  |  |  +--Entry.key\n"
                        + "              2       24 B           24       48 B              48               2    50.00 %         48 B              48    50.00 %      |  |  |  |  +--[B String.value [2 of 2 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             27   100.00 %       29 B           29      784 B             784   100.00 %   (total)\n"
                        + "             10    37.04 %       24 B           24      240 B             240    30.61 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.util.LinkedHashMap$Entry\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Entry.value\n"
                        + "              3    60.00 %       24 B           24       72 B              72    60.00 %      |  |  |  +--java.util.LinkedHashMap$Entry [i]\n"
                        + "              3   100.00 %       24 B           24       72 B              72   100.00 %      |  |  |  |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table\n"
                        + "              2    40.00 %       24 B           24       48 B              48    40.00 %      |  |  |  +--java.util.LinkedHashMap$Entry LinkedHashMap.head/tail\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Entry.key\n"
                        + "              3    60.00 %       24 B           24       72 B              72    60.00 %      |  |  |  +--java.util.LinkedHashMap$Entry [i]\n"
                        + "              3   100.00 %       24 B           24       72 B              72   100.00 %      |  |  |  |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table\n"
                        + "              2    40.00 %       24 B           24       48 B              48    40.00 %      |  |  |  +--java.util.LinkedHashMap$Entry LinkedHashMap.head/tail\n"
                        + "             10    37.04 %       24 B           24      240 B             240    30.61 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.util.LinkedHashMap$Entry\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Entry.value\n"
                        + "              3    60.00 %       24 B           24       72 B              72    60.00 %      |  |  |  |  +--java.util.LinkedHashMap$Entry [i]\n"
                        + "              3   100.00 %       24 B           24       72 B              72   100.00 %      |  |  |  |  |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table\n"
                        + "              2    40.00 %       24 B           24       48 B              48    40.00 %      |  |  |  |  +--java.util.LinkedHashMap$Entry LinkedHashMap.head/tail\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Entry.key\n"
                        + "              3    60.00 %       24 B           24       72 B              72    60.00 %      |  |  |  |  +--java.util.LinkedHashMap$Entry [i]\n"
                        + "              3   100.00 %       24 B           24       72 B              72   100.00 %      |  |  |  |  |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table\n"
                        + "              2    40.00 %       24 B           24       48 B              48    40.00 %      |  |  |  |  +--java.util.LinkedHashMap$Entry LinkedHashMap.head/tail\n"
                        + "              5    18.52 %       40 B           40      200 B             200    25.51 %      +--java.util.LinkedHashMap$Entry\n"
                        + "              3    60.00 %       40 B           40      120 B             120    60.00 %      |  +--java.util.LinkedHashMap$Entry [i]\n"
                        + "              3   100.00 %       40 B           40      120 B             120   100.00 %      |  |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table\n"
                        + "              2    40.00 %       40 B           40       80 B              80    40.00 %      |  +--java.util.LinkedHashMap$Entry LinkedHashMap.head/tail\n"
                        + "              1     3.70 %       56 B           56       56 B              56     7.14 %      +--java.util.LinkedHashMap\n"
                        + "              1     3.70 %       48 B           48       48 B              48     6.12 %      +--[Ljava.util.HashMap$Node;\n"
                        + "              1   100.00 %       48 B           48       48 B              48   100.00 %      |  +--[Ljava.util.HashMap$Node; LinkedHashMap.table\n");
    }

    @Test
    void linkedList() {
        givenRoot(new LinkedList(_templateList));

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              31   100.00 %           --      752 B             752   100.00 %   (total)\n"
                        + "              10    32.26 %           24      240 B             240    31.91 %   java.util.LinkedList$Node\n"
                        + "              10    32.26 %           24      240 B             240    31.91 %   java.lang.String\n"
                        + "              10    32.26 %           24      240 B             240    31.91 %   [B\n"
                        + "               1     3.23 %           32       32 B              32     4.26 %   java.util.LinkedList\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       32 B           32       32 B              32              31   100.00 %        752 B             752   100.00 %   (total)\n"
                        + "              1       32 B           32       32 B              32              31   100.00 %        752 B             752   100.00 %      +--java.util.LinkedList\n"
                        + "             10       24 B           24      240 B             240              30    96.77 %        720 B             720    95.74 %      |  +--java.util.LinkedList$Node LinkedList.first/last\n"
                        + "             10       24 B           24      240 B             240              20    66.67 %        480 B             480    66.67 %      |  |  +--java.lang.String Node.item\n"
                        + "             10       24 B           24      240 B             240              10    50.00 %        240 B             240    50.00 %      |  |  |  +--[B String.value [10 of 10 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             31   100.00 %       24 B           24      752 B             752   100.00 %   (total)\n"
                        + "             10    32.26 %       24 B           24      240 B             240    31.91 %      +--java.util.LinkedList$Node\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.util.LinkedList$Node LinkedList.first/last\n"
                        + "             10    32.26 %       24 B           24      240 B             240    31.91 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.lang.String Node.item\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.util.LinkedList$Node LinkedList.first/last\n"
                        + "             10    32.26 %       24 B           24      240 B             240    31.91 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.lang.String Node.item\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  |  +--java.util.LinkedList$Node LinkedList.first/last\n"
                        + "              1     3.23 %       32 B           32       32 B              32     4.26 %      +--java.util.LinkedList\n");
    }

    @Test
    void treeMap() {
        givenRoot(new TreeMap<>(_templateMap));

        thenFootprintIs( //
                "           COUNT    % COUNT       AVG SZ        SUM         RAW SUM      % SUM   DESCRIPTION\n"
                        + "              26   100.00 %           --      728 B             728   100.00 %   (total)\n"
                        + "              10    38.46 %           24      240 B             240    32.97 %   java.lang.String\n"
                        + "              10    38.46 %           24      240 B             240    32.97 %   [B\n"
                        + "               5    19.23 %           40      200 B             200    27.47 %   java.util.TreeMap$Entry\n"
                        + "               1     3.85 %           48       48 B              48     6.59 %   java.util.TreeMap\n");

        thenHeapTreeIs( //
                "          COUNT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ     RETAINED CT  PAR% R CT  RETAINED SZ        RAW R SZ  PAR% R SZ   DESCRIPTION\n"
                        + "              1       48 B           48       48 B              48              26   100.00 %        728 B             728   100.00 %   (total)\n"
                        + "              1       48 B           48       48 B              48              26   100.00 %        728 B             728   100.00 %      +--java.util.TreeMap\n"
                        + "              5       40 B           40      200 B             200              25    96.15 %        680 B             680    93.41 %      |  +--java.util.TreeMap$Entry TreeMap.root\n"
                        + "             10       24 B           24      240 B             240              20    80.00 %        480 B             480    70.59 %      |  |  +--java.lang.String\n"
                        + "              5       24 B           24      120 B             120              10    50.00 %        240 B             240    50.00 %      |  |  |  +--Entry.value\n"
                        + "              5       24 B           24      120 B             120               5    50.00 %        120 B             120    50.00 %      |  |  |  |  +--[B String.value [5 of 5 used (100.00 %)]\n"
                        + "              5       24 B           24      120 B             120              10    50.00 %        240 B             240    50.00 %      |  |  |  +--Entry.key\n"
                        + "              5       24 B           24      120 B             120               5    50.00 %        120 B             120    50.00 %      |  |  |  |  +--[B String.value [5 of 5 used (100.00 %)]\n");

        thenClassHistogramIs( //
                "          COUNT    PAR% CT   AVG SIZE   RAW AVG SZ TOTAL SIZE        RAW T SZ  PAR% T SZ   DESCRIPTION\n"
                        + "             26   100.00 %       28 B           28      728 B             728   100.00 %   (total)\n"
                        + "             10    38.46 %       24 B           24      240 B             240    32.97 %      +--java.lang.String\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--java.util.TreeMap$Entry\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Entry.value\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  +--java.util.TreeMap$Entry TreeMap.root\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  +--Entry.key\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  +--java.util.TreeMap$Entry TreeMap.root\n"
                        + "             10    38.46 %       24 B           24      240 B             240    32.97 %      +--[B\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  +--[B String.value\n"
                        + "             10   100.00 %       24 B           24      240 B             240   100.00 %      |  |  +--java.util.TreeMap$Entry\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Entry.value\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--java.util.TreeMap$Entry TreeMap.root\n"
                        + "              5    50.00 %       24 B           24      120 B             120    50.00 %      |  |  |  +--Entry.key\n"
                        + "              5   100.00 %       24 B           24      120 B             120   100.00 %      |  |  |  |  +--java.util.TreeMap$Entry TreeMap.root\n"
                        + "              5    19.23 %       40 B           40      200 B             200    27.47 %      +--java.util.TreeMap$Entry\n"
                        + "              5   100.00 %       40 B           40      200 B             200   100.00 %      |  +--java.util.TreeMap$Entry TreeMap.root\n"
                        + "              1     3.85 %       48 B           48       48 B              48     6.59 %      +--java.util.TreeMap\n");
    }
}
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

import java.io.PrintWriter;

import static org.openjdk.jol.addons.HeapLayout.humanReadableByteCountBin;


final class ClassHistogramReporter extends HeapLayoutReporter {

    private final String description;

    public ClassHistogramReporter(PermNode root, int stackDepth, String description) {
        super(root, stackDepth);
        this.description = description;
    }

    /**
     * Get the stringly representation of footprint table
     *
     * @param pw the PrintWriter used for output
     */
    public void toFootprint(PrintWriter pw) {
        pw.println(description + " footprint:");
        pw.printf(" %15s %10s %12s %10s %15s %10s   %s%n", //
                "COUNT", "% COUNT", "AVG SZ", "SUM", "RAW SUM", "% SUM", "DESCRIPTION");

        pw.printf(" %,15d %8.2f %% %12s %10s %,15d %8.2f %%   %s%n", //
                root.getTotalCount(), root.getParentCountPercentage(), "--", humanReadableByteCountBin(root.getSize()), root.getSize(),
                root.getParentSizePercentage(), "(total)");

        NodeWithChildren.walk(root, 1, 1, -1, (node, depth) -> {
            String label = Strings.isNullOrEmpty(node.getPrefix()) ? node.getLabel() : node.getPrefix();
            pw.printf(" %,15d %8.2f %% %,12d %10s %,15d %8.2f %%   %s%n", //
                    node.getCount(), node.getParentCountPercentage(), node.getAverage(), humanReadableByteCountBin(node.getSize()), node.getSize(),
                    node.getParentSizePercentage(), label);
        }, stackDepth);
    }

    @Override
    protected void printHeadline(PrintWriter pw) {
        pw.printf("%15s %10s %10s %12s %10s %15s %10s   %s%n", //
                "COUNT", "PAR% CT", "AVG SIZE", "RAW AVG SZ", "TOTAL SIZE", "RAW T SZ", "PAR% T SZ", "DESCRIPTION");
    }

    @Override
    protected void printRow(PermNode node, int depth, PrintWriter pw) {
        if (Strings.isNullOrEmpty(node.getPrefix())) {
            pw.printf("%,15d %8.2f %% %10s %,12d %10s %,15d %8.2f %%   %s%s%n", //
                    node.getCount(), node.getParentCountPercentage(), humanReadableByteCountBin(node.getAverage()), node.getAverage(),
                    humanReadableByteCountBin(node.getSize()), node.getSize(), node.getParentSizePercentage(), getIndentFor(depth), node.getLabel());
        } else {
            pw.printf("%,15d %8.2f %% %10s %,12d %10s %,15d %8.2f %%   %s%s %s%n", //
                    node.getCount(), node.getParentCountPercentage(), humanReadableByteCountBin(node.getAverage()), node.getAverage(),
                    humanReadableByteCountBin(node.getSize()), node.getSize(), node.getParentSizePercentage(), getIndentFor(depth), node.getPrefix(),
                    node.getLabel());
        }
    }
}
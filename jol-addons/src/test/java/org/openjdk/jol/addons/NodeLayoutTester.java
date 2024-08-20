package org.openjdk.jol.addons;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.datamodel.Model64_COOPS_CCPS;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.layouters.HotSpotLayouter;

import java.io.PrintWriter;


public class NodeLayoutTester {

    @Test
    void run() {
        HotSpotLayouter layouter = new HotSpotLayouter(new Model64_COOPS_CCPS(16), 11);
        try {
            PrintWriter pw = new PrintWriter(System.out);

            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.BaseNode"), layouter).toPrintable(pw);
            pw.println();

            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.GatheringNode"), layouter).toPrintable(pw);
            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.GatheringNodeForArray"), layouter).toPrintable(pw);
            pw.println();

            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.InitialNode"), layouter).toPrintable(pw);
            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.InitialNodeForArray"), layouter).toPrintable(pw);
            pw.println();

            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.PermNode"), layouter).toPrintable(pw);
            pw.println();
            ClassLayout.parseClass(Class.forName("org.openjdk.jol.addons.PermNodeForArray"), layouter).toPrintable(pw);
            pw.println();

            pw.flush();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

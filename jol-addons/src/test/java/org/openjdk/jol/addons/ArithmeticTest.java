package org.openjdk.jol.addons;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openjdk.jol.addons.BaseNode.shiftIn;

import org.junit.jupiter.api.Test;


public class ArithmeticTest {

    private static final long MAX_UINT = (long)Integer.MAX_VALUE * 2 + 1;

    @Test
    void overflowDetectionSucceeds() {
        BaseNode.checkOverflow(MAX_UINT);
    }

    @Test
    void overflowDetectionThrows() {
        assertThrows(ArithmeticException.class, () -> BaseNode.checkOverflow(MAX_UINT + 1));
    }

    @Test
    void countWithOverflowThrows() {
        assertThrows(ArithmeticException.class, () -> new BaseNodeForTest().setCount(MAX_UINT + 1));
    }

    @Test
    void sizeWithOverflowThrows() {
        assertThrows(ArithmeticException.class, () -> new BaseNodeForTest().setSize(shiftIn((MAX_UINT + 1))));
    }

    @Test
    void retainedChildSizeWithOverflowThrows() {
        assertThrows(ArithmeticException.class, () -> new PermNode("test").setRetainedChildSize(shiftIn((MAX_UINT + 1))));
    }

    private static class BaseNodeForTest extends BaseNode {}

}

package org.openjdk.jol.addons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArrayNodeTest {

    @Test
    void BaseNodeThrowsOnArrayInfoSetters() {
        assertThrows(UnsupportedOperationException.class, () -> new TestNode().setLength(0L));
        assertThrows(UnsupportedOperationException.class, () -> new TestNode().setUsed(0L));
    }

    @Test
    void InitialNodeThrowsOnArrayInfoSetters() {
        assertThrows(UnsupportedOperationException.class, () -> new InitialNode().setLength(0L));
        assertThrows(UnsupportedOperationException.class, () -> new InitialNode().setUsed(0L));
    }

    @Test
    void GatheringNodeThrowsOnArrayInfoSetters() {
        assertThrows(UnsupportedOperationException.class, () -> new GatheringNode().setLength(0L));
        assertThrows(UnsupportedOperationException.class, () -> new GatheringNode().setUsed(0L));
    }

    @Test
    void PermNodeThrowsOnArrayInfoSetters() {
        assertThrows(UnsupportedOperationException.class, () -> new PermNode("test").setLength(0L));
        assertThrows(UnsupportedOperationException.class, () -> new PermNode("test").setUsed(0L));
    }

    @Test
    void BaseNodeSucceedsOnArrayInfoGetters() {
        new TestNode().getLength();
        new TestNode().getUsed();
    }

    @SuppressWarnings("WriteOnlyObject")
    @Test
    void InitialNodeForArraySucceedsOnArrayInfoSetters() {
        new InitialNodeForArray().setLength(0L);
        new InitialNodeForArray().setUsed(0L);
    }

    @SuppressWarnings("WriteOnlyObject")
    @Test
    void GatheringNodeForArraySucceedsOnArrayInfoSetters() {
        new GatheringNodeForArray().setLength(0L);
        new GatheringNodeForArray().setUsed(0L);
    }

    @SuppressWarnings("WriteOnlyObject")
    @Test
    void PermNodeForArraySucceedsOnArrayInfoSetters() {
        new PermNodeForArray("test").setLength(0L);
        new PermNodeForArray("test").setUsed(0L);
    }

    private static final class TestNode extends BaseNode {
    }

}

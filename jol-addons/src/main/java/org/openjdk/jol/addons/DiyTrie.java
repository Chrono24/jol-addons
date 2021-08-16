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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


final class DiyTrie<E, K extends Collection<E>, V> {

    private final Node<E, K, V> _root = new Node<>(null);

    public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
        final Node<E, K, V> node = computeIfAbsent(key);
        return node.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Unused, untested; simply jotted down
     */
    @Nullable
    public Node<E, K, V> get(K key) {
        Node<E, K, V> node = _root;
        for (E keyElement : key) {
            node = node.get(keyElement);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    TreeIterator iterator() {
        return new TreeIterator();
    }

    private Node<E, K, V> computeIfAbsent(K key) {
        Node<E, K, V> node = _root;
        for (E keyElement : key) {
            node = node.computeIfAbsent(keyElement);
        }
        return node;
    }

    public static final class NodeContext<N> {

        private final int _depth;
        private final NodeContext<N> _parent;
        private final N _node;

        public NodeContext(int depth, NodeContext<N> parent, N node) {
            _depth = depth;
            _parent = parent;
            _node = node;
        }

        public int getDepth() {
            return _depth;
        }

        public N getNode() {
            return _node;
        }

        @Nullable
        public NodeContext<N> getParent() {
            return _parent;
        }
    }


    static final class Node<E, K extends Collection<E>, V> {

        private final E _keyElement;
        private Map<E, Node<E, K, V>> _next;
        private K _key;
        private V _value;

        private Node(E keyElement) {
            _keyElement = keyElement;
        }

        public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
            if (_key == null && _value == null) {
                _key = key;
                _value = mappingFunction.apply(key);
            }
            return _value;
        }

        public Node<E, K, V> computeIfAbsent(E keyElement) {
            if (_next == null) {
                _next = new HashMap<>(1);
            }
            return _next.computeIfAbsent(keyElement, Node::new);
        }

        public Set<Map.Entry<E, Node<E, K, V>>> entrySet() {
            return _next == null ? Collections.emptySet() : _next.entrySet();
        }

        /**
         * Unused, untested; simply jotted down
         */
        @Nullable
        public Node<E, K, V> get(E keyElement) {
            if (_next == null) {
                return null;
            }
            return _next.get(keyElement);
        }

        @Nullable
        public K getKey() {
            return _key;
        }

        public E getKeyElement() {
            return _keyElement;
        }

        @Nullable
        public V getValue() {
            return _value;
        }

        public void setValue(V value) {
            _value = value;
        }

        public boolean isEmpty() {
            return _next == null;
        }

        public Iterator<? extends Node<E, K, V>> iterator() {
            return (_next != null ? _next : Collections.<E, Node<E, K, V>>emptyMap()).values().iterator();
        }

        public int size() {
            return _next == null ? 0 : _next.size();
        }

        public Collection<Node<E, K, V>> values() {
            return _next == null ? Collections.emptyList() : _next.values();
        }
    }


    private static final class Segment<N> {

        private final Iterator<? extends N> _currentIterator;
        private final NodeContext<N> _nodeContext;

        public Segment(Iterator<? extends N> currentIterator, NodeContext<N> nodeContext) {
            _currentIterator = currentIterator;
            _nodeContext = nodeContext;
        }

        public Iterator<? extends N> getCurrentIterator() {
            return _currentIterator;
        }

        public NodeContext<N> getNodeContext() {
            return _nodeContext;
        }
    }


    /**
     * In-order iteration with optional pre- and post-order operations.
     */
    public final class TreeIterator implements Iterator<NodeContext<Node<E, K, V>>> {

        private final Stack<Segment<Node<E, K, V>>> _stack = new Stack<>();

        private Consumer<NodeContext<Node<E, K, V>>> _preOrderOperation;
        private Consumer<NodeContext<Node<E, K, V>>> _postOrderOperation;

        public TreeIterator() {
            push(_root, null); // root is never part of the payload, so we don't yield its context
        }

        public boolean hasNext() {
            while (!_stack.isEmpty() && !_stack.peek().getCurrentIterator().hasNext()) {
                pop();
            }
            return !_stack.isEmpty() && _stack.peek().getCurrentIterator().hasNext();
        }

        @Override
        public NodeContext<Node<E, K, V>> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            final Segment<Node<E, K, V>> segment = _stack.peek();
            final NodeContext<Node<E, K, V>> parentContext = segment.getNodeContext();
            final Node<E, K, V> parent = parentContext.getNode();
            final Node<E, K, V> current = segment.getCurrentIterator().next();

            return push(current, parentContext);
        }

        public NodeContext<Node<E, K, V>> push(Node<E, K, V> current, NodeContext<Node<E, K, V>> parentContext) {
            final NodeContext<Node<E, K, V>> context = new NodeContext<>(getDepth(), parentContext, current);
            if (_preOrderOperation != null) {
                _preOrderOperation.accept(context);
            }
            _stack.push(new Segment<>(current.iterator(), context));
            return context;
        }

        public TreeIterator withPostOrder(Consumer<NodeContext<Node<E, K, V>>> operation) {
            _postOrderOperation = operation;
            return this;
        }

        public TreeIterator withPreOrder(Consumer<NodeContext<Node<E, K, V>>> operation) {
            _preOrderOperation = operation;
            return this;
        }

        private int getDepth() {
            return _stack.size() - 1; // subtract root
        }

        private void pop() {
            final Segment<Node<E, K, V>> segment = _stack.pop();
            final NodeContext<Node<E, K, V>> context = segment.getNodeContext();
            if (_postOrderOperation != null) {
                _postOrderOperation.accept(context);
            }
        }
    }
}

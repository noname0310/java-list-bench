package list;

import java.util.NoSuchElementException;

/**
 * Linked Link that has next-link between nodes.<br/>
 * Loosely optimized for readability.<br/>
 * @param <E> Container type
 */
public final class LinkedList<E> implements List<E> {
    /**
     * <code>LinkedList</code> node object pool for GC optimization.<br/>
     * @param <E> Container type
     */
    public interface NodePool<E> {
        /**
         * return instance of <code>Node</code>.<br/>
         * @param value return instance's value member will be initialized this value
         * @param next return instance's next member will be initialized this value
         * @return rented instance from this pool
         */
        Node<E> rent(E value, Node<E> next);

        /**
         * dispose given node.<br/>
         * @param node node for dispose to this <code>NodePool</code>
         */
        void dispose(Node<E> node);
    }

    /**
     * <code>LinkedList</code> node object pool implementation that use freelist.<br/>
     * @param <E> Container type
     */
    public static class FreeListNodePool<E> implements NodePool<E> {
        private Node<E> freeList;

        @Override
        public Node<E> rent(E value, Node<E> next) {
            if (this.freeList == null) {
                return new Node<>(value, next);
            }

            Node<E> instance = this.freeList;
            this.freeList = this.freeList.next;

            return instance;
        }

        @Override
        public void dispose(Node<E> node) {
            node.value = null;
            node.next = this.freeList;
            this.freeList = node;
        }
    }

    /**
     * <code>LinkedList</code> node object pool implementation that use only constructor.<br/>
     * @param <E> Container type
     */
    public static class CtorNodePool<E> implements NodePool<E> {
        @Override
        public Node<E> rent(E value, Node<E> next) {
            return new Node<>(value, next);
        }

        @Override
        public void dispose(Node<E> node) {
            /* do noting */
        }
    }

    private static final class Node<E> {
        public E value;
        public Node<E> next;

        public Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    private final NodePool<E> nodePool;
    private int length;
    private final Node<E> head;
    private Node<E> tail;

    /**
     * Create <code>LinkedList</code>.<br/>
     * @param nodePool nodePool for instancing nodes
     */
    public LinkedList(NodePool<E> nodePool) {
        this.nodePool = nodePool;
        length = 0;
        this.head = this.nodePool.rent(null, null);
        this.tail = this.head;
    }

    /**
     * Create <code>LinkedList</code> initialized with <code>CtorNodePool</code>.<br/>
     */
    public LinkedList() {
        this(new CtorNodePool<>());
    }

    private void insertBackFromNode(Node<E> insertPos, E item) {
        Node<E> newNode = this.nodePool.rent(item, insertPos.next);
        insertPos.next = newNode;
        if (insertPos == this.tail) {
            this.tail = newNode;
        }

        this.length += 1;
    }

    private Node<E> getNodeFromIndex(int index) {
        if (index < 0 || this.length <= index) {
            throw new IndexOutOfBoundsException();
        }

        Node<E> node = this.head;
        int iterationCount = index + 1;

        for (int i = 0; i < iterationCount; ++i) {
            node = node.next;
        }

        return node;
    }

    /**
     * Remove all items in this container.<br/>
     * Time complexity: O(1).<br/>
     */
    @Override
    public void clear() {
        this.head.next = null;
        this.tail = this.head;
        this.length = 0;
    }

    /**
     * Insert given item to this container.<br/>
     * Time complexity: O(n) (The further away from head, more slower.).<br/>
     * @param pos Non-negative integer for insert position
     * @param item Item for insert
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public void insert(int pos, E item) {
        Node<E> insertPos = pos == 0
                ? this.head
                : getNodeFromIndex(pos - 1);
        insertBackFromNode(insertPos, item);
    }

    /**
     * Append given item to this container's back.<br/>
     * Time complexity: O(1).<br/>
     * @param item Item to append
     */
    @Override
    public void append(E item) {
        insertBackFromNode(this.tail, item);
    }

    /**
     * Update data in a <code>pos</code> to a given value.<br/>
     * Time complexity: O(n) (The further away from head, more slower.).<br/>
     * @param pos Non-negative integer for update item position
     * @param item New item for update
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public void update(int pos, E item) {
        Node<E> node = getNodeFromIndex(pos);
        node.value = item;
    }

    /**
     * Get value in <code>pos</code>.<br/>
     * Time complexity: O(n) (The further away from head, more slower.).<br/>
     * @param pos Non-negative integer for item position
     * @return Value
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public E getValue(int pos) {
        return getNodeFromIndex(pos).value;
    }

    /**
     * Remove value in <code>pos</code>.<br/>
     * Time complexity: O(n) (The further away from head, more slower.).<br/>
     * @param pos Non-negative integer for item position
     * @return Removed value
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public E remove(int pos) {
        if (this.length == 0) throw new IndexOutOfBoundsException();
        if (this.length <= pos) throw new IndexOutOfBoundsException();
        Node<E> prevNode = pos == 0
            ? this.head
            : getNodeFromIndex(pos - 1);
        Node<E> removeNode = prevNode.next;
        prevNode.next = prevNode.next.next;

        if (removeNode == this.tail) {
            this.tail = prevNode;
        }

        E value = removeNode.value;
        this.nodePool.dispose(removeNode);

        this.length -= 1;

        return value;
    }

    /**
     * Get length of this container.<br/>
     * @return Length of this container
     */
    @Override
    public int length() {
        return this.length;
    }

    /**
     * Create list iterator of this container.<br/>
     * Backward iteration not supported.<br/>
     * @return <code>ListIterator</code> initialized by list head position
     */
    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<>() {
            private Node<E> current = head;

            @Override
            public boolean hasNext() {
                return this.current.next != null;
            }

            @Override
            public E next() {
                if (this.current.next == null) {
                    throw new NoSuchElementException();
                }
                this.current = this.current.next;
                return this.current.value;
            }

            @Override
            public boolean hasPrevious() {
                throw new UnsupportedOperationException();
            }

            @Override
            public E previous() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

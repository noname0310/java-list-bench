package list;

import java.util.NoSuchElementException;

/**
 * Linked Link that has two-way link between nodes.<br/>
 * Loosely optimized for readability.<br/>
 * @param <E> Container type
 */
public final class DoublyLinkedList<E> implements List<E> {
    private static final class Node<E> {
        public E value;
        public Node<E> prev;
        public Node<E> next;

        public Node(E value, Node<E> prev, Node<E> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private int length;
    private final Node<E> head;
    private final Node<E> tail;

    /**
     * Create <code>DoublyLinkedList</code>.<br/>
     */
    public DoublyLinkedList() {
        this.length = 0;
        this.head = new Node<>(null, null, null);
        this.tail = new Node<>(null, null, null);

        this.linkInitialize();
    }

    private void linkInitialize() {
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    private void insertFromNode(Node<E> insertPos, E item) {
        Node<E> prevNode = insertPos.prev;
        Node<E> newNode = new Node<>(item, prevNode, insertPos);
        prevNode.next = newNode;
        insertPos.prev = newNode;

        this.length += 1;
    }

    private Node<E> getNodeFromIndex(int index) {
        if (index < 0 || this.length <= index) {
            throw new IndexOutOfBoundsException();
        }

        boolean isReverse = this.length / 2 < index;

        Node<E> node;
        int iterationCount;

        if (!isReverse) {
            node = this.head;
            iterationCount = index + 1;
        } else {
            node = this.tail;
            iterationCount = this.length - index;
        }

        if (!isReverse) {
            for (int i = 0; i < iterationCount; ++i) {
                node = node.next;
            }
        } else {
            for (int i = 0; i < iterationCount; ++i) {
                node = node.prev;
            }
        }

        return node;
    }

    /**
     * Remove all items in this container.<br/>
     * Time complexity: O(1).<br/>
     */
    @Override
    public void clear() {
        this.linkInitialize();
        this.length = 0;
    }

    /**
     * Insert given item to this container.<br/>
     * Time complexity: O(n) (The further away from head and tail, more slower.).<br/>
     * @param pos Non-negative integer for insert position
     * @param item Item for insert
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public void insert(int pos, E item) {
        Node<E> insertPos = pos == this.length ? this.tail : getNodeFromIndex(pos);
        insertFromNode(insertPos, item);
    }

    /**
     * Append given item to this container's back.<br/>
     * Time complexity: O(1).<br/>
     * @param item Item to append
     */
    @Override
    public void append(E item) {
        insertFromNode(this.tail, item);
    }

    /**
     * Update data in a <code>pos</code> to a given value.<br/>
     * Time complexity: O(n) (The further away from head and tail, more slower.).<br/>
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
     * Time complexity: O(n) (The further away from head and tail, more slower.).<br/>
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
     * Time complexity: O(n) (The further away from head and tail, more slower.).<br/>
     * @param pos Non-negative integer for item position
     * @return Removed value
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public E remove(int pos) {
        Node<E> node = getNodeFromIndex(pos);
        node.next.prev = node.prev;
        node.prev.next = node.next;

        this.length -= 1;

        return node.value;
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
     * @return <code>ListIterator</code> initialized by list head position
     */
    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<>() {
            private Node<E> current = head;

            @Override
            public boolean hasNext() {
                return this.current.next != tail;
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
                return current.prev != head && current.prev != null;
            }

            @Override
            public E previous() {
                if (this.current.prev == null) {
                    throw new NoSuchElementException();
                }
                this.current = this.current.prev;
                return this.current.value;
            }
        };
    }
}

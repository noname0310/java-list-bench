package list;

import java.util.NoSuchElementException;

/**
 * ArrayList that supports resizing.<br/>
 * loosely optimized for readability.<br/>
 * @param <E> Container type
 */
public class ArrayList<E> implements List<E> {
    private static final int INITIAL_SIZE = 1;
    private int length;
    private E[] data;

    /**
     * Create <code>ArrayList</code>.<br/>
     */
    @SuppressWarnings("unchecked")
    public ArrayList() {
        this.length = 0;
        this.data = (E[]) new Object[ArrayList.INITIAL_SIZE];
    }

    /**
     * Remove all items in this container.<br/>
     * Make internal array into garbage.<br/>
     * Time complexity: O(1).<br/>
     */
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        this.length = 0;
        this.data = (E[]) new Object[this.data.length];
    }

    @SuppressWarnings("unchecked")
    private void tryResize() {
        if (length < this.data.length) return;

        E[] newData = (E[]) new Object[this.data.length * 2];
        System.arraycopy(this.data, 0, newData, 0, this.data.length);
        this.data = newData;
    }

    /**
     * Insert given item to this container.<br/>
     * time complexity: O(n) (The further away from back, more slower.).<br/>
     * @param pos Non-negative integer for insert position
     * @param item Item for insert
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public void insert(int pos, E item) {
        tryResize();

    }

    /**
     * Append given item to this container's back.
     * Time complexity: O(1).<br/>
     * @param item Item to append
     */
    @Override
    public void append(E item) {
        tryResize();
        this.data[this.length] = item;
        this.length += 1;
    }

    /**
     * Update data in a <code>pos</code> to a given value.<br/>
     * Time complexity: O(1).<br/>
     * @param pos Non-negative integer for update item position
     * @param item New item for update
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public void update(int pos, E item) {
        this.data[pos] = item;
    }

    /**
     * Get value in <code>pos</code>
     * Time complexity: O(1).<br/>
     * @param pos Non-negative integer for item position
     * @return Value
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public E getValue(int pos) {
        return this.data[pos];
    }

    /**
     * Remove value in <code>pos</code>
     * Time complexity: O(n) (The further away from back, more slower.).<br/>
     * @param pos Non-negative integer for item position
     * @return Removed value
     * @throws java.lang.IndexOutOfBoundsException when <code>pos</code> is out of range
     */
    @Override
    public E remove(int pos) {
        return null;
    }

    /**
     * Get length of this container.
     * @return Length of this container
     */
    @Override
    public int length() {
        return this.length;
    }

    /**
     * Create list iterator of this container.<br/>
     * @return <code>ListIterator</code> initialized to index -1
     */
    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return this.index < length - 1;
            }

            @Override
            public E next() {
                if (length <= this.index) {
                    throw new NoSuchElementException();
                }
                index += 1;
                return data[index];
            }

            @Override
            public boolean hasPrevious() {
                return 0 < this.index;
            }

            @Override
            public E previous() {
                if (this.index <= 0) {
                    throw new NoSuchElementException();
                }
                index -= 1;
                return data[index];
            }
        };
    }
}
package list;

public interface List<E> {
	void clear();
	void insert(int pos, E item);
	void append(E item);
	void update(int pos, E item);
	E getValue(int pos);
	E remove(int pos);
	int length();
	ListIterator<E> listIterator();
}

package util.iterator;

import java.util.Iterator;

public class UnmodifiableIterator<T> extends IteratorDecorator<T> {
	public UnmodifiableIterator(Iterator<T> decorated) {
		super(decorated);
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}
}

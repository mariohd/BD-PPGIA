package db.modules.buffer;

public interface BufferAlgorithm<T> {

	public Node<T> search(String rowid);
	
	public void set(String key, T value);
	
	public Node<T> getNode(String key);
	
	public T getValue(String key);
}

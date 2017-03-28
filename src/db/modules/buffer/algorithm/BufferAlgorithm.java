package db.modules.buffer.algorithm;

import java.util.List;

public interface BufferAlgorithm<T> {

	public Node<T> search(String rowid);
	
	public void set(String key, T value);
	
	public Node<T> getNode(String key);
	
	public T getValue(String key);
	
	public int getSize();
	
	public List<T> getCollection();
	
	public void addHit();
	
	public void addMiss();
	
	public int getHits();
	
	public int getMisses();
	
	public double getHitRatio();
}

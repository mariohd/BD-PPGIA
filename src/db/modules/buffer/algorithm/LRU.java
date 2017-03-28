package db.modules.buffer.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class LRU<T> implements BufferAlgorithm<T> {
	int capacity;
	private HashMap<String, Node<T>> map = new HashMap<String, Node<T>>();
	private Node<T> head = null;
	private Node<T> end = null;
	private int hit = 0;
	private int miss = 0;

	public LRU(int capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public T getValue(String key) {
		if (map.containsKey(key)) {
			Node<T> n = map.get(key);
			remove(n);
			setHead(n);
			return n.getValue();
		}

		return null;
	}
	
	@Override
	public Node<T> getNode(String key) {
		return map.get(key);
	}

	public void remove(Node<T> n) {
		if (n.getPre() != null) {
			n.getPre().setNext(n.getNext());
		} else {
			this.head = n.getNext();
		}

		if (n.getNext() != null) {
			n.getNext().setPre(n.getPre());
		} else {
			this.end = n.getPre();
		}
	}

	public void setHead(Node<T> n) {
		n.setNext(head);
		n.setPre(null);

		if (this.head != null)
			head.setPre(n);

		this.head = n;

		if (end == null)
			this.end = this.head;
	}

	@Override
	public void set(String key, T value) {
		if (map.containsKey(key)) {
			Node<T> old = map.get(key);
			old.setValue(value);
			remove(old);
			setHead(old);
		} else {
			Node<T> created = new Node<T>(key, value);
			if (map.size() >= capacity) {
				this.map.remove(this.end.getKey());
				remove(this.end);
				setHead(created);

			} else {
				setHead(created);
			}

			this.map.put(key, created);
		}
	}

	@Override
	public Node<T> search(String rowid) {
		return this.map.get(rowid);
	}
	
	public int getSize() {
		return this.capacity;
	}

	@Override
	public List<T> getCollection() {
		List<T> list = new LinkedList<T>();
		for (Node<T> t : this.map.values()) {
			list.add(t.getValue());
		}
		return list;
	}

	@Override
	public void addHit() {
		this.hit++;
	}
	
	public void addMiss() {
		this.miss++;
	}
	
	public double getHitRatio() {
		return this.hit/new Double(this.hit + this.miss);
	}

	@Override
	public int getHits() {
		return this.hit;
	}

	@Override
	public int getMisses() {
		return this.miss;
	}

}

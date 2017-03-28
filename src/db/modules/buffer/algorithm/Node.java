package db.modules.buffer.algorithm;

public class Node<T>{
    private String key;
    private T value;
    private Node<T> pre;
    private Node<T> next;
 
    public Node(String key, T value){
        this.key = key;
        this.value = value;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Node<T> getPre() {
		return pre;
	}

	public void setPre(Node<T> pre) {
		this.pre = pre;
	}

	public Node<T> getNext() {
		return next;
	}

	public void setNext(Node<T> next) {
		this.next = next;
	}
    
    
}

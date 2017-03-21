package db.modules.descriptors;

public class ColumnDescriptor {

	private String name;
	private Class type;
	private int size;

	public ColumnDescriptor(String name, Class type, int size) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class getType() {
		return type;
	}
	
	public String asString() {
		return "N=" + this.name + "%" + "T=" + (this.getType() == Integer.class ? "I" : "S") + "%S=" + this.size + ";";
	}

	public void setType(Class type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	
}

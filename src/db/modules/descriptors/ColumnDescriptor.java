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

	public boolean isSsystemColumn() {
		return this.name.toUpperCase().equals("ROWID");
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
	
	public String toString() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnDescriptor other = (ColumnDescriptor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size != other.size)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
	
}

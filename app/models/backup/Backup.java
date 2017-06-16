package models.backup;

public class Backup {
	private String name;
	private String path;
	private int size;
	private String type;
	
	public Backup(String name, String path, int size, String type){
		this.name = name;
		this.path = path;
		this.size = size;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

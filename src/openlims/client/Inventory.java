package openlims.client;

public class Inventory {
	
	private int id;
	private String title;
	
	public Inventory() {
		
	}
	
	public Inventory(int id, String title) {
		this.id=id;
		this.title=title;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}

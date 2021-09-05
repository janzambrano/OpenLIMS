package openlims.client;

public class Notebook {
	
	private int id;
	private String title;
	
	public Notebook() {
		
	}
	
	public Notebook(int id, String title) {
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

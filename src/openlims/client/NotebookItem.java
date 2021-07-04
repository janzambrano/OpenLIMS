package openlims.client;

public class NotebookItem {
	
	private String date=null;
	private String author=null;
	private String note=null;
	
	public NotebookItem(){
		
	}
	
	public NotebookItem(String date, String author, String note) {
		this.setDate(date);
		this.setAuthor(author);
		this.setNote(note);
	}
	
	//Getters and setters

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}

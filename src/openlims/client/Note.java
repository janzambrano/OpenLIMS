package openlims.client;

public class Note {
	
	private int ID=0;
	private String date=null;
	private String author=null;
	private String note=null;
	
	public Note(){
		
	}
	
	public Note(int ID, String date, String author, String note) {
		this.setID(ID);
		this.setDate(date);
		this.setAuthor(author);
		this.setNote(note);
	}
	
	//Getters and setters

	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
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

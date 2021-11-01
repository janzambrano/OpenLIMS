package openlims.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddNoteWindow extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public static void launch(Stage primaryStage, int subNotebookID, Statement stat) {
		primaryStage.setTitle("Add note");
		
		TextField title = new TextField();
		TextField author = new TextField();
		TextField date = new TextField();
		
		Button addItem = new Button("Add");
		addItem.setOnAction(e->{
			try {
				stat.execute("INSERT INTO notes(id_notebook, date, author, title) VALUES ("
						+subNotebookID+", '"+date.getText()+"', '"+author.getText()+"', '"+title.getText()+"')");
				int id = getNoteID(stat);				
				chooseFile(primaryStage,subNotebookID,id);
			} catch (SQLException f) {
				f.printStackTrace();
				primaryStage.close();
			}
			primaryStage.close();
		});
		
		VBox vbox = new VBox(new HBox(new Label("Title: "), title),
				new HBox(new Label("Author: "), author),
				new HBox(new Label("Date: "), date),
				addItem);
		
		Scene scene = new Scene(vbox, 300,300);
		scene.getStylesheets().add("control.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	static void chooseFile(Stage noteStage, int subNotebookID, int noteID) {//Choose markdown file and copy it to the notebook folder
    	//Get original note file
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Add note");
    	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown", "*.md"));
    	File noteFileOriginal = fileChooser.showOpenDialog(noteStage);
    	
    	//Copy original note file to note folder and rename it note_<noteID>
    	try {
			Files.copy(noteFileOriginal.toPath(), Paths.get("./notes/note"+Integer.toString(noteID)+".md"), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	static int getNoteID(Statement stat) {
		try { //Get last ID (I have no idea why max(id) doesn't work here)
			ResultSet result = stat.executeQuery("SELECT id FROM notes");
			int id=-999;
			while(result.next()) {
				id=result.getInt("id");
			}
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -999;
	}

}

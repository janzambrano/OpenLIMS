package openlims.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AddNotebookWindow extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
	}

	public static void launch(Stage primaryStage, Connection conn, Statement stat, SplitMenuButton notebookSelectButton) {
		primaryStage.setTitle("Add notebook");
		
		TextField subNotebookTitleField = new TextField();
		Button addNotebookButt = new Button("Add");
		addNotebookButt.setOnAction(e->{ //Create new subinventory -> add to inventories table and create new subinventoryID table
			try {
				stat.execute("INSERT INTO notebooks(title) VALUES ('"+subNotebookTitleField.getText()+"')");				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			notebookSelectButton.fire();//Invoke action on selectButton so the notebook list can be updated
			//Close window
			primaryStage.close();
		});
		
		HBox hbox = new HBox(new Label("Notebook name: "),subNotebookTitleField,addNotebookButt);
		
		Scene scene = new Scene(hbox, 300,30);
		primaryStage.setScene(scene);
		primaryStage.show();	
	}

}

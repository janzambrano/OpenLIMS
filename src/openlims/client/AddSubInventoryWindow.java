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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddSubInventoryWindow extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
	}

	public static void launch(Stage primaryStage, Connection conn, Statement stat, SplitMenuButton subInventorySelectButton) {
		primaryStage.setTitle("Add subinventory");
		
		TextField subInvTitleField = new TextField();
		Button addSubInvButt = new Button("Add");
		addSubInvButt.setOnAction(e->{ //Create new subinventory -> add to inventories table and create new subinventoryID table
			try {
				stat.execute("INSERT INTO inventories(title) VALUES ('"+subInvTitleField.getText()+"')");
				ResultSet result = stat.executeQuery("SELECT * FROM inventories WHERE title='"+subInvTitleField.getText()+"'");
				int id = result.getInt("id");
				stat.execute("CREATE TABLE subinventory"+Integer.toString(id)+"(id INTEGER, quantity INTEGER, note TEXT)");
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			subInventorySelectButton.fire();//Invoke action on selectButton so the subInv list can be updated
			//Close window
			primaryStage.close();
		});
		
		HBox hbox = new HBox(new Label("Subinventory name: "),subInvTitleField,addSubInvButt);
		
		Scene scene = new Scene(hbox, 300,30);
		primaryStage.setScene(scene);
		primaryStage.show();	
	}

}

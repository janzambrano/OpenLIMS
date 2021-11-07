package openlims.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MoveNoteWindow extends Application {

	static int targetNotebookID;
	
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public static void launch(Stage primaryStage, int itemID, Statement stat) {
		primaryStage.setTitle("Move to notebook");
		
		SplitMenuButton targetNotebookSelectButton=new SplitMenuButton();    	
		targetNotebookSelectButton.setText("Choose notebook");
		targetNotebookSelectButton.setOnAction(e->{
			updateTargetInventorySelectButton(targetNotebookSelectButton, stat);//Insert notebooks into the splitMenuButton
    	});
		
		Button moveItemButton = new Button("Move to notebook");
		moveItemButton.setOnAction(e->{		
			moveNote(itemID, targetNotebookID, stat);
			primaryStage.close();
		});
		
		VBox vbox = new VBox(new HBox(new Label("Target notebook: "), targetNotebookSelectButton),
				moveItemButton);
		
		Scene scene = new Scene(vbox, 300,200);
		scene.getStylesheets().add("control.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	static void updateTargetInventorySelectButton(SplitMenuButton targetInvSelectButton, Statement stat) {
		List<Notebook> inventoriesList=getNotebooksList(stat);
		for(int i=0; i<inventoriesList.size(); i++) {
    		int id=inventoriesList.get(i).getId();
    		MenuItem inventoryItem=new MenuItem(inventoriesList.get(i).getTitle());
    		targetInvSelectButton.getItems().add(inventoryItem);
    		inventoryItem.setOnAction(e->{
    			targetInvSelectButton.setText(inventoryItem.getText());
    			targetNotebookID=id;
    		});
    	}
	}
	
	static void moveNote(int itemID, int targetSubInvID, Statement stat) {
		try {
			stat.execute("UPDATE notes SET id_notebook="+Integer.toString(targetSubInvID)+" WHERE id="+itemID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	static List<Notebook> getNotebooksList(Statement stat) {
    	List<Notebook> notebooksList = new ArrayList<Notebook>();
    	//Update list of notebooks
    	try {
			ResultSet result=stat.executeQuery("SELECT * FROM notebooks");
			int id;
			String title;
			while(result.next()) {
				id=result.getInt("id");
				title=result.getString("title");
				notebooksList.add(new Notebook(id,title));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return notebooksList;
    }
}

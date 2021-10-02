package openlims.client;

import java.sql.Connection;
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

public class MoveItemWindow extends Application {

	static int targetSubInvID;
	
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
	}
	
	public static void launch(Stage primaryStage, int currentSubInvID, int itemID, Statement stat) {
		primaryStage.setTitle("Move to inventory");

		TextField quantityField = new TextField();
		
		SplitMenuButton targetInvSelectButton=new SplitMenuButton();    	
		targetInvSelectButton.setText("Choose subinventory");
		targetInvSelectButton.setOnAction(e->{
			updateTargetInventorySelectButton(targetInvSelectButton, stat);//Insert notebooks into the splitMenuButton
    	});
		
		Button moveItemButton = new Button("Move to inventory");
		moveItemButton.setOnAction(e->{		
			moveItem(itemID, Integer.parseInt(quantityField.getText()), currentSubInvID, targetSubInvID, stat);
			primaryStage.close();
		});
		
		VBox vbox = new VBox(new HBox(new Label("Target subinventory: "), targetInvSelectButton),
				new HBox(new Label("Quantity: "), quantityField),
				moveItemButton);
		
		Scene scene = new Scene(vbox, 300,300);
		scene.getStylesheets().add("control.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	static void updateTargetInventorySelectButton(SplitMenuButton targetInvSelectButton, Statement stat) {
		List<Inventory> inventoriesList=getInventoryList(stat);
		for(int i=0; i<inventoriesList.size(); i++) {
    		int id=inventoriesList.get(i).getId();
    		MenuItem inventoryItem=new MenuItem(inventoriesList.get(i).getTitle());
    		targetInvSelectButton.getItems().add(inventoryItem);
    		inventoryItem.setOnAction(e->{
    			targetInvSelectButton.setText(inventoryItem.getText());
    			targetSubInvID=id;
    		});
    	}
	}
	
	static void moveItem(int itemID, int quantity, int prevSubInvID, int targetSubInvID, Statement stat) {
		int prevSubInvQuant=getQuantityInSubInv(prevSubInvID, itemID, stat);
		int targetSubInvQuant=getQuantityInSubInv(targetSubInvID, itemID, stat);
		String note=getNoteInSubInv(targetSubInvID, itemID, stat);
		
		try {
			//Add item to target inventory
			if (targetSubInvQuant==0) //If there's no such item in target inventory, just add it
				stat.execute("INSERT INTO subinventory"+Integer.toString(targetSubInvID)+"(id, quantity, note) VALUES ("+itemID+", "+quantity+", '"+note+"')");
			else {
				int finalQuant=targetSubInvQuant+quantity;
				stat.execute("UPDATE subinventory"+Integer.toString(targetSubInvID)+" SET quantity="+Integer.toString(finalQuant)+" WHERE id="+Integer.toString(itemID));
			}
			
			//Remove item from previous inventory
			if (prevSubInvQuant<=quantity) //If all items need to be moved, just remove them from table
				stat.execute("DELETE FROM subinventory"+Integer.toString(prevSubInvID)+" WHERE id="+Integer.toString(itemID));
			else {
				int finalQuant=prevSubInvQuant-quantity;
				stat.execute("UPDATE subinventory"+Integer.toString(prevSubInvID)+" SET quantity="+Integer.toString(finalQuant)+" WHERE id="+Integer.toString(itemID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	static List<Inventory> getInventoryList(Statement stat) {
    	List<Inventory> inventoriesList = new ArrayList<Inventory>();
    	//Update list of notebooks
    	try {
			ResultSet result=stat.executeQuery("SELECT * FROM inventories");
			int id;
			String title;
			while(result.next()) {
				id=result.getInt("id");
				title=result.getString("title");
				inventoriesList.add(new Inventory(id,title));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return inventoriesList;
    }
	
	static int getQuantityInSubInv(int subInvID, int itemID, Statement stat) {
		int quantity=0;
		try {
			ResultSet result = stat.executeQuery("SELECT * FROM subinventory"+subInvID+" WHERE id="+itemID);			
			while(result.next()) {
				quantity=result.getInt("quantity");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return quantity;
	}
	
	static String getNoteInSubInv(int subInvID, int itemID, Statement stat) {
		String note="";
		try {
			ResultSet result = stat.executeQuery("SELECT * FROM subinventory"+subInvID+" WHERE id="+itemID);			
			while(result.next()) {
				note=result.getString("note");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return note;
	}

}

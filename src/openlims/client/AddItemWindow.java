package openlims.client;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import np.com.ngopal.control.AutoFillTextBox;

public class AddItemWindow extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public static void launch(Stage primaryStage, int subInvID, Connection conn, Statement stat) {
		primaryStage.setTitle("Add item to inventory");
		
		//Name field with autofilling from global inventory table
		List<Item> itemList = getItemList(conn, stat); //Get info about all items
		ObservableList<Object> data = FXCollections.observableArrayList();
		for(int i=0; i<itemList.size(); i++) { //Add items names to name suggestions' set
			data.add(itemList.get(i).getName());
		}		
		final AutoFillTextBox<Object> box = new AutoFillTextBox<Object>(data);
		
		TextField quantityField = new TextField();
		TextField webpageField = new TextField();
		TextField noteField = new TextField();
		
		Button addItem = new Button("Add");
		addItem.setOnAction(e->{		
			int itemID=searchForItemID(box.getText(), itemList);
			int quantity=Integer.parseInt(quantityField.getText());
			String note=noteField.getText();
			if(itemID<=0) { //At first add to global invItem table if there's no such item
				Item itemTMP = new Item(0,box.getText(),Integer.parseInt(quantityField.getText()),noteField.getText());
				itemID=addToGlobalItemTable(itemTMP,webpageField.getText(),conn,stat);
			}
			try {
				stat.execute("INSERT INTO subinventory"+Integer.toString(subInvID)+"(id, quantity, note) VALUES ("+itemID+", "+quantity+", '"+note+"')");
			} catch (SQLException f) {
				f.printStackTrace();
			}
			primaryStage.close();
		});
		
		VBox vbox = new VBox(new HBox(new Label("Name: "), box),
				new HBox(new Label("Quantity: "), quantityField),
				new HBox(new Label("Product webpage: "), webpageField),
				new HBox(new Label("Note: "), noteField),
				addItem);
		
		Scene scene = new Scene(vbox, 300,150);
		scene.getStylesheets().add("control.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	static List<Item> getItemList(Connection conn, Statement stat){		
		List<Item> itemList = new ArrayList<Item>();
		try {
			ResultSet result=stat.executeQuery("SELECT * FROM invItems");
			int id, quantity;
			String name, note;
			while(result.next()) {
				id=result.getInt("id");
				quantity=result.getInt("quantity");
				name=result.getString("name");
				note=result.getString("note");
				itemList.add(new Item(id,name,quantity,note));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return itemList;
	}
	
	static int searchForItemID(String name, List<Item> itemList){
		for(int i=0; i<itemList.size(); i++) {
			if(name.equals(itemList.get(i).getName()))
				return itemList.get(i).getID();
		}
		return -999;
	}
	
	static int addToGlobalItemTable(Item item, String webpage, Connection conn, Statement stat) {
		System.out.println("INSERT INTO invItems(name, quantity, weblink, note) VALUES ('"
				+item.getName()+"', "+item.getCount()+", '"+webpage+"', '"+item.getNote()+"')");
		try {			
			stat.execute("INSERT INTO invItems(name, quantity, weblink, note) VALUES ('"
					+item.getName()+"', "+item.getCount()+", '"+webpage+"', '"+item.getNote()+"')");
			ResultSet result = stat.executeQuery("SELECT * FROM invItems WHERE name='"+item.getName()+"'");
			item.setID(result.getInt("id"));
			generateQRCode(item); //Generate QR code for it
			return result.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -999;
	}
	
	@SuppressWarnings("deprecation")
	static void generateQRCode(Item selectedItem) {	
    	try {
			BitMatrix bitMatrix = new QRCodeWriter().encode(selectedItem.getName(), BarcodeFormat.QR_CODE, 200, 200);//2D Barcode
			String path="./qrcodes/QR_"+Integer.toString(selectedItem.getID())+".png";
			MatrixToImageWriter.writeToFile(bitMatrix,path.substring(path.lastIndexOf('.') + 1),new File(path));
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
    }

}

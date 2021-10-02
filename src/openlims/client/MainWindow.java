/**
 * Open LIMS
 * 
 * Created by Jacek Piłka
 * 
 * Open source laboratory inventory management system
 */

package openlims.client;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends Application {
	
	//SQLite connection
	static Connection conn;
	static Statement stat;
	
	//Separators
	Separator separatorH=new Separator(Orientation.HORIZONTAL);
	Separator separatorV=new Separator(Orientation.VERTICAL);
	
	static int selectedSubInvID=0;
	static int selectedNotebookID=0;
	
    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("OpenLIMS"); 	

    	//Left Bar
    	Button inventoryButton=new Button("Inventory");
    	Button notebookButton=new Button("Notebook");
    	Label credentials=new Label("OpenLIMS\nCreated by Jacek Piłka\n2021");
    	
    	/**
    	 * Create scene
    	 */
    	
    	VBox leftBar=new VBox(inventoryButton,notebookButton,separatorH,credentials);
    	VBox rightLayout=new VBox();
    	
    	inventoryButton.setOnAction(value -> {
    		inventoryScene(leftBar,rightLayout,primaryStage);
    	});
    	notebookButton.setOnAction(value -> {
    		notebookScene(leftBar,rightLayout,primaryStage);
    	});
    	
    	primaryStage.initStyle(StageStyle.DECORATED);
    	primaryStage.setWidth(1280);
    	primaryStage.setHeight(720);
    	primaryStage.show();
    	inventoryScene(leftBar,rightLayout,primaryStage);   	    	
    }
    
    //Create layout for inventory view
    void inventoryScene(VBox leftBar,VBox rightLayout, Stage stage) {
    	/**
    	 * Create inventory table
    	 */
    	TableView<Item> inventoryTab=new TableView<Item>();
    	TableColumn<Item, Integer> inventoryTabColumnID=new TableColumn<>("ID");
    	inventoryTabColumnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    	TableColumn<Item, String> inventoryTabColumnName=new TableColumn<>("Name");
    	inventoryTabColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    	TableColumn<Item, Integer> inventoryTabColumnCount=new TableColumn<>("Quantity");
    	inventoryTabColumnCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    	TableColumn<Item, Integer> inventoryTabColumnNote=new TableColumn<>("Note");
    	inventoryTabColumnNote.setCellValueFactory(new PropertyValueFactory<>("note"));
    	
    	inventoryTab.getColumns().add(inventoryTabColumnID);
    	inventoryTab.getColumns().add(inventoryTabColumnName);
    	inventoryTab.getColumns().add(inventoryTabColumnCount);
    	inventoryTab.getColumns().add(inventoryTabColumnNote);
    	
    	/**
    	 * Create scene elements
    	 */
    	
    	//Select inventory menu
    	SplitMenuButton subInventorySelectButton=new SplitMenuButton();    	
    	subInventorySelectButton.setText("Choose subinventory");
    	subInventorySelectButton.setOnAction(e->{
    		updateInventorySelectButton(subInventorySelectButton,inventoryTab);//Insert notebooks into the splitMenuButton
    	});
    	subInventorySelectButton.fire();//Invoke action on selectButton so the subInv list can be updated
    	
    	//Buttons //TODO Make this buttons actually do something
    	Button addToInventoryButton=new Button("Add");
    	addToInventoryButton.setOnAction(e->{
    		Stage itemStage = new Stage();
    		AddItemWindow.launch(itemStage, selectedSubInvID, conn, stat);
    	});
    	
    	Button moveToInventoryButton=new Button("Move to inventory");
    	moveToInventoryButton.setOnAction(e->{
    		Stage itemStage = new Stage();
    		moveItem(inventoryTab, itemStage);
    	});
    	
    	Button removeFromInventoryButton=new Button("Remove");
    	removeFromInventoryButton.setOnAction(e->{
    		Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to delete this item?", ButtonType.YES, ButtonType.CANCEL);
    		alert.showAndWait();
    		if (alert.getResult() == ButtonType.YES)
    			removeItemFromSubInventory(inventoryTab);    		
    	});
    	
    	Button removeSubinventoryButton=new Button("Remove Subinventory");
    	removeSubinventoryButton.setOnAction(e->{
    		Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to delete this inventory?", ButtonType.YES, ButtonType.CANCEL);
    		alert.showAndWait();
    		if (alert.getResult() == ButtonType.YES)
    			removeSubinventory(subInventorySelectButton, inventoryTab);
    	});
    	
    	Button searchForItemButton=new Button("Search");
    	
    	//Adding elements to the scene
    	HBox upperBar=new HBox(subInventorySelectButton, new Label("\t\t"),
    			addToInventoryButton, moveToInventoryButton, removeFromInventoryButton, removeSubinventoryButton, searchForItemButton);
    	rightLayout=new VBox(upperBar,separatorH,inventoryTab);
    	
    	HBox mainLayout=new HBox(leftBar,separatorV,rightLayout);
    	
    	Scene scene=new Scene(mainLayout);    	
    	stage.setScene(scene);//Add scene to the stage
    }
    
    /**
     * TODO Notes should be contained in .md files
     * (imported using this client to the special folder, files should have names like "<notebookID>_<noteID>.md")
     * At the beginning, when a table item is clicked, the window for saving note should be shown (so we can export .md to our personal folder)
     * In the next step a simple text editor should be created, so we could actually edit the note using this client
     */
    
  //Create layout for notebook view
    void notebookScene(VBox leftBar,VBox rightLayout, Stage stage) {
    	
    	/**
    	 * Create inventory table
    	 */
    	TableView<Note> notebookTab=new TableView<Note>();
    	TableColumn<Note, Integer> notebookTabColumnID=new TableColumn<>("ID");
    	notebookTabColumnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    	TableColumn<Note, String> notebookTabColumnDate=new TableColumn<>("Date");
    	notebookTabColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    	TableColumn<Note, String> notebookTabColumnName=new TableColumn<>("Author");
    	notebookTabColumnName.setCellValueFactory(new PropertyValueFactory<>("author"));
    	TableColumn<Note, String> notebookTabColumnCount=new TableColumn<>("Title");
    	notebookTabColumnCount.setCellValueFactory(new PropertyValueFactory<>("note"));
    	
    	notebookTab.getColumns().add(notebookTabColumnID);
    	notebookTab.getColumns().add(notebookTabColumnDate);
    	notebookTab.getColumns().add(notebookTabColumnName);
    	notebookTab.getColumns().add(notebookTabColumnCount);
    	
    	//Upper Bar
    	
    	//Buttons //TODO Make this buttons actually do something
    	Button addToNotebookButton=new Button("Add");
    	Button moveToNotebookButton=new Button("Move to inventory");
    	Button removeFromNotebooksButton=new Button("Remove");
    	removeFromNotebooksButton.setOnAction(e->{
    		Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to delete this note?", ButtonType.YES, ButtonType.CANCEL);
    		alert.showAndWait();
    		if (alert.getResult() == ButtonType.YES)
    			removeNoteFromNotebook(notebookTab);
    	});
    	Button searchForNoteButton=new Button("Search");
    	
    	SplitMenuButton notebookSelectButton=new SplitMenuButton();    	
    	notebookSelectButton.setText("Choose notebooks");
    	notebookSelectButton.setOnAction(e->{
    		updateNotebookSelectButton(notebookSelectButton,notebookTab);//Insert notebooks into the splitMenuButton
    	});
    	notebookSelectButton.fire();//Invoke action on selectButton so the notebook list can be updated
    	
    	
    	/**
    	 * Create scene elements
    	 */
    	
    	HBox upperBar=new HBox(notebookSelectButton, new Label("\t\t"), addToNotebookButton, moveToNotebookButton, removeFromNotebooksButton, searchForNoteButton);
    	rightLayout=new VBox(upperBar,separatorH,notebookTab);
    	
    	leftBar.setFillWidth(true);
    	
    	HBox mainLayout=new HBox(leftBar,separatorV,rightLayout);
    	
    	Scene scene=new Scene(mainLayout);    	
    	stage.setScene(scene);//Add scene to the stage
    }
    
    /**
     * Operations on items
     */
    
    static void removeItemFromSubInventory(TableView<Item> inventoryTab) {
    	TableViewSelectionModel<Item> selectionModel = inventoryTab.getSelectionModel();
    	ObservableList<Item> selectedTableItem = selectionModel.getSelectedItems();
    	Item selectedItem=null;
    	try {
    		selectedItem = selectedTableItem.get(0);
    	} catch (java.lang.IndexOutOfBoundsException e){
    		e.printStackTrace();
    		return;
    	}
    	try {
			stat.execute("DELETE FROM subinventory"+selectedSubInvID+" WHERE id="+selectedItem.getID());
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	updateItemsTable(inventoryTab, selectedSubInvID);
    }
    
    static void moveItem(TableView<Item> inventoryTab, Stage itemStage) {
    	TableViewSelectionModel<Item> selectionModel = inventoryTab.getSelectionModel();
    	ObservableList<Item> selectedTableItem = selectionModel.getSelectedItems();
    	Item selectedItem=null;
    	try {
    		selectedItem = selectedTableItem.get(0);
    	} catch (java.lang.IndexOutOfBoundsException e){
    		e.printStackTrace();
    		return;
    	}
    	MoveItemWindow.launch(itemStage, selectedSubInvID, selectedItem.getID(), stat);
    	updateItemsTable(inventoryTab, selectedSubInvID);
    }
    
    /**
     * Functions for inventories
     */
    
    static List<Inventory> getInventoryList() {
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
    
    static void createAddInventoryButt(SplitMenuButton subInventorySelectButton, TableView<Item> inventoryTab) {
    	MenuItem addSubInventory=new MenuItem("Add");
		subInventorySelectButton.getItems().add(addSubInventory);
		addSubInventory.setOnAction(e->{
			Stage addSubInvStage = new Stage();
			AddSubInventoryWindow.launch(addSubInvStage, conn, stat, subInventorySelectButton);
		});
    }
    
    static void updateInventorySelectButton(SplitMenuButton subInventorySelectButton, TableView<Item> inventoryTab) {
    	subInventorySelectButton.getItems().clear();
    	List<Inventory> inventoriesList=getInventoryList();
    	for(int i=0; i<inventoriesList.size(); i++) {
    		int id=inventoriesList.get(i).getId();
    		MenuItem inventoryItem=new MenuItem(inventoriesList.get(i).getTitle());
    		subInventorySelectButton.getItems().add(inventoryItem);
    		inventoryItem.setOnAction(e->{
    			subInventorySelectButton.setText(inventoryItem.getText());
    			updateItemsTable(inventoryTab,id);
    			selectedSubInvID=id;
    		});
    	}
    	createAddInventoryButt(subInventorySelectButton, inventoryTab);
    }
    
    static void removeSubinventory(SplitMenuButton subInventorySelectButton, TableView<Item> inventoryTab) {
    	try {
			stat.execute("DROP TABLE subinventory"+selectedSubInvID);
	    	stat.execute("DELETE FROM inventories WHERE id="+selectedSubInvID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	updateInventorySelectButton(subInventorySelectButton, inventoryTab);
    }
    
    static void updateItemsTable(TableView<Item> inventoryTab, int inventoryID) {
    	//Get list of notes from the database
    	inventoryTab.getItems().clear();//Clear table
    	
    	/**
    	 * I cannot simply get a name of the one exact item during creation of table of items, so before it I need to prepare a list of them
    	 * and then use it (id from database is an index of the list)
    	 * 
    	 * Because you cannot make an "empty space" in the list, when next item's id > list's size, empty strings are added to "inflate" the list
    	 * that's what happens in the if statement
    	 * 
    	 * Really overcomplicated, I'm sure there's much easier way to solve this issue :/
    	 */
    	List<String> itemsNames=new ArrayList<String>();
    	try {
    		ResultSet result=stat.executeQuery("SELECT * FROM invItems");
    		while(result.next()) {
    			if(result.getInt("id")>itemsNames.size()) {
    				for(int i=0;i<result.getInt("id")-itemsNames.size();i++)
    					itemsNames.add("");
    			}
    			itemsNames.add(result.getInt("id"), result.getString("name"));
    		}
    	} catch (SQLException e1) {
			e1.printStackTrace();
		}
    	
    	try {
			ResultSet result=stat.executeQuery("SELECT * FROM subinventory"+Integer.toString(inventoryID));
			int id, quantity;
			String name, note;
			while(result.next()) {
				id=result.getInt("id");
				name=itemsNames.get(id);
				quantity=result.getInt("quantity");
				note=result.getString("note");
				inventoryTab.getItems().add(new Item(id,name,quantity,note));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
    }
       
    /**
     * Operations on notes
     */
    
    static void removeNoteFromNotebook(TableView<Note> notebookTab) {
    	//TODO Add removing physical note from the notes' folder
    	TableViewSelectionModel<Note> selectionModel = notebookTab.getSelectionModel();
    	ObservableList<Note> selectedTableItem = selectionModel.getSelectedItems();
    	Note selectedItem=null;
    	try {
    		selectedItem = selectedTableItem.get(0);
    	} catch (java.lang.IndexOutOfBoundsException e){
    		e.printStackTrace();
    		return;
    	}
    	try {
			stat.execute("DELETE FROM notes WHERE id="+selectedItem.getID()+" AND id_notebook="+selectedNotebookID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	updateNotesTable(notebookTab, selectedSubInvID);
    }
    
    
    /**
     * Functions for notebooks
     */
    
    static List<Notebook> getNotebookList() {
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
    
    static void createAddNotebookButt(SplitMenuButton notebookSelectButton, TableView<Note> notebookTab) {
    	MenuItem addNotebook=new MenuItem("Add");
    	notebookSelectButton.getItems().add(addNotebook);
    	addNotebook.setOnAction(e->{
			//TODO Add subinventory -> add item to inventories table and create new table subinventory<id>
			Stage addNotebookStage = new Stage();
			AddNotebookWindow.launch(addNotebookStage, conn, stat, notebookSelectButton);
		});
    }
    
    static void updateNotebookSelectButton(SplitMenuButton notebookSelectButton, TableView<Note> notebookTab) {
    	notebookSelectButton.getItems().clear();
    	List<Notebook> notebooksList=getNotebookList();
    	for(int i=0; i<notebooksList.size(); i++) {
    		int id=notebooksList.get(i).getId();
    		MenuItem notebookItem=new MenuItem(notebooksList.get(i).getTitle());
    		notebookSelectButton.getItems().add(notebookItem);
    		notebookItem.setOnAction(e->{
    			notebookSelectButton.setText(notebookItem.getText());
    			updateNotesTable(notebookTab,id);
    			selectedNotebookID=id;
    		});
    	}
    	createAddNotebookButt(notebookSelectButton, notebookTab);
    }
    
    static void updateNotesTable(TableView<Note> notebookTab, int notebookID) {
    	//Get list of notes from the database
    	notebookTab.getItems().clear();//Clear table
    	try {
			ResultSet result=stat.executeQuery("SELECT * FROM notes WHERE id_notebook="+Integer.toString(notebookID));
			int id;
			String date, author, title;
			while(result.next()) {
				id=result.getInt("id");
				date=result.getString("date");
				author=result.getString("author");
				title=result.getString("title");
				notebookTab.getItems().add(new Note(id,date,author,title));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
    }
    
    /**
     * Functions for basic database operations
     */
    
    static void SQLconnect() {
    	try {
			conn=DriverManager.getConnection("jdbc:sqlite:openlims.db");
			stat=conn.createStatement();
			System.out.println("SQL connection enabled");
		} catch (SQLException e) {
			System.out.println("Error during SQL connection");
			e.printStackTrace();
		}
    	createTables();
    }
    
    static void closeConnection() {
    	try {
			conn.close();
			System.out.println("SQL connection ended");
		} catch (SQLException e) {
			System.out.println("Error during SQL connection ending");
			e.printStackTrace();
		}
    }
    
    static void createTables() {
    	try {//Create tables if not exist
			stat.execute("CREATE TABLE IF NOT EXISTS invItems (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, quantity INTEGER, weblink TEXT, note TEXT)");
			stat.execute("CREATE TABLE IF NOT EXISTS inventories (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)");
			stat.execute("CREATE TABLE IF NOT EXISTS notebooks (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)");
			stat.execute("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, id_notebook INTEGER, date TEXT, author TEXT, title TEXT)");
		} catch (SQLException e) {
			System.out.println("Error during creating tables");
			e.printStackTrace();
		}
    }
    
    /**
     * Main
     */
    
    public static void main(String[] args) {
    	SQLconnect();
    	Application.launch(args);
    	closeConnection();
    }
}
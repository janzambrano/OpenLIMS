/**
 * Open LIMS
 * 
 * Created by Jacek Piłka
 * 
 * Open source laboratory inventory management system
 */

package openlims.client;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    	
    	//Upper Bar
    	
    	//Select inventory menu
    	SplitMenuButton subInventorySelectButton=new SplitMenuButton();    	
    	subInventorySelectButton.setText("Choose subinventory");
    	updateInventorySelectButton(subInventorySelectButton,inventoryTab);//Insert notebooks into the splitMenuButton
    	
    	//"Add" option
    	MenuItem addSubInventory=new MenuItem("Add");
		subInventorySelectButton.getItems().add(addSubInventory);
		addSubInventory.setOnAction(e->{
			//TODO Add subinventory -> add item to inventories table and create new table subinventory<id>
		});
    	
    	//Buttons //TODO Make this buttons actually do something
    	Button addToInventoryButton=new Button("Add");
    	Button moveToInventoryButton=new Button("Move to inventory");
    	Button removeFromInventoryButton=new Button("Remove");    	
    	Button searchForItemButton=new Button("Search");
    	
    	//Adding elements to the scene
    	HBox upperBar=new HBox(subInventorySelectButton, new Label("\t\t"), addToInventoryButton, moveToInventoryButton, removeFromInventoryButton, searchForItemButton);
    	rightLayout=new VBox(upperBar,separatorH,inventoryTab);
    	
    	HBox mainLayout=new HBox(leftBar,separatorV,rightLayout);
    	
    	Scene scene=new Scene(mainLayout);    	
    	stage.setScene(scene);//Add scene to the stage
    }
    
    /**
     * TODO Notes should be contained in .md files
     * (imported using this client to the special folder, files should have names like "<notebookID>_<noteID>.md")
     * At the beginnig, when table item is clicked, the window for saving note should be shown (so we can export .md to our personal folder)
     * In the next step a simple text editor should be created, so we could actually edit the note using this client
     */
    
  //Create layout for notebook view
    void notebookScene(VBox leftBar,VBox rightLayout, Stage stage) {
    	
    	/**
    	 * Create inventory table
    	 */
    	TableView<NotebookItem> notebookTab=new TableView<NotebookItem>();
    	TableColumn<NotebookItem, Integer> notebookTabColumnID=new TableColumn<>("ID");
    	notebookTabColumnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    	TableColumn<NotebookItem, String> notebookTabColumnDate=new TableColumn<>("Date");
    	notebookTabColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    	TableColumn<NotebookItem, String> notebookTabColumnName=new TableColumn<>("Author");
    	notebookTabColumnName.setCellValueFactory(new PropertyValueFactory<>("author"));
    	TableColumn<NotebookItem, String> notebookTabColumnCount=new TableColumn<>("Title");
    	notebookTabColumnCount.setCellValueFactory(new PropertyValueFactory<>("note"));
    	
    	notebookTab.getColumns().add(notebookTabColumnID);
    	notebookTab.getColumns().add(notebookTabColumnDate);
    	notebookTab.getColumns().add(notebookTabColumnName);
    	notebookTab.getColumns().add(notebookTabColumnCount);
    	
    	//Upper Bar
    	
    	//Buttons //TODO Make this buttons actually do something
    	Button addToNotebookButton=new Button("Add");
    	Button moveToNotebookButton=new Button("Move to inventory");
    	Button removeFromNotebookyButton=new Button("Remove");    	
    	Button searchForNoteButton=new Button("Search");
    	
    	SplitMenuButton notebookSelectButton=new SplitMenuButton();    	
    	notebookSelectButton.setText("Choose notebooks");
    	updateNotebookSelectButton(notebookSelectButton,notebookTab);//Insert notebooks into the splitMenuButton
    	
    	//"Add" option
    	MenuItem addNotebook=new MenuItem("Add");
    	notebookSelectButton.getItems().add(addNotebook);
    	addNotebook.setOnAction(e->{
			//TODO Add notebook -> add item to notebooks table
    		//stat.executeQuery("INSERT INTO notebooks (text) VALUES (...)")
		});
    	
    	/**
    	 * Create scene elements
    	 */
    	
    	HBox upperBar=new HBox(notebookSelectButton, new Label("\t\t\t\t\t\t\t"));
    	rightLayout=new VBox(upperBar,separatorH,notebookTab);
    	
    	leftBar.setFillWidth(true);
    	
    	HBox mainLayout=new HBox(leftBar,separatorV,rightLayout);
    	
    	Scene scene=new Scene(mainLayout);    	
    	stage.setScene(scene);//Add scene to the stage
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
    
    static void updateInventorySelectButton(SplitMenuButton subInventorySelectButton, TableView<Item> inventoryTab) {
    	List<Inventory> inventoriesList=getInventoryList();
    	for(int i=0; i<inventoriesList.size(); i++) {
    		int id=inventoriesList.get(i).getId();
    		MenuItem inventoryItem=new MenuItem(inventoriesList.get(i).getTitle());
    		subInventorySelectButton.getItems().add(inventoryItem);
    		inventoryItem.setOnAction(e->{
    			subInventorySelectButton.setText(inventoryItem.getText());
    			updateItemsTable(inventoryTab,id);
    		});
    	}
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
			System.out.println("SELECT * FROM subinventory"+Integer.toString(inventoryID));
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
    
    static void updateNotebookSelectButton(SplitMenuButton notebookSelectButton, TableView<NotebookItem> notebookTab) {
    	List<Notebook> notebooksList=getNotebookList();
    	for(int i=0; i<notebooksList.size(); i++) {
    		int id=notebooksList.get(i).getId();
    		MenuItem notebookItem=new MenuItem(notebooksList.get(i).getTitle());
    		notebookSelectButton.getItems().add(notebookItem);
    		notebookItem.setOnAction(e->{
    			notebookSelectButton.setText(notebookItem.getText());
    			updateNotesTable(notebookTab,id);
    		});
    	}
    }
    
    static void updateNotesTable(TableView<NotebookItem> notebookTab, int notebookID) {
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
				notebookTab.getItems().add(new NotebookItem(id,date,author,title));
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
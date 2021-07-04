/**
 * Open LIMS
 * 
 * Created by Jacek Pi³ka
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindow extends Application {
	
	//Separators
	Separator separatorH=new Separator(Orientation.HORIZONTAL);
	Separator separatorV=new Separator(Orientation.VERTICAL);
	
    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("OpenLIMS"); 	

    	//Left Bar
    	Button inventoryButton=new Button("Inventory");
    	Button notebookButton=new Button("Notebook");
    	Label credentials=new Label("OpenLIMS\nCreated by Jacek Pi³ka\n2021");
    	
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
    	//TODO Table created 
    	TableView<Item> inventoryTab=new TableView<Item>();
    	TableColumn<Item, Integer> inventoryTabColumnID=new TableColumn<>("ID");
    	inventoryTabColumnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    	TableColumn<Item, String> inventoryTabColumnName=new TableColumn<>("Name");
    	inventoryTabColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    	TableColumn<Item, Integer> inventoryTabColumnCount=new TableColumn<>("Count");
    	inventoryTabColumnCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    	
    	inventoryTab.getColumns().add(inventoryTabColumnID);
    	inventoryTab.getColumns().add(inventoryTabColumnName);
    	inventoryTab.getColumns().add(inventoryTabColumnCount);
    	
    	inventoryTab.getItems().add(new Item(1,"Mirror",10));
    	
    	/**
    	 * Create scene elements
    	 */
    	//Upper Bar
    	SplitMenuButton subInventorySelectButton=new SplitMenuButton();    	
    	subInventorySelectButton.setText("Choose subinventory");
    	//TODO Read inventory list from database and add to the list
    	//For now just some example menus
    	MenuItem subInventoryChoice0=new MenuItem("Unused");
    	MenuItem subInventoryChoice1=new MenuItem("Setup 1");
    	MenuItem subInventoryChoice2=new MenuItem("Setup 2");
    	subInventorySelectButton.getItems().addAll(subInventoryChoice0,subInventoryChoice1,subInventoryChoice2);
    	
    	subInventoryChoice0.setOnAction(e->{
    		subInventorySelectButton.setText(subInventoryChoice0.getText());
    	});
    	subInventoryChoice1.setOnAction(e->{
    		subInventorySelectButton.setText(subInventoryChoice1.getText());
    	});
    	subInventoryChoice2.setOnAction(e->{
    		subInventorySelectButton.setText(subInventoryChoice2.getText());
    	});
    	
    	HBox upperBar=new HBox(subInventorySelectButton, new Label("\t\t\t\t\t\t\t"));
    	rightLayout=new VBox(upperBar,separatorH,inventoryTab);
    	
    	HBox mainLayout=new HBox(leftBar,separatorV,rightLayout);
    	
    	Scene scene=new Scene(mainLayout);    	
    	stage.setScene(scene);//Add scene to the stage
    }
    
  //Create layout for notebook view
    void notebookScene(VBox leftBar,VBox rightLayout, Stage stage) {
    	/**
    	 * Create inventory table
    	 */
    	//TODO Table created 
    	TableView<NotebookItem> notebookTab=new TableView<NotebookItem>();
    	TableColumn<NotebookItem, Integer> notebookTabColumnID=new TableColumn<>("Date");
    	notebookTabColumnID.setCellValueFactory(new PropertyValueFactory<>("date"));
    	TableColumn<NotebookItem, String> notebookTabColumnName=new TableColumn<>("Author");
    	notebookTabColumnName.setCellValueFactory(new PropertyValueFactory<>("author"));
    	TableColumn<NotebookItem, Integer> notebookTabColumnCount=new TableColumn<>("Note");
    	notebookTabColumnCount.setCellValueFactory(new PropertyValueFactory<>("note"));
    	
    	notebookTab.getColumns().add(notebookTabColumnID);
    	notebookTab.getColumns().add(notebookTabColumnName);
    	notebookTab.getColumns().add(notebookTabColumnCount);
    	
    	notebookTab.getItems().add(new NotebookItem("2021-07-04","Jacek Pi³ka","This is my first note!"));
    	
    	/**
    	 * Create scene elements
    	 */
    	//Upper Bar
    	SplitMenuButton subInventorySelectButton=new SplitMenuButton();    	
    	subInventorySelectButton.setText("Choose notebooks");
    	//TODO Read inventory list from database and add to the list
    	//For now just some example menus
    	MenuItem subInventoryChoice0=new MenuItem("All");
    	MenuItem subInventoryChoice1=new MenuItem("Mine");
    	subInventorySelectButton.getItems().addAll(subInventoryChoice0,subInventoryChoice1);
    	
    	subInventoryChoice0.setOnAction(e->{
    		subInventorySelectButton.setText(subInventoryChoice0.getText());
    	});
    	subInventoryChoice1.setOnAction(e->{
    		subInventorySelectButton.setText(subInventoryChoice1.getText());
    	});
    	
    	HBox upperBar=new HBox(subInventorySelectButton, new Label("\t\t\t\t\t\t\t"));
    	rightLayout=new VBox(upperBar,separatorH,notebookTab);
    	
    	leftBar.setFillWidth(true);
    	
    	HBox mainLayout=new HBox(leftBar,separatorV,rightLayout);
    	
    	Scene scene=new Scene(mainLayout);    	
    	stage.setScene(scene);//Add scene to the stage
    }
    
    public static void main(String[] args) {
    	Application.launch(args);
    }
}
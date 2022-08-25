package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import app.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserAlbumsController {
    @FXML AnchorPane rootPaneUI;
    @FXML Button enterAlbumUI;
    @FXML Button logOutUI;
    @FXML Button createAlbumUI;
    @FXML Button renameAlbumUI;
    @FXML Button deleteAlbumUI;
    @FXML Text usernameUI;

    public String username;
    private File userFolder;
    private ArrayList<String> albumlist = new ArrayList<String>();
    private ObservableList<String> obUsersUI;
    @FXML ListView <String> listOfAlbumsUI;


    
    /** 
     * getUsername collects username for later use
     * @param incomingUser string value of the username
     */
    // collects the username of the album
    public void getUsername(String incomingUser) {
        username = incomingUser;
    }// end get username

    
    /** 
     * actuallyInitialize is called on the loading of the user's album page from login
     * and loads up the listView with all of the albums for the user currently save in the file system
     * @throws FileNotFoundException throws exception if file path is incorrect and does not provide a useable file
     */
    // no param initialize for logging into user albums
    public void actuallyInitialize() throws FileNotFoundException {
        // change name of user
        usernameUI.setText(username + "'s Albums");

        // find and add every file in users
        String filestring = "./data/users/" + username + ".txt";
        userFolder = new File(filestring);
        Scanner sc = new Scanner(userFolder);
        String currentReadLine;

        while(sc.hasNext()) {
            currentReadLine = sc.nextLine();
            if (currentReadLine.substring(0, 3).equals("|+|")) albumlist.add(currentReadLine.substring(3));
        }

        sc.close();
        // display information on listview
        obUsersUI = FXCollections.observableArrayList(albumlist);
        listOfAlbumsUI.setItems(obUsersUI);

        listOfAlbumsUI.getSelectionModel().select(0);
    }// end preload file

    
    /** actuallyInitialize is called on the loading of the user's album page from one of the user's albums
     * and loads up the listView with all of the albums for the user currently save in the file system
     * @param primaryStage the display of the photos app page
     * @throws FileNotFoundException throws exception if file path is incorrect and does not provide a useable file
     */
    // initialize with stage param used to rest stage from a specific album
    public void actuallyInitialize(Stage primaryStage) throws FileNotFoundException {
        // change name of user
        usernameUI.setText(username + "'s Albums");

        // find and add every file in users
        String filestring = "./data/users/" + username + ".txt";
        userFolder = new File(filestring);
        Scanner sc = new Scanner(userFolder);
        String currentReadLine;

        while(sc.hasNext()) {
            currentReadLine = sc.nextLine();
            if (currentReadLine.substring(0, 3).equals("|+|")) albumlist.add(currentReadLine.substring(3));
        }

        sc.close();

        // reset stage
        Group root = new Group(rootPaneUI);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // display information on listview
        obUsersUI = FXCollections.observableArrayList(albumlist);
        listOfAlbumsUI.setItems(obUsersUI);
        listOfAlbumsUI.getSelectionModel().select(0);
    }
    

    
    /** 
     * Button Event is called every time a button is selected and
     * redirects to another method that preforms the action selected
     * @param e action event that specifies which button was selected
     * @throws IOException throws if FXML file cannot be accessed upon logout
     */
    // event for when button is pressed
    public void buttonEvent(ActionEvent e) throws IOException {
        // check which button was pressed and respond accordingly
        Button btn = (Button)e.getSource();
        
        if (btn == createAlbumUI) createAlbum();
        else if (btn == deleteAlbumUI) deleteAlbum();
        else if (btn == enterAlbumUI) {
            Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            enterAlbum(primaryStage);
        }
        else if (btn == renameAlbumUI) renameAlbum();
        else logOut();
    }// end of button event


    
     /** 
     * logout selected by the logout button closes out the user page and returns back to login page
     * @throws IOException throws exception if FXML file needed cannot be accessed
     */
    private void logOut() throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));
        rootPaneUI.getChildren().setAll(pane);
    }
    
    
    /** 
     * Create Album inserts a new album into the file system, does not allow for duplicates
     * @throws IOException  throws exception is userfolder does not exist
     */
    public void createAlbum() throws IOException {
        // collect name of album, returns and sends alert if no name was entered. also sends alert for duplicate albums
        String albumName = App.sendInputDialog("Create Album", "Creating New Album", "Enter name for album:", "Enter name here!");
        if (albumName.equals("||**ERROR**||")) {
            App.sendAlert("Error", "Error Creating Album", "You did not enter the name for the album.");
            return;
        }
        if (albumlist.contains(albumName)) {
            App.sendAlert("Error", "Error Creating Album", "You entered a duplicate album.");
            return;
        }

        albumlist.add(albumName);
        FileWriter fw = new FileWriter(userFolder, true);
        fw.write("|+|" + albumName + "\n");

        fw.close();
        // display information on listview
        obUsersUI = FXCollections.observableArrayList(albumlist);
        listOfAlbumsUI.setItems(obUsersUI);
    }

    
    /** 
     * delete album deletes the album selected in the listView from the file system
     * @throws IOException throws exception if userfolder does not exist
     */
    public void deleteAlbum() throws IOException {
         String selectedUser = listOfAlbumsUI.getSelectionModel().getSelectedItem();

        // try to delete file from folder
      
           if(albumlist.contains(selectedUser)){
               albumlist.remove(selectedUser);
                App.sendAlert("Delete User", "Deleted user:", selectedUser);
           }else{
               App.sendAlert("Delete User", "Error deleting user:", selectedUser);
           }                
            String massiveString = ""; 
            Scanner sc = new Scanner(userFolder);
            boolean skiptillnext = false;
            String currentReadLine;

            while(sc.hasNext()){
                currentReadLine = sc.nextLine();
                if(currentReadLine.equals("|+|" + selectedUser )){
                    //skip here
                    skiptillnext = true;
                }
                else if(skiptillnext == true && !currentReadLine.substring(0, 3).equals("|+|")){
                    // more skipping
                }
                else{
                    massiveString = massiveString + currentReadLine + "\n";
                    skiptillnext = false;
                    // write here 
                }
                
            }
            FileWriter myWriter = new FileWriter(userFolder);
            myWriter.write(massiveString);
            myWriter.close();
            sc.close();

            obUsersUI = FXCollections.observableArrayList(albumlist);
        listOfAlbumsUI.setItems(obUsersUI);      
    }

    
    /** 
     * rename Album renames the album selected in the listview and file system
     * @throws IOException throws exception if userfolder does not exist
     */
    public void renameAlbum() throws IOException {
        // collect name of album, returns and sends alert if no name was entered
        String selectedUser = listOfAlbumsUI.getSelectionModel().getSelectedItem();
        String albumName = App.sendInputDialog("Rename Album", "Renaming Current Album", "Enter new name for album:", "Enter new name here!");
        if (albumName.equals("||**ERROR**||")) {
            App.sendAlert("Error", "Error Renaming Album", "You did not enter the new name for the album");
            return;
        }else if(albumlist.contains(albumName)){
            App.sendAlert("Error", "Error Renaming Album", "This album name is already in use");
            return;
        }          
        if(albumlist.contains(selectedUser)){
            int index = albumlist.indexOf(selectedUser);
            albumlist.add(index, albumName);
            albumlist.remove(selectedUser);
             App.sendAlert("Rename User", "Rename user:", selectedUser +" to " + albumName);
        }else{
            App.sendAlert("Rename User", "Error deleting user:", selectedUser);
        }     
        String massiveString = ""; 
        Scanner sc = new Scanner(userFolder);
        String currentReadLine;

        while(sc.hasNext()){
            currentReadLine = sc.nextLine();
            if(currentReadLine.equals("|+|" + selectedUser )){
                //skip here
            massiveString = massiveString + "|+|" + albumName + "\n";           
         }
            else{
                massiveString = massiveString + currentReadLine + "\n";
                // write here 
            }
            
        }
        FileWriter myWriter = new FileWriter(userFolder);
        myWriter.write(massiveString);
        myWriter.close();
        sc.close();

        obUsersUI = FXCollections.observableArrayList(albumlist);
        listOfAlbumsUI.setItems(obUsersUI);  
        
    }

    
    /** 
     * enter album enters into the album page for the selected album
     * @param primaryStage the display of the photos app page
     * @throws IOException throws exception if userfile does not exist
     */
    public void enterAlbum(Stage primaryStage) throws IOException {
        // check if an album was selected
        if (listOfAlbumsUI.getSelectionModel().getSelectedItem() == null) {
            App.sendAlert("Error", "Error Entering Album", "No album was selected.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Album.fxml"));
        rootPaneUI.getChildren().setAll((Pane)loader.load());

        // passes the username and album into the useralbums scene
        AlbumController loaderController = loader.getController();
        loaderController.getUsername(username);

        String album = listOfAlbumsUI.getSelectionModel().getSelectedItem();
        loaderController.getAlbum(album);
        loaderController.getStage(primaryStage);
        loaderController.actuallyInitialize();
    }
    
}// end UserAlbumsController
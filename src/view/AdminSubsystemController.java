package view;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.io.File;
import java.util.ArrayList;

import app.App;


public class AdminSubsystemController {

    @FXML AnchorPane rootPaneUI;
    
    @FXML Button addUserUI;
    @FXML Button deleteUserUI;
    @FXML TextField usernameInputUI;
    @FXML ListView <String> listOfUsersUI;

    private ObservableList<String> obUsersUI;

    private ArrayList<String> userlist = new ArrayList<String>();
    private File userFolder;

    // load in method
    /** 
     * Initalizes page upon load. Loads up current usernames from file into listview
     */
    public void initialize() {
        // find and add every file in users
        userFolder = new File("./data/users");
        File[] listOfUsers = userFolder.listFiles();
        for (File file : listOfUsers) {
            if (file.isFile()) userlist.add(file.getName().substring(0, file.getName().length() - 4));
        }

        // display information on listview
        obUsersUI = FXCollections.observableArrayList(userlist);
        listOfUsersUI.setItems(obUsersUI);

        listOfUsersUI.getSelectionModel().select(0);
    }// end preload file


    
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
        String username = String.valueOf(usernameInputUI.getText());
        
        if (btn == addUserUI) addUser(username);
        else if (btn == deleteUserUI) deleteUser();
        else logOut();
    }// end of button event

    
    /** 
     * Add User adds a new user (no duplicates allowed) into the list view and into the file system
     * @param username string value of the username to be inputted
     */
    public void addUser(String username){
        if (username.equals("admin")) App.sendAlert("Invalid Username", "Username cannot be:", "admin");
        else if (username.equals("")) App.sendAlert("Invalid Username", "Username cannot be:", "empty");
        else {
            // check if inputted user already has a profile
            File[] listOfUsers = userFolder.listFiles();
            boolean createdUser = false;
            for (File file : listOfUsers) {
                if (file.isFile() && file.getName().equals(username + ".txt")) {
                    createdUser = true;
                    break;
                }
            }
            if (createdUser) App.sendAlert("Invalid Username", "Username is already in use:", username);
            else {
                File newUser = new File(userFolder, username + ".txt");
                try {
                    newUser.createNewFile();
                    userlist.add(username);
                } catch (IOException e) {
                    App.sendAlert("Create User", "Error creating user:", username);
                }
                // display information on listview
                obUsersUI = FXCollections.observableArrayList(userlist);
                listOfUsersUI.setItems(obUsersUI);
            }
        }
    }// add User method

/** 
     * Delete user, deletes username selected from the listView and from the file system
     */
    public void deleteUser() {
        String selectedUser = listOfUsersUI.getSelectionModel().getSelectedItem();

        // try to delete file from folder
        File[] listOfUsers = userFolder.listFiles();
        for (File file : listOfUsers) {
            if (file.isFile() && file.getName().equals(selectedUser + ".txt")) {
                if (file.delete()){
                    userlist.remove(selectedUser);
                    App.sendAlert("Delete User", "Deleted user:", selectedUser);
                }
                else App.sendAlert("Delete User", "Error deleting user:", selectedUser);
            }
        }

        // display information on listview
        obUsersUI = FXCollections.observableArrayList(userlist);
        listOfUsersUI.setItems(obUsersUI);
    }// delete user method

    
    /** 
     * logout selected by the logout button closes out the admin subsystem and returns back to login page
     * @throws IOException throws exception if FXML file needed cannot be accessed
     */
    private void logOut() throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));
        rootPaneUI.getChildren().setAll(pane);
    }
}

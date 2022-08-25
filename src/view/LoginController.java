package view;

import java.io.File;
import java.io.IOException;

import app.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;


public class LoginController{
    
    @FXML Pane rootPaneUI;

    @FXML Button loginButtonUI;
    @FXML TextField usernameInputUI;

    
    /** 
     * login method using param e takes in the value of the username and redirects to
     * the appropriate page once the login page is selected.
     * @param e value of the action event
     * @throws IOException exception is thrown if an incorrect file path is given
     */
    public void login(ActionEvent e) throws IOException {
        Button b = (Button)e.getSource();

        // button was pressed
        if (b == loginButtonUI) {
            String username = String.valueOf(usernameInputUI.getText());

            // username was "admin"
            if (username.equals("admin")) loadAdmin();
            else {
                // try to find user in users folder
                File userFolder = new File("./data/users");
                File[] listOfUsers = userFolder.listFiles();
                boolean foundUser = false;
                for (File file : listOfUsers) {
                    if (file.isFile() && file.getName().equals(username + ".txt")) foundUser = true;
                }
                if (foundUser) loadUser(username);
                else App.sendAlert("User not found!", "Username not in system:", username);
            }
        }
    }

    
    /** 
     * loadAdmin redirects to the admin page when called
     * @throws IOException is thrown if given an incorrect file path to the page's FXML
     */
    private void loadAdmin() throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("AdminSubsystem.fxml"));
        rootPaneUI.getChildren().setAll(pane);
    }

    
    /** 
     * load user redirects to the user's albums when it is selected in the login page
     * @param username string value of the username given for the page
     * @throws IOException throws an exception if an incorrect file path is given for the page's FXML
     */
    private void loadUser(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserAlbums.fxml"));
        rootPaneUI.getChildren().setAll((Pane)loader.load());

        // passes the username into the useralbums scene
        UserAlbumsController loaderController = loader.getController();
        loaderController.getUsername(username);
        loaderController.actuallyInitialize();
    }
}
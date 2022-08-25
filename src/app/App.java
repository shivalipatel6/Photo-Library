package app;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/*
CS213 Project 3: Photos33
By: Shivali Patel & David Dong
*/

public class App extends Application {
    
    /** 
     * Public void start method initalizes the start scene Login
     * @param primaryStage the display of the photos app
     * @throws Exception if there is incorrect filepath for the fxml
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // create FXML loader
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/Login.fxml"));
		
		// load fmxl, root layout manager in fxml file is GridPane
		Pane root = (Pane)loader.load();

		// set scene to root
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
        primaryStage.setTitle("Photos");
		primaryStage.show();
    }
    
    /** 
     * Main method
     * @param args takes in arguments from terminal for launch
     */
    public static void main(String[] args) {
		launch(args);
    }

	
    /** 
     * Helper Method used to send alerts to user
     * @param title the title of the alert 
     * @param snippet the comments held in the alert to inform the user of the reason for the alert
     * @param body information on how to format response in input textbox for alert.
     */
    // ----- Helper Methods -----
	// send an alert to the user
	public static void sendAlert(String title, String snippet, String body) {
        Alert warning = new Alert(AlertType.INFORMATION);
        // warning.initOwner(mainStage);
        warning.setTitle(title);
        warning.setHeaderText(snippet);
        warning.setContentText(body);
        warning.showAndWait();
    }

    
    /** 
     * Input Dialog used for gaining information for adding albums, photos and users etc.
     * @param title the title of the alert 
     * @param snippet the comments held in the alert to inform the user of the reason for the alert
     * @param body information on how to format response in input textbox for alert.
     * @param textInfo string in textbox that gives an example of what the input should look like
     * @return String returns user input
     */
    // send a text input dialog to the user
    public static String sendInputDialog(String title, String snippet, String body, String textInfo) {
        TextInputDialog dialog = new TextInputDialog(textInfo);
        dialog.setTitle(title);
        dialog.setHeaderText(snippet);
        dialog.setContentText(body);

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) return result.get();
        else return "||**ERROR**||";
    }
}
package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import app.App;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AlbumController {

    @FXML AnchorPane rootPaneUI;
    @FXML Button captionUI;
    @FXML Button openUI;
    @FXML Button logOutUI;
    @FXML MenuItem addPhotoUI;
    @FXML MenuItem deletePhotoUI;
    @FXML MenuItem addTagsUI;
    @FXML MenuItem deleteTagsUI;
    @FXML MenuItem copyPhotoUI;
    @FXML MenuItem movePhotoUI;
    @FXML MenuItem sortByDateUI;
    @FXML MenuItem sortByTagUI;
    @FXML MenuItem sortByDefaultUI;
    @FXML Text selectedPhotoUI;
    @FXML Text shownPhotosUI;
    
    Stage primaryStage;
    String username;
    String albumName;
    ArrayList <String> allAlbums = new ArrayList<String>();
    ArrayList <String> otherAlbums = new ArrayList<String>();
    private File userFolder;
    int albumLocation;
    String selectedPhoto;

    
   /** 
     * getUsername collects username for later use
     * @param incomingUser string value of the username
     */
    // collects the username of the album
    public void getUsername(String incomingUser) {
        username = incomingUser;
    }

    
    /** 
     * get Album collects album name for later use
     * @param incomingAlbum string value of album name
     * @throws FileNotFoundException throws exception if album controller cannot be found from 
     * where the method is called.
     */
    // collects the name of the album
    public void getAlbum(String incomingAlbum) throws FileNotFoundException {
        albumName = incomingAlbum;
    }

    
    /** 
     * get stage collects the primary information for later use 
     * @param incomingStage the display of the photos app page
     */
    // collects the current stage
    public void getStage(Stage incomingStage) {
        primaryStage = incomingStage;
    }

    
    /** 
     * Actually Initailize loads the photo information from the file system
     * @throws IOException thrown if the file in question cannot be found
     */
    // intializes allAlbums arralist and collects location of album
    public void actuallyInitialize() throws IOException{
        String filestring = "./data/users/" + username + ".txt";
        userFolder = new File(filestring);
        Scanner sc = new Scanner(userFolder);
        int arrayPosition = 0;
        
        // populate list while looking for location of album
        while(sc.hasNext()) {
            String currentLine = sc.nextLine();
            if (currentLine.substring(0, 3).equals("|+|")) {
                if (currentLine.equals("|+|" + albumName)) albumLocation = arrayPosition;
                else otherAlbums.add(currentLine.substring(3));
            }
            allAlbums.add(currentLine);
            arrayPosition++;
        }
        sc.close();

        updateFD();
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
        if (btn == captionUI) addCaption();
        else if (btn == openUI) startSlideshow();
        else logOut();
    }

    
    /** 
     * menu event holds menu style selection systems that when selected preform an action
     *@param e action event that specifies which item on the menu was selected
     * @throws IOException throws if FXML file cannot be accessed upon logout
     */
    // event for when menuitem is pressed
    public void menuEvent(ActionEvent e) throws IOException {
        MenuItem item = (MenuItem)e.getSource();
        if (item == addPhotoUI) addPhoto();
        else if (item == deletePhotoUI) deletePhoto();
        else if (item == addTagsUI) addTags();
        else if (item == deleteTagsUI) deleteTags();
        else if (item == copyPhotoUI) copyPhoto();
        else if (item == movePhotoUI) movePhoto();
        else if (item == sortByDateUI) sortByDate();
        else if (item == sortByTagUI) sortByTag();
        else if (item == sortByDefaultUI) updateFD();
    }

    
     /** 
     * logout selected by the logout button closes out the admin subsystem and returns back to login page
     * @throws IOException throws exception if FXML file needed cannot be accessed
     */
    // leaves current album
    private void logOut() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserAlbums.fxml"));
        rootPaneUI.getChildren().setAll((Pane)loader.load());

        // passes the username into the useralbums scene
        UserAlbumsController loaderController = loader.getController();
        loaderController.getUsername(username);
        loaderController.actuallyInitialize(primaryStage);
    }

    
    /** 
     * update file sends all new update to the file system for the user's albums
     * @throws IOException throws exception is file cannot be accessed
     */
    // updates file
    public void updateF() throws IOException {
        // update file
        FileWriter fw = new FileWriter(userFolder);
        for (String i : allAlbums) {
            fw.write(i + "\n");
        }
        fw.close();
    }

    
    /** 
     * update display updates the current scene with any changes to the photos or the captions
     * @param startDate array of ints that holds the start date for sort by date
     * @param endDate array of ints that holds the end date for sort by date
     * @throws IOException throws exception if file cannot be accessed
     */
    // updates display with start and end dates
    public void updateD(int[] startDate, int[] endDate) throws IOException{
        // update display
        Group root = new Group(rootPaneUI);
        int imageNumber = 0;
        ArrayList<String> filteredPhotos = new ArrayList<>();
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            // check if loop is still within album
            if (allAlbums.get(i).substring(0, 3).equals("|+|")) break;

            // collect date of image
            String date = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|d|") + 3, allAlbums.get(i).lastIndexOf("|c|"));
            String[] stringSplitDate = date.split("/");
            int[] splitDate = new int[3];
            for (int j = 0; j < stringSplitDate.length; j++) {
                splitDate[j] = Integer.parseInt(stringSplitDate[j]);
            }

            // filter images
            if (splitDate[0] < startDate[2]) {
                continue;
            } else if (splitDate[0] == startDate[2]) {
                if (splitDate[1] < startDate[0]) {
                    continue;
                } else if (splitDate[1] == startDate[0]) {
                    if (splitDate[2] < startDate[1]) {
                        continue;
                    }
                }
            }
            if (splitDate[0] > endDate[2]) {
                continue;
            } else if (splitDate[0] == endDate[2]) {
                if (splitDate[1] > endDate[0]) {
                    continue;
                } else if (splitDate[1] == endDate[0]) {
                    if (splitDate[2] < endDate[1]) {
                        continue;
                    }
                }
            }

            // add photo to filtered array
            filteredPhotos.add(allAlbums.get(i));

            // collect path to image
            String oldPath = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|p|") + 3, allAlbums.get(i).lastIndexOf("|d|"));
            String path = oldPath.replace("\\", "\\\\");

            // add image to group
            InputStream stream = null;
            try {
                stream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                stream = new FileInputStream("./docs/imageNotFound.PNG");
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setX(imageNumber % 6 * 135);
            imageView.setY(110 + imageNumber / 6 * 165);
            imageView.setFitWidth(130);
            imageView.setFitHeight(130);

            // add caption to group
            String captionString = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|c|") + 3, allAlbums.get(i).indexOf("|t|"));
            Text caption = new Text(captionString);
            if (captionString.length() >= 30) caption = new Text(captionString.substring(0, 27) + "...");
            if (captionString.equals("||**ERROR**||")) caption = new Text("*No Caption*");
            caption.setFont(new Font(15));
            caption.setWrappingWidth(130);
            caption.setTextAlignment(TextAlignment.JUSTIFY);
            caption.setX(imageNumber % 6 * 135);
            caption.setY(252 + imageNumber / 6 * 165);

            // give clickability to image
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    selectedPhoto = oldPath.substring(oldPath.lastIndexOf("\\") + 1, oldPath.lastIndexOf("."));
                    selectedPhotoUI.setText(selectedPhoto);
                    if (selectedPhoto.length() >= 38) selectedPhotoUI.setText(selectedPhoto.substring(0, 37) + "...");
                }
            });

            // add content to group and increment
            root.getChildren().add(imageView);
            root.getChildren().add(caption);
            imageNumber++;
        }

        // add button to scene
        Button newAlbum = new Button("Create Album With Filtered Photos");
        newAlbum.setLayoutX(220);
        newAlbum.setLayoutY(5);
        newAlbum.setMinHeight(30);
        newAlbum.setMinWidth(200);
        newAlbum.addEventHandler(MouseEvent.MOUSE_CLICKED, 
        new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                // check for duplicate album
                String newAlbum = App.sendInputDialog("Create Album", "Create new album with filtered photos!", "Enter album name:", "Album Name");
                if (otherAlbums.contains(newAlbum) || albumName.equals(newAlbum)) {
                    App.sendAlert("Error", "Error Creating Album", "You entered an album name that already exists: " + newAlbum);
                    return;
                }
                if (newAlbum.equals("||**ERROR**||")) {
                    App.sendAlert("Error", "Error Creating Album", "You did not pick an album name!");
                    return;
                }

                try (FileWriter fw = new FileWriter(userFolder, true)) {
                    fw.write("|+|" + newAlbum + "\n");
                    for (String photo : filteredPhotos) {
                        fw.write(photo + "\n");
                    }
                    
                    fw.close();
                    otherAlbums.add(newAlbum);
                } catch (IOException e1) {
                    App.sendAlert("Error", "Writing Error", "Error writing to user!");
                }
            }
        });
        root.getChildren().add(newAlbum);

        // display group
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        shownPhotosUI.setText("By Date");
    }

    
    /** 
     * updates display with a single tag for display of results for search by single tag
     * @param tag string tag being searched by
     * @throws IOException throws exception if file cannot be accessed
     */
    // updates display with single tag
    public void updateD(String tag) throws IOException {
        // update display
        Group root = new Group(rootPaneUI);
        int imageNumber = 0;
        ArrayList<String> filteredPhotos = new ArrayList<>();
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            // check if loop is still within album
            if (allAlbums.get(i).substring(0, 3).equals("|+|")) break;

            // collect tag of image
            String allCurrentTags = allAlbums.get(i).substring(allAlbums.get(i).indexOf("|t|"));
            if (allCurrentTags.equals("|t|||*ERROR**||")) continue;

            // find tag
            boolean foundTag = false;
            while (true) {
                allCurrentTags = allCurrentTags.substring(3, allCurrentTags.length());
                if (allCurrentTags.contains("|t|")) {
                    if (allCurrentTags.substring(0, allCurrentTags.indexOf("|t|")).equals(tag)) {
                        foundTag = true;
                        break;
                    }
                    allCurrentTags = allCurrentTags.substring(allCurrentTags.indexOf("|t|"));
                } else {
                    if (allCurrentTags.equals(tag)) {
                        foundTag = true;
                        break;
                    }
                    else break;
                }
            }
            if (!foundTag) continue;

            // add photo to filtered array
            filteredPhotos.add(allAlbums.get(i));

            // collect path to image
            String oldPath = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|p|") + 3, allAlbums.get(i).lastIndexOf("|d|"));
            String path = oldPath.replace("\\", "\\\\");

            // add image to group
            InputStream stream = null;
            try {
                stream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                stream = new FileInputStream("./docs/imageNotFound.PNG");
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setX(imageNumber % 6 * 135);
            imageView.setY(110 + imageNumber / 6 * 165);
            imageView.setFitWidth(130);
            imageView.setFitHeight(130);

            // add caption to group
            String captionString = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|c|") + 3, allAlbums.get(i).indexOf("|t|"));
            Text caption = new Text(captionString);
            if (captionString.length() >= 30) caption = new Text(captionString.substring(0, 27) + "...");
            if (captionString.equals("||**ERROR**||")) caption = new Text("*No Caption*");
            caption.setFont(new Font(15));
            caption.setWrappingWidth(130);
            caption.setTextAlignment(TextAlignment.JUSTIFY);
            caption.setX(imageNumber % 6 * 135);
            caption.setY(252 + imageNumber / 6 * 165);

            // give clickability to image
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    selectedPhoto = oldPath.substring(oldPath.lastIndexOf("\\") + 1, oldPath.lastIndexOf("."));
                    selectedPhotoUI.setText(selectedPhoto);
                    if (selectedPhoto.length() >= 38) selectedPhotoUI.setText(selectedPhoto.substring(0, 37) + "...");
                }
            });

            // add content to group and increment
            root.getChildren().add(imageView);
            root.getChildren().add(caption);
            imageNumber++;
        }

        // add button to scene
        Button newAlbum = new Button("Create Album With Filtered Photos");
        newAlbum.setLayoutX(220);
        newAlbum.setLayoutY(5);
        newAlbum.setMinHeight(30);
        newAlbum.setMinWidth(200);
        newAlbum.addEventHandler(MouseEvent.MOUSE_CLICKED, 
        new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                // check for duplicate album
                String newAlbum = App.sendInputDialog("Create Album", "Create new album with filtered photos!", "Enter album name:", "Album Name");
                if (otherAlbums.contains(newAlbum) || albumName.equals(newAlbum)) {
                    App.sendAlert("Error", "Error Creating Album", "You entered an album name that already exists: " + newAlbum);
                    return;
                }
                if (newAlbum.equals("||**ERROR**||")) {
                    App.sendAlert("Error", "Error Creating Album", "You did not pick an album name!");
                    return;
                }

                try (FileWriter fw = new FileWriter(userFolder, true)) {
                    fw.write("|+|" + newAlbum + "\n");
                    for (String photo : filteredPhotos) {
                        fw.write(photo + "\n");
                    }
                    
                    fw.close();
                    otherAlbums.add(newAlbum);
                } catch (IOException e1) {
                    App.sendAlert("Error", "Writing Error", "Error writing to user!");
                }
            }
        });
        root.getChildren().add(newAlbum);

        // display group
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        shownPhotosUI.setText("By Tag");
    }

    
    /** 
     * update display for search by two tags
     * @param tag array of strings with the two tags to be sorted by
     * @param operation boolean value of operation between the two tags (such as && or ||)
     * @throws IOException throws exception if file cannot be accessed
     */
    // updates display with two tag
    public void updateD(String[] tag, boolean operation) throws IOException {
        // update display
        Group root = new Group(rootPaneUI);
        int imageNumber = 0;
        ArrayList<String> filteredPhotos = new ArrayList<>();
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            // check if loop is still within album
            if (allAlbums.get(i).substring(0, 3).equals("|+|")) break;

            // collect tag of image
            String allCurrentTags = allAlbums.get(i).substring(allAlbums.get(i).indexOf("|t|"));
            if (allCurrentTags.equals("|t|||*ERROR**||")) continue;

            // find tag
            boolean foundTag = false;
            if (!operation) {
                while (true) {
                    allCurrentTags = allCurrentTags.substring(3, allCurrentTags.length());
                    if (allCurrentTags.contains("|t|")) {
                        if (allCurrentTags.substring(0, allCurrentTags.indexOf("|t|")).equals(tag[0]) || allCurrentTags.substring(0, allCurrentTags.indexOf("|t|")).equals(tag[1])) {
                            foundTag = true;
                            break;
                        }
                        allCurrentTags = allCurrentTags.substring(allCurrentTags.indexOf("|t|"));
                    } else {
                        if (allCurrentTags.equals(tag[0]) || allCurrentTags.equals(tag[1])) {
                            foundTag = true;
                            break;
                        }
                        else break;
                    }
                }
            } else {
                boolean zeroSatisfied = false;
                boolean oneSatisfied = false;
                while (true) {
                    allCurrentTags = allCurrentTags.substring(3, allCurrentTags.length());
                    if (allCurrentTags.contains("|t|")) {
                        if (allCurrentTags.substring(0, allCurrentTags.indexOf("|t|")).equals(tag[0])) {
                            zeroSatisfied = true;
                        } else if (allCurrentTags.substring(0, allCurrentTags.indexOf("|t|")).equals(tag[1])) {
                            oneSatisfied = true;
                        }
                        allCurrentTags = allCurrentTags.substring(allCurrentTags.indexOf("|t|"));
                    } else {
                        if (allCurrentTags.equals(tag[0])) {
                            zeroSatisfied = true;
                            break;
                        } else if (allCurrentTags.equals(tag[1])) {
                            oneSatisfied = true;
                        }
                        else break;
                    }
                }
                if (zeroSatisfied && oneSatisfied) foundTag = true;
            }
            if (!foundTag) continue;

            // add photo to filtered array
            filteredPhotos.add(allAlbums.get(i));

            // collect path to image
            String oldPath = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|p|") + 3, allAlbums.get(i).lastIndexOf("|d|"));
            String path = oldPath.replace("\\", "\\\\");

            // add image to group
            InputStream stream = null;
            try {
                stream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                stream = new FileInputStream("./docs/imageNotFound.PNG");
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setX(imageNumber % 6 * 135);
            imageView.setY(110 + imageNumber / 6 * 165);
            imageView.setFitWidth(130);
            imageView.setFitHeight(130);

            // add caption to group
            String captionString = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|c|") + 3, allAlbums.get(i).indexOf("|t|"));
            Text caption = new Text(captionString);
            if (captionString.length() >= 30) caption = new Text(captionString.substring(0, 27) + "...");
            if (captionString.equals("||**ERROR**||")) caption = new Text("*No Caption*");
            caption.setFont(new Font(15));
            caption.setWrappingWidth(130);
            caption.setTextAlignment(TextAlignment.JUSTIFY);
            caption.setX(imageNumber % 6 * 135);
            caption.setY(252 + imageNumber / 6 * 165);

            // give clickability to image
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    selectedPhoto = oldPath.substring(oldPath.lastIndexOf("\\") + 1, oldPath.lastIndexOf("."));
                    selectedPhotoUI.setText(selectedPhoto);
                    if (selectedPhoto.length() >= 38) selectedPhotoUI.setText(selectedPhoto.substring(0, 37) + "...");
                }
            });

            // add content to group and increment
            root.getChildren().add(imageView);
            root.getChildren().add(caption);
            imageNumber++;
        }

        // add button to scene
        Button newAlbum = new Button("Create Album With Filtered Photos");
        newAlbum.setLayoutX(220);
        newAlbum.setLayoutY(5);
        newAlbum.setMinHeight(30);
        newAlbum.setMinWidth(200);
        newAlbum.addEventHandler(MouseEvent.MOUSE_CLICKED, 
        new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                // check for duplicate album
                String newAlbum = App.sendInputDialog("Create Album", "Create new album with filtered photos!", "Enter album name:", "Album Name");
                if (otherAlbums.contains(newAlbum) || albumName.equals(newAlbum)) {
                    App.sendAlert("Error", "Error Creating Album", "You entered an album name that already exists: " + newAlbum);
                    return;
                }
                if (newAlbum.equals("||**ERROR**||")) {
                    App.sendAlert("Error", "Error Creating Album", "You did not pick an album name!");
                    return;
                }

                try (FileWriter fw = new FileWriter(userFolder, true)) {
                    fw.write("|+|" + newAlbum + "\n");
                    for (String photo : filteredPhotos) {
                        fw.write(photo + "\n");
                    }
                    
                    fw.close();
                    otherAlbums.add(newAlbum);
                } catch (IOException e1) {
                    App.sendAlert("Error", "Writing Error", "Error writing to user!");
                }
            }
        });
        root.getChildren().add(newAlbum);

        // display group
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        shownPhotosUI.setText("By Tag");
    }

    
    /** 
     * updates file and display for the cases of adding or deleting photos
     * @throws IOException throws exception if file cannot be accessed
     */
    // updates file and display
    public void updateFD() throws IOException {
        updateF();

        // update display
        Group root = new Group(rootPaneUI);
        int imageNumber = 0;
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            // check if loop is still within album
            if (allAlbums.get(i).substring(0, 3).equals("|+|")) break;

            // collect path to image
            String oldPath = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|p|") + 3, allAlbums.get(i).lastIndexOf("|d|"));
            String path = oldPath.replace("\\", "\\\\");

            // add image to group
            InputStream stream = null;
            try {
                stream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                stream = new FileInputStream("./docs/imageNotFound.PNG");
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setX(imageNumber % 6 * 135);
            imageView.setY(110 + imageNumber / 6 * 165);
            imageView.setFitWidth(130);
            imageView.setFitHeight(130);

            // add caption to group
            String captionString = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|c|") + 3, allAlbums.get(i).indexOf("|t|"));
            Text caption = new Text(captionString);
            if (captionString.length() >= 30) caption = new Text(captionString.substring(0, 27) + "...");
            if (captionString.equals("||**ERROR**||")) caption = new Text("*No Caption*");
            caption.setFont(new Font(15));
            caption.setWrappingWidth(130);
            caption.setTextAlignment(TextAlignment.JUSTIFY);
            caption.setX(imageNumber % 6 * 135);
            caption.setY(252 + imageNumber / 6 * 165);

            // give clickability to image
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    selectedPhoto = oldPath.substring(oldPath.lastIndexOf("\\") + 1, oldPath.lastIndexOf("."));
                    selectedPhotoUI.setText(selectedPhoto);
                    if (selectedPhoto.length() >= 38) selectedPhotoUI.setText(selectedPhoto.substring(0, 37) + "...");
                }
            });

            // add content to group and increment
            root.getChildren().add(imageView);
            root.getChildren().add(caption);
            imageNumber++;
        }

        // display group
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        shownPhotosUI.setText("All");
    }

    // add photo
    public void addPhoto() {
        // collect photo file using FileChooser
        FileChooser photoChooser = new FileChooser();
        photoChooser.setTitle("Open Photo");
        Stage newStage = new Stage();
        File selectedPhoto = photoChooser.showOpenDialog(newStage);

        String photoPath = "";
        // check if file is appropriate for photo
        if (selectedPhoto != null) {
            photoPath = selectedPhoto.getAbsolutePath();
            String photoType = photoPath.substring(photoPath.lastIndexOf(".") + 1).toUpperCase(); //toUppserCase()?
            if (!photoType.equals("BMP") && !photoType.equals("GIF") && !photoType.equals("JPEG") && !photoType.equals("PNG")) {
                App.sendAlert("Error", "Error Collecting Photo", "Incorrect file type: " + photoType);
                return;
            }
            for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
                if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
                String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|p|") + 3, allAlbums.get(i).lastIndexOf("|d|"));
                if (currentPhoto.equals(photoPath)){
                    App.sendAlert("Error", "Error Collecting Photo", "Duplicate photo already exists in album!");
                    return;
                }
            }
        } else {
            App.sendAlert("Error", "Error Collecting Photo", "You did not pick a image.");
            return;
        }

        // collect caption and tags
        String captionString = App.sendInputDialog("Caption (Optional)", "Adding Caption", "Enter caption for photo: (Cancel For No Caption)", "Best day ever!");
        String tagsString = App.sendInputDialog("Tag(s) (Optional)", "Adding Tag(s). Separate tags using ':'.", "Enter tag(s) for photo: (Cancel For No Tags)", "person=you:location=here");
        
        String[] tags = {};
        // check tag authenticity
        if (!tagsString.equals("||**ERROR**||")) {
            tags = tagsString.split(":");
            for (String tag : tags) {
                String[] tagContent = tag.split("=");
                if (tagContent.length != 2 || tagContent[0].equals("") || tagContent[1].equals("")) {
                    App.sendAlert("Error", "Incorrect Tag | Correct Tag Format: *type*=*object*", "Incorrect Tag: " + tag);
                    return;
                }
            }
            for (int i = 0; i < tags.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (tags[i].equals(tags[j])) {
                        App.sendAlert("Error", "Duplicate Tags", "Duplicate Tag: " + tags[j]);
                        return;
                    }
                }
            }
        }

        // collect date
        Calendar rightNow = Calendar.getInstance();
        String time = rightNow.get(Calendar.YEAR) + "/" + rightNow.get(Calendar.MONTH) + "/" + rightNow.get(Calendar.DAY_OF_MONTH); // set second/minutes/milliseconds?

        // create string of photo
        String photoString = "|p|" + photoPath + "|d|" + time + "|c|" + captionString;
        if (tagsString.equals("||**ERROR**||")) photoString = photoString + "|t|" + tagsString;
        else for (String tag : tags) photoString = photoString + "|t|" + tag;

        // add to arrayList, file, and show photo
        allAlbums.add(albumLocation + 1, photoString);
        try {
            updateFD();
        } catch(Exception e) {
            App.sendAlert("Error", "Error Writing To File", "File for user does not exist!");
        }
    }

    
    /** 
     * addCaption adds a caption for a photo and echos the change to all other albums with that same photo
     * @throws IOException throws exception if file cannot be accessed
     */
    // add caption to photo
    public void addCaption() throws IOException {
        String currentPhoto = "";
        String captionString = App.sendInputDialog("Caption", "Adding Caption", "Enter caption for photo:", "Best day ever!");
        // selectedPhoto
        for (int i = 0; i < allAlbums.size(); i++) {
            if (allAlbums.get(i).substring(0, 3).equals("|+|")) continue;
            currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).lastIndexOf("."));
            if (currentPhoto.equals(selectedPhoto)){
                String tagString = allAlbums.get(i).substring(allAlbums.get(i).indexOf("|t|"));
                Calendar rightNow = Calendar.getInstance();
                String time = rightNow.get(Calendar.YEAR) + "/" + rightNow.get(Calendar.MONTH) + "/" + rightNow.get(Calendar.DAY_OF_MONTH); // set second/minutes/milliseconds?
                String photoStringPlusDate = allAlbums.get(i).substring(allAlbums.get(i).indexOf("|p|"), allAlbums.get(i).indexOf("|d|")) + "|d|" + time;
                String temp = photoStringPlusDate + "|c|" + captionString + tagString;
                allAlbums.set(i, temp);
            }
        }// end for loop
 
        updateFD();

    }

    
    /** 
     * starts slide show scene where image is shown larger with a click through function
     * @throws IOException throws exception if file cannot be accessed
     */
    // starts slideshow
    public void startSlideshow() throws IOException {
        // check if a photo is selected
        if (selectedPhoto == null) {
            App.sendAlert("Error", "Error Opening Photo", "No photo selected!");
            return;
        }
        
        String photoPath = "";
        // find selected photo
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
            String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
            if (currentPhoto.equals(selectedPhoto)){
                String oldPath = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("|p|") + 3, allAlbums.get(i).lastIndexOf("|d|"));
                photoPath = oldPath.replace("\\", "\\\\");
                break;
            }
        }

        // create group and fill with image
        Group root = new Group();
        InputStream stream = null;
        try {
            stream = new FileInputStream(photoPath);
        } catch (FileNotFoundException e) {
            stream = new FileInputStream("./docs/imageNotFound.PNG");
        }
        Image image = new Image(stream);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(80);
        imageView.setY(80);
        imageView.setFitWidth(650);
        imageView.setFitHeight(500);
        imageView.setSmooth(true);
        root.getChildren().add(imageView);

        // fill group with left and right buttons
        Button leftImage = new Button("<");
        leftImage.setLayoutX(10);
        leftImage.setLayoutY(300);
        leftImage.setMinHeight(50);
        leftImage.setMinWidth(50);
        leftImage.addEventHandler(MouseEvent.MOUSE_CLICKED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
                        if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
                        String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
                        if (currentPhoto.equals(selectedPhoto)){
                            if (allAlbums.get(i - 1) == null || allAlbums.get(i - 1).substring(0, 3).equals("|+|")) {
                                App.sendAlert("Slideshow", "End of Album", "This is the farthest left you can go!");
                            } else {
                                String nextPhoto = allAlbums.get(i - 1).substring(allAlbums.get(i - 1).lastIndexOf("\\") + 1, allAlbums.get(i - 1).indexOf("."));
                                selectedPhoto = nextPhoto;
                                try {
                                    startSlideshow();
                                } catch (IOException e1) {
                                    App.sendAlert("Error", "Error Changing Image", "Error going to left image!");
                                }
                            }
                            break;
                        }
                    }
                }
            }
        );
        Button rightImage = new Button(">");
        rightImage.setLayoutX(740);
        rightImage.setLayoutY(300);
        rightImage.setMinHeight(50);
        rightImage.setMinWidth(50);
        rightImage.addEventHandler(MouseEvent.MOUSE_CLICKED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
                        if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
                        String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
                        if (currentPhoto.equals(selectedPhoto)){
                            if (i + 1 >= allAlbums.size() || allAlbums.get(i + 1) == null || allAlbums.get(i + 1).substring(0, 3).equals("|+|")) {
                                App.sendAlert("Slideshow", "End of Album", "This is the farthest right you can go!");
                            } else {
                                String nextPhoto = allAlbums.get(i + 1).substring(allAlbums.get(i + 1).lastIndexOf("\\") + 1, allAlbums.get(i + 1).indexOf("."));
                                selectedPhoto = nextPhoto;
                                try {
                                    startSlideshow();
                                } catch (IOException e1) {
                                    App.sendAlert("Error", "Error Changing Image", "Error going to right image!");
                                }
                            }
                            break;
                        }
                    }
                }
            }
        );
        root.getChildren().add(leftImage);
        root.getChildren().add(rightImage);

        // fill group with exit button
        Button close = new Button("Close");
        close.setFont(new Font(20));
        close.setLayoutX(370);
        close.setLayoutY(20);
        close.setMinHeight(50);
        close.setMinWidth(80);
        close.addEventHandler(MouseEvent.MOUSE_CLICKED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    try {
                        updateFD();
                    } catch (IOException e1) {
                        App.sendAlert("Error", "Album Error", "Error going back to album!");
                    }
                }
            }
        );
        root.getChildren().add(close);

        // display group
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    /** 
     * deletes a selected photo from the album 
     * @throws IOException throws exception if file cannot be accessed
     */
    // delete photo
    public void deletePhoto() throws IOException {
        // check if a photo is selected
        if (selectedPhoto == null) {
            App.sendAlert("Error", "Error Deleting Photo", "No photo selected!");
            return;
        }

        String currentPhoto = "";
        // selectedPhoto
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
            currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
            if (currentPhoto.equals(selectedPhoto)){
                selectedPhoto = null;
                selectedPhotoUI.setText("None");
                allAlbums.remove(i);
                break;
            }
        }// for loop to find the right image

        updateFD();
    }// end delete photos


     /** 
     * Adds new tags for selected photo and echos changes to all other photos with the same
     * path in all of the user' albums
     */
    // add tags
    public void addTags() {      
        if (selectedPhoto == null) {
            App.sendAlert("Error", "Adding Tags", "No photo selected!");
            
            return;
        }

        String currentPhoto = "";
       // selectedPhoto
       
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
            currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
           
            if (currentPhoto.equals(selectedPhoto)){
            String tagsString = App.sendInputDialog("Tag(s) (Optional)", "Adding Tag(s). Separate tags using ':'.", "Enter tag(s) for photo.", "person=you:location=here");
            String photoString = allAlbums.get(i);
            // collect date
            Calendar rightNow = Calendar.getInstance();
            String time = rightNow.get(Calendar.YEAR) + "/" + rightNow.get(Calendar.MONTH) + "/" + rightNow.get(Calendar.DAY_OF_MONTH);
            photoString = photoString.substring(0, photoString.indexOf("|d|") + 3) + time + photoString.substring(photoString.indexOf("|c|"));
            String oldtagstring = allAlbums.get(i).substring((allAlbums.get(i).indexOf("|t|")+3));
            if(oldtagstring.equals("||**ERROR**||")){
                photoString = allAlbums.get(i).substring(0,(allAlbums.get(i).indexOf("|t|")));
            }
            ArrayList<String> oldtags = new ArrayList<String>();
            String[] tags = {};
            // check tag authenticity
           while(oldtagstring.length() != 0){
                
                if(oldtagstring.contains("|t|")){
                    String tagToSave = oldtagstring.substring(0,oldtagstring.indexOf("|t|")); 
                    oldtags.add(tagToSave);
                    oldtagstring = oldtagstring.substring(oldtagstring.indexOf("|t|")+3);
                }
                else{
                    oldtags.add(oldtagstring);
                    oldtagstring = "";

                }

            }// while loop for saving old tags

             
            if (!tagsString.equals("||**ERROR**||")) {
                tags = tagsString.split(":");
                for (String tag : tags) {
                    String[] tagContent = tag.split("=");
                    if (tagContent.length != 2 || tagContent[0].equals("") || tagContent[1].equals("")) {
                        App.sendAlert("Error", "Incorrect Tag | Correct Tag Format: *type*=*object*", "Incorrect Tag: " + tag);
                        return;
                    }
                }
                //checks for duplicate tags in submission
                for (int k = 0; k < tags.length; k++) {
                    for (int j = 0; j < k; j++) {
                        if (tags[k].equals(tags[j])) {

                            App.sendAlert("Error", "Duplicate Tags", "Duplicate Tag: " + tags[j]);
                            return;
                        }
                    }
                }
                //checks for duplicates against existing tags
                for(int y = 0; y <oldtags.size(); y++){
                    for(int x = 0; x <tags.length; x++){
                        if(tags[x].equals(oldtags.get(y))){
                            App.sendAlert("Error", "Duplicate Tags", "Duplicate Tag: " + tags[x]);
                            return;

                        }
                    }

                }


            }
            for (String tag : tags) photoString = photoString + "|t|" + tag;
            for(int u = 0; u < allAlbums.size(); u++){
                
                if (allAlbums.get(u).substring(0, 3).equals("|+|")) continue;
                currentPhoto = allAlbums.get(u).substring(allAlbums.get(u).lastIndexOf("\\") + 1, allAlbums.get(u).lastIndexOf("."));
                if (currentPhoto.equals(selectedPhoto)){
                    String photoStringPlusDate = allAlbums.get(u).substring(allAlbums.get(u).indexOf("|p|"), allAlbums.get(u).indexOf("|t|") );
                   
                    allAlbums.set(u, photoString);
                }
            }
         
                break;
            }
        }// for loop to find the right image
        try {
            updateFD();
        } catch(Exception e) {
            App.sendAlert("Error", "Error Writing To File", "File for user does not exist!");
        }
        
    }// end add tags

    
    /** 
     * deletes selected tag from the selected photo as well as all other occurances of 
     * the photo in the user's album
     * @throws IOException throws exception if file cannot be accessed
     */
    // delete tags
    public void deleteTags() throws IOException {
        System.out.println("deleteTags");
        if (selectedPhoto == null) {
            App.sendAlert("Error", "Error Deleting Tag", "No photo selected!");
            return;
        }

        String currentPhoto = "";
        // selectedPhoto
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
            currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
            if (currentPhoto.equals(selectedPhoto)){
                String photoString = allAlbums.get(i).substring(0,allAlbums.get(i).indexOf("|t|"));
                // collect date
                Calendar rightNow = Calendar.getInstance();
                String time = rightNow.get(Calendar.YEAR) + "/" + rightNow.get(Calendar.MONTH) + "/" + rightNow.get(Calendar.DAY_OF_MONTH);
                photoString = photoString.substring(0, photoString.indexOf("|d|") + 3) + time + photoString.substring(photoString.indexOf("|c|"));
                String oldtagstring = allAlbums.get(i).substring((allAlbums.get(i).indexOf("|t|")+3));
                System.out.println(oldtagstring);
                if(oldtagstring.equals("||**ERROR**||")){
                    App.sendAlert("Error", "Error Deleting Tag", "There are no tags to delete!");
                    return;

                    }
                    ArrayList<String> oldtags = new ArrayList<String>();
                    String displaytags = "";
                    boolean firstpass = true;
                    // load all tags held
                   while(oldtagstring.length() != 0){
                        
                        if(oldtagstring.contains("|t|")){
                            String tagToSave = oldtagstring.substring(0,oldtagstring.indexOf("|t|")); 
                            if(firstpass){
                                displaytags = displaytags + tagToSave;  
                                firstpass = false;
                            }else{
                                displaytags = displaytags + ", " + tagToSave;
                            }
                            oldtags.add(tagToSave);
                            oldtagstring = oldtagstring.substring(oldtagstring.indexOf("|t|")+3);
                        }
                        else{
                            displaytags = displaytags + ", " + oldtagstring;
                            oldtags.add(oldtagstring);
                            oldtagstring = "";
        
                        }
        
                    }// while loop for saving old tags

                String tagsString = App.sendInputDialog("Tag", "Deleting Tag(s) \n These are the tags available for deletion: " + displaytags,
                "Enter one (1) tag for deletion", "person=you");
                boolean deleted = false;
                for(int j =0; j < oldtags.size(); j++ ){
                    if(tagsString.equals(oldtags.get(j))){
                        // delete sequence
                        oldtags.remove(tagsString);
                        deleted = true;

                    }

                }
                if(deleted == false){
                    App.sendAlert("Error", "Error Deleting Tag", "This tag does not exist!");
                    return;  
                }
                System.out.println(oldtags.size());
                if(oldtags.size() == 0){
                    oldtags.add("||**ERROR**||");
                }
                    for (int k = 0; k < oldtags.size(); k++) photoString = photoString + "|t|" + oldtags.get(k);
                
                    for(int u = 0; u < allAlbums.size(); u++){
                
                        if (allAlbums.get(u).substring(0, 3).equals("|+|")) continue;
                        currentPhoto = allAlbums.get(u).substring(allAlbums.get(u).lastIndexOf("\\") + 1, allAlbums.get(u).lastIndexOf("."));
                        if (currentPhoto.equals(selectedPhoto)){
                            String photoStringPlusDate = allAlbums.get(u).substring(allAlbums.get(u).indexOf("|p|"), allAlbums.get(u).indexOf("|t|") );
                           
                            allAlbums.set(u, photoString);
                        }
                    }



                break;
            }
        }// for loop to find the right image
        updateFD();

    }// end delete tags

    
    /** 
     * copies selected photo into another selected album
     * @throws IOException throws exception if file cannot be accessed
     */
    // copy photo to another album
    public void copyPhoto() throws IOException {
        // check if user has other albums
        if (otherAlbums.size() == 0) {
            App.sendAlert("Error", "Error Copying Photo", "You don't have another album!");
            return;
        }

        // check if user has picked an image
        if (selectedPhoto == null) {
            App.sendAlert("Error", "Error Copying Photo", "No photo selected!");
            return;
        }

        // send dialog to ask for album to move to
        String otherAlbumsString = "";
        boolean firstAlbum = true;
        for (String album : otherAlbums) {
            if (firstAlbum) {
                otherAlbumsString = album;
                firstAlbum = false;
            } else otherAlbumsString = otherAlbumsString + ", " + album;
        }
        String newAlbum = App.sendInputDialog("Copying Photo", "Availabile Albums: " + otherAlbumsString, "Choose album to copy to: ", "Album Name");

        // check if user put a correct input
        if (!otherAlbums.contains(newAlbum)) {
            App.sendAlert("Error", "Error Copying Photo", "You did not pick a valid album!");
            return;
        }
        int newAlbumLocation = 0;
        boolean foundNewAlbum = false;
        for (int i = 0; i < allAlbums.size(); i++) {
            if (foundNewAlbum) {
                if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
                String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
                if (currentPhoto.equals(selectedPhoto)){
                    App.sendAlert("Error", "Error Copying Photo", "That album already has this photo: " + selectedPhoto);
                    return;
                }
            }
            else if (allAlbums.get(i).equals("|+|" + newAlbum)) {
                newAlbumLocation = i + 1;
                foundNewAlbum = true;
            }
        }

        // collect photo information
        String photoInformation = "";
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
            String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
            if (currentPhoto.equals(selectedPhoto)){
                photoInformation = allAlbums.get(i);
                break;
            }
        }

        // add photo to other album
        allAlbums.add(newAlbumLocation, photoInformation);

        // update file
        updateF();
    }

    
    /** 
     * moves selected photo into another selected album
     * @throws IOException throws exception if file cannot be accessed
     */
    // move photo to another album
    public void movePhoto() throws IOException {
        // check if user has other albums
        if (otherAlbums.size() == 0) {
            App.sendAlert("Error", "Error Moving Photo", "You don't have another album!");
            return;
        }

        // check if user has picked an image
        if (selectedPhoto == null) {
            App.sendAlert("Error", "Error Moving Photo", "No photo selected!");
            return;
        }

        // send dialog to ask for album to move to
        String otherAlbumsString = "";
        boolean firstAlbum = true;
        for (String album : otherAlbums) {
            if (firstAlbum) {
                otherAlbumsString = album;
                firstAlbum = false;
            } else otherAlbumsString = otherAlbumsString + ", " + album;
        }
        String newAlbum = App.sendInputDialog("Moving Photo", "Availabile Albums: " + otherAlbumsString, "Choose album to move to: ", "Album Name");

        // check if user put a correct input
        if (!otherAlbums.contains(newAlbum)) {
            App.sendAlert("Error", "Error Moving Photo", "You did not pick a valid album!");
            return;
        }
        int newAlbumLocation = 0;
        boolean foundNewAlbum = false;
        for (int i = 0; i < allAlbums.size(); i++) {
            if (foundNewAlbum) {
                if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
                String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
                if (currentPhoto.equals(selectedPhoto)){
                    App.sendAlert("Error", "Error Moving Photo", "That album already has this photo: " + selectedPhoto);
                    return;
                }
            }
            else if (allAlbums.get(i).equals("|+|" + newAlbum)) {
                newAlbumLocation = i + 1;
                foundNewAlbum = true;
            }
        }

        // collect photo information
        String photoInformation = "";
        for (int i = albumLocation + 1; i < allAlbums.size(); i++) {
            if (allAlbums.get(i) == null || allAlbums.get(i).substring(0, 3).equals("|+|")) break;
            String currentPhoto = allAlbums.get(i).substring(allAlbums.get(i).lastIndexOf("\\") + 1, allAlbums.get(i).indexOf("."));
            if (currentPhoto.equals(selectedPhoto)){
                photoInformation = allAlbums.get(i);
                allAlbums.remove(i);
                break;
            }
        }

        // add photo to other album
        allAlbums.add(newAlbumLocation, photoInformation);

        // update file
        updateFD();
    }

    
    /** 
     * sort by date function takes in the start date and end date by alert and provides a new scene with all
     * applicable photos
     * @throws IOException throws exception if file cannot be accessed
     */
    // sort photos by date
    public void sortByDate() throws IOException {
        // receive date
        String userDates = App.sendInputDialog("Sort By Date", "Sorting photos by date: Start-End", "Enter Date: ", "1/1/2022-4/17/2022");

        // check for invalid inputs and collect dates
        String[] twoDates = userDates.split("-");
        if (twoDates.length != 2) {
            App.sendAlert("Error", "Error Sorting By Date", "You sent an invalid input!");
            return;
        }
        String[] startDateString = twoDates[0].split("/");
        String[] endDateString = twoDates[1].split("/");
        if (startDateString.length != 3 || endDateString.length != 3) {
            App.sendAlert("Error", "Error Sorting By Date", "You sent an invalid input: " + userDates);
            return;
        }

        // convert string dates to int
        int[] startDate = new int[3];
        int[] endDate = new int[3];
        for (int i = 0; i < startDateString.length; i++) {
            try {
                startDate[i] = Integer.parseInt(startDateString[i]);
            } catch (NumberFormatException e) {
                App.sendAlert("Error", "Error Sorting By Date", "You sent an invalid input: " + userDates);
                return;
            }
        }
        for (int i = 0; i < endDateString.length; i++) {
            try {
                endDate[i] = Integer.parseInt(endDateString[i]);
            } catch (NumberFormatException e) {
                App.sendAlert("Error", "Error Sorting By Date", "You sent an invalid input: " + userDates);
                return;
            }
        }

        // update display with dates
        updateD(startDate, endDate);
    }

    
    /** 
     * sort by tag takes in tags to sort by via alert and provides a scene with all the 
     * applicable photos
     * @throws IOException throws exception if file cannot be accessed
     */
    // sort photos by tags
    public void sortByTag() throws IOException {
        // receive date
        String userTags = App.sendInputDialog("Sort By Tag(s)", "Sort by Tag(s)! Use *type*=*object* as the format and separate using congunctive AND or disjuntive OR!", "Enter Tag(s): ", "person=andrew AND person=maya");
        if (userTags.contains("||**ERROR**||")) {
            App.sendAlert("Error", "Error Sorting By Tag", "You did not enter a tag!");
            return;
        }

        if (!userTags.contains("AND") && !userTags.contains("OR")) {
            if (!userTags.contains("=")) {
                App.sendAlert("Error", "Error Sorting By Tag", "You sent an invalid input: " + userTags);
                return;
            }
            updateD(userTags);
        } else {
            boolean operation = true;
            String[] tags;
            if (userTags.contains("AND")) {
                tags = userTags.split("AND");
                tags[0] = tags[0].substring(0, tags[0].length() - 1);
                tags[1] = tags[1].substring(1);
                operation = true;
            } else {
                tags = userTags.split("OR");
                tags[0] = tags[0].substring(0, tags[0].length() - 1);
                tags[1] = tags[1].substring(1);
                operation = false;
            }
            updateD(tags, operation);
        }
    }
}

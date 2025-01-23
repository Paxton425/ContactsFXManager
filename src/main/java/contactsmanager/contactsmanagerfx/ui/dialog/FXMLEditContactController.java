/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.ui.dialog;

/**
 *
 * @author admin
 */

import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.contacts.ContactBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

public class FXMLEditContactController extends DialogWindow implements Initializable {

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TextField editNameField;
    @FXML
    private TextField editNumberField;
    @FXML
    private TextField editEmailField;
    @FXML
    private TextField editStreetField;
    @FXML
    private TextField editCityField;
    @FXML
    private TextField editStateField;
    @FXML
    private TextField editZipCodeField;

    @FXML
    private Button saveButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Circle imageCircle;
    @FXML
    private Button editImageButton;
    @FXML
    private ContextMenu editImageMenu;

    private Contact preUpdatedContact;
    private Contact updatedContact;
    
    //Image dimentions and tollerance
    private static final int MIN_WIDTH = 150;
    private static final int MIN_HEIGHT = 150;
    private static final double DESIRED_ASPECT_RATIO = 1.0; // Square
    private static final double TOLERANCE = 0.01;
    
    private String oldImageName;
    private String imageName;
    private String imagePath;
    private String rootImagePath = ("C:\\Users\\admin\\IdeaProjects\\ContactsMnagerFX\\src\\main\\resources\\contactsmanager\\contactsmanagerfx\\images\\contactimages\\");

    private boolean contactModified = false;

    public EventHandler<ActionEvent> onSaveChanges;
    public void setOnSaveChanges(EventHandler<ActionEvent> handler){this.onSaveChanges = handler;}


    Alerts alerts = new Alerts();
    
    @FXML
    public void handleSaveContactEditAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String errorMessage = "";

        if (editNameField.getText().isEmpty() || editNumberField.getText().isEmpty()) {
            errorMessage = "You Must Enter all Compulsory Fields";
        } else if (!isOnlyDigits(editNumberField.getText())) {
            errorMessage = "Phone number must contain digits only";
        } else if (!editZipCodeField.getText().isEmpty() && !isOnlyDigits(editZipCodeField.getText())) {
            errorMessage = "Zip code must contain digits only";
        } else if (!editEmailField.getText().isEmpty() && !editEmailField.getText().contains("@")) {
            errorMessage = "You have entered an incorrect email format";
        }
        if (!errorMessage.isEmpty())
            alerts.showErrorAlert(errorMessage);
        else if(preUpdatedContact != null ){
            ContactBuilder contactBuilder = new ContactBuilder();
            contactBuilder.setId(preUpdatedContact.Id);
            contactBuilder.setName(editNameField.getText().trim());
            contactBuilder.setPhone(editNumberField.getText().trim());
            contactBuilder.setEmail(editEmailField.getText().trim().toLowerCase());
            contactBuilder.setAddress(
                    editStreetField.getText().trim(),
                    editCityField.getText().trim(),
                    editStateField.getText().trim(),
                    editZipCodeField.getText().trim());
            updatedContact = contactBuilder.getContact();

            if (imageName != null && imageName != "default.png") {
                contactBuilder.setImage(imageName);
                copyImage(imagePath);
            } else {
                contactBuilder.setImage("default.png");
            }
            try{ //Save contact
                if(updatedContact != null) {
                    onSaveChanges.handle(event);
                } else{
                    alerts.showErrorAlert("No data from updates found.");
                    throw new NullPointerException("Updated Contact is Null");
                }
            }
            catch(Exception e){
                alerts.showErrorAlert("Something went wrong");
                e.printStackTrace();
            }
        }
        else {
            alerts.showErrorAlert("No data from pre-updates found.");
            throw new NullPointerException("Pre-updated contact is null");
        }
    }

    @FXML
    private void handleInputChanged(ActionEvent event){
        contactModified=true;
        refreshButton.setDisable(false);
    }

    @Override
    public void setContactsManager(ContactsManager manager){
        this.manager = manager;
    }
    public Contact getUpdatedContact(){
        return updatedContact;
    }
    public void setPreUpdatedContact(Contact preUpdatedContact){
        this.preUpdatedContact = preUpdatedContact;

        if (preUpdatedContact != null) {
            editNameField.setText(preUpdatedContact.Name);
            editNumberField.setText(preUpdatedContact.Phone);
            editEmailField.setText(preUpdatedContact.Email);

            if (preUpdatedContact.address != null) { // Check if address is not null
                editStreetField.setText(preUpdatedContact.address.Street);
                editCityField.setText(preUpdatedContact.address.City);
                editStateField.setText(preUpdatedContact.address.State);
                editZipCodeField.setText(preUpdatedContact.address.ZipCode);
            }

            if(preUpdatedContact.Image != null){
                setImage(preUpdatedContact.Image);
            }
        }
        else{
            alerts.showErrorAlert("Something went wrong while reading contact!");
            throw new NullPointerException("Set pre-updated contact is null!");
        }
    }

    @FXML
    private void handleRefreshAction(){
        setPreUpdatedContact(preUpdatedContact);
    }

    @FXML
    public void handleDiscardChanges() {
        discardChanges();
    }

    private void discardChanges(){
        if(contactModified == true){
            boolean confirmation = alerts.showConfirmationAlert("Discard Changes?");
            if(confirmation)
                closeWindow();
        }
        else closeWindow();
    }
    @FXML
    public void handleExitWindowAction(ActionEvent event) {
        // Close the window without saving
        discardChanges();
    }

    private boolean menuOpenToggle = true;
    private boolean menuOpen(){
        return menuOpenToggle = !menuOpenToggle;
    }
    @FXML
    private void handleImageMenu(ActionEvent event){
        Node anchorNode = (Node) event.getSource();
        if(menuOpen())
            editImageMenu.show(anchorNode, Side.BOTTOM,anchorNode.getLayoutX(), anchorNode.getLayoutY());
        else
            editImageMenu.hide();

    }

    @FXML
    public void handleChooseImage() {
        Stage stage = (Stage) imageCircle.getScene().getWindow();
        if (stage == null) {
            System.err.println("Stage is null. Cannot open FileChooser.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Contact Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            if (isValidContactPhoto(file)) { // Use the new isValidContactPhoto method
                try {
                    Image image = new Image(file.toURI().toString());
                    imageCircle.setFill(new ImagePattern(image));
                    image.errorProperty().addListener((obs, oldVal, newVal) ->{
                        if (newVal){
                            System.err.println("Error loading image: " + image.getException().getMessage());
                            imageCircle.setFill(Paint.valueOf("#e1e2e1"));
                            alerts.showErrorAlert("Error loading image. Please select another image.");
                        }
                    });
                    imageName = file.getName();
                    imagePath = file.getPath();
                    contactModified=true;
                    refreshButton.setDisable(false);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                    imageCircle.setFill(Paint.valueOf("#e1e2e1"));
                    alerts.showErrorAlert("Error loading image. Please select another image.");
                }
            } else {
                alerts.showErrorAlert("Image does not meet the requirements.\nMinimum size: " + MIN_WIDTH + "x" + MIN_HEIGHT + "\nAspect ratio: Near " + DESIRED_ASPECT_RATIO + " (tolerance: " + TOLERANCE + ")");
            }
        }
    }

    @FXML
    private void handleRemoveImageAction(){}
        
    private void setImage(String image){
        File currImagePath = new File(rootImagePath+image);
        Image currImage = new Image(currImagePath.toURI().toString());
        imageCircle.setFill(new ImagePattern(currImage));
    }
    
    public static boolean isOnlyDigits(String str) 
    { 
        // Traverse the string from start to end 
        for (char c : str.toCharArray()) { 
            // Check if character is not a digit between 0-9 then return false
            if (Character.isDigit(c) || c == '-' || c == '+') continue;
            else {
                System.out.println(Character.isDigit(c));
                return false;
            } 
        } 
          // If we reach here, that means all characters were digits.
        return true; 
    } 
    
    private boolean isValidContactPhoto(File imageFile) {
    
        try (FileImageInputStream input = new FileImageInputStream(imageFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                System.err.println("No ImageReader found for this file type");
                return false;
            }

            ImageReader reader = readers.next();
            reader.setInput(input);

            int width = reader.getWidth(reader.getMinIndex());
            int height = reader.getHeight(reader.getMinIndex());
            double aspectRatio = (double) width / height;

            return width >= MIN_WIDTH && height >= MIN_HEIGHT && Math.abs(aspectRatio - DESIRED_ASPECT_RATIO) <= TOLERANCE;
        } catch (IOException e) {
            System.err.println("Error reading image metadata: " + e.getMessage());
            alerts.showErrorAlert("Error reading image metadata!");
            return false;
        }
    }
    
    private void deleteImage(String targetImage) {
        if (targetImage != null && !targetImage.isEmpty()) {
            try {
                Path path = Paths.get(rootImagePath+targetImage);
                if (Files.exists(path)) {
                    Files.delete(path);
                    System.out.println("Image deleted: " + imagePath);
                } else {
                    System.out.println("Image not found: " + imagePath);
                }
            } catch (IOException e) {
                System.err.println("Error deleting image: " + e.getMessage());
                // Consider more robust error handling, like displaying an alert to the user
                e.printStackTrace(); // For debugging
        }
        } else {
             System.out.println("No image path provided for deletion.");
        }
    }
    
    private void copyImage(String sourcePath){
        try {
            Path source = Paths.get(sourcePath);
            Path destinationDir = Paths.get(rootImagePath);

            // Extract the filename from the source path
            String fileName = source.getFileName().toString();

            // Create the full destination path by combining the directory and filename
            Path destination = destinationDir.resolve(fileName);

            // Ensure the destination directory exists
            Files.createDirectories(destinationDir);

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copied successfully to: " + destination);
        } catch (IOException e) {
            System.err.println("Error copying image: " + e.getMessage());
            e.printStackTrace();
        }
    }
     
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
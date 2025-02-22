/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package contactsmanager.contactsmanagerfx.ui.dialog;

import contactsmanager.contactsmanagerfx.contacts.*;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import java.net.URL;
import java.nio.file.*;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.*;
import javax.imageio.stream.FileImageInputStream;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class AddContactFXMLController implements Initializable, DialogWindow{

    @FXML
    private javafx.scene.layout.AnchorPane AnchorPane;
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
    private Circle imageCircle;
    @FXML
    private Button editImageButton;
    @FXML
    private ContextMenu editImageMenu;
    private String contactImagesRootPath = ("C:\\Users\\admin\\IdeaProjects\\ContactsMnagerFX\\src\\main\\resources\\contactsmanager\\contactsmanagerfx\\images\\contactimages\\");
    
    private ContactsManager manager;
    private Runnable reloadAction;
    private EventHandler<ActionEvent> onExitRequest;
    
    //Image dimentions and tollerance
    private static final int MIN_WIDTH = 150;
    private static final int MIN_HEIGHT = 150;
    private static final double DESIRED_ASPECT_RATIO = 1.0; // Square
    private static final double TOLERANCE = 0.01;
    
    private String imageName;
    private String imagePath;

    // Define an event handler for the Save button click
    private EventHandler<ActionEvent> onSaveButtonClick;
    private Contact contact = null;
    Alerts alerts = new Alerts();
    
    // Set the event handler for the Save button click
    public void setOnSaveButtonClick(EventHandler<ActionEvent> eventHandler) {
        onSaveButtonClick = eventHandler;
    }
    
    @FXML
    private void handleExitWindowAction(ActionEvent event){
        // Close the window without saving
        boolean proceed = alerts.showConfirmationAlert("Discard Changes?");
        if(proceed) {
            onExitRequest.handle(event);
        }
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
    private void handleChooseImage(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
                            alerts.showErrorAlert("Error loading image. Please select another image.");
                        }
                    });
                    imageName = file.getName();
                    imagePath = file.getPath();
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                    alerts.showErrorAlert("Error loading image. Please select another image.");
                }
            } else {
                alerts.showErrorAlert("Image does not meet the requirements.\nMinimum size: " + MIN_WIDTH + "x" + MIN_HEIGHT + "\nAspect ratio: Near " + DESIRED_ASPECT_RATIO + " (tolerance: " + TOLERANCE + ")");
            }
        }
    }
    @FXML
    private void handleRemoveImageAction(){}
    
    @FXML
    private void handleSaveContactAction(ActionEvent event){
        
        String errorMessage = "";
        if (onSaveButtonClick != null) {
            
            //Validate Fields Inputs
            if(editNameField.getText().isEmpty() || editNumberField.getText().isEmpty())
                errorMessage = "You Must Enter all Compulsory Fields";
            else if (!isOnlyDigits(editNumberField.getText()))
                errorMessage = "Phone number must contain digits only";
            else if (!isOnlyDigits(editZipCodeField.getText()))
                errorMessage = "Zip code must contain digits only";
            else if(!editEmailField.getText().contains("@"))
                errorMessage = "You have entered an incorrect email format";
            
            if(!errorMessage.isEmpty()){
                alerts.showErrorAlert(errorMessage);
            }
            else if(manager != null) {
                ContactBuilder contactBuilder = new ContactBuilder();
                contactBuilder.setId(manager.getContactsCount()+1);
                contactBuilder.setName(editNameField.getText().trim());
                contactBuilder.setPhone(editNumberField.getText().trim());
                contactBuilder.setEmail(editEmailField.getText().trim().toLowerCase());
                contactBuilder.setAddress(
                        editStreetField.getText().trim(),
                        editCityField.getText().trim(),
                        editStateField.getText().trim(),
                        editZipCodeField.getText().trim());
                
                if(imageName != null) {
                    contactBuilder.setImage(imageName);
                    copyImage(imagePath);
                }else contactBuilder.setImage("default.png");
                contact = contactBuilder.getContact();
                try {
                    onSaveButtonClick.handle(event);// Trigger the event
                } catch (Exception ex) {
                    Logger.getLogger(AddContactFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                alerts.showErrorAlert("Could Not Save Contact");
                throw new NullPointerException("Contacts Manager is Null");
            }
        }
    }
    
    private void copyImage(String sourcePath){
        String destinationDirectory = contactImagesRootPath;
        try {
            Path source = Paths.get(sourcePath);
            Path destinationDir = Paths.get(destinationDirectory);

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

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public void setContactsManager(ContactsManager manager){
        this.manager = manager;
    }

    @Override
    public void setReloadAction(Runnable reloadAction) {
        this.reloadAction = reloadAction;
    }

    @Override
    public void setOnRequestWindowExit(EventHandler<ActionEvent> event) {
        this.onExitRequest = event;
    }


    public Contact getContact() throws NullPointerException{
        return contact;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}

package contactsmanager.contactsmanagerfx;

import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import contactsmanager.contactsmanagerfx.ui.dialog.Alerts;
import contactsmanager.contactsmanagerfx.ui.dialog.FXMLEditContactController;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

abstract class ContactInformationController {
    @FXML
    private AnchorPane rootAchorPane; //Pane from subclass

    @FXML
    private Button contactInfoEmailButton;
    @FXML
    private Button InfoDeleteContactButton;
    @FXML
    private Button InfoEditContactButton;

    @FXML
    private Label nameInfo;
    @FXML
    private Label phoneInfo;
    @FXML
    private Hyperlink emailInfo;
    @FXML
    private Label streetInfo;
    @FXML
    private Label cityInfo;
    @FXML
    private Label stateInfo;
    @FXML
    private Label zipCodeInfo;

    @FXML
    private Circle contactPictureCircle;

    protected Contact viewedContact;
    protected ContactsManager contactsManager;
    protected Runnable reloadAction;

    protected String imageRootPath = ("images/contactimages/");

    Alerts alerts = new Alerts();

    public abstract Application getApplication();
    @FXML
    public void handleEmailAction(ActionEvent event) {
        event.consume();
        Application application = getApplication();
        emailInfo.cursorProperty().set(Cursor.WAIT);
        if (emailInfo == null || emailInfo.getText().isEmpty()) {
            alerts.showErrorAlert("No email address available.");
            return;
        }

        try {
            String encodedEmail = URLEncoder.encode(emailInfo.getText(), StandardCharsets.UTF_8);
            String uriString = "mailto:" + encodedEmail;

            HostServices hostServices = application.getHostServices();

            if (hostServices != null) {
                hostServices.showDocument(uriString);
            } else {
                alerts.showErrorAlert("HostServices not available. Cannot open email.");
            }
        } catch (Exception e) { // Catching a broader exception here for simplicity
            alerts.showErrorAlert("An error occurred while opening the email application: " + e.getMessage());
            e.printStackTrace();
            emailInfo.cursorProperty().set(Cursor.HAND);
        }
        emailInfo.cursorProperty().set(Cursor.HAND);
    }

    @FXML
    void handleCloseContactAction(ActionEvent event){
        closeContactInformation();
    }

    @FXML
    public void handleDeleteContactAction(ActionEvent event) {
        deleteContact(viewedContact);
    }
    protected abstract void deleteContact(Contact contact);

    @FXML
    protected void handleEditContactAction() {
        editContact(viewedContact);
    }

    protected abstract void editContact(Contact contact);

    protected abstract void closeContactInformation();

    protected void setRequestedContactInfo(Contact viewedContact) {
        this.viewedContact = viewedContact;

        nameInfo.setText(viewedContact.Name);
        phoneInfo.setText(viewedContact.Phone);
        emailInfo.setText(viewedContact.Email);

        streetInfo.setText(viewedContact.address.Street);
        cityInfo.setText(viewedContact.address.City);
        stateInfo.setText(viewedContact.address.State);
        zipCodeInfo.setText(viewedContact.address.ZipCode);

        setImage(viewedContact.Image);
    }

    private void setImage(String imageName){
        try{
            Image image;
            if(imageName != null && !imageName.isEmpty()) {
                URL image_URL = getClass().getResource(imageRootPath+imageName);
                image = new Image(image_URL.toURI().toString());
            }
            else {
                URL image_URL = getClass().getResource(imageRootPath+"default.png");
                image = new Image(image_URL.toURI().toString());
            }

            contactPictureCircle.setFill(new ImagePattern(image));
        }
        catch(Exception e){
            alerts.showErrorAlert("Something went wrong while getting image!");
            e.printStackTrace();
        }
    }
}

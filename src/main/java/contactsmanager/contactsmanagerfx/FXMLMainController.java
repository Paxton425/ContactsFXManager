/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package contactsmanager.contactsmanagerfx;

import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import contactsmanager.contactsmanagerfx.contacts.Contact;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import contactsmanager.contactsmanagerfx.ui.ContactsItem;
import contactsmanager.contactsmanagerfx.ui.dialog.AddContactFXMLController;
import contactsmanager.contactsmanagerfx.ui.dialog.Alerts;
import contactsmanager.contactsmanagerfx.ui.dialog.FXMLEditContactController;
import javafx.animation.Interpolator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;


/**
 *noContactsLabel
    * @author Sphelele
 */
public class FXMLMainController extends ContactInformationController implements Initializable {
    @FXML
    private AnchorPane rootAchorPane;
    @FXML
    private TextField contactSearchBox;
    @FXML
    private AnchorPane contactsAnchorPane;
    @FXML
    private ScrollPane contactsScrollPane;
    @FXML
    private SplitPane mainSplitPain;
    @FXML
    private VBox sideBarVBox;
    @FXML
    private AnchorPane contactsListAnchorPane;
    @FXML
    private VBox contactsListVBox;
    
    @FXML
    private ContextMenu viewByContextMenu;
    @FXML
    private Button viewByTriggerButton;
    @FXML
    private Button sortByButton;
    @FXML
    private Button searchButton;
    
    @FXML
    private MenuItem sortByNameMenu;
    @FXML
    private MenuItem sortByEmailMenu;
    @FXML
    private MenuItem sortByPhoneMenu;

    private ContactsManager contactsManager;
    private ArrayList<Contact> contacts;
    private boolean isViewByOpen = false;
    private Runnable reloadAction;
    protected Application application;

    private SplitPane.Divider divider1;
    private final double d1closed = 0.0814, d1open = 0.259; //Closed and Open Split Pane Divider positions
    private SplitPane.Divider divider2;
    private final double d2closed = 0.9986, d2open = 0.6575; //Closed and Open Split Pane Divider positions

    private Alerts alerts = new Alerts();

    @FXML
    private void handleAboutAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAboutView.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("About");
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void handleExitAction(ActionEvent event){
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleSortByName(ActionEvent event){
        contactsManager.sortBy(ContactsManager.SortBy.NAME);
        contactsListVBox.getChildren().clear();
        loadContacts(contacts);
    }
    @FXML
    private void handleSortByPhone(ActionEvent event){
        contactsManager.sortBy(ContactsManager.SortBy.PHONE);
        contactsListVBox.getChildren().clear();
        loadContacts(contacts);
    }
    @FXML
    private void handleSortByEmail(ActionEvent event){
        contactsManager.sortBy(ContactsManager.SortBy.EMAIL);
        contactsListVBox.getChildren().clear();
        loadContacts(contacts);
    }
    @FXML
    private void handleViewByAction(ActionEvent event){
        if(isViewByOpen){
            viewByContextMenu.hide();
            isViewByOpen = false;
        }
        else {
            viewByContextMenu.show((Node)event.getSource(), Side.BOTTOM, 0, viewByTriggerButton.getLayoutY());
            isViewByOpen = true;
        }
    }
    @FXML
    private void handleViewByName(ActionEvent event){
        contactsManager.viewBy(ContactsManager.ViewBy.NAME);
        loadContacts(contacts);
    }
    @FXML
    private void handleViewByEmail(ActionEvent event){
        contactsManager.viewBy(ContactsManager.ViewBy.EMAIL);
        loadContacts(contacts);
    }
    
    @FXML
    private void handleViewContactsAction(ActionEvent event){
        loadContacts(contacts);
    }
    @FXML
    private void handleViewFavouritesAction(ActionEvent event){
        ArrayList<Contact> favourites = contactsManager.getFavouriteContacts();
        loadContacts(favourites);
        
        Label stateLabel =  getStateMessage("Favourites");
        contactsListVBox.getChildren().add(0, stateLabel);
    }
    @FXML
    private void handleViewRecentAction(ActionEvent event){
        ArrayList<Contact> recent = contactsManager.getRecentContacts();
        loadContacts(recent);
        
        Label stateLabel = getStateMessage("Recent");
        contactsListVBox.getChildren().add(0, stateLabel);
    }
    

    
    @FXML
    private void handleDeleteSelection(ActionEvent event){
        String selectionStyle = "-fx-border-color: blue;"; 
        contactsListVBox.setStyle(selectionStyle);
        
        loadContacts(contacts);
        contactsListVBox.getChildren().add(0, getOptionsButton());
    }
    //private enum Options {DELETE, EXPORT, ALL}
    private ButtonBar getOptionsButton(){
        ButtonBar buttonBar = new ButtonBar();
        if(true){
            Button delete = new Button("Delete");
            buttonBar.getButtons().add(delete);
        }
        return buttonBar;
    }
    
    @FXML
    private void handleImportAction(ActionEvent event){
        
    }
    @FXML
    private void handleExportContactAction(ActionEvent event){

    }

    @FXML
    private void handleToggleNavBarAction(){
        closeContactInformation();
        toggleNav(Direction.AUTO);
    }

    enum Direction {CLOSE, OPEN, AUTO};
    void toggleNav(Direction direction){
        double current = divider1.positionProperty().get();
        double target;

        if(Direction.AUTO == direction) {
            if (Math.abs(current - d1closed) > Math.abs(current - d1open))
                target = d1closed;
            else target = d1open;
        } else target = (direction == Direction.OPEN)? d1open : d1closed;

        if(current != target){
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(350),
                            new KeyValue(divider1.positionProperty(), target, Interpolator.EASE_BOTH)));
            timeline.play();
        }
    }
    protected void toggleContactInformation(Direction direction){
        double current = divider2.positionProperty().getValue();
        double target = (direction == Direction.OPEN)? d2open : d2closed;

        if(current != target){
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(divider2.positionProperty(), target, Interpolator.EASE_BOTH)));
            timeline.play();
        }
    }

    @Override
    protected void closeContactInformation(){
        toggleContactInformation(Direction.CLOSE);
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @FXML
    private void handleAddContactAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/dialog/FXMLAddContactView.fxml"));

            WindowControl windowControll = new WindowControl();
            windowControll.createWindow(loader);

            //Access the controller to perfom futher actions
            AddContactFXMLController addContactController = (AddContactFXMLController)windowControll.getController();
            addContactController.setContactsManager(contactsManager);

            windowControll.setFocusBlur(rootAchorPane);
            windowControll.showWindow(StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);

            addContactController.setOnSaveButtonClick(e -> {
                // Perform actions in the main window when the Save button is clicked
                Contact contact = addContactController.getContact();

                addNewContact(contact);
                addContactController.closeWindow(); //Close the window

                alerts.showInformationAlert("Contact Saved!");
                reloadContacts();
            });
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private void addNewContact(Contact contact){
        try {
            contactsManager.addContact(contact);
            reloadContacts();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void editContact(Contact contact){
        if(contact == null){
            alerts.showErrorAlert("Error reading contact.");
            throw new NullPointerException("Contact is null");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/dialog/FXMLEditContactView.fxml"));

            WindowControl windowControl = new WindowControl();
            windowControl.createWindow(loader);
            FXMLEditContactController controller = (FXMLEditContactController) windowControl.getController();
            controller.setPreUpdatedContact(contact);
            controller.setContactsManager(contactsManager);
            controller.setOnSaveChanges(event -> {
                saveEditChanges(controller.getUpdatedContact());
                closeContactInformation();
                controller.closeWindow();
            });

            windowControl.setFocusBlur(rootAchorPane);
            windowControl.showWindow(StageStyle.UTILITY, Modality.APPLICATION_MODAL);
        } catch(IOException e){
            alerts.showErrorAlert("Could Not Open Contact Dialog!");
            e.printStackTrace();
        }
    }

    private void saveEditChanges(Contact updatedContact) throws NullPointerException {
        try {
            contactsManager.updateContact(updatedContact);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        reloadContacts();
    }

    @Override
    protected void deleteContact(Contact contact) {
        if (contact != null) {
            boolean proceed = alerts.deleteConfirmation(contact.Name);
            if (proceed) {
                try {
                    closeContactInformation();

                    contactsManager.deleteContact(contact.Id);
                    contactsManager.reload();
                } catch (Exception e) {
                    alerts.showErrorAlert("Could Not Delete Contact");
                    e.printStackTrace();
                }
            }
        } else alerts.showErrorAlert("Contact is null");
    }

    public void loadContacts(ArrayList<Contact> contacts){
        try
        {
            if(contacts != null){
                contactsListVBox.getChildren().clear();
                for(int i=0; i<contacts.size(); i++)
                {
                    ContactsItem item = new ContactsItem(contactsListVBox, contacts.get(i));
                    item.setOnClickContact((event->{
                        setRequestedContactInfo(item.getContact());
                        toggleNav(Direction.CLOSE); //Close nav Bar
                        toggleContactInformation(Direction.OPEN); //Open Contact Info
                        System.out.println(divider1.getPosition());
                    }));
                    item.setOnRequestDelete(event -> {
                        deleteContact(item.getContact());
                    });
                    item.setOnRequestEditing(event -> {
                        editContact(item.getContact());
                    });

                    item.setOnAddToFavourites(event -> {
                        contactsManager.addToFavourites(item.getContact());
                    });
                    item.setOnRemoveFromFavourites(event -> {
                        contactsManager.removeFromFavourites(item.getContact());
                    });

                    contactsListVBox
                            .getChildren()
                            .add(i, item.getContactItem(contactsManager.getViewBy()));
                }
            }
            else if(contactsManager.getContactsCount() == 0)
            {
                Label state = getStateMessage("0 contacts found");
                contactsListVBox.getChildren().add(state);
            }
        } catch(Exception e)
        {
            alerts.showErrorAlert("Something went wrong while loading contacts!");
            e.printStackTrace();
        }
    }
    private void reloadContacts(){
        contactsManager.reload();
        contacts = contactsManager.getContacts();
        loadContacts(contacts);
    }

    ArrayList<Contact> filterSearchContact(String input){
        ArrayList<Contact> matches = new ArrayList<>();
        
        if(contacts == null){
            return null;
        }
        
        for(int i=0; i<contactsManager.getContactsCount(); i++)
        {
            try {
                if(contacts.get(i).Name == null) 
                    break;
                else if(findSubstring(contacts.get(i).Name.toLowerCase(), input.toLowerCase()) > -1) {
                    matches.add(contacts.get(i)); 
                }
            }catch(NullPointerException e){
                return null;
            }
        }
        return matches;
    }
    int findSubstring(String fullString, String subString){
        int m = fullString.length();
        int n = subString.length();

        for(int i=0; i<=m-n; i++){

            //Search for substring match
            int j;
            for(j=0; j<n; j++)
                //Missmatch Found
                if(fullString.charAt(i+j) != subString.charAt(j))
                    break;

            //When inner loop finishes, we found match
            if(j == n)
                return i; //Return substring starting index
        }

        return -1; //Substring does not match
    }

    private Label getStateMessage(String message){
        Label messageLabel = new Label(message);
        return messageLabel;
    }
    
    private void applyAccelerators(){
        KeyCombination nameShortcut = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        sortByNameMenu.setText("Sort By Name\t"+nameShortcut.getShortcut().toString().replace("UP", ""));
        sortByNameMenu.setAccelerator(nameShortcut);
        
        KeyCombination emailShortcut = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        sortByEmailMenu.setText("Sort By Email\t"+emailShortcut.getShortcut().toString().replace("UP", ""));
        sortByEmailMenu.setAccelerator(emailShortcut);
        
        KeyCombination phoneShortcut = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        sortByPhoneMenu.setText("Sort By Phone\t"+phoneShortcut.getShortcut().toString().replace("UP", ""));
        sortByPhoneMenu.setAccelerator(phoneShortcut);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contactsListAnchorPane.prefHeightProperty().bind(mainSplitPain.heightProperty());


        //Evaluate Split Pane Divider Fields
        divider1 = mainSplitPain.getDividers().get(0);
        divider2 = mainSplitPain.getDividers().get(1);
        divider1.positionProperty().set(d1closed); //Initially close

        //Initialize Constructors
        contactsManager = ContactsManager.getInstance();
        contacts = contactsManager.getContacts();

        applyAccelerators(); //Shortcut Keys

        loadContacts(contacts);

        contactSearchBox.textProperty().addListener(new ChangeListener<String>(){
            
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue){
                if(!(newValue.trim()).matches("")){
                    ArrayList<Contact> clist = filterSearchContact(newValue.trim());
                    loadContacts(contacts);
                } 
            }
        });
    }   
}

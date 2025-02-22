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
import java.util.function.Supplier;

import contactsmanager.contactsmanagerfx.ui.ContactsItem;
import contactsmanager.contactsmanagerfx.ui.dialog.AddContactFXMLController;
import contactsmanager.contactsmanagerfx.ui.dialog.Alerts;
import contactsmanager.contactsmanagerfx.ui.dialog.FXMLEditContactController;
import contactsmanager.contactsmanagerfx.ui.dialog.FXMLImportController;
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
    private ArrayList<Contact> favouriteContacts;
    private Contact clickedContact;
    private boolean isViewByOpen = false;
    private Runnable reloadAction;
    protected Application application;

    private SplitPane.Divider divider1;
    private double d1closed = 0.0814, d1open = 0.259; //Closed and Open Split Pane Divider positions
    private SplitPane.Divider divider2;
    private final double d2closed = 0.9986, d2open = 0.4173; //Closed and Open Split Pane Divider positions

    private Alerts alerts = new Alerts();

    @FXML
    private void handleSearchTextChange(KeyEvent event){
        String searchInput = ((TextField) event.getSource()).getText().trim();
        if((searchInput).matches(""))
            loadContacts(contacts);
        else
        {
            ArrayList<Contact> matchedContacts = filterSearchContact(searchInput);
            loadContacts(matchedContacts);
        }
    }

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
        loadContacts(favourites, "Favourites");
    }
    @FXML
    private void handleViewRecentAction(ActionEvent event){
        ArrayList<Contact> recent = contactsManager.getRecentContacts();
        loadContacts(recent, "Recent");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/dialog/FXMLImportView.fxml"));

            WindowControl windowControl = new WindowControl();
            windowControl.createWindow(loader);
            windowControl.setFocusBlur(rootAchorPane);

            FXMLImportController controller = (FXMLImportController) windowControl.getController();
            controller.setContactsManager(contactsManager);
            controller.setReloadAction(reloadAction);

            windowControl.showWindow(StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void handleExportContactAction(ActionEvent event){
    }

    @FXML
    private void handleToggleNavBarAction(){
        toggleNav(Direction.AUTO);
    }

    private enum Direction {CLOSE, OPEN, AUTO};
    private void toggleNav(Direction direction){
        double current = divider1.getPosition();
        double target;

        Supplier<Double> openSupplier = () -> {
            closeContactInformation();
            return d1open;
        };

        switch (direction) {
            case AUTO: target = (Math.abs(current - d1closed) > Math.abs(current - d1open)) ?
                    d1closed
                        :
                    openSupplier.get();
                break;
            case OPEN: target = openSupplier.get();
                break;
            case CLOSE: target = d1closed;
                break;
            default: target = d1closed;
                break;
        }

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

    @Override
    public Application getApplication() {
        return this.application;
    }
    public void setApplication(Application application) {
        this.application = application;
    }

    @FXML
    private void handleAddContactAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/dialog/FXMLAddContactView.fxml"));

            WindowControl windowControl = new WindowControl();
            windowControl.createWindow(loader);

            //Access the controller to perfom futher actions
            AddContactFXMLController addContactController = (AddContactFXMLController)windowControl.getController();
            addContactController.setContactsManager(contactsManager);

            windowControl.setFocusBlur(rootAchorPane);
            windowControl.showWindow(StageStyle.UNDECORATED, Modality.APPLICATION_MODAL);

            addContactController.setOnSaveButtonClick(e -> {
                // Perform actions in the main window when the Save button is clicked
                Contact contact = addContactController.getContact();

                addNewContact(contact);
                windowControl.closeWindow(); //Close the window

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
                windowControl.closeWindow();
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
            String confirmationMessage = "Delete "+contact.Name+" from contacts?";
            boolean proceed = alerts.showConfirmationAlert(confirmationMessage);
            if (proceed) {
                try {
                    closeContactInformation();

                    contactsManager.deleteContact(contact.Id);
                    reloadContacts();
                } catch (Exception e) {
                    alerts.showErrorAlert("Could Not Delete Contact");
                    e.printStackTrace();
                }
            }
        } else alerts.showErrorAlert("Contact is null");
    }

    public void loadContacts(ArrayList<Contact> contacts, String... stateMessages){
        try
        {
            int vBoxIndex = 0;
            contactsListVBox.getChildren().clear();

            if(stateMessages.length > 0)
                for(String stateMessage : stateMessages) {
                    contactsListVBox.getChildren().add(vBoxIndex, getStateMessage(stateMessage));
                    vBoxIndex++;
                }

            if(contacts.size() == 0) {
                contactsListVBox.getChildren().add(vBoxIndex, getStateMessage("0 contacts found"));
                vBoxIndex++;
            }
            else {
                for(int i=0; i<contacts.size(); i++)
                {
                    boolean isFavourite = false;
                    if (favouriteContacts != null)
                        for(Contact contact : favouriteContacts)
                            if(contact.Id == contacts.get(i).Id)
                                isFavourite = true;

                    ContactsItem item = new ContactsItem(contactsListVBox, contacts.get(i));
                    item.setOnClickContact((event->{
                        if(clickedContact != null) {
                            if (item.getContact() == clickedContact) {
                                clickedContact = null;
                                closeContactInformation();
                            }
                            else {
                                clickedContact = item.getContact();
                                setRequestedContactInfo(clickedContact);
                                toggleNav(Direction.CLOSE); //Close nav Bar
                                toggleContactInformation(Direction.OPEN); //Open Contact Info
                            }
                        }else {
                            clickedContact = item.getContact();
                            setRequestedContactInfo(clickedContact);
                            toggleNav(Direction.CLOSE); //Close nav Bar
                            toggleContactInformation(Direction.OPEN); //Open Contact Info
                        }
                        contactsManager.addToRecent(clickedContact);
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
                            .add(vBoxIndex, item.getContactItem(contactsManager.getViewBy(), isFavourite));
                    vBoxIndex++;
                }
            }

        }
        catch (NullPointerException e){
            alerts.showErrorAlert("Something went wrong while loading contacts!");
            System.err.println("Provided contact is null\n"+e.getMessage());
        }
        catch(Exception e)
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
                    continue;
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
        messageLabel.setStyle("-fx-font-size: 13; -fx-fill: derive(#d6d6d6, 80%); -fx-font-style: italic;");
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

        //Initially close Both Panels
        divider1.positionProperty().set(d1closed);
        divider2.positionProperty().set(d2closed);

        rootAchorPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Persistently close as window changes its size
                closeContactInformation();

                //Change didvider positions relative the window width
                d1closed = Math.abs(61.152/((double)newValue)); // y = |k/x| : inverse rlationship
                d1open = Math.abs(194/((double)newValue)); // y = |k/x| : inverse rlationship
                System.out.println("nv "+newValue);
            }
        });

        //Initialize Constructors
        contactsManager = ContactsManager.getInstance();
        contacts = contactsManager.getContacts();
        favouriteContacts = contactsManager.getFavouriteContacts();
        loadContacts(contacts);
        reloadAction = ()->{
            reloadContacts();
        };

        applyAccelerators(); //Shortcut Keys
    }   
}

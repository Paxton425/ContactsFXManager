package contactsmanager.contactsmanagerfx.ui.dialog;

import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressController implements Initializable, DialogWindow{
    @FXML
    private Label progressMessage;
    private EventHandler<ActionEvent> exitRequestHnalder;

    public void setProgressMessage(String message){
        progressMessage.setText(message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public void setContactsManager(ContactsManager manager) {

    }

    @Override
    public void setReloadAction(Runnable reloadAction) {

    }

    @Override
    public void setOnRequestWindowExit(EventHandler<ActionEvent> eventHandler) {
        exitRequestHnalder = eventHandler;
    }
}

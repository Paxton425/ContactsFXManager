package contactsmanager.contactsmanagerfx.ui.dialog;

import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;


public interface DialogWindow {

    Runnable reloadAction = null;
    ContactsManager manager = null;
    Contact contact = null;
    public Stage stage = null;

    public void setStage(Stage stage);
    public abstract void setContactsManager(ContactsManager manager);
    public abstract void setReloadAction(Runnable reloadAction);
    public void setOnRequestWindowExit(EventHandler<ActionEvent> event);
}

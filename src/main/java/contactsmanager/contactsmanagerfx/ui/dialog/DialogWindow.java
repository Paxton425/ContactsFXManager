package contactsmanager.contactsmanagerfx.ui.dialog;

import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.contacts.ContactsManager;

public abstract class DialogWindow {

    protected Runnable reloadAction;
    protected Runnable exitControl;
    protected ContactsManager manager;
    protected Contact contact;

    public void setExitControl(Runnable exitControl){ this.exitControl = exitControl; }
    public abstract void setContactsManager(ContactsManager manager);
    public void closeWindow(){
        exitControl.run();
    }
}

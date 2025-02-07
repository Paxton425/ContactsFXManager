package contactsmanager.contactsmanagerfx.contacts;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

/**
 *
 * @author Sphelele
 */
public class ContactsManager {
    private ArrayList<Contact> contactsList;
    private ArrayList<Contact> favourites;
    private ArrayList<Contact> recent;
    
    public enum ViewBy{ NAME, EMAIL }
    public enum SortBy { NAME, PHONE, EMAIL }
    
    private ViewBy viewBy = ViewBy.NAME; //Default View By Value
    private SortBy sortBy = SortBy.NAME;  //Default Sort By Value
    
    private static ContactsManager manager;
    private ContactsManager(){//Private to Prevenet instantiatiion
        this.contactsList = loadContactsFromFile().getContactList();
        this.favourites = new ArrayList<>();
        this.recent = new ArrayList<>();
    }
    
    public static ContactsManager getInstance(){
        if(manager == null)
            manager = new ContactsManager();
        return manager;
    }

    private String path = ("contactsdata/SavedContactsList.xml");

    public void reload(){
        this.contactsList = loadContactsFromFile().getContactList();
    }
    
    public void updateContact(Contact updatedContact) throws Exception {
        Contacts contacts = loadContactsFromFile(); // Use helper function

        ArrayList<Contact> contactList = contacts.getContactList();
        int indexToUpdate = findContactIndex(contactList, updatedContact.Id); // Use helper function

        if (indexToUpdate != -1) {
            contactList.set(indexToUpdate, updatedContact);
            saveContactsToFile(contacts); // Use helper function
        } else {
            throw new Exception("Contact with ID " + updatedContact.Id + " not found!");
        }
    }

    public void deleteContact(int contactId) throws Exception {
        Contacts contacts = loadContactsFromFile();

        ArrayList<Contact> contactList = contacts.getContactList();
        int indexToRemove = findContactIndex(contactList, contactId);

        if (indexToRemove != -1) {
            contactList.remove(indexToRemove);
            saveContactsToFile(contacts);
        } else {
            throw new Exception("Contact with ID " + contactId + " not found!");
        }
    }
    
    public void addContact(Contact newContact) throws Exception {
        Contacts contacts = loadContactsFromFile();
        contactsList = contacts.getContactList();
        if (contactsList == null) {
            contactsList = new ArrayList<>(); // Ensure contactList is initialized
            contacts.setContactList(contactsList); // Set the list back to the Contacts object
        }

        if(newContact == findContactById(newContact.Id)) //Check if contact has an existing ID
            newContact.Id = contactsList.size()+1;

        contactsList.add(newContact); // Add the new contact
        saveContactsToFile(contacts); //Save Contact
    }
    
    private Contacts loadContactsFromFile() {
        try(InputStream FILE_INPUT_STREAM = getClass().getResourceAsStream(path)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Contacts.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (FILE_INPUT_STREAM.available() > 0) {
                return (Contacts) unmarshaller.unmarshal(FILE_INPUT_STREAM);
            } else {
                return new Contacts();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void saveContactsToFile(Contacts contacts) {
        URL fileUrl = getClass().getResource(path);
        try(OutputStream FILE_OUTPUT_STREAM = Files.newOutputStream(Paths.get(fileUrl.toURI()))) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Contacts.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(contacts, FILE_OUTPUT_STREAM);
        }
        catch(Exception e){
            System.err.println("Error while saving xml updates to file\n"+e.getMessage());
        }
    }
    
    public void sortBy(SortBy category){
        this.sortBy = category;
        ContactBuilder.sortContacts(contactsList, category);  //Re-sort arraylist 
    }
    public SortBy getSortBy(){
        return this.sortBy;
    }
    public void viewBy(ViewBy viewBy){
        this.viewBy = viewBy;
    }
    public ViewBy getViewBy(){
        return this.viewBy;
    }

    public void addToFavourites(Contact contact){
        favourites.add(contact);
    }
    public void removeFromFavourites(Contact contact) { favourites.remove(contact);}
    public ArrayList<Contact> getFavouriteContacts(){
        return favourites;
    }

    public void addToRecent(Contact contact){
        boolean alreadyExists = false;
        for(Contact c : recent)
            if(c == contact) alreadyExists = true;

        if(!alreadyExists) {
            if(recent.size() >= 10)
                recent.remove(0);
            recent.add(contact);
        }
    }
    public ArrayList<Contact> getRecentContacts(){
        if(recent != null){
            return recent;
        }
        return null;
    }

    public int findContactIndex(ArrayList<Contact> contactList, int contactId) {
        if (contactList == null) {
            return -1;
        }
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).Id == contactId) {
                return i;
            }
        }
        return -1;
    }

    public Contact findContactById(int Id){
        if(contactsList != null)
            for(Contact c : contactsList)
                if(Id == c.Id) return c;
        return null;
    }

    public ArrayList<Contact> getContacts()
    {
        if(contactsList != null){
            return contactsList;
        }
        return null;
    }
    public int getContactsCount(){
        if(contactsList != null)
            return contactsList.size();
        return 0;
    }
}

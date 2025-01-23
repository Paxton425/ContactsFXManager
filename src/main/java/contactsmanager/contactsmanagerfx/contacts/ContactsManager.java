package contactsmanager.contactsmanagerfx.contacts;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


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
    private ContactsManager(){
        //Private to Prevenet instantiatiion
        this.contactsList = loadContactsArray();
        this.favourites = new ArrayList<>();
        this.recent = new ArrayList<>();
    }
    
    public static ContactsManager getInstance(){
        if(manager == null)
            manager = new ContactsManager();
        return manager;
    }
    
    
    private String path = ("C:\\Users\\admin\\IdeaProjects\\ContactsMnagerFX\\src\\main\\resources\\contactsmanager\\contactsmanagerfx\\contactsdata\\SavedContactsList.xml");
    File CONTACTS_FILE = new File(path);
 
    private ArrayList<Contact> loadContactsArray(){
        ArrayList<Contact> contactsLoad = new ArrayList<>();
        try {
            if (!CONTACTS_FILE.exists()) {
                return new ArrayList<>();
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(Contacts.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Contacts contacts = loadContactsFromFile(CONTACTS_FILE);
            return contacts.getContactList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void reload(){
        this.contactsList = loadContactsArray();
    }
    
    public void updateContact(Contact updatedContact) throws Exception {
        Contacts contacts = loadContactsFromFile(CONTACTS_FILE); // Use helper function

        ArrayList<Contact> contactList = contacts.getContactList();
        int indexToUpdate = findContactIndex(contactList, updatedContact.Id); // Use helper function

        if (indexToUpdate != -1) {
            contactList.set(indexToUpdate, updatedContact);
            saveContactsToFile(contacts, CONTACTS_FILE); // Use helper function
        } else {
            throw new Exception("Contact with ID " + updatedContact.Id + " not found!");
        }
    }

    public void deleteContact(int contactId) throws Exception {
        Contacts contacts = loadContactsFromFile(CONTACTS_FILE);

        ArrayList<Contact> contactList = contacts.getContactList();
        int indexToRemove = findContactIndex(contactList, contactId);

        if (indexToRemove != -1) {
            contactList.remove(indexToRemove);
            saveContactsToFile(contacts, CONTACTS_FILE);
        } else {
            throw new Exception("Contact with ID " + contactId + " not found!");
        }
    }
    
    public void addContact(Contact newContact) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Contacts.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Contacts contacts;
        if (CONTACTS_FILE.exists()) {
            contacts = (Contacts) unmarshaller.unmarshal(CONTACTS_FILE);
        } else {
            contacts = new Contacts(); // Create new Contacts if file doesn't exist
        }


        contactsList = contacts.getContactList();
        if (contactsList == null) {
            contactsList = new ArrayList<>(); // Ensure contactList is initialized
            contacts.setContactList(contactsList); // Set the list back to the Contacts object
        }

        if(newContact == findContactById(newContact.Id)) //Check if contact has an existing ID
            newContact.Id = contactsList.size()+1;

        contactsList.add(newContact); // Add the new contact

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(contacts, CONTACTS_FILE);
    }
    
    private Contacts loadContactsFromFile(File file) throws Exception,JAXBException  {
        JAXBContext jaxbContext = JAXBContext.newInstance(Contacts.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        if (file.exists()) {
            return (Contacts) unmarshaller.unmarshal(file);
        } else {
            return new Contacts();
        }
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
    
    private void saveContactsToFile(Contacts contacts, File file) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Contacts.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(contacts, file);
    }
    
    void AddFavourite(Contact contact){
        favourites.add(contact);
    }
    void AddRecent(Contact contact){
        recent.add(contact);
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
    
    public ArrayList<Contact> getContacts()
    {
        if(contactsList != null){
            return contactsList;
        }
        return null;
    }
    public int getContactsCount(){
        return contactsList.size();
    }
    
    public void addToFavourites(Contact contact){
        favourites.add(contact);
    }
    public void removeFromFavourites(Contact contact) { favourites.remove(contact);}

    public ArrayList<Contact> getFavouriteContacts(){
        if(favourites != null){
            return favourites;
        }
        return null;
    }
    public void addToRecent(Contact contact){
        recent.add(contact);
    }
    public ArrayList<Contact> getRecentContacts(){
        if(recent != null){
            return recent;
        }
        return null;
    }
}

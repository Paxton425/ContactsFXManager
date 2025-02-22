package contactsmanager.contactsmanagerfx.contacts;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        this.favourites = loadFavourites();
        this.recent = new ArrayList<>();
    }
    
    public static ContactsManager getInstance(){
        if(manager == null)
            manager = new ContactsManager();
        return manager;
    }

    private String contacts_path = ("contactsdata/SavedContactsList.xml");
    private String meta_data_path = ("contactsdata/Favourites.json");

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

    public void addContacts(ArrayList<Contact> newContacts) throws Exception {
        Contacts contacts = loadContactsFromFile();
        contactsList = contacts.getContactList();
        if (contactsList == null) {
            contactsList = new ArrayList<>(); // Ensure contactList is initialized
            contacts.setContactList(contactsList); // Set the list back to the Contacts object
        }

        contactsList.addAll(newContacts); // Add the new contact
        saveContactsToFile(contacts); //Save Contact
    }
    
    private Contacts loadContactsFromFile() {
        try(InputStream FILE_INPUT_STREAM = getClass().getResourceAsStream(contacts_path)) {
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

    private ArrayList<Contact> loadFavourites(){
        try {
            if(favourites == null)
                favourites = new ArrayList<>();
            URL file_url = getClass().getResource(meta_data_path);
            File file = new File(file_url.toURI());

            int[] favIds = getFavouritesIds(file);
            if(contactsList !=null && favIds != null)
                for(Contact contact : contactsList)
                    for(int id : favIds)
                        if(contact.Id == id)
                            favourites.add(contact);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return favourites;
    }

    public int[] getFavouritesIds(File jsonFile) throws Exception {
        int[] favIds = new int[0];
        ObjectMapper mapper = new JsonMapper();
        JsonNode rootNode = mapper.readTree(jsonFile);
        JsonNode favouritesNode = rootNode.path("favourites");
        if(favouritesNode.isArray())
            favIds = new int[favouritesNode.size()];
            for(int i =0; i<favouritesNode.size(); i++) {
                JsonNode node = favouritesNode.get(i);
                favIds[i] = node.path("contactId").asInt();
            }

        return favIds;
    }
    
    public void saveContactsToFile(Contacts contacts) {
        URL fileUrl = getClass().getResource(contacts_path);
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

        favourites = (favourites != null )? favourites : new ArrayList<>();
        URL file_url = getClass().getResource(meta_data_path);
        try {
            File json_file = new File(file_url.toURI());
            ObjectMapper mapper = new JsonMapper();
            ObjectNode rootNode = (ObjectNode) mapper.readTree(json_file);
            ArrayNode favouritesNode = (ArrayNode) rootNode.get("favourites");

            ObjectNode newNode = mapper.createObjectNode();
            newNode.put("contactId", contact.Id);
            favouritesNode.add(newNode);

            rootNode.set("favourites", favouritesNode);
            mapper.writeValue(json_file, rootNode);

            favourites.add(contact);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void removeFromFavourites(Contact contact) {
        favourites = (favourites != null )? favourites : new ArrayList<>();
        URL file_url = getClass().getResource(meta_data_path);
        try{
            File json_file = new File(file_url.toURI());
            ObjectMapper mapper = new JsonMapper();
            JsonNode rootNode = mapper.readTree(json_file);
            ArrayNode favouritesNode = (ArrayNode) rootNode.get("favourites");
            for(JsonNode node : favouritesNode)
                if(node.get("contactId").asInt() == contact.Id)
                    favourites.remove(node);

            ((ObjectNode)rootNode).set("favourites", favouritesNode);
            mapper.writeValue(json_file, rootNode);

            favourites.remove(contact);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<Contact> getFavouriteContacts(){
        return (favourites != null )? favourites : new ArrayList<>();
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
        return new ArrayList<>();
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

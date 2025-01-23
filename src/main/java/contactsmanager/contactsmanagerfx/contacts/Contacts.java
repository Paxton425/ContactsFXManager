/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.contacts;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
        
/**
 *
 * @author Sphelele
 */


@XmlRootElement(name = "contacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contacts {
    @XmlElement(name = "contact")
    private ArrayList<Contact> contactList;

    public Contacts() {}

    public Contacts(ArrayList<Contact> contactList) {
        this.contactList = contactList;
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<Contact> contactList) {
        this.contactList = contactList;
    }
    
    public Contact getContactById(int id) {
        if (contactList == null) {
            return null; // Handle the case where the list is null or empty
        }

        for (Contact contact : contactList) {
            if (contact.Id == id) {
                return contact; // Return the contact if the ID matches
            }
        }
        return null; // Return null if no contact with the given ID is found
    }
}

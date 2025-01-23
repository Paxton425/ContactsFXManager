/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.contacts;

import contactsmanager.contactsmanagerfx.contacts.ContactsManager.SortBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author admin
 */
public class ContactBuilder {
    private Integer Id;
    private String Name;
    private String Phone;
    private String Email;
    private String Image;
    
    private Address address;
    
    
    //enum SortBy { NAME, PHONE, EMAIL }
    
    public Integer getId(){
        return Id;
    }
    
    public void setId(int Id) {
        this.Id = Id;
    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Number) {
        this.Phone = Number;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
    
    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(String street, String city, String state, String zipCode) {
        this.address =  new Address(street, city, state, zipCode);
    }
    
    public Contact getContact() throws NullPointerException { 
        return (Id == null)? 
                new Contact(0, Name, Phone, Email, Image, address) 
                : 
                new Contact(Id, Name, Phone, Email, Image, address);
    }
    
    public static void sortContacts(ArrayList<Contact> contacts, SortBy field){
        switch(field){
            case NAME:
                Collections.sort(contacts, Comparator.comparing(c -> c.Name));
                break;
            case EMAIL:
                Collections.sort(contacts, Comparator.comparing(c -> c.Email));
                break;
            case PHONE:
                Collections.sort(contacts, Comparator.comparing(c -> c.Phone));
                break;
                
            default:
                System.out.println("Invalid Field");
        } 
    }
}

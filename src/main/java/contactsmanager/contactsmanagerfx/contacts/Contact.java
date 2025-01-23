/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import jakarta.xml.bind.annotation.*;

/**
 *
 * @author admin
 */
@XmlRootElement(name = "contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact {
    //Contact Information
    @XmlElement(name = "id")
    public int Id;
    @XmlElement(name = "name")
    public String Name;
    @XmlElement(name = "phone")
    public String Phone;
    @XmlElement(name = "email")
    public String Email;
    @XmlElement(name = "image")
    public String Image;
    
    //Adresses
    @XmlElement(name = "address") // This is the key change
    public Address address;
    
    public Contact() {} //JAXB requires this default constructor to properly instantiate the class during unmarshalling (loading) from XML.

    public Contact(int Id, String Name, String Phone, String Email, String Image, Address address) {
        this.Id = Id;
        this.Name = Name;
        this.Phone = Phone;
        this.Email = Email;
        this.Image = Image;
        this.address = address;
    }
    
    @Override
    public String toString() {
        return "Contact{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Phone='" + Phone + '\'' +
                ", Email='" + Email + '\'' +
                ", Image='" + Image + '\'' +
                address.toString()+
                '}';
    }
}

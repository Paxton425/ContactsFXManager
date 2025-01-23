/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.contacts;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author admin
 */
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD) // Important for nested elements
public class Address {
    @XmlElement(name = "street")
    public String Street;
    @XmlElement(name = "city")
    public String City;
    @XmlElement(name = "state")
    public String State;
    @XmlElement(name = "zipCode")
    public String ZipCode;

    public Address() {} // No-arg constructor

    public Address(String street, String city, String state, String zipCode) {
        Street = street;
        City = city;
        State = state;
        ZipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "Street='" + Street + '\'' +
                ", City='" + City + '\'' +
                ", State='" + State + '\'' +
                ", ZipCode='" + ZipCode + '\'' +
                '}';
    }
}
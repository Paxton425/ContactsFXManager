/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.ui;

import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.ui.dialog.Alerts;
import contactsmanager.contactsmanagerfx.contacts.ContactsManager.ViewBy;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 *
 * @author Sphelele
 */
public class ContactsItem {

    private Pane anchorPane;
    private Contact contact;
    private String contactText;
    private Alerts alerts;

    private boolean menuOpenToggle = true;
    private boolean isMenuOpen(){
        return menuOpenToggle = !menuOpenToggle;
    }

    public URL getImageURL(String file) {
        try {
            // Get the path to the class file
            URL classUrl = getClass().getResource(getClass().getSimpleName() + ".class");
            Path classPath = Paths.get(classUrl.toURI());

            // Get the path to the project's base directory
            Path projectBasePath = classPath.getParent().getParent();

            // Construct the full path to the file
            Path filePath = projectBasePath.resolve("images").resolve(file);

            // Attempt to open the file
            return filePath.toUri().toURL();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Handlers
    private EventHandler<MouseEvent> onClickContact;
    private EventHandler<ActionEvent> onRequestEditing;
    private EventHandler<ActionEvent> onRequestDelete;
    private EventHandler<ActionEvent> onAddTofavourites;
    private EventHandler<ActionEvent> onRemoveFromFavourites;

    public void setOnClickContact(EventHandler<MouseEvent> eventHandler){this.onClickContact = eventHandler;}
    public void setOnRequestEditing(EventHandler<ActionEvent> eventHandler) {this.onRequestEditing = eventHandler;}
    public void setOnRequestDelete(EventHandler<ActionEvent> eventHandler) {this.onRequestDelete = eventHandler;}
    public void setOnAddToFavourites(EventHandler<ActionEvent> eventHandler){this.onAddTofavourites = eventHandler;}
    public void setOnRemoveFromFavourites(EventHandler<ActionEvent> eventHandler){this.onRemoveFromFavourites = eventHandler;}


    public ContactsItem(Pane anchorPane, Contact contact){
        this.contact = contact;
        this.anchorPane = anchorPane;
        alerts = new Alerts();
    }
    
    public void setContat(Contact contact){
        this.contact = contact;
    }
    
    //Button to be created each time a new contact is added.
    public StackPane getContactItem(ViewBy viewBy) {
        
        if(viewBy == ViewBy.NAME)
            this.contactText = (contact.Name+"\n "+contact.Phone);
        else if (viewBy == ViewBy.EMAIL)
            this.contactText = (contact.Email+"\n "+contact.Phone);
        
        StackPane pane = new StackPane();
        pane.prefWidthProperty().bind(anchorPane.widthProperty());
        Button contactButton = new Button(contactText);
        
        Circle imageCircle = new Circle(17); //Contact item image layout.
        imageCircle.strokeWidthProperty().set(0);
        imageCircle.setFill(Color.LIGHTGREY);
        imageCircle.setFill(new ImagePattern(getItemImage(contact.Image)));
        
        StackPane.setMargin(imageCircle, new Insets(0, 30, 0, 10));
        StackPane.setAlignment(imageCircle, Pos.CENTER_LEFT);
        
        contactButton.setId(contact.Id+"");
        contactButton.prefWidthProperty().bind(pane.widthProperty());
        contactButton.getStyleClass().add("contact-item-button");
        contactButton.setAlignment(Pos.BASELINE_LEFT);
        contactButton.setTextFill(Paint.valueOf("#5e5e5e"));
        contactButton.setPrefHeight(50);
        contactButton.setMinHeight(45);
        contactButton.setPadding(new Insets(0, 0, 0, 50));

        Image image = null;
        try {
            image = new Image(getImageURL("icons/3dots-v.png").toURI().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        
        ContextMenu contactMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit Contact");
        MenuItem delete = new MenuItem("Delete Contact");
        MenuItem addToFavourites = new MenuItem("Add to Favourites");
        MenuItem removeFromFavourites = new MenuItem("Remove From Favourites");
        MenuItem exportContact = new MenuItem("Export Contact");
        SeparatorMenuItem separator = new SeparatorMenuItem();

        contactMenu.getItems().add(0, delete);
        contactMenu.getItems().add(1, edit);
        contactMenu.getItems().add(2, addToFavourites);
        contactMenu.getItems().add(3, separator);
        contactMenu.getItems().add(4, exportContact);
        
        Button trigger = new Button(); //Will act as the menu button
        trigger.setGraphic(imageView);
        trigger.setContextMenu(contactMenu);
        trigger.getStyleClass().add("contact-menu-triger-btn");
        trigger.setOnMouseClicked(e-> {
            if(isMenuOpen()) contactMenu.hide();
            else contactMenu.show((Node)e.getSource(), Side.BOTTOM, trigger.getLayoutX(), trigger.getLayoutY());
        });
        edit.setOnAction(e ->{
            onRequestEditing.handle(e);
            this.menuOpenToggle = false;
        });
        delete.setOnAction(e ->{
            onRequestDelete.handle(e);
            this.menuOpenToggle = false;
        });
        
        addToFavourites.setOnAction(e->{
            onAddTofavourites.handle(e);
            contactMenu.getItems().remove(addToFavourites);
            contactMenu.getItems().add(2, removeFromFavourites);
        });
        removeFromFavourites.setOnAction(e->{
            onRemoveFromFavourites.handle(e);
            contactMenu.getItems().remove(removeFromFavourites);
            contactMenu.getItems().add(2, addToFavourites);
        });
        
        StackPane.setMargin(trigger, new Insets(0, 20, 0, 0));
        StackPane.setAlignment(trigger, Pos.CENTER_RIGHT);
    
        contactButton.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){ //Open Contact Dialog on left click
                onClickContact.handle(e);
            } else {
                //open menu on right click
                contactMenu.show(contactButton, Side.BOTTOM,
                        (contactButton.widthProperty().get()/2)+contactButton.getLayoutX(),
                        contactButton.getLayoutY());
            }
        });

        pane.getChildren().addAll(contactButton, imageCircle, trigger);
        pane.setId(contact.Id+"");
        return pane;
    }

    public Contact getContact(){
        return contact;
    }
    
    private Image getItemImage(String imageName) {
        try {
            if (imageName != null && !imageName.isEmpty()) { 
                URL path = getImageURL("contactImages/"+imageName);
                return new Image(path.toURI().toString());
            }
        } catch (Exception e) {
            System.err.println("Something went wrong while getting contact image \n" + e.getMessage());
        }

        // Default image logic 
        return new Image(getImageURL("contactImages/default.png").toString());
    }
}

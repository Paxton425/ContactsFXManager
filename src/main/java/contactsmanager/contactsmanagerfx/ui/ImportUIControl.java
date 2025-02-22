package contactsmanager.contactsmanagerfx.ui;

import com.jfoenix.controls.JFXTreeCell;
import com.jfoenix.controls.JFXTreeView;
import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.contacts.Contacts;
import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import contactsmanager.contactsmanagerfx.ui.dialog.Alerts;
import contactsmanager.contactsmanagerfx.ui.dialog.DialogWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.ArrayList;

public abstract class ImportUIControl {

    protected Runnable reloadAction2;
    protected Button importButton;
    private ArrayList<Contact> contacts;
    protected Alerts alerts = new Alerts();

    protected enum LayOut { DRAG_AND_DROP, LOADING, OUTPUT_VIEW};

    private JFXTreeView contactsTree = new JFXTreeView();
    protected ContactsManager manager;

    protected abstract void handleDragOverAction(DragEvent event);
    protected abstract void handleDragDropAction(DragEvent event);
    protected abstract void chooseFile(ActionEvent event);
    protected abstract void finishProcess(ActionEvent event);
    protected abstract void saveContacts(ArrayList<Contact> contacts) throws Exception;

    protected void setLayout(LayOut layOut, AnchorPane parent){
        if(layOut == LayOut.DRAG_AND_DROP)
            setDropPane(layOut, parent);
        else if(layOut == LayOut.LOADING)
            setLoadingLayout(parent);
        else
            setOutPutLayout(parent);
    }
    private void updateUI(Runnable runnable){
        Platform.runLater(()->{
            try{
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setDropPane(LayOut layOut, AnchorPane parent){
        updateUI(()-> {
            Label dropLabel = new Label("Drag and drop CSV file here");
            AnchorPane.setTopAnchor(dropLabel, 65.0);
            AnchorPane.setRightAnchor(dropLabel, 130.0);
            AnchorPane.setBottomAnchor(dropLabel, 65.0);
            AnchorPane.setLeftAnchor(dropLabel, 135.0);
            dropLabel.setFont(new Font(16.0));
            dropLabel.setLayoutX(133.0);
            dropLabel.setLayoutY(67.0);
            dropLabel.setTextFill(Paint.valueOf("#6b6b6b"));

            AnchorPane dropPane = new AnchorPane();
            dropPane.setPrefSize(470.0, 275.0);
            dropPane.getStyleClass().add("import-box");
            dropPane.getChildren().add(dropLabel);
            dropPane.setOnDragOver(e -> {
                handleDragOverAction(e);
            });
            dropPane.setOnDragDropped(e -> {
                handleDragDropAction(e);
            });

            parent.getChildren().clear();
            parent.getChildren().add(dropPane);
            importButton.setText("Choose From Files");
            importButton.setOnAction(e -> {
                chooseFile(e);
            });
        });
    }

    private void setLoadingLayout(AnchorPane parent){
       updateUI(()->{
           System.out.println("Loading");
           Label loadingLabel = new Label("Loading Please Wait...");
           loadingLabel.setFont(new Font(16.0));
           loadingLabel.setLayoutX(133.0);
           loadingLabel.setLayoutY(67.0);
           loadingLabel.setTextFill(Paint.valueOf("#6b6b6b"));
           AnchorPane.setTopAnchor(loadingLabel, 65.0);
           AnchorPane.setRightAnchor(loadingLabel, 130.0);
           AnchorPane.setBottomAnchor(loadingLabel, 65.0);
           AnchorPane.setLeftAnchor(loadingLabel, 130.0);

           AnchorPane loadingPane = new AnchorPane();
           loadingPane.setPrefSize(470.0, 275.0);
           loadingPane.getStyleClass().add("loading-box");
           loadingPane.getChildren().add(loadingLabel);

           importButton.setText("...");
           importButton.setPrefWidth(120.0);
           importButton.setDisable(true);

           parent.getChildren().clear();
           parent.getChildren().add(loadingPane);
       });
    }

    private void setOutPutLayout(AnchorPane parent){
        updateUI(()->{
            Label outPutLabel = new Label("Imported Contacts");
            outPutLabel.setFont(new Font(16.0));
            outPutLabel.setLayoutX(133.0);
            outPutLabel.setLayoutY(67.0);
            outPutLabel.setTextFill(Paint.valueOf("#6b6b6b"));

            contactsTree.setPrefSize(442.0, 253.0);

            ScrollPane contactsScrollPane = new ScrollPane();
            contactsScrollPane.setContent(contactsTree);
            contactsScrollPane.getStyleClass().add("imports-croll-pane");

            VBox outPutVBox = new VBox();
            outPutVBox.setAlignment(Pos.CENTER);
            AnchorPane.setTopAnchor(outPutVBox, 0.0);
            AnchorPane.setRightAnchor(outPutVBox, 0.0);
            AnchorPane.setBottomAnchor(outPutVBox, 0.0);
            AnchorPane.setLeftAnchor(outPutVBox, 0.0);
            outPutVBox.getChildren().addAll(outPutLabel, contactsScrollPane);

            importButton.setText("Import Contact(s)");
            importButton.setPrefWidth(155.0);
            importButton.setDisable(false);
            importButton.setOnAction(e->{
                try {
                    saveContacts(contacts);
                    finishProcess(e);
                    alerts.showSuccessAlert("Contact(s) Saved Successfully!");
                } catch (Exception ex) {
                    alerts.showErrorAlert("Could Not Save Contact(s)!");
                    throw new RuntimeException(ex);
                }
            });

            parent.getChildren().clear();
            parent.getChildren().add(outPutVBox);
        });
    }

    protected void setOutPutList(ArrayList<Contact> contacts){
        if(contactsTree != null){
            this.contacts = contacts;
            TreeItem rootItem = new TreeItem("Contacts");
            for(Contact contact : contacts){
                TreeItem contactItem = new TreeItem(contact.Name+"("+contact.Phone+")");
                //Contact Information
                TreeItem name = new TreeItem("Name: "+contact.Name);
                TreeItem phone = new TreeItem("Phone: "+contact.Phone);
                TreeItem email = new TreeItem("Email: "+contact.Email);
                TreeItem address = new TreeItem("address");
                //Address
                TreeItem street = new TreeItem("Street: "+contact.address.Street);
                TreeItem city = new TreeItem("City: "+contact.address.City);
                TreeItem state = new TreeItem("State: "+contact.address.State);
                TreeItem zipCode = new TreeItem("Zip Code: "+contact.address.ZipCode);
                address.getChildren().addAll(street, city, state, zipCode);

                contactItem.getChildren().addAll(name, phone, email, address);
                rootItem.getChildren().add(contactItem);
            }
            contactsTree.setShowRoot(false);
            contactsTree.setRoot(rootItem);
        }else{
            alerts.showWarningAlert("Error Contact OutPut.");
            throw new NullPointerException("Contacts Tree is null");
        }

    };
}

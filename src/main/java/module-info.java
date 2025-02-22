module contactsmanager.contactsmnagerfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.jfoenix;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.logging;
    requires java.desktop;
    requires jakarta.xml.bind;
    requires com.fasterxml.jackson.databind;

    opens contactsmanager.contactsmanagerfx to javafx.fxml;
    exports contactsmanager.contactsmanagerfx;

    opens contactsmanager.contactsmanagerfx.ui.dialog to javafx.fxml;
    exports contactsmanager.contactsmanagerfx.ui.dialog;

    opens contactsmanager.contactsmanagerfx.contacts to jakarta.xml.bind, com.fasterxml.jackson.core;
    exports contactsmanager.contactsmanagerfx.contacts;
}
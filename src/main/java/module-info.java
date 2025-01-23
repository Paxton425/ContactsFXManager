module contactsmanager.contactsmnagerfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.logging;
    requires java.desktop;
    requires jakarta.xml.bind;

    opens contactsmanager.contactsmanagerfx to javafx.fxml;
    exports contactsmanager.contactsmanagerfx;

    opens contactsmanager.contactsmanagerfx.ui.dialog to javafx.fxml;
    exports contactsmanager.contactsmanagerfx.ui.dialog;

    opens contactsmanager.contactsmanagerfx.contacts to jakarta.xml.bind;
    exports contactsmanager.contactsmanagerfx.contacts;
}
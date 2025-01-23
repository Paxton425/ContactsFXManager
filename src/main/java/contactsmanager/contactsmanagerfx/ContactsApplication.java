/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package contactsmanager.contactsmanagerfx;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class ContactsApplication extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMainView.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        FXMLMainController controller = loader.getController();
        controller.setApplication(this);
        
        Image favicon = new Image(new File("C:\\Users\\admin\\IdeaProjects\\ContactsMnagerFX\\src\\main\\resources\\contactsmanager\\contactsmanagerfx\\images\\telephone-icon.png").toURI().toString());
        stage.setTitle(STYLESHEET_MODENA);
        stage.setTitle("Contacts ManagerFx");
        stage.getIcons().add(favicon);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

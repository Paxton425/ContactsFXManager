/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contactsmanager.contactsmanagerfx.ui.dialog;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author admin
 */
public class Alerts {
    private Stage alertStage;
    private CustomAlertController alertController;

    //Set up alert stage
    public Alerts(){

    }

    private Stage getStage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomAlertView.fxml"));
        try {
            Parent root = loader.load();

            alertController = loader.getController();

            alertStage = new Stage();
            alertStage.setScene(new Scene(root));
            alertStage.initStyle(StageStyle.UNDECORATED);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            return alertStage;
        } catch (IOException e) {
            return null;
        }
    }

    public void showSuccessAlert(String message){
        alertStage = getStage();
        alertController.setSuccessType();
        alertController.setTitle("Success");
        alertController.setMessage(message);
        alertStage.show();
    }

    public boolean showConfirmationAlert(String message) {
        alertStage = getStage();
        alertController.setAlertType(Alert.AlertType.CONFIRMATION);
        alertController.setTitle("Confirmation");
        alertController.setMessage(message);
        alertStage.showAndWait();

        return alertController.getConfrimationResult(); // True if user clicks "Yes", False otherwise
    }
    
    public void showErrorAlert(String errorMessage) {
        alertStage = getStage();
        alertController.setAlertType(Alert.AlertType.ERROR);
        alertController.setTitle("Error");
        alertController.setMessage(errorMessage);
        alertStage.show();
    }
    public void showErrorAlert(String title, String errorMessage) {
        alertStage = getStage();
        alertController.setAlertType(Alert.AlertType.ERROR);
        alertController.setTitle(title);
        alertController.setMessage(errorMessage);
        alertStage.show();
    }

    public void showInformationAlert(String message) {
        alertStage = getStage();
        alertController.setAlertType(Alert.AlertType.INFORMATION);
        alertController.setTitle("Information");
        alertController.setMessage(message);
        alertStage.show();
    }

    public void showWarningAlert(String message) {
        alertStage = getStage();
        alertController.setAlertType(Alert.AlertType.WARNING);
        alertController.setTitle("Warning");
        alertController.setMessage(message);
        alertStage.show();
    }
}

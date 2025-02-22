package contactsmanager.contactsmanagerfx.ui.dialog;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;


public class CustomAlertController implements Initializable {
    @FXML
    Button closeAlertButton;
    @FXML
    FontIcon alertIcon;
    @FXML
    Label alertTitle;
    @FXML
    Text alertMessage;
    @FXML
    HBox alertFooter;

    private boolean confirmationResult;
    private Button okButton;
    private Button yesButton;
    private Button noButton;

    @FXML
    private void handleCloseAlert(ActionEvent event){
        confirmationResult = false;
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setTitle(String title){
        alertTitle.setText(title);
    }
    public void setMessage(String message){
        alertMessage.setText(message);
    }

    public void setAlertType(AlertType type){
        if(type == AlertType.ERROR) {
            setIconGraphic("mdi2c-close-circle", "#ee0000");
            alertFooter.getChildren().add(okButton);
        }
        else if(type == AlertType.WARNING) {
            setIconGraphic("mdi2a-alert-outline", "#ffb300");
            alertFooter.getChildren().add(okButton);
        }
        else if (type == AlertType.CONFIRMATION){
            setIconGraphic("mdi2h-help-circle", "#979797");
            alertFooter.getChildren().addAll(yesButton, noButton);
        }
    }

    public void setSuccessType(){
        setIconGraphic("mdi2c-checkbox-marked-circle", "#8BC34A");
        alertFooter.getChildren().addAll(okButton);
    }

    private void setIconGraphic(String iconLiteral, String color){
        alertIcon.setIconLiteral(iconLiteral);
        alertIcon.iconColorProperty().set(Paint.valueOf(color));
    }

    public boolean getConfrimationResult(){
        try {
            return confirmationResult;
        }
         catch(NullPointerException e) {
             return false;
         }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        okButton = new Button("Ok");
        okButton.getStyleClass().add("alert-button");
        okButton.setOnAction(e->{
            Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
            stage.close();
        });

        noButton = new Button("No");
        noButton.getStyleClass().add("alert-button");
        noButton.setOnAction(e->{
            confirmationResult = false;
            Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
            stage.close();
        });

        yesButton = new Button("Yes");
        yesButton.getStyleClass().add("alert-button");
        yesButton.setOnAction(e->{
            confirmationResult = true;
            Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
            stage.close();
        });
    }
}

package contactsmanager.contactsmanagerfx;

import contactsmanager.contactsmanagerfx.ui.dialog.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class WindowControl<T extends DialogWindow> {
    private FXMLLoader loader;
    private Parent root;
    private T controller;
    private Stage windowStage;
    private Runnable exitControl;
    private boolean focusBlur;
    private Node blurNode;

    public WindowControl(){
        this.windowStage = new Stage();
        focusBlur = false;
        exitControl = ()->{
            if(focusBlur) blurNode.setEffect(null);
            windowStage.close();
        };
    }

    public Object getController() throws NullPointerException {
        return controller;
    }
    public void setFocusBlur(Node blurNode){
        this.blurNode = blurNode;
        this.focusBlur = true;
    }
    public void createWindow(FXMLLoader loader)
            throws IOException {
        this.loader = loader;
        root = loader.load();
        controller = loader.getController();
        controller.setStage(windowStage);
        controller.setOnRequestWindowExit(e->{
            exitControl.run();
        });
    }
    public void showWindow(StageStyle stageStyle, Modality modality){
        windowStage.initModality(Modality.APPLICATION_MODAL);
        windowStage.initStyle(StageStyle.UNDECORATED);
        windowStage.setScene(new Scene(root));
        windowStage.setOnCloseRequest(e->{
            exitControl.run();
        });
        Stage pstage = getProgressWindow();
        pstage.show();
        windowStage.show();
        pstage.close();

        if(focusBlur)
            blurNode.setEffect(new BoxBlur(3, 3, 3));
    }

    private Stage getProgressWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/dialog/ProgressView.fxml"));
        try {
            Parent progressRoot = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(progressRoot));
            stage.initModality(Modality.APPLICATION_MODAL);

            return stage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeWindow(){
        exitControl.run();
    }
}

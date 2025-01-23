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
            windowStage.close();
            if(focusBlur) blurNode.setEffect(null);
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
    }
    public void showWindow(StageStyle stageStyle, Modality modality){
        windowStage.initModality(Modality.APPLICATION_MODAL);
        windowStage.initStyle(StageStyle.UNDECORATED);
        windowStage.setScene(new Scene(root));
        windowStage.show();

        controller.setExitControl(exitControl);

        if(focusBlur)
            blurNode.setEffect(new BoxBlur(3, 3, 3));
    }
    public void closeWindow(){
        exitControl.run();
    }
}

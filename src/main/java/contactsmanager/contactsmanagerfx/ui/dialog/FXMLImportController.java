package contactsmanager.contactsmanagerfx.ui.dialog;

import contactsmanager.contactsmanagerfx.contacts.Contact;
import contactsmanager.contactsmanagerfx.contacts.ContactBuilder;
import contactsmanager.contactsmanagerfx.contacts.ContactsManager;
import contactsmanager.contactsmanagerfx.ui.ImportUIControl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

public class FXMLImportController  extends ImportUIControl implements Initializable, DialogWindow {
    @FXML
    private AnchorPane layoutBody;
    @FXML
    private Button chooseFileButton;

    private Runnable reloadAction;
    private EventHandler<ActionEvent> onRequestExit;

    private ContactsManager manager;
    private String importStage;
    private Alerts alerts= new Alerts();

    HashMap<String, String[]> keyWordMap;
    @FXML
    private void handleCancelAction(ActionEvent event){
        onRequestExit.handle(event);
    }

    @Override
    protected void handleDragOverAction(DragEvent event){
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
            File draggedFile = event.getDragboard().getFiles().get(0);
            String draggedFileName = draggedFile.getName();
            System.out.println("File Dragged Over");
        }
    }
    @Override
    protected void handleDragDropAction(DragEvent event){
        setLayout(LayOut.LOADING, layoutBody);

        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
            File droppedFile = event.getDragboard().getFiles().get(0);
            String droppedFileName = droppedFile.getName();
            if (droppedFileName.substring(droppedFileName.lastIndexOf(".")).toLowerCase().equals(".csv")) {
                System.out.println("Dropped File: "+droppedFileName);

                ArrayList<Contact> importedContacts = importFromFile(droppedFile);
                if(importedContacts.size() > 0) {
                    setOutPutList(importedContacts);
                    setLayout(LayOut.OUTPUT_VIEW, layoutBody);
                }
                else {
                    alerts.showWarningAlert("No Contacts Found");
                    setLayout(LayOut.DRAG_AND_DROP, layoutBody);
                }
            }
            else {
                alerts.showErrorAlert("Unsupported File!");
                setLayout(LayOut.DRAG_AND_DROP, layoutBody);
            }
        }
    }
    @Override
    protected void chooseFile(ActionEvent event){
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choosing CSV File");
        fileChooser.setInitialDirectory( new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );

        File file = fileChooser.showOpenDialog(stage);
        setLayout(LayOut.LOADING, layoutBody);
        ArrayList<Contact> importedContacts;
        if(file != null) {
            importedContacts = importFromFile(file);

            if (importedContacts.size() > 0) {
                setOutPutList(importedContacts);
                setLayout(LayOut.OUTPUT_VIEW, layoutBody);
            } else {
                alerts.showWarningAlert("No Contacts Found");
                setLayout(LayOut.DRAG_AND_DROP, layoutBody);
            }
        }
        else
            alerts.showErrorAlert("File Not Found");

        setLayout(LayOut.DRAG_AND_DROP, layoutBody);
    }

    @Override
    protected void finishProcess(ActionEvent event) {
        reloadAction.run();
        onRequestExit.handle(event);
    }

    @Override
    protected void saveContacts(ArrayList<Contact> contacts) throws Exception {
        manager.addContacts(contacts);
    }

    private ArrayList<Contact> importFromFile(File file){
        ArrayList<Contact> importedContacts = new ArrayList<>();
        try {
            ArrayList<String[]> csvData = getCSVData(file);
            HashMap<String, Integer[]> indexMap;

            System.out.print(csvData.size()-1+"Contact(s) Found");

            indexMap = getIndexMap(csvData.getFirst()); //Get Column Index from first row
            for(int i=1; i<csvData.size(); i++)
                importedContacts.add(getContactFormat(csvData.get(i), indexMap));

        } catch (Exception e) {
            alerts.showErrorAlert("Could not import from File!");
            System.err.println("Error importing \n"+e.getMessage());
            return importedContacts;
        }
        return importedContacts;
    }

    private ArrayList<String[]> getCSVData(File file)
            throws FileNotFoundException {
        ArrayList<String[]> csvData = new ArrayList<>();
        String[] csvRow;
        Scanner scanner = new Scanner(file);
        for(int i=0; scanner.hasNextLine(); i++) {
            csvRow = (scanner.nextLine()).split(",");
            csvData.add(csvRow);
        }
        scanner.close();
        return csvData;
    }

    private Contact getContactFormat(String[] csvRow, HashMap<String, Integer[]> indexMap){
        ContactBuilder contactBuilder = new ContactBuilder();
        try {
            contactBuilder.setId(manager.getContactsCount() + 1);

            contactBuilder.setName(supplyField(csvRow, indexMap.get("name")));
            contactBuilder.setPhone(supplyField(csvRow, indexMap.get("phone")));
            contactBuilder.setEmail(supplyField(csvRow, indexMap.get("email")));
            contactBuilder.setAddress(
                    supplyField(csvRow, indexMap.get("street")),
                    supplyField(csvRow, indexMap.get("city")),
                    supplyField(csvRow, indexMap.get("state")),
                    supplyField(csvRow, indexMap.get("zipCode"))
            );
        }catch(Exception e){
            e.printStackTrace();
        }
        return contactBuilder.getContact();
    }

    private String supplyField(String[] csvRow, Integer[] indexes){
        String finalValue = "";
        try {
            if(indexes != null)
            for (Integer i : indexes)
                if (i != null)
                    finalValue += csvRow[i]+" ";
        }
        catch(NullPointerException e){
            System.err.println("Null index array");
            alerts.showErrorAlert("An error ocurred while formingatting contact.");
            e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("Supply error\n"+e.getMessage());
            alerts.showErrorAlert("An error ocurred while formingatting contact.");
            e.printStackTrace();
        }
        System.out.println("Supplied field " + finalValue+" indexes:\n");

        return finalValue;
    }

    private HashMap<String, Integer[]> getIndexMap(String[] csvRow) {
        HashMap<String, Integer[]> iMap = new HashMap<>();
        try {
            Integer[] indexes;
            int icount;
            for (String key : keyWordMap.keySet()) {
                if (key.equals("name"))
                    indexes = new Integer[3];
                else
                    indexes = new Integer[1];
                icount = 0;
                for (int i = 0; i < (keyWordMap.get(key)).length; i++) {
                    for (int j = 0; j < csvRow.length; j++)
                        if(icount != indexes.length) {
                            System.out.println("Matching " + keyWordMap.get(key)[i]+" found?"+(csvRow[j].trim().toLowerCase()).equals(keyWordMap.get(key)[i]));
                            if ((csvRow[j].trim().toLowerCase()).equals(keyWordMap.get(key)[i])) {
                                indexes[icount] = j;
                                System.out.println("index m:"+j);
                                icount++;
                            }
                        }
                        else break;
                }
                iMap.put(key, indexes);
            }
            System.out.println(iMap);
        } catch (Exception e) {
            System.err.println("Indexing error \n");
            e.printStackTrace();
        }
            return iMap;
    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public void setContactsManager(ContactsManager manager) {
        this.manager = manager;
    }

    @Override
    public void setReloadAction(Runnable reloadAction) {
        this.reloadAction = reloadAction;
        this.reloadAction2 = reloadAction;
    }

    @Override
    public void setOnRequestWindowExit(EventHandler<ActionEvent> event) {
        this.onRequestExit = event;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        keyWordMap = new HashMap<>();
        keyWordMap.put("name", new String[]{"first_name", "first name", "middle name", "last_name", "last name"});
        keyWordMap.put("phone", new String[]{"phone 1 - value", "phone"});
        keyWordMap.put("email", new String[]{"e-mail 1 - value", "email"});
        keyWordMap.put("street", new String[]{"street"});
        keyWordMap.put("city", new String[]{"city"});
        keyWordMap.put("state", new String[]{"state", "province"});
        keyWordMap.put("ziCode", new String[]{"zip", "postal"});

        importButton = chooseFileButton;
        setLayout(LayOut.DRAG_AND_DROP, layoutBody);
    }
}

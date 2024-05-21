package main.doanjava2;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import main.doanjava2.filePanel.FilePanel;
import main.doanjava2.graphCanvas.GraphCanvas;
import main.doanjava2.graphList.ControlCode;
import main.doanjava2.graphList.GraphList;
import main.doanjava2.inputKeyboard.InputKeyboard;
import main.doanjava2.topNavBar.SaveObject;
import main.doanjava2.topNavBar.TopNavBar;
import main.doanjava2.ultilities.PopDialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static main.doanjava2.Main.CreateNewWindow;

public class MainController implements Initializable {
    public File currentFile = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        organizeRef();
        initData();
        init2();
    }
    private void organizeRef(){
        graphList.setManagerRef(this);
        inputKeyboard.setManagerRef(this);
        graphCanvas.setManagerRef(this);
        topNavbar.setManagerRef(this);
        saveObject.setManagerRef(this);

        filePanel.setManagerRef(this);
    }
    private void initData(){
        graphData = FXCollections.observableArrayList();
    }
    private void init2(){
        graphCanvas.init();
        graphList.initDataBinding();

        setUIReponsiveness();
    }

    public void setUIReponsiveness() {
        mainUIScreen.widthProperty().addListener(ob -> graphCanvas.setWidth(mainUIScreen.getWidth()*0.75));
        mainUIScreen.heightProperty().addListener(ob -> graphCanvas.setHeight(mainUIScreen.getHeight()));
    }

    public void forwardInputRequest(String content) {
        graphList.insertContentIntoSelectingBlock(content);
    }
    public void forwardControlRequest(ControlCode code) {
        graphList.handleControlRequest(code);
    }
    public void createNewGraphDataAtEnd(){
        graphData.add(new GraphData());
    }

    public void Save(){
        try{
            saveObject.getCurrentProps();
            //set up jaxb
            JAXBContext context = JAXBContext.newInstance(SaveObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            if(currentFile!=null && (!currentFile.exists() || currentFile.isDirectory())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Unable to locate file");
                alert.setHeaderText("Unable to locate file\nDo you wish to create a new one?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    //open file chooser
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save File");

                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files (.graph)", ".graph");
                    fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showSaveDialog(null);
                    if(file==null){
                        return;
                    }
                    marshaller.marshal(saveObject, file);
                    currentFile = file;
                    //PopDialog.popSuccessDialog("Save file successfully");
                }
                return;
            }
            if(currentFile==null){
                //open file chooser
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files (.graph)", ".graph");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showSaveDialog(null);
                if(file==null){
                    return;
                }
                marshaller.marshal(saveObject, file);
                currentFile = file;
                //PopDialog.popSuccessDialog("Save file successfully");
            }
            else{
                marshaller.marshal(saveObject, currentFile);
            }
            AppendNewFileLocationToRecentFiles(currentFile.getAbsolutePath());
        }
        catch(JAXBException e){
            PopDialog.popErrorDialog("Save file failed",e.getMessage());
        }

    }
    public void SaveAs(){
        try {
            //set up jaxb
            saveObject.getCurrentProps();
            JAXBContext context = JAXBContext.newInstance(SaveObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //open file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files (.graph)", ".graph");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(null);
            if(file==null){
                return;
            }
            marshaller.marshal(saveObject, file);
            currentFile = file;
            AppendNewFileLocationToRecentFiles(currentFile.getAbsolutePath());
        }
        catch (JAXBException e){
            PopDialog.popErrorDialog("Save file failed",e.getMessage());
        }
    }

    public void Open(){
        //open fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(null);

        if(file!=null){
            try {
                MainController controller = CreateNewWindow();
                controller.PrimitiveLoad(file);
            } catch (JAXBException | IOException e) {
                e.printStackTrace();
                //PopDialog.popErrorDialog("Unable to load file",e.toString());
            }
        }
    }
    public void PrimitiveLoad(File file) throws JAXBException{
        if (file != null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SaveObject.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SaveObject saveObject = (SaveObject) unmarshaller.unmarshal(file);

            if (saveObject != null) {
                //System.out.println("Load successful.");
                graphData.clear();
                //System.out.println(graphData.size());

                for(GraphData cGraphData : saveObject.getGraphDatas()){
                    graphData.add(cGraphData);
                }
                //System.out.println(graphData.size());
                graphCanvas.setSetting(saveObject.getSetting());
                currentFile = file;
            }
        }
    }

    public Set<String> ReadRecentFiles(){
        File file = new File(Main.RecentFilesPath);

        Set<String> fileLocations = null;
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(Main.RecentFilesPath));
                fileLocations = new HashSet<>(lines);

                System.out.println("File content:");
                for (String line : fileLocations) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                PopDialog.popErrorDialog("Can't read recent files","");
            }
        }
        else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                PopDialog.popErrorDialog("Can't create recent files","");
            }
        }
        return fileLocations;
    }
    public void AppendNewFileLocationToRecentFiles(String newPath){
        Set<String> fileLocations = ReadRecentFiles();
        fileLocations.add(newPath);

        clearRecentFilesContent();
        try (FileWriter fw = new FileWriter(Main.RecentFilesPath, true);
             PrintWriter pw = new PrintWriter(fw)){
            for(String fileLocation: fileLocations){
                pw.println(fileLocation);
            }
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Can't add recent files","");
        }
    }
    public void RemoveFileLocationFromRecentFiles(String path){
        Set<String> fileLocations = ReadRecentFiles();
        fileLocations.remove(path);

        clearRecentFilesContent();
        try (FileWriter fw = new FileWriter(Main.RecentFilesPath, true);
             PrintWriter pw = new PrintWriter(fw)){
            for(String fileLocation: fileLocations){
                pw.println(fileLocation);
            }
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Can't add recent files","");
        }
    }
    private void clearRecentFilesContent(){
        try (FileWriter fw = new FileWriter(Main.RecentFilesPath)) {
            // Opening the file in write mode without append will clear the file
        } catch (IOException e) {
            PopDialog.popErrorDialog("Small error appear!","");
        }
    }
    public void CreateNewSave(File file) throws JAXBException{
        saveObject.getCurrentProps();
        //set up jaxb
        JAXBContext context = JAXBContext.newInstance(SaveObject.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        if(file==null){
            PopDialog.popErrorDialog("Save file failed", "file is null");
            return;
        }
        marshaller.marshal(saveObject, file);
        PopDialog.popSuccessDialog("Save file successfully");
    }

    public void ToggleFilePanel(){
        if(filePanel.isVisible()){
            filePanel.setVisible(false);
        }
        else{
            filePanel.setVisible(true);
            inputKeyboard.close();
        }
    }


    private @FXML Region mainUIScreen;

    public @FXML FilePanel filePanel;
    public @FXML GraphList graphList;
    public @FXML InputKeyboard inputKeyboard;
    public @FXML GraphCanvas graphCanvas;
    public @FXML TopNavBar topNavbar;

    public ObservableList<GraphData> graphData;
    public SaveObject saveObject = new SaveObject();
}

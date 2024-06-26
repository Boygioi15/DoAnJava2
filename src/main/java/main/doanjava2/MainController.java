package main.doanjava2;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import main.doanjava2.filePanel.FilePanel;
import main.doanjava2.graphCanvas.GraphCanvas;
import main.doanjava2.graphList.ControlCode;
import main.doanjava2.graphList.GraphBlock;
import main.doanjava2.graphList.GraphExpression;
import main.doanjava2.graphList.GraphList;
import main.doanjava2.inputKeyboard.InputKeyboard;
import main.doanjava2.topNavBar.SaveObject;
import main.doanjava2.topNavBar.TopNavBar;
import main.doanjava2.ultilities.PopDialog;
import org.apache.commons.io.FilenameUtils;

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
    public final BooleanProperty isChanged = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        organizeRef();
        initData();
        init2();

        AnchorPane.setTopAnchor(graphList,topNavbar.getHeight());
        AnchorPane.setTopAnchor(graphCanvas,topNavbar.getHeight());

        filePanel.CloseFilePanel();
        graphCanvas.CloseSettingUI();
        createNewGraphDataAtEnd();
        mainUIScreen.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
           // System.out.println(event.getSource().toString());
            if(!isEventFromNode(event,topNavbar)){
                filePanel.CloseFilePanel();
            }
            //setSelectedGraph(-1);
        });
        mainUIScreen.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
           // System.out.println(event.getSource().toString());
            if(!isEventFromNode(event,topNavbar)){
                filePanel.CloseFilePanel();
            }
        });
    }
    // Method to check if an event source is from a specific node or its descendants
    private boolean isEventFromNode(MouseEvent event, Node node) {
        // Check if the event target is the node or any of its children
        return node.equals(event.getSource()) || isDescendantOf(node, (Node) event.getTarget());
    }

    // Method to check if a target node is a descendant of a given node
    private boolean isDescendantOf(Node parent, Node child) {
        while (child != null) {
            if (child.equals(parent)) {
                return true;
            }
            child = child.getParent();
        }
        return false;
    }

    private void organizeRef() {
        graphList.setManagerRef(this);
        inputKeyboard.setManagerRef(this);
        graphCanvas.setManagerRef(this);
        topNavbar.setManagerRef(this);
        saveObject.setManagerRef(this);
        graphExpression.setManagerRef(this);

        filePanel.setManagerRef(this);
    }

    private void initData() {
        graphData = FXCollections.observableArrayList();
        graphData.addListener((ListChangeListener<? super GraphData>) ob -> {
            isChanged.setValue(true);
        });
    }

    private void init2() {
        graphCanvas.init();
        graphList.initDataBinding();

        setUIReponsiveness();
    }

    public void setUIReponsiveness() {
        mainUIScreen.widthProperty().addListener(ob -> graphCanvas.setWidth(mainUIScreen.getWidth() * 0.75));
        mainUIScreen.heightProperty().addListener(ob -> graphCanvas.setHeight(mainUIScreen.getHeight() - topNavbar.getHeight()));
    }

    public void forwardInputRequest(String content) {
        graphList.insertContentIntoSelectingBlock(content);
    }

    public void forwardControlRequest(ControlCode code) {
        graphList.handleControlRequest(code);
    }

    public void createNewGraphDataAtEnd() {
        graphData.add(new GraphData());
    }

    public void Save() {
        try {
            saveObject.getCurrentProps();
            //set up jaxb
            JAXBContext context = JAXBContext.newInstance(SaveObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            if (currentFile != null && (!currentFile.exists() || currentFile.isDirectory())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Unable to locate file");
                alert.setHeaderText("Unable to locate file\nDo you wish to create a new one?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    //open file chooser
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save File");

                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files (*.graph)");
                    fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showSaveDialog(null);
                    if (file == null) {
                        return;
                    }
                    marshaller.marshal(saveObject, file);
                    currentFile = file;
                    filePanel.saveAsButton.setText("Save a new copy");

                    topNavbar.UpdateFileName(FilenameUtils.getBaseName(currentFile.getName()));
                    isChanged.setValue(false);
                    //PopDialog.popSuccessDialog("Save file successfully");
                }
                return;
            }
            if (currentFile == null) {
                //open file chooser
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files ", "*.graph");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showSaveDialog(null);
                if (file == null) {
                    return;
                }
                marshaller.marshal(saveObject, file);
                currentFile = file;
                filePanel.saveAsButton.setText("Save a new copy");

                topNavbar.UpdateFileName(FilenameUtils.getBaseName(currentFile.getName()));
                //PopDialog.popSuccessDialog("Save file successfully");
            } else {
                marshaller.marshal(saveObject, currentFile);
            }
            isChanged.setValue(false);
            AddNewFileLocationToRecentFiles(currentFile.getAbsolutePath());
        } catch (JAXBException e) {
            PopDialog.popErrorDialog("Save file failed", e.getMessage());
        }

    }

    public void SaveAs() {
        try {
            //set up jaxb
            saveObject.getCurrentProps();
            JAXBContext context = JAXBContext.newInstance(SaveObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //open file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files", "*.graph");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(null);
            if (file == null) {
                return;
            }
            marshaller.marshal(saveObject, file);
            currentFile = file;
            filePanel.saveAsButton.setText("Save a new copy");

            topNavbar.UpdateFileName(FilenameUtils.getBaseName(currentFile.getName()));
            isChanged.setValue(false);
            AddNewFileLocationToRecentFiles(currentFile.getAbsolutePath());
        } catch (JAXBException e) {
            PopDialog.popErrorDialog("Save file failed", e.getMessage());
        }
    }

    public static void Open() {
        //open fileChooser
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files", "*.graph");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                MainController controller = CreateNewWindow();
                controller.PrimitiveLoad(file);
            } catch (JAXBException | IOException e) {
                PopDialog.popErrorDialog("Unable to load data", "");
            }
        }
    }

    public void PrimitiveLoad(File file) throws JAXBException {
        if (file != null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SaveObject.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SaveObject saveObject = (SaveObject) unmarshaller.unmarshal(file);

            if (saveObject != null) {
                //System.out.println("Load successful.");
                graphData.clear();
                //System.out.println(graphData.size());

                for (GraphData cGraphData : saveObject.getGraphDatas()) {
                    graphData.add(cGraphData);
                }
                //System.out.println(graphData.size());
                graphCanvas.setSetting(saveObject.getSetting());
                currentFile = file;
                filePanel.saveAsButton.setText("Save a new copy");

                topNavbar.UpdateFileName(FilenameUtils.removeExtension(currentFile.getName()));
                isChanged.setValue(false);
            }
        }
    }

    static public Map<String, Boolean> ReadRecentFiles() {
        File file = new File(Main.RecentFilesPath);

        Map<String, Boolean> fileMap = null;
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(Main.RecentFilesPath));
                fileMap = new HashMap<>();

                //System.out.println("File content:");
                for (String line : lines) {
                    char firstChar = line.charAt(0);
                    Boolean pinned;
                    if (firstChar == '0') { // So sánh với ký tự '0'
                        pinned = false;
                    } else {
                        pinned = true;
                    }
                    String fileLocation = line.substring(2); // Sử dụng index 2 để bắt đầu từ ký tự thứ 3
                    fileMap.put(fileLocation, pinned);
                }
            } catch (IOException e) {
                PopDialog.popErrorDialog("Can't read recent files", "");
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                PopDialog.popErrorDialog("Can't create recent files", "");
            }
        }
        return fileMap;
    }

    static public void editPinned(String filePath, boolean pinned) {
        File file = new File(filePath);

        // Kiểm tra sự tồn tại của tệp
        if (!file.exists()) {
            PopDialog.popErrorDialog("File not found", "The system cannot find the file specified: " + file.getPath() +
                    "\n\nYou can remove the file by right-click and select delete");
        }
        else {
            Map<String, Boolean> fileMap = ReadRecentFiles();
            if (fileMap.containsKey(filePath)) {
                pinned = !pinned;
                fileMap.put(filePath, pinned);

                clearRecentFilesContent();
                try (FileWriter fw = new FileWriter(Main.RecentFilesPath, true);
                     PrintWriter pw = new PrintWriter(fw)) {
                    for (Map.Entry<String, Boolean> entry : fileMap.entrySet()) {
                        String line = entry.getValue() ? "1" : "0";
                        line += " " + entry.getKey();
                        pw.println(line);
                    }
                } catch (IOException e) {
                    PopDialog.popErrorDialog("Can't edit recent files", e.getMessage());
                }
            } else {
                PopDialog.popErrorDialog("File not found in recent files", "");
            }
        }
    }

    static public void AddNewFileLocationToRecentFiles(String newPath) {
        Map<String, Boolean> fileMap = ReadRecentFiles();
        fileMap.put(newPath, false);

        clearRecentFilesContent();
        try (FileWriter fw = new FileWriter(Main.RecentFilesPath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            for (Map.Entry<String, Boolean> entry : fileMap.entrySet()) {
                String line = "";
                if (entry.getValue()) {
                    line = line.concat("1");
                } else {
                    line = line.concat("0");
                }
                line = line.concat(" ");
                line = line.concat(entry.getKey());
                pw.println(line);
            }
        } catch (IOException e) {
            PopDialog.popErrorDialog("Can't add recent files", "");
        }
    }

    static public void RemoveFileLocationFromRecentFiles(String path) {
        Map<String, Boolean> fileMap = ReadRecentFiles();
        fileMap.remove(path);

        clearRecentFilesContent();
        try (FileWriter fw = new FileWriter(Main.RecentFilesPath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            for (Map.Entry<String, Boolean> entry : fileMap.entrySet()) {
                String line = "";
                if (entry.getValue()) {
                    line = line.concat("1");
                } else {
                    line = line.concat("0");
                }
                line = line.concat(" ");
                line = line.concat(entry.getKey());
                pw.println(line);
            }
        } catch (IOException e) {
            PopDialog.popErrorDialog("Can't add recent files", "");
        }
    }

    static private void clearRecentFilesContent() {
        try (FileWriter fw = new FileWriter(Main.RecentFilesPath)) {
            // Opening the file in write mode without append will clear the file
        } catch (IOException e) {
            PopDialog.popErrorDialog("Small error appear!", "");
        }
    }

    public void duplicateGraphData(GraphData original) {
        if(graphData.size()>19){
            return;
        }
        if (original != null) {
            GraphData temp = original.clone();
            if (temp.getExpressionString().isEmpty()) {
                graphData.add(temp);
                return;
            }
            String tempExpressionPart = original.getExpressionString();
            if (temp.getExpressionString().contains("=")) {
                String[] parts = original.getExpressionString().split("=");
                tempExpressionPart = parts[1].trim();
            }
            temp.setExpressionName("");
            temp.setExpressionString((tempExpressionPart));

            graphData.add(temp);
        } else {
            throw new IllegalArgumentException("origanel null ");
        }

    }

    public void ToggleFilePanel() {
        filePanel.ToggleFilePanel();
    }

    public void removeGraphData(GraphData original) {
        if (graphData != null) {
            List<GraphData> toRemove = new ArrayList<>();
            Set<GraphData> visited = new HashSet<>();
            String originalName = extractFunctionName(original.getExpressionName());
            if (originalName.isEmpty()) {
                graphData.remove(original);
                return;
            }
            toRemove.add(original);
            visited.add(original);

            findDependentGraphs(originalName, toRemove, visited);

            for (GraphData dependent : toRemove) {
                graphData.remove(dependent);
                String expressionName = dependent.getExpressionName();
                graphExpression.setExpressionValue(expressionName, "");
            }

            graphExpression.printFunctionMap();
        } else {
            throw new IllegalArgumentException("null");
        }
    }

    public void setSelectedGraph(int selection) {
        if (selectedGraph != -1) {
            graphData.get(selectedGraph).setSelected(false);
        }
        selectedGraph = selection;
        if (selection != -1) {
            graphData.get(selection).setSelected(true);
        }
        graphList.updateSelectedGraphBlock(selection);
        //System.out.println("Check selection" + selection);
    }

    public int getSelectedGraph() {
        return selectedGraph;
    }

    private void findDependentGraphs(String originalName, List<GraphData> toRemove, Set<GraphData> visited) {
        for (GraphData data : graphData) {
            if (!visited.contains(data) && data.getExpressionString().contains(originalName + "(")) {
                visited.add(data);
                toRemove.add(data);
                String dependentExpName = extractFunctionName(data.getExpressionName());
                findDependentGraphs(dependentExpName, toRemove, visited);
            }
        }
    }

    private String extractFunctionName(String expressionName) {
        if (expressionName.contains("(")) {
            return expressionName.substring(0, expressionName.indexOf('(')).trim();
        }
        return expressionName.trim();
    }

    public String handleReplaceExpressions(GraphData model) {
        if (model.getExpressionName().isEmpty()) {
            if (model.getExpressionString().contains("=")) {
                String expressionPart = graphExpression.parseExpression(model.getExpressionString());
                String exp = graphExpression.transExpressions(expressionPart);
                return exp;
            } else {
                String exp = graphExpression.transExpressions(model.getExpressionString());
                return exp;
            }

        } else {
            // Kiểm tra nếu biểu thức trống và tên hàm không trống
            if (!model.getExpressionString().contains("=")) {
                return "";
            }


            String expressionPart = graphExpression.parseExpression(model.getExpressionString());

            // Đảm bảo expressionPart không null trước khi sử dụng
            if (expressionPart != null && !expressionPart.isEmpty()) {
                graphExpression.setExpressionValue(model.getExpressionName(), expressionPart);
                String exprValue = graphExpression.getExpressionValue(model.getExpressionName());
                String exp = graphExpression.transExpressions(exprValue);
                return exp;
            } else {
                // Xử lý trường hợp expressionPart là null
                return "";
            }

        }
    }

    public void OpenBlockPane(){
        filePanelBlockPane.setVisible(true);
    }
    public void CloseBlockPane(){
        filePanelBlockPane.setVisible(false);
    }
    private @FXML Region mainUIScreen;
    public @FXML FilePanel filePanel;
    public @FXML GraphList graphList;
    public @FXML InputKeyboard inputKeyboard;
    public @FXML GraphCanvas graphCanvas;
    public @FXML TopNavBar topNavbar;

    @FXML public Pane filePanelBlockPane;
    public ObservableList<GraphData> graphData;
    public SaveObject saveObject = new SaveObject();
    private int selectedGraph = -1;
    public GraphExpression graphExpression = new GraphExpression();
}

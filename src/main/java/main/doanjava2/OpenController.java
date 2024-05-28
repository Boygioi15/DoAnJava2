package main.doanjava2;

import jakarta.xml.bind.JAXBException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.doanjava2.openRecent.RecentBlock;
import main.doanjava2.ultilities.PopDialog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static main.doanjava2.Main.CreateNewWindow;
import static main.doanjava2.Main.RecentFilesPath;

public class OpenController implements Initializable {
    private List<File> recentFiles = new ArrayList<>();

    @FXML
    private HBox createNewButton;
    @FXML
    private HBox openFromFileSystemButton;
    @FXML
    private VBox pinnedContainer;
    @FXML
    private VBox otherContainer;
    @FXML
    private TextField searchTextField;
    @FXML
    private TitledPane titledPaneOther;
    @FXML
    private TitledPane titledPanePinned;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRecentBlocks();
    }

    public void initManager() {
        createNewButton.setOnMouseClicked(mouseEvent -> {
            try {
                CreateNewWindow();
                primaryStage.close();
            } catch (IOException e) {
                PopDialog.popErrorDialog("Unable to create new window", "");
            }
        });
        openFromFileSystemButton.setOnMouseClicked(mouseEvent -> {
            MainController.Open();
            primaryStage.close();
        });
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchRecent(newValue);
        });
    }

    private void searchRecent(String key) {
        pinnedContainer.getChildren().clear();
        otherContainer.getChildren().clear();

        for (File file : recentFiles) {
            String fileName = file.getName().toLowerCase();
            String filePath = file.getAbsolutePath().toLowerCase();
            if (fileName.contains(key.toLowerCase()) || filePath.contains(key.toLowerCase())) {
                boolean pinned = MainController.ReadRecentFiles().getOrDefault(file.getAbsolutePath(), false);
                addRecentBlock(file, pinned);
            }
        }
    }


    private void loadRecentBlocks() {
        Map<String, Boolean> recentFileMap = MainController.ReadRecentFiles();
        List<Map.Entry<String, Boolean>> sortedEntries = new ArrayList<>(recentFileMap.entrySet());

        // Sắp xếp các tệp theo thời gian sửa đổi lần cuối
        sortedEntries.sort((entry1, entry2) -> {
            File file1 = new File(entry1.getKey());
            File file2 = new File(entry2.getKey());
            long lastModified1 = file1.lastModified();
            long lastModified2 = file2.lastModified();
            return Long.compare(lastModified2, lastModified1);
        });

        for (Map.Entry<String, Boolean> entry : sortedEntries) {
            File file = new File(entry.getKey());
            recentFiles.add(file);
            addRecentBlock(file, entry.getValue());
        }
    }


    private void addRecentBlock(File file, boolean pinned) {
        RecentBlock recentBlock = new RecentBlock();
        recentBlock.setFileNameString(file.getName());
        recentBlock.setFileLocationString(file.getAbsolutePath());
        recentBlock.setPinButtonAction(event -> {
                    MainController.editPinned(file.getAbsolutePath(), pinned);
                    refreshRecentBlocks();
                },
                pinned);

        // Lấy thời gian sửa đổi lần cuối của tệp
        long lastModified = file.lastModified();
        Date lastModifiedDate = new Date(lastModified);

        // Định dạng thời gian theo định dạng mong muốn
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String lastModifiedString = dateFormat.format(lastModifiedDate);

        // Đặt thời gian vào recentBlock
        recentBlock.setLastOpenedTimeString(lastModifiedString);

        // Các bước còn lại
        Region tempBlock = recentBlock.getRecentBlock(); // Get the HBox from RecentBlock
        setupRecentBlockUI(file, tempBlock);
        tempBlock.setOnMouseEntered(mouseEvent ->
                recentBlock.appearPinBtn());
        tempBlock.setOnMouseExited(mouseEvent ->
                recentBlock.hidePinBtn());
        if (pinned) {
            pinnedContainer.getChildren().add(tempBlock);
        } else {
            otherContainer.getChildren().add(tempBlock);
        }
    }


    private void setupRecentBlockUI(File file, Region block) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem openItem = new MenuItem("Open");
        MenuItem deleteItem = new MenuItem("Delete");

        contextMenu.getItems().addAll(openItem, deleteItem);

        openItem.setOnAction(event -> handleFileAction(file, "open"));
        deleteItem.setOnAction(event -> handleFileAction(file, "delete"));

        block.setOnContextMenuRequested(event -> contextMenu.show(block, event.getScreenX(), event.getScreenY()));

        block.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleFileAction(file, "open");
            }
        });
    }

    private void handleFileAction(File file, String action) {
        switch (action) {
            case "open":
                if (!file.exists()) {
                    PopDialog.popErrorDialog("File not found", "The system cannot find the file specified: " + file.getAbsolutePath() +
                            "\n\nDự án bạn chọn có thể không còn tồn tại. Bạn có thể xóa khỏi danh sách bằng cách click chuột phải vào dự án đó, chọn Delete.");
                    return;
                }
                try {
                    MainController controller = CreateNewWindow();
                    controller.PrimitiveLoad(file);
                    primaryStage.close();
                } catch (JAXBException | IOException e) {
                    PopDialog.popErrorDialog("Unable to load data", e.getMessage());
                }
                break;
            case "delete":
                recentFiles.remove(file);
                MainController.RemoveFileLocationFromRecentFiles(file.getAbsolutePath());
                refreshRecentBlocks();
                break;
            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    private void refreshRecentBlocks() {
        pinnedContainer.getChildren().clear();
        otherContainer.getChildren().clear();

        for (File file : recentFiles) {
            boolean pinned = MainController.ReadRecentFiles().getOrDefault(file.getAbsolutePath(), false);
            addRecentBlock(file, pinned);
        }
        titledPaneOther.setExpanded(true);
        titledPanePinned.setExpanded(true);
    }
}

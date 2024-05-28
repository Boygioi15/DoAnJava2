    package main.doanjava2;

    import jakarta.xml.bind.JAXBException;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.TextField;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.Region;
    import javafx.scene.layout.VBox;
    import javafx.stage.FileChooser;
    import main.doanjava2.openRecent.RecentBlock;
    import main.doanjava2.ultilities.PopDialog;

    import java.io.File;
    import java.io.IOException;
    import java.net.URL;
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
        @FXML private TextField searchTextField;

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            loadRecentBlocks();
        }

        public void initManager() {
            createNewButton.setOnMouseClicked(mouseEvent -> {
                try {
                    CreateNewWindow();
                } catch (IOException e) {
                    PopDialog.popErrorDialog("Unable to create new window", "");
                }
            });
            openFromFileSystemButton.setOnMouseClicked(mouseEvent -> {
                MainController.Open();
            });
            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
               searchRecent(newValue);
            });


        }

        private void searchRecent(String key) {
            pinnedContainer.getChildren().clear();

            for (File file : recentFiles) {
                if (file.getName().toLowerCase().contains(key.toLowerCase())) {
                    addRecentBlock(file);
                }
            }
        }
        private void loadRecentBlocks() {
            Map<String,Boolean> recentFileMap = MainController.ReadRecentFiles();
            //sort lai map
            //sua lai pinned tuong ung, nho them time
            for(Map.Entry<String,Boolean> entry : recentFileMap.entrySet()){
                //them phan tu vao
            }
            try {
                Scanner scanner = new Scanner(new File(RecentFilesPath));
                while (scanner.hasNextLine()) {
                    String fileMap = scanner.nextLine();
                    Boolean pinned;

                    /*
                    File file = new File(filePath);
                    recentFiles.add(file);
                    addRecentBlock(file);

                     */
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void addRecentBlock(File file) {
            RecentBlock recentBlock = new RecentBlock();
            recentBlock.setFileNameString(file.getName());
            recentBlock.setFileLocationString(file.getAbsolutePath());

            Region tempBlock = recentBlock.getRecentBlock(); // Get the HBox from RecentBlock
            tempBlock.setOnMouseClicked(event -> {
                try {
                    MainController controller = CreateNewWindow();
                    controller.PrimitiveLoad(file);
                } catch (JAXBException | IOException e) {
                    PopDialog.popErrorDialog("Unable to load data", "");
                }
            });

            pinnedContainer.getChildren().add(tempBlock);
        }
        /*
        public void Open() {
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

         */
    }

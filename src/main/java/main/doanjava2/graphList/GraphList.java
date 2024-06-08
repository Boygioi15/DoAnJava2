package main.doanjava2.graphList;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class GraphList extends GridPane {
    public boolean isLightTheme = true;
    TextField currentlySelectedTextField;
    @FXML
    Button addNewBtn;
    MainController mnr;
    @FXML
    private VBox graphListBox;
    @FXML
    private GridPane graphListPane;
    @FXML
    private Button openButton;

    public GraphList() {
        loadFXML();
        initEvent();

    }

    public void setManagerRef(MainController ref) {
        mnr = ref;
    }

    private void loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GraphList/GraphListUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void initDataBinding() {
        mnr.graphData.addListener(new ListChangeListener<GraphData>() {
            @Override
            public void onChanged(Change<? extends GraphData> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        int index = c.getFrom();
                        addGraphBlock(index, mnr.graphData.get(index));
                    } else if (c.wasRemoved()) {
                        int index = c.getFrom();
                        removeGraphBlock(index);
                    }
                }
            }

        });
    }

    boolean isOpen = true;

    private void initEvent() {
        openButton.setOnAction(event -> {
            if (isOpen) {
                closeGraphList();
            } else {
                openGraphList();
            }
            isOpen = !isOpen;
        });

        Platform.runLater(() -> {
            //dua event len goc
            Scene scene = this.getScene();
            if (scene != null) {
                //them su kien chuot
                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    //lay node xuat phat
                    Node target = (Node) event.getTarget();
                    //dang chon & ko phai node minh dang select
                    if (currentlySelectedTextField != null && !isDescendantOf(target, currentlySelectedTextField)) {
                        updateSelectedGraphBlock(null);
                        mnr.setSelectedGraph(-1);
                        requestLosingFocus();
                    }
                });
            }
        });
    }

    public boolean isDescendantOf(Node descendant, Node ancestor) {
        while (descendant != null) {
            if (descendant == ancestor) {
                return true;
            } else if (descendant == mnr.inputKeyboard) {
                return true;
            }
            descendant = descendant.getParent();
        }
        return false;
    }

    @FXML
    private void requestAddingGraphData() {
        mnr.createNewGraphDataAtEnd();
    }

    public void addGraphBlock(int pos, GraphData graphData) {
        GraphBlock toAdd = new GraphBlock();
        toAdd.setPrefWidth(graphListBox.getWidth());
        toAdd.setDataSource(graphData);
        toAdd.requestFocus();
        toAdd.setManagerRef(mnr);
        toAdd.updateTheme(isLightTheme);
        TextField textField = toAdd.getExpressionTextField();
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                currentlySelectedTextField = textField;
                mnr.setSelectedGraph(graphListBox.getChildren().indexOf(toAdd));
                updateSelectedGraphBlock(toAdd);
            }
        });


        graphListBox.getChildren().add(pos, toAdd);

        Platform.runLater(() -> textField.requestFocus());

        if (graphListBox.getChildren().size() > 19) {
            addNewBtn.setVisible(false);
        }
        updateLabels();
    }

    PseudoClass graphBlockSelected = PseudoClass.getPseudoClass("selected");

    private void updateSelectedGraphBlock(GraphBlock selectedGraphBlock) {
        for (Node node : graphListBox.getChildren()) {
            node.pseudoClassStateChanged(graphBlockSelected, false);
        }
        if (selectedGraphBlock == null) {
            return;
        }
        selectedGraphBlock.pseudoClassStateChanged(graphBlockSelected, true);
    }

    public void updateSelectedGraphBlock(int index) {
        for (Node node : graphListBox.getChildren()) {
            node.pseudoClassStateChanged(graphBlockSelected, false);
        }
        if (index == -1) {
            return;
        }
        GraphBlock selected = (GraphBlock) graphListBox.getChildren().get(index);
        selected.pseudoClassStateChanged(graphBlockSelected, true);
    }

    public void removeGraphBlock(int pos) {
        graphListBox.getChildren().remove(pos);
        if (graphListBox.getChildren().size() < 20) {
            addNewBtn.setVisible(true);
        }
        updateSelectedGraphBlock(null);
        mnr.setSelectedGraph(-1);
        requestLosingFocus();
        updateLabels();
    }

    public void insertContentIntoSelectingBlock(String content) {
        if (currentlySelectedTextField != null) {
            int oldCaretPosition = currentlySelectedTextField.getCaretPosition();
            String currentText = currentlySelectedTextField.getText();
            String newText = currentText.substring(0, oldCaretPosition) + content + currentText.substring(oldCaretPosition);

            currentlySelectedTextField.setText(newText);
            currentlySelectedTextField.positionCaret(oldCaretPosition + content.length());
        }
    }

    public void handleControlRequest(ControlCode code) {
        if (code == ControlCode.MoveCaretLeft) {
            if (currentlySelectedTextField == null) {
                return;
            }
            if (currentlySelectedTextField.getCaretPosition() > 0) {
                currentlySelectedTextField.positionCaret(currentlySelectedTextField.getCaretPosition() - 1);
            }
        } else if (code == ControlCode.MoveCaretRight) {
            if (currentlySelectedTextField == null) {
                return;
            }
            currentlySelectedTextField.positionCaret(currentlySelectedTextField.getCaretPosition() + 1);
        } else if (code == ControlCode.Delete) {
            int caretPosition = currentlySelectedTextField.getCaretPosition();
            if (caretPosition > 0) {
                String currentText = currentlySelectedTextField.getText();
                String newText = currentText.substring(0, caretPosition - 1) + currentText.substring(caretPosition);
                currentlySelectedTextField.setText(newText);
                currentlySelectedTextField.positionCaret(caretPosition - 1);
            }
        }

    }

    public void requestLosingFocus() {
        currentlySelectedTextField = null;
        graphListBox.requestFocus();
    }


    @FXML
    public void closeGraphList() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.1), graphListPane);
        tt.setToX(-graphListPane.getWidth() + 20);
        tt.play();

        tt.setOnFinished(e -> {
            openButton.setVisible(true);
            this.setManaged(false);
        });
    }

    @FXML
    public void openGraphList() {
        for (var node : graphListPane.getChildren()) {
            node.setVisible(true);
        }
        this.setManaged(true);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.1), graphListPane);
        tt.setToX(0);
        tt.play();
    }

    private void updateLabels() {
        for (int i = 0; i < graphListBox.getChildren().size(); i++) {
            if (graphListBox.getChildren().get(i) instanceof GraphBlock temp) {
                temp.updateIndex(i);
            }
        }
    }

    public int getSizeOfBox() {
        return graphListBox.getChildren().size();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void updateBlockTheme() {
        for (Node block : graphListBox.getChildren()) {
            if (block instanceof GraphBlock temp) {
                temp.updateTheme(isLightTheme);
            }
            //mnr.filePanel.getStylesheets().add(getClass().getResource("/css/LightTheme/FilePanel/FilePanelUI.css").toExternalForm());
        }
    }
}
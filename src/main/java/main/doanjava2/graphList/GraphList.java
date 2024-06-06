package main.doanjava2.graphList;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class GraphList extends GridPane {
    TextField currentlySelectedTextField;
    @FXML
    MenuButton addNewBtn;
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
        toAdd.getStyleClass().add("graph-block");
        TextField textField = toAdd.getExpressionTextField();

        graphListBox.getChildren().add(pos, toAdd);
        if (graphListBox.getChildren().size() > 9) {
            addNewBtn.setVisible(false);
        }
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                currentlySelectedTextField = textField;
                System.out.println("graphListBox.getChildren().indexOf(toAdd) " +graphListBox.getChildren().indexOf(toAdd));
                System.out.println("pos" +pos);
                mnr.setSelectedGraph(graphListBox.getChildren().indexOf(toAdd));
                updateSelectedGraphBlock(toAdd);
            }
        });
    }

    private void updateSelectedGraphBlock(GraphBlock selectedGraphBlock) {
        for (Node node : graphListBox.getChildren()) {
            node.getStyleClass().remove("selected");
        }
        selectedGraphBlock.getStyleClass().add("selected");
    }

    public void removeGraphBlock(int pos) {
        graphListBox.getChildren().remove(pos);
        if (graphListBox.getChildren().size() < 10) {
            // addNewBtn.setVisible(false);
        }
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
            if (currentlySelectedTextField.getCaretPosition() > 0) {
                currentlySelectedTextField.positionCaret(currentlySelectedTextField.getCaretPosition() - 1);
            }
        } else if (code == ControlCode.MoveCaretRight) {
            currentlySelectedTextField.positionCaret(currentlySelectedTextField.getCaretPosition() + 1);
        }
    }

    public void requestLosingFocus() {
        currentlySelectedTextField = null;
        graphListBox.requestFocus();
    }


    @FXML
    public void closeGraphList() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), graphListPane);
        tt.setToX(-graphListPane.getWidth() + 35);
        tt.play();

        tt.setOnFinished(e -> {
            openButton.setVisible(true);
            openButton.setText(">>");
            this.setManaged(false);
        });
    }

    @FXML
    public void openGraphList() {
        for (var node : graphListPane.getChildren()) {
            node.setVisible(true);
        }
        this.setManaged(true);
        openButton.setText("<<");

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), graphListPane);
        tt.setToX(0);
        tt.play();
    }

    private void updateLabels() {
        for (int i = 0; i < graphListBox.getChildren().size(); i++) {
            HBox hbox = (HBox) graphListBox.getChildren().get(i);
            Label label = (Label) hbox.getChildren().get(0);
            label.setText((i + 1) + ".");
        }
    }

    public int getSizeOfBox() {
        return graphListBox.getChildren().size();
    }
}
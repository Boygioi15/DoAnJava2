package main.doanjava2.inputKeyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import main.doanjava2.graphList.ControlCode;
import main.doanjava2.MainController;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;


import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class InputKeyboard extends AnchorPane {
	MainController mnr;
	boolean isClosed = false;
	public boolean isLightTheme=true;
	private @FXML Button closeButton;

	public InputKeyboard() {
		loadFXML();
		initLoseFocusForAllButton();
		initControlButtonEvent();
		initInsertButtonEvent();
		initFunction();
	}

	public void setManagerRef(MainController ref) {
		mnr = ref;
	}

	private void loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/InputKeyboard/InputKeyboardUI.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

	}

	private @FXML HBox numberKeyboard;
	private @FXML VBox normalLetterKeyboard;
	private @FXML VBox cappedLetterKeyboard;

	private void initLoseFocusForAllButton() {
		// used for inputting graph list
		for (Node node : getAllChildrenNodes(this)) {
			node.setFocusTraversable(false);
		}
	}

	@FXML
	protected void toggleOnOff() {
		// System.out.println("Input keyboard closed!");
		if (!isClosed) {
			close();
		} else {
			open();
		}

	}

	public void close() {
		if (this != null) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), this);
			transition.setToY(this.getHeight());
			transition.play();
		}
		if (closeButton != null) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), closeButton);
			transition.setToY(-closeButton.getHeight() * 1.5);
			transition.play();
		}
		isClosed = true;
	}

	public void open() {
		if (closeButton != null) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(0.1), closeButton);
			transition.setToY(0);
			transition.play();
		}
		if (this != null) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(0.1), this);
			transition.setToY(0);
			transition.play();
		}
		isClosed = false;
	}

	// control buttons
	private @FXML Button control_LetterKeyboardSwitcher;

	private void initControlButtonEvent() {
		control_LetterKeyboardSwitcher.setOnAction(ob -> switchToNormalLetterKeyboard());
	}

	private void switchToNormalLetterKeyboard() {
		normalLetterKeyboard.setVisible(true);
		numberKeyboard.setVisible(false);
	}

	@FXML
	private void switchNoramlLetterToNumberKeyBoard() {
		normalLetterKeyboard.setVisible(false);
		numberKeyboard.setVisible(true);
	}

	@FXML
	private void switchCappedLetterToNumberKeyBoard() {
		cappedLetterKeyboard.setVisible(false);
		numberKeyboard.setVisible(true);
	}

	@FXML
	private void switchNormalLetterToCappedLetterKeyBoard() {
		normalLetterKeyboard.setVisible(false);
		cappedLetterKeyboard.setVisible(true);
	}

	@FXML
	private void switchCappedLetterToNormalLetterKeyBoard() {
		cappedLetterKeyboard.setVisible(false);
		normalLetterKeyboard.setVisible(true);
	}

	// insert buttons
	@FXML
	private void handleButtonPressing_Type1(MouseEvent event) {
		// System.out.println("Begin press");
		Button button = (Button) event.getSource();
		String buttonText = button.getText();

		mnr.forwardInputRequest(buttonText);
	}

	@FXML
	private void insertSpecialButtonPressing(MouseEvent event) {
		Button button = (Button) event.getSource();
		String buttonText = button.getText().concat("()");
		mnr.forwardInputRequest(buttonText);

		mnr.forwardControlRequest(ControlCode.MoveCaretLeft);
	}

	@FXML
	private void deleteButtonPressing() {
		mnr.forwardControlRequest(ControlCode.Delete);
	}

	@FXML
	private void moveCaretLeftButtonPressing() {
		mnr.forwardControlRequest(ControlCode.MoveCaretLeft);
	}

	@FXML
	private void moveCaretRightButtonPressing() {
		mnr.forwardControlRequest(ControlCode.MoveCaretRight);
	}

	@FXML
	private void insertAbsButtonPressing(MouseEvent event) {
		Button button = (Button) event.getSource();
		String buttonText = button.getText();
		mnr.forwardInputRequest(buttonText);

		mnr.forwardControlRequest(ControlCode.MoveCaretLeft);
		mnr.forwardControlRequest(ControlCode.Delete);
	}

	// control buttons
	private @FXML Button insert_squarePower;
	private @FXML Button insert_genericPower;
	private @FXML Button insert_multiplication;

	private void initInsertButtonEvent() {
		// forward message
		insert_squarePower.setOnAction(ob -> mnr.forwardInputRequest("^2"));
		insert_genericPower.setOnAction(ob -> mnr.forwardInputRequest("^"));
		insert_multiplication.setOnAction(ob -> mnr.forwardInputRequest("*"));
	}

	private @FXML Button functionBtn;
	private PopOver popover = new PopOver();

	private void initFunction() {

	    functionBtn.setOnAction(event -> {

	        if (popover.isShowing()) {
	            popover.hide();
	        } else {
	            try {
	                // Load nội dung từ file FXML
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InputKeyboard/functionUI.fxml"));
	                loader.setController(this);
	                Parent root = loader.load();

	                // Tạo một PopOver và đặt nội dung là root
	                popover.setContentNode(root);

	                // Đặt vị trí cho mũi tên
	                popover.setArrowLocation(ArrowLocation.BOTTOM_CENTER);

	                // Không cho phép PopOver bị rời đi
	                popover.setDetachable(false);
	                popover.setDetached(false);
					setTheme(isLightTheme);
	                // Hiển thị PopOver
	                popover.show(functionBtn);
	            }
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    });
	}
	public void setTheme(boolean isThemeLight) {
		String cssPath = isThemeLight ? "/css/LightTheme/InputKeyboard/functionUI.css" : "/css/DarkTheme/InputKeyboard/functionUI.css";
		String css = Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm();

		if (popover.getContentNode() instanceof Parent) {
			Parent content = (Parent) popover.getContentNode();
			content.getStylesheets().clear();
			content.getStylesheets().add(css);

			// Force reapply the styles to ensure the changes take effect
			content.applyCss();
			content.layout();
		}

	}
	// ultilities functions
	public static ArrayList<Node> getAllChildrenNodes(Parent root) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		addAllDescendents(root, nodes);
		return nodes;
	}

	private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodes.add(node);
			if (node instanceof Parent) {
				addAllDescendents((Parent) node, nodes);
			}
		}
	}
}
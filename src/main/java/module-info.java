module main.doanjava2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.udojava.evalex;
    requires java.xml;
    requires ch.obermuhlner.math.big;
    requires jakarta.xml.bind;
    requires org.apache.commons.io;

    opens main.doanjava2;
    opens main.doanjava2.topNavBar to javafx.fxml, jakarta.xml.bind;
    opens main.doanjava2.graphCanvas;
    opens main.doanjava2.graphList to javafx.fxml;
    opens main.doanjava2.inputKeyboard to javafx.fxml;
    opens main.doanjava2.filePanel to javafx.fxml;

    exports  main.doanjava2.graphCanvas;
    exports  main.doanjava2.graphList;
    exports  main.doanjava2.inputKeyboard;
    exports  main.doanjava2.topNavBar;
    exports  main.doanjava2.filePanel;
    exports main.doanjava2;
}
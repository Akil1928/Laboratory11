module ucr.lab.laboratory11 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ucr.lab.laboratory11 to javafx.fxml;
    exports ucr.lab.laboratory11;
}
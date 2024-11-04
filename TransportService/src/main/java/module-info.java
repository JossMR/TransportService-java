module transportservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens transportservice to javafx.fxml;
    opens transportservice.Controllers to javafx.fxml;
    exports transportservice;
}

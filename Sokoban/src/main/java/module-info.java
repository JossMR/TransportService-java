module sokoban {
    requires javafx.controls;
    requires javafx.fxml;

    opens sokoban to javafx.fxml;
    opens sokoban.Controllers to javafx.fxml;
    exports sokoban;
}

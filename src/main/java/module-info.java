module lit.litfx {
    requires javafx.controls;
    requires javafx.fxml;

    opens lit.litfx to javafx.fxml;
    exports lit.litfx;
}
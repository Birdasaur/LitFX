module lit.litfx {
    requires javafx.controls;
    requires javafx.fxml;

    opens lit.litfx to javafx.fxml;
    opens lit.litfx.demos to javafx.fxml;
    opens lit.litfx.controllers to javafx.fxml;
    
    exports lit.litfx;
    exports lit.litfx.demos;
    exports lit.litfx.controllers;
}
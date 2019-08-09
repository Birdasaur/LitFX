/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module lit.litfx.demos {
    requires lit.litfx.core;

    requires javafx.controls;
    requires javafx.fxml;

    opens lit.litfx.demos to javafx.fxml;
    opens lit.litfx.demos.controllers to javafx.fxml;

    exports lit.litfx.demos to javafx.graphics;
}
/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * Birdasaur
 * carld
 */
module lit.litfx.demos {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive lit.litfx.controls;
    opens lit.litfx.demos to javafx.fxml;
    opens lit.litfx.demos.controllers to javafx.fxml;
    exports lit.litfx.demos to javafx.graphics;
}

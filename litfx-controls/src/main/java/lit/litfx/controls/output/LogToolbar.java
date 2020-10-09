package lit.litfx.controls.output;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author phillsm1
 */
public class LogToolbar extends HBox {
    public ToggleButton textToggleButton;
    public ColorPicker picker;
    public Spinner fontSizeSpinner;
    public ObservableList<String> fonts;
    public ChoiceBox fontChoiceBox;
    
    public LogToolbar(double spacing, double padding) {
        setSpacing(spacing);
        setPadding(new Insets(padding));
        setAlignment(Pos.CENTER_LEFT);

        Image image = new Image(this.getClass().getResource("text.png").toExternalForm());
        ImageView im = new ImageView(image);
        im.setPreserveRatio(true);
        im.setFitHeight(32);
        im.setFitWidth(32);        
        textToggleButton = new ToggleButton("Plain Text", im );
        textToggleButton.setSelected(false);
        textToggleButton.setPrefWidth(150);

        picker = new ColorPicker(Color.GREEN);
        picker.setPrefWidth(125);
        fontSizeSpinner = new Spinner(8, 100, 20);
        fontSizeSpinner.setPrefWidth(125);
        fonts = FXCollections.observableList(Font.getFontNames());
        fontChoiceBox = new ChoiceBox(fonts);
        Optional<String> strOpt = fonts.stream().filter(font -> font.contains("Consolas")).findFirst();
        if(strOpt.isPresent())
            fontChoiceBox.getSelectionModel().select(strOpt.get());
        else
            fontChoiceBox.getSelectionModel().selectFirst();

        fontChoiceBox.setPrefWidth(200);
        getChildren().addAll(textToggleButton, fontChoiceBox, picker, fontSizeSpinner);
    }
}

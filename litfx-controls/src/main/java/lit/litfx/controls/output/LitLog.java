package lit.litfx.controls.output;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
public class LitLog extends BorderPane {
    static double DEFAULT_LINESPACING = 5;
    static int DEFAULT_MAXLINES = 50;
    private double lineSpacing;
    private int maxLines;
    private VBox vbox;
    private ScrollPane scrollPane;
    private TextArea textArea;
    private StackPane stackPane;
    public ObservableList<Text> lines;
    public SimpleBooleanProperty selectingProperty = new SimpleBooleanProperty(false);
    
    public LitLog() {
        this(DEFAULT_LINESPACING, DEFAULT_MAXLINES);
    }
    public LitLog(double lineSpacing, int maxLines) {
        this.lineSpacing = lineSpacing;
        this.maxLines = maxLines;
        lines = FXCollections.observableArrayList();
        Background transBack = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
        vbox = new VBox(this.getLineSpacing());
        vbox.setBackground(transBack);
        vbox.setAlignment(Pos.BOTTOM_LEFT);
        vbox.setFillWidth(true);
        
        textArea = new TextArea();
        textArea.getStyleClass().add("litlog-textarea");
        textArea.setEditable(false);
        textArea.minWidthProperty().bind(vbox.widthProperty());
        textArea.minHeightProperty().bind(vbox.heightProperty());
        textArea.maxWidthProperty().bind(vbox.widthProperty());
        textArea.maxHeightProperty().bind(vbox.heightProperty());
        
        stackPane = new StackPane(vbox, textArea);

        scrollPane = new ScrollPane(stackPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);    
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHvalue(1.0);    
        
        vbox.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));        
        setCenter(scrollPane);
        selectingProperty.addListener((obs,oV, nV)-> {
            FadeTransition fadeTextArea = new FadeTransition(Duration.millis(60), textArea);
            fadeTextArea.setAutoReverse(false);
            FadeTransition fadeVBox = new FadeTransition(Duration.millis(60), vbox);
            fadeVBox.setAutoReverse(false);
            if(selectingProperty.get()) {
                fadeTextArea.setToValue(1.0);
                fadeVBox.setToValue(0.0);
            } else {
                fadeTextArea.setToValue(0.0);
                fadeVBox.setToValue(1);
            }
            ParallelTransition pt = new ParallelTransition(fadeTextArea, fadeVBox);
            pt.play();
        });
//        stackPane.addEventHandler(MouseEvent.MOUSE_PRESSED, e-> {
//            if(e.isPrimaryButtonDown()) {
//                selectingProperty.set(true);
//                vbox.setMouseTransparent(true);
//            }
//        });
//        stackPane.addEventHandler(MouseEvent.MOUSE_RELEASED, e-> { 
//            if(!e.isPrimaryButtonDown() && selectingProperty.get()) {
//                vbox.setMouseTransparent(false);
//                selectingProperty.set(false);
//                
//            }
//        });
        textArea.setOpacity(0);
        getStyleClass().add("litlog-pane");
    }
    
    public void addLine(String line, Font font, Color color) {
        Text text = new Text(line);
        //Override default font
        text.setFont(font);
        text.setFill(color);
        appendText(text);
        
    }
    public void addLine(String line) {
        Text text = new Text(line);
        text.getStyleClass().add("litlog-text");
        appendText(text);
    }
    private void appendText(Text text) {
        lines.add(text);
        Platform.runLater(()-> animateLine(text));
    }
    private void animateLine(Text text) {
        final IntegerProperty i = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        String animatedString = text.getText();
        KeyFrame keyFrame1 = new KeyFrame( Duration.millis(30), event -> {
            if (i.get() > animatedString.length()) {
                timeline.stop();
                textArea.appendText(System.lineSeparator() + animatedString);
            } else {
                text.setText(animatedString.substring(0, i.get()));
                i.set(i.get() + 1);
            }
        });
        timeline.getKeyFrames().addAll(keyFrame1);
        timeline.setCycleCount(Animation.INDEFINITE);
        vbox.getChildren().add(text);
        timeline.play();
    }

    /**
     * @return the lineSpacing
     */
    public double getLineSpacing() {
        return lineSpacing;
    }

    /**
     * @param lineSpacing the lineSpacing to set
     */
    public void setLineSpacing(double lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    /**
     * @return the maxLines
     */
    public int getMaxLines() {
        return maxLines;
    }

    /**
     * @param maxLines the maxLines to set
     */
    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }
}

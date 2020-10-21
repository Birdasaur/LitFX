package lit.litfx.controls.covalent;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import lit.litfx.controls.covalent.CursorMappings.RESIZE_DIRECTION;
import lit.litfx.controls.covalent.events.CovalentPaneEvent;
import java.util.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseButton;
import static lit.litfx.controls.covalent.BindablePointBuilder.*;
import static lit.litfx.controls.covalent.CursorMappings.RESIZE_DIRECTION.NONE;
import static lit.litfx.controls.covalent.CursorMappings.cursorMap;
import static lit.litfx.controls.covalent.CursorMappings.cursorSegmentArray;
import lit.litfx.core.components.StyleableRectangle;

public class PathPane extends AnchorPane {

    public SimpleStringProperty mainTitleTextProperty = new SimpleStringProperty("");
    public SimpleStringProperty mainTitleText2Property = new SimpleStringProperty("");
    private Scene scene;
    private double borderTimeMs, contentTimeMs;
    private ResizePaneTracker resizePaneTracker;
    private Map<Integer, Line> lineSegmentMap = new HashMap<>();
    private IntegerProperty segmentSelected = new SimpleIntegerProperty(-1);
    public Pane contentPane;
    public Path outerFrame = null; //new Path();
    public MainContentViewArea mainContentBorderFrame;
    public Node leftAccent;
    public Node leftTab;
    public Pane windowButtons;
    public Pane mainTitleArea;
    private boolean enableDrag = true;
    private Point2D anchorPt;
    private Point2D previousLocation;
    Animation enterScene;
    SimpleBooleanProperty minimizedProperty = new SimpleBooleanProperty(false);

    public PathPane(Scene scene,
                    int width,
                    int height,
                    Pane userContent,
                    String mainTitleText,
                    String mainTitleText2,
                    double borderTimeMs,
                    double contentTimeMs) {
        this.scene = scene;
        this.contentPane = userContent;
        this.borderTimeMs = borderTimeMs;
        this.contentTimeMs = contentTimeMs;
        mainTitleTextProperty.set(null != mainTitleText ? mainTitleText : "");
        mainTitleText2Property.set(null != mainTitleText2 ? mainTitleText2 : "");
        setManaged(false);
        setWidth(width);
        setHeight(height);
        getStyleClass().add("path-window-background");

        outerFrame = createFramePath(this);
        outerFrame.getStyleClass().add("outer-path-frame");

        scene.setOnMouseMoved(me -> {
            // update segment listener (s0 s2, s2, none...)
            // when segment listener's invalidation occurs fire cursor to change.
            logLineSegment(me.getSceneX(), me.getSceneY());
            outerFrame.toBack();
//            System.out.println("scene mouse moved");
        });

        // reset cursor
        setOnMouseExited( mouseEvent -> {
            segmentSelected.set(-1);
            System.out.println("mouse exited group");
        });
        // createWindowButtons
        windowButtons = createWindowButtons(this);
        // drag window buttons.
        windowButtons.setOnMouseDragged( mouseEvent -> handleMouseDragged(mouseEvent));
        windowButtons.setOnMousePressed(mouseEvent -> handleMousePressed(mouseEvent));
        // createLeftAccent
        leftAccent = createLeftAccent(this);
        // createLeftTab
        leftTab = createLeftTab(this);
        // createMainTitleArea
        mainTitleArea = createMainTitleArea();
        // drag window buttons area. 
        mainTitleArea.setOnMouseDragged( mouseEvent -> handleMouseDragged(mouseEvent));
        mainTitleArea.setOnMousePressed(mouseEvent -> handleMousePressed(mouseEvent));
        // build bottom area
        mainContentBorderFrame = createMainContentViewArea();
        // IMPORTANT THIS IS THE CONTENT SET INTO THE main content pane (nestedPane)
        AnchorPane.setTopAnchor(contentPane, 15.0);
        AnchorPane.setLeftAnchor(contentPane, 15.0);
        AnchorPane.setRightAnchor(contentPane, 15.0);
        AnchorPane.setBottomAnchor(contentPane, 15.0);
        mainContentBorderFrame.getMainContentPane().getChildren().add(this.contentPane);
        //Disable interactions with the content while minimized.
        contentPane.mouseTransparentProperty().bind(minimizedProperty);
        
        getChildren().addAll(windowButtons, mainTitleArea,
            leftAccent, leftTab, outerFrame, mainContentBorderFrame);
        enterScene = createEnterAnimation(
                this.contentPane,
                windowButtons,
                mainTitleArea,
                leftAccent,
                leftTab,
                outerFrame,
                mainContentBorderFrame.getMainContentInnerPath());
        
        // starting initial anchor point
//        root.setOnMousePressed(mouseEvent -> {
//            int segment = resizeWindowTracker.currentSegmentIndex.get();
//            if (segment == -1) {
//                anchorPt = new Point2D(mouseEvent.getScreenX(),
//                        mouseEvent.getScreenY());
//            }
//            System.out.println("press root sees segment " + resizeWindowTracker.currentSegmentIndex.get());
//
//        });
        // Dragging the stage by moving its xTo,yTo
        // based on the previous location.
//        root.setOnMouseDragged(mouseEvent -> {
//            System.out.println("drag root sees segment " + resizeWindowTracker.currentSegmentIndex.get());
//            int segment = resizeWindowTracker.currentSegmentIndex.get();
//            if (segment == -1 && anchorPt != null && previousLocation != null) {
//                stage.setX(previousLocation.getX()
//                        + mouseEvent.getScreenX()
//                        - anchorPt.getX());
//                stage.setY(previousLocation.getY()
//                        + mouseEvent.getScreenY()
//                        - anchorPt.getY());
//            }
//        });

        // Set the new previous to the current mouse xTo,yTo coordinate
        setOnMouseReleased(mouseEvent -> {
            int segment = resizePaneTracker.currentSegmentIndex.get();
            //if (segment == -1) {
                previousLocation = new Point2D(getTranslateX(),getTranslateY());
            //}
            System.out.println("released");
        });
        //if the pane is minimized, double click to bring it back
        addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(minimizedProperty.get() && 
                e.getClickCount() > 1 && e.getButton() == MouseButton.PRIMARY) {
                restore();
            }
        });
        //if the pane is minimized, enable all components to handle mouse presses
        addEventHandler(MouseEvent.MOUSE_PRESSED, me -> {
            if(minimizedProperty.get()) {
                handleMousePressed(me);
                me.consume();
            }
        });
        //if the pane is minimized, enable all components to assist with dragging
        addEventHandler(MouseEvent.MOUSE_DRAGGED, me -> {
            if(minimizedProperty.get()) {
                handleMouseDragged(me);
                me.consume();
            }
        });
                
        // Initialize previousLocation after Stage is shown
        //@TODO SMP Replace Stage oriented Window events with custom versions
        this.scene.addEventHandler(CovalentPaneEvent.COVALENT_PANE_SHOWN,
            (CovalentPaneEvent t) -> {
                previousLocation = new Point2D(getTranslateX(),getTranslateY());
            });

        wireListeners();
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        if(isEnableDrag()) {
            System.out.println("drag root sees segment " + resizePaneTracker.currentSegmentIndex.get());
            int segment = resizePaneTracker.currentSegmentIndex.get();
            if (segment == -1 && anchorPt != null && previousLocation != null) {
                this.setTranslateX(previousLocation.getX()
                        + mouseEvent.getScreenX()
                        - anchorPt.getX());
                this.setTranslateY(previousLocation.getY()
                        + mouseEvent.getScreenY()
                        - anchorPt.getY());
            }        
        }
    }
    private void handleMousePressed(MouseEvent mouseEvent) {
        int segment = resizePaneTracker.currentSegmentIndex.get();
        if (segment == -1) {
            anchorPt = new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
        System.out.println("press root sees segment " + resizePaneTracker.currentSegmentIndex.get());        
    }

    private Animation createEnterBorderAnimation(Path borderFrame, double totalMS) {
        double totalLength = Utils.getTotalLength(borderFrame);
        borderFrame.getStrokeDashArray().add(totalLength);
        borderFrame.setStrokeDashOffset(totalLength);
        KeyValue strokeOffsetStart = new KeyValue(borderFrame.strokeDashOffsetProperty(), totalLength);
        KeyValue visible = new KeyValue(borderFrame.visibleProperty(), true);
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(1), strokeOffsetStart, visible);
        KeyValue strokeOffsetEnd = new KeyValue(borderFrame.strokeDashOffsetProperty(), 0);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(1000), handler -> {
            System.out.println("done.");
            borderFrame.getStrokeDashArray().clear();
        }, strokeOffsetEnd);

        Timeline anim = new Timeline(keyFrame1, keyFrame2);
        return anim;
    }
    private Animation createEnterAnimation(Pane pane,
                                        Pane windowButtons,
                                        Pane mainTitleArea,
                                        Node leftAccent,
                                        Node leftTab,
                                        Path outerBorderFrame,
                                        Path mainBorderFrame) {

        ParallelTransition borderParallelTransition = new ParallelTransition();
        Animation anim1 = createEnterBorderAnimation(outerBorderFrame, borderTimeMs);
        Animation anim2 = createEnterBorderAnimation(mainBorderFrame, borderTimeMs);
        borderParallelTransition.getChildren().addAll(anim1, anim2);

        ParallelTransition windowParallelTransition = new ParallelTransition();
        Animation anim3 = createFadeAnim(windowButtons, contentTimeMs);
        Animation anim4 = createFadeAnim(mainTitleArea, contentTimeMs);
        Animation anim5 = createFadeAnim(leftAccent, contentTimeMs);
        Animation anim6 = createFadeAnim(leftTab, contentTimeMs);
        windowParallelTransition.getChildren().addAll(anim3, anim4, anim5, anim6);
        
        Animation anim7 = createFadeAnim(pane, contentTimeMs); //createEnterRootAnim(1000, stage);
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition
                .getChildren()
                .addAll(
                    anim7,
                    borderParallelTransition,
                    windowParallelTransition);                    
//                    anim7); //show content last
//                    anim3, anim4, anim5, anim6);
        return sequentialTransition;
    }

//    // todo fix this is broke.
//    private Animation createEnterRootAnim(double millis, Stage stage) {
//        double width = stage.getScene().getWidth();
//        double height = stage.getScene().getHeight();
//        AnchorPane root = (AnchorPane) stage.getScene().getRoot();
//        KeyValue widthStart = new KeyValue(root.maxWidthProperty(), 0);
//        KeyValue heightStart = new KeyValue(root.maxHeightProperty(), 0);
//        KeyFrame start = new KeyFrame(Duration.millis(1), widthStart, heightStart );
//
//        KeyValue widthEnd = new KeyValue(root.maxWidthProperty(), width);
//        KeyValue heightEnd = new KeyValue(root.maxHeightProperty(), height);
//        KeyFrame end = new KeyFrame(Duration.millis(millis), widthEnd, heightEnd);
//
//        return new Timeline(start, end);
//    }

    private Animation createFadeAnim(Node view, double totalTimeMs) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(view);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setDuration(Duration.millis(totalTimeMs));
        return fadeTransition;
    }

    private Node createLeftAccent(AnchorPane root) {
        // create the accent shape left
        ShapedPath accentShape = ShapedPathBuilder.create(root)
                .addStyleClass("window-accent-shape")
                .moveTo(20, 9)
                .horzSeg(35)
                .vertSeg(9+15)
                .lineSeg(15,9+15+20)
                .vertSeg(9+15)
                .lineSeg(20,9+15)
                .closeSeg()
                .build();

        //root.getChildren().add(accentShape);
        return accentShape;
    }

    private Node createLeftTab(AnchorPane root) {
        ShapedPath leftTabShape = ShapedPathBuilder.create(root)
                .addStyleClass("left-tab-shape")
                .moveTo(5, 30+3)
                .lineSeg(xTo(13).yTo(9+15))
                .vertSeg(yTo(9+140-13))
                .lineSeg(xTo(5).yTo(9+140-13-8))
                .closeSeg()
                .build();

        //root.getChildren().add(leftTabShape);
        return leftTabShape;
    }
    private Pane createWindowButtons(AnchorPane root) {

        AnchorPane  windowHeader = new AnchorPane();
        windowHeader.getStyleClass().add("window-header");

        windowHeader.setPrefHeight(12);
        AnchorPane.setTopAnchor(windowHeader, 8.0);

        double leftAnchor = 10 + 10 + 15;
        AnchorPane.setLeftAnchor(windowHeader, leftAnchor);

        double rightAnchor = 15;
        AnchorPane.setRightAnchor(windowHeader, rightAnchor);

        // create buttons
        HBox buttonArea = new HBox(4);
        StyleableRectangle b1 = new StyleableRectangle(50, 20, Color.rgb(253,253,253, .8));
        b1.getStyleClass().add("window-header-minimize-button");
        b1.setOnMouseClicked(e -> {
            b1.getScene().getRoot().fireEvent(
                new CovalentPaneEvent(CovalentPaneEvent.COVALENT_PANE_MINIMIZE, this));
            this.minimize();
        });
        
        StyleableRectangle b2 = new StyleableRectangle(50, 20, Color.rgb(235,235,80, .8));
        b2.getStyleClass().add("window-header-maximize-button");
        b2.setOnMouseClicked(e ->{
            b2.getScene().getRoot().fireEvent(
                new CovalentPaneEvent(CovalentPaneEvent.COVALENT_PANE_MAXIMIZE, this));
            this.maximize();
        });

        StyleableRectangle b3 = new StyleableRectangle(50, 20, Color.rgb(250, 50, 50, .8));
        b3.getStyleClass().add("window-header-close-button");
        b3.setOnMouseClicked(e -> {
            b3.getScene().getRoot().fireEvent(
                new CovalentPaneEvent(CovalentPaneEvent.COVALENT_PANE_CLOSE, this));
            this.close();
        });
        
        buttonArea.getChildren().addAll(b1,b2, b3);

        AnchorPane.setTopAnchor(buttonArea, (double)(12-5f)/2);
        double buttonAreaRightAnchor = 5;
        AnchorPane.setRightAnchor(buttonArea, buttonAreaRightAnchor);

        windowHeader.getChildren().add(buttonArea);

        return windowHeader;
    }
    private Pane createMainTitleArea() {

        AnchorPane mainTitleView = new AnchorPane();
        mainTitleView.setPrefHeight(70);
        AnchorPane.setTopAnchor(mainTitleView, 30.0);

        double leftAnchor = 10 + 15;
        AnchorPane.setLeftAnchor(mainTitleView, leftAnchor);

        double rightAnchor = 15;
        AnchorPane.setRightAnchor(mainTitleView, rightAnchor);

        Path titlePath = new Path();
        titlePath.getStyleClass().add("main-title-path");
        MoveTo a0 = new MoveTo(10,5);
        LineTo a1 = new LineTo();
        a1.xProperty().bind(mainTitleView.widthProperty());
//        a1.yProperty().set(10);
        a1.yProperty().set(5);

        LineTo a2 = new LineTo();
        a2.xProperty().bind(mainTitleView.widthProperty());
        a2.yProperty().bind(mainTitleView.heightProperty().subtract(10 + 5));

//        LineTo a3 = new LineTo();
//        a3.xProperty().bind(mainTitleView.widthProperty().subtract(10));
//        a3.yProperty().bind(mainTitleView.heightProperty().subtract(10 + 10));

        LineTo a4 = new LineTo();
        a4.xProperty().set(10);
        a4.yProperty().bind(mainTitleView.heightProperty().subtract(10 + 5));

        LineTo a5 = new LineTo();
        a5.xProperty().set(0);
        a5.yProperty().bind(mainTitleView.heightProperty().subtract(7));

        LineTo a6 = new LineTo();
        a6.xProperty().set(0);
        a6.yProperty().set(15);

        ClosePath closePath = new ClosePath();
        titlePath.getElements()
                .addAll(a0, a1, a2, /*a3,*/ a4, a5, a6, closePath);


        AnchorPane nestedPane = new AnchorPane(); // has the clipped
        Path titlePathAsClip = new Path();
        titlePathAsClip.getElements().addAll(a0, a1, a2, /*a3,*/ a4, a5, a6, closePath);
        titlePathAsClip.setFill(Color.WHITE);
        nestedPane.setClip(titlePathAsClip);

        Text text1 = new Text(mainTitleTextProperty.get() + " ");
        text1.textProperty().bind(mainTitleTextProperty);
        text1.setFill(Color.WHITE);
        text1.getStyleClass().add("main-title-text");

        Text text2 = new Text(mainTitleText2Property.get());
        text2.textProperty().bind(mainTitleText2Property);
        text2.setFill(Color.WHITE);
        text2.getStyleClass().add("main-title-text2");

        TextFlow textFlow = new TextFlow(text1, text2);
        AnchorPane.setLeftAnchor(textFlow, 20.0);
        AnchorPane.setTopAnchor(textFlow, 5.0);
        nestedPane.getChildren().add(textFlow);

        mainTitleView.getChildren().addAll(titlePath, nestedPane);

        //root.getChildren().add(mainTitleView);
        return mainTitleView;

    }

    private MainContentViewArea createMainContentViewArea() {
        // Create a main content region
        // 1) create pane (transparent style)
        // 2) create path outline
        // 3) use path outline as clip region
        MainContentViewArea mainContentView = new MainContentViewArea();
        mainContentView.getStyleClass().add("main-content-view");
        AnchorPane.setTopAnchor(mainContentView, 90.0);
        double leftAnchor = 10 + 15;
        AnchorPane.setLeftAnchor(mainContentView, leftAnchor);
        double rightAnchor = 15;
        AnchorPane.setRightAnchor(mainContentView, rightAnchor);
        AnchorPane.setBottomAnchor(mainContentView, 10 + 15.0);
        //root.getChildren().add(mainContentView);

        ShapedPath mainContentInnerPath = ShapedPathBuilder.create(mainContentView)
                .addStyleClass("main-content-inner-path")
                .moveTo(10, 0)
                .horzSeg(bindXToWidth(-10))  // 0
                .lineSeg(bindXToWidth().yTo(10))   // 1
                .vertSeg(bindYToHeight(-50)) // 2
                .lineSeg(bindXToWidth(-10).bindYToHeight(- (50 - 10))) //3
                .horzSeg(bindXToWidth(-(190-10))) // 4
                .lineSeg(bindXToPrevX(-10).bindYToHeight(-(50-10-10))) //5
                .vertSeg(bindXToPrevX(0).bindYToHeight(-10)) // 6
                .lineSeg(bindXToPrevX(-10).bindYToHeight()) // 7
                .horzSeg(xTo(10))
                .lineSeg(xTo(0).bindYToHeight(-10))
                .vertSeg(10)
                .closeSeg()
                .build();

        // THIS IS WHERE THE USER CONTENT IS PLACED INTO!!!!
        AnchorPane nestedPane = new AnchorPane(); // has the clipped
        nestedPane.getStyleClass().add("main-content-pane");
        // clone path for clip region
        Path mainContentInnerPathAsClip = new Path();
        mainContentInnerPathAsClip.getElements().addAll(mainContentInnerPath.getElements());
        mainContentInnerPathAsClip.setFill(Color.WHITE);

        // punch out the path for the main content
        nestedPane.setClip(mainContentInnerPathAsClip);

        // set the nested pane for the caller to put stuff inside.
        mainContentView.setMainContentPane(nestedPane);
        //Add the nestedPane containging user content last so it has mouse priority
        mainContentView.getChildren().addAll(mainContentInnerPath, nestedPane);

        mainContentView.setMainContentInnerPath(mainContentInnerPath);

        return mainContentView;
    }
    
    private Animation createMinimizeAnim(double totalTimeMs) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(totalTimeMs), this);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.10);
        scaleTransition.setToY(0.10);
        return scaleTransition;
    }
    private Animation createRestoreAnim(double totalTimeMs) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(totalTimeMs), this);
        scaleTransition.setFromX(0.1);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        return scaleTransition;
    }    
    public void minimize() {
        System.out.println("Minimize called on Pane: " + this.toString());
        Animation minimizeAnimation = createMinimizeAnim(contentTimeMs);
        minimizedProperty.set(true);
        minimizeAnimation.play();
    }
    public void restore() {
        System.out.println("Restoring Pane: " + this.toString());
        Animation restoreAnimation = createRestoreAnim(contentTimeMs);
        minimizedProperty.set(false);
        restoreAnimation.play();
    }
    public void maximize() {
        System.out.println("Maximize called on Pane: " + this.toString());
        
    }
    public void close() {
        System.out.println("Close called on Pane: " + this.toString());
    }
    public void show() {
        enterScene.play();
    }

    private RESIZE_DIRECTION getCurrentResizeDirection() {
        int segmentIndex = segmentSelected.get();
        if (segmentIndex == -1) return NONE;
        return cursorSegmentArray[segmentIndex];
    }

    private void wireListeners() {
        resizePaneTracker = new ResizePaneTracker(contentPane);

        resizePaneTracker.setOnMousePressed((mouseEvent, wt) -> {
            // store anchor x,y of the stage
            wt.anchorStageXYCoordValue.set(new Point2D(getTranslateX(), getTranslateY()));

            // TODO Revisit code b/c this might be doing the same thing as line above.
            wt.paneXCoordValue.set(getTranslateX());
            wt.paneYCoordValue.set(getTranslateY());

            // anchor of the mouse screen x,y position.
            wt.anchorCoordValue.set(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));

            // current width and height
            wt.anchorWidthSizeValue.set(contentPane.getWidth());
            wt.anchorHeightSizeValue.set(contentPane.getHeight());
            System.out.println("press mouseX = " + mouseEvent.getX() + " translateX = " + getTranslateX());

            // current resize direction
            wt.currentResizeDirection.set(getCurrentResizeDirection());

            // current line segment
            wt.currentSegmentIndex.set(segmentSelected.get());
        });

        resizePaneTracker.setOnMouseDragged((mouseEvent, wt) -> {

//        resizePaneTracker.setOnMouseDragged((mouseEvent, wt) -> {
            RESIZE_DIRECTION direction = wt.currentResizeDirection.get();

            switch (direction) {
                case NW:
                    // TODO Northwest or Upper Left accuracy
                    resizeNorth(mouseEvent, wt);
                    resizeWest(mouseEvent, wt);
                    break;
                case N:
                    resizeNorth(mouseEvent, wt);
                    break;
                case NE:
                    //TODO Northeast Upper right corner accuracy
                    resizeNorth(mouseEvent, wt);
                    resizeEast(mouseEvent, wt);
                    break;
                case E:
                    resizeEast(mouseEvent, wt);
                    break;
                case SE:
                    resizeSouth(mouseEvent, wt);
                    resizeEast(mouseEvent, wt);
                    break;
                case S:
                    resizeSouth(mouseEvent, wt);
                    break;
                case SW:
                    resizeSouth(mouseEvent, wt);
                    resizeWest(mouseEvent, wt);
                    break;
                case W:
                    // TODO update offset West left side accuracy
                    resizeWest(mouseEvent, wt);
                    break;
                default:
                    break;
            }

        });

        // Mouse cursor touching segments
        segmentSelected.addListener( (ob, oldv, newv) -> {
            int index = newv.intValue();
            if (index > -1) {
                RESIZE_DIRECTION direction = cursorSegmentArray[index];
                cursorProperty().set(cursorMap.get(direction));
            } else {
                cursorProperty().set(Cursor.DEFAULT);
            }
        });

        // populate the lines for the outerframe.
//        //@TODO SMP Replace with custom event
//        scene.getRoot().addEventHandler(CovalentPaneEvent.COVALENT_PANE_SHOWN, e -> {
//            System.out.println("generateLineMap");
//            generateLineMap(outerFrame.getElements());
//        });
        generateLineMap(outerFrame.getElements());
    }

    private void resizeNorth(MouseEvent mouseEvent, ResizePaneTracker wt) {
        double screenY = mouseEvent.getScreenY();
        double distance = wt.anchorStageXYCoordValue.get().getY() - screenY;

        wt.paneYCoordValue.set(wt.anchorStageXYCoordValue.get().getY() - distance);
        double newHeight = wt.anchorHeightSizeValue.get() + distance;
        wt.resizeHeightValue.set(newHeight);
    }

    private void resizeSouth(MouseEvent mouseEvent, ResizePaneTracker wt) {
        double y = mouseEvent.getScreenY();
        double newHeight = wt.anchorHeightSizeValue.get() + y - wt.anchorCoordValue.get().getY();
        wt.resizeHeightValue.set(newHeight);
        //System.out.println("newHeight " + newHeight + " dragging      xTo " + yTo + ", length " + (yTo - wt.anchorCoordValue.get().getY()));
    }

    private void resizeEast(MouseEvent mouseEvent, ResizePaneTracker wt) {
        double x = mouseEvent.getScreenX();
        double newWidth = wt.anchorWidthSizeValue.get() + x - wt.anchorCoordValue.get().getX();
        wt.resizeWidthValue.set(newWidth);
        //System.out.println("newWidth " + newWidth + " dragging      xTo " + xTo + ", length " + (xTo - wt.anchorCoordValue.get().getX()));
    }

    private void resizeWest(MouseEvent mouseEvent, ResizePaneTracker wt) {
        double screenX = mouseEvent.getScreenX();
        double offset = wt.currentSegmentIndex.intValue() == 8 ? 10 : 0; // TODO magic numbers fix.
        System.out.println("offset for segment " + offset);
        double distance = wt.anchorStageXYCoordValue.get().getX() - screenX + offset; // offset left side segment 8 (10 pixels)
        wt.paneXCoordValue.set(wt.anchorStageXYCoordValue.get().getX() - distance);

        double newWidth = wt.anchorWidthSizeValue.get() + distance;
        wt.resizeWidthValue.set(newWidth);
    }

    private Map.Entry<Integer, Line> logLineSegment(double targetX, double targetY) {
        Set<Map.Entry<Integer, Line>> entries = lineSegmentMap.entrySet();
        Optional<Map.Entry<Integer, Line>> entry = entries.stream()
                .filter(e -> Utils.isPointNearLine(targetX, targetY, e.getValue(), 5, e.getKey()))
                .findAny();

        entry.ifPresentOrElse(a -> segmentSelected.set(a.getKey()),
                () -> segmentSelected.set(-1)
        );

        return entry.isPresent() ? entry.get() : null;
    }

    private void generateLineMap(List<PathElement> framePath) {
        PathElement prev = null;
        int cnt = 0;

        for(PathElement pe:framePath) {
            if (prev == null) {
                prev = pe;
                continue;
            }
            if (pe instanceof ClosePath) {
                continue;
            }
            Line segment = createLine(prev, pe);

            //segment.startXProperty().bind(((MoveTo)prev).;
            lineSegmentMap.put(cnt, segment);

            Line line = segment;
            double sX = line.startXProperty().get();
            double sY = line.startYProperty().get();
            double eX = line.endXProperty().get();
            double eY = line.endYProperty().get();
//            System.out.print("segment name " + cnt + " line x1,y1 to x2,y2 ");
//            System.out.printf(" (%s, %s) (%s, %s) \n", sX, sY, eX, eY);
            prev = pe;
            cnt++;
        }
        Line segment = createLine(prev, framePath.get(0));
        lineSegmentMap.put(cnt, segment);
    }
    private <T> T getPathElementAs(Class<?> clazz, PathElement pathElement) {
        return (T) clazz.cast(pathElement);
    }

    private Line createLine(PathElement p1, PathElement p2) {
        Line line = null;
        if (p1 instanceof MoveTo) {
            MoveTo moveTo = getPathElementAs(MoveTo.class, p1);
            LineTo lineTo = getPathElementAs(LineTo.class, p2);
            line = new Line();

            line.startXProperty().bind(moveTo.xProperty());
            line.startYProperty().bind(moveTo.yProperty());
            line.endXProperty().bind(lineTo.xProperty());
            line.endYProperty().bind(lineTo.yProperty());
        } else if (p1 instanceof LineTo && p2 instanceof LineTo) {
            LineTo p1lineTo = getPathElementAs(LineTo.class, p1);
            LineTo p2lineTo = getPathElementAs(LineTo.class, p2);
            line = new Line();

            line.startXProperty().bind(p1lineTo.xProperty());
            line.startYProperty().bind(p1lineTo.yProperty());
            line.endXProperty().bind(p2lineTo.xProperty());
            line.endYProperty().bind(p2lineTo.yProperty());

        } else if (p1 instanceof LineTo && p2 instanceof MoveTo) {
            LineTo lineTo = getPathElementAs(LineTo.class, p1);
            MoveTo moveTo = getPathElementAs(MoveTo.class, p2);

            line = new Line();

            line.startXProperty().bind(lineTo.xProperty());
            line.startYProperty().bind(lineTo.yProperty());
            line.endXProperty().bind(moveTo.xProperty());
            line.endYProperty().bind(moveTo.yProperty());
        }
        return line;
    }

    private Path createFramePath(Pane pane) {
        // draw
        ShapedPath newFrame = ShapedPathBuilder.create(pane)
                .moveTo(20, 0)
                .horzSeg(bindXToWidth(-10)) // 0
                .lineSeg(bindXToWidth().yTo(10))  // 1
                .vertSeg(bindYToHeight(-10)) // 2
                .lineSeg(bindXToWidth(-10).bindYToHeight()) //3
                .horzSeg(bindXToWidth(-200)) // 4
                .lineSeg(bindXToWidth(-210).bindYToHeight(-10)) // 5
                .horzSeg(xTo(20).bindYToHeight(-10)) // 6
                .lineSeg(xTo(10).bindYToHeight(-20))  //7
                .vertSeg(140) //8
                .lineSeg(0,130) //9
                .vertSeg(30) //10
                .lineSeg(10, 20) // 11
                .vertSeg(10)
                .closeSeg()
                .build();

        return newFrame;
    }

    /**
     * @return the enableDrag
     */
    public boolean isEnableDrag() {
        return enableDrag;
    }

    /**
     * @param enableDrag the enableDrag to set
     */
    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }
}

interface PaneMousePressed {
    void pressed(MouseEvent mouseEvent, ResizePaneTracker paneTracker);
}
interface PaneMouseDragged {
    void dragged(MouseEvent mouseEvent, ResizePaneTracker paneTracker);
}

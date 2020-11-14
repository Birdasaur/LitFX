package lit.litfx.controls.covalent;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lit.litfx.controls.covalent.CursorMappings.RESIZE_DIRECTION;

import java.util.*;

import static lit.litfx.controls.covalent.BindablePointBuilder.*;
import static lit.litfx.controls.covalent.CursorMappings.RESIZE_DIRECTION.NONE;
import static lit.litfx.controls.covalent.CursorMappings.cursorMap;

/*
 notes: https://stackoverflow.com/questions/47213156/how-to-get-length-of-path
  */
public class PathWindow {

    private Stage stage;
    private ResizeWindowTracker resizeWindowTracker;

    private Map<Integer, Line> lineSegmentMap = new HashMap<>();

//    private static Map<RESIZE_DIRECTION, Cursor> cursorMap = new HashMap<>();
//    static {
//        cursorMap.put(RESIZE_DIRECTION.NW, Cursor.NW_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.N, Cursor.N_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.NE, Cursor.NE_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.E, Cursor.E_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.SE, Cursor.SE_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.S, Cursor.S_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.SW, Cursor.SW_RESIZE);
//        cursorMap.put(RESIZE_DIRECTION.W, Cursor.W_RESIZE);
//    }
    private RESIZE_DIRECTION[] cursorSegmentArray = new RESIZE_DIRECTION[14];
    private IntegerProperty segmentSelected = new SimpleIntegerProperty(-1);
//    static {
//        cursorSegmentArray[0] = RESIZE_DIRECTION.N;
//        cursorSegmentArray[1] = RESIZE_DIRECTION.NE;
//        cursorSegmentArray[2] = RESIZE_DIRECTION.E;
//        cursorSegmentArray[3] = RESIZE_DIRECTION.SE;
//        cursorSegmentArray[4] = RESIZE_DIRECTION.S;
//        cursorSegmentArray[5] = RESIZE_DIRECTION.S;
//        cursorSegmentArray[6] = S;
//        cursorSegmentArray[7] = SW;
//        cursorSegmentArray[8] = W;
//        cursorSegmentArray[9] = W;
//        cursorSegmentArray[10] = W;
//        cursorSegmentArray[11] = NW;
//        cursorSegmentArray[12] = NW;
//        cursorSegmentArray[13] = NW;
//    }

    private Path outerFrame = null; //new Path();


    private Point2D anchorPt;
    private Point2D previousLocation;

    public PathWindow(Stage stage) {
        this.stage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);

        AnchorPane root = new AnchorPane();
        root.getStyleClass().add("path-window-background");

        final Scene scene = new Scene(root,
                640, 480,
                Color.rgb(0,0,0,0));

        scene.getStylesheets().add(PathWindow.class.getResource("main.css").toExternalForm());

        outerFrame = createFramePath(stage);
        outerFrame.getStyleClass().add("outer-path-frame");

        scene.setOnMouseMoved(me -> {
            // update segment listener (s0 s2, s2, none...)
            // when segment listener's invalidation occurs fire cursor to change.
            logLineSegment(me.getSceneX(), me.getSceneY());
            outerFrame.toBack();
        });

        // reset cursor
        scene.setOnMouseExited( mouseEvent -> {
            segmentSelected.set(-1);
            System.out.println("mouse exited scene");
        });

        root.getChildren().add(outerFrame);

        // createWindowButtons
        Pane windowButtons = createWindowButtons(root);

        // drag window buttons area.
        windowButtons.setOnMouseDragged( mouseEvent -> {
            System.out.println("drag root sees segment " + resizeWindowTracker.currentSegmentIndex.get());
            int segment = resizeWindowTracker.currentSegmentIndex.get();
            if (segment == -1 && anchorPt != null && previousLocation != null) {
                stage.setX(previousLocation.getX()
                        + mouseEvent.getScreenX()
                        - anchorPt.getX());
                stage.setY(previousLocation.getY()
                        + mouseEvent.getScreenY()
                        - anchorPt.getY());
            }
        });
        windowButtons.setOnMousePressed(mouseEvent -> {
            int segment = resizeWindowTracker.currentSegmentIndex.get();
            if (segment == -1) {
                anchorPt = new Point2D(mouseEvent.getScreenX(),
                        mouseEvent.getScreenY());
            }
            System.out.println("press root sees segment " + resizeWindowTracker.currentSegmentIndex.get());

        });
        // createLeftAccent
        Node leftAccent = createLeftAccent(root);

        // createLeftTab
        Node leftTab = createLeftTab(root);

        // createMainTitleArea
        Pane mainTitleArea = createMainTitleArea(root);

        // build bottom area
        Path mainContentBorderFrame = createMainContentViewArea(root);


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
        root.setOnMouseReleased(mouseEvent -> {
            int segment = resizeWindowTracker.currentSegmentIndex.get();
            //if (segment == -1) {
                previousLocation = new Point2D(stage.getX(),
                        stage.getY());
            //}
            System.out.println("released");
        });

        // Initialize previousLocation after Stage is shown
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN,
                (WindowEvent t) -> {
                    previousLocation = new Point2D(stage.getX(),
                            stage.getY());
                });

        stage.setScene(scene);

        wireListeners();

        Stage control = new Stage();
        Text animButton = new Text("animate");
        animButton.setOnMouseClicked( actionEvent -> {
            Animation enterScene = createEnterAnimation(
                    stage,
                    windowButtons,
                    mainTitleArea,
                    leftAccent,
                    leftTab,
                    outerFrame,
                    mainContentBorderFrame);
            enterScene.play();
        });
        Text popPane = new Text("Pop Pane");
//        popPane.setOnMouseClicked( actionEvent -> {
//            Animation enterScene = createEnterAnimation(
//                    stage,
//                    windowButtons,
//                    mainTitleArea,
//                    leftAccent,
//                    leftTab,
//                    outerFrame,
//                    mainContentBorderFrame);
//            enterScene.play();
//        });
        Text closeAll = new Text("Close Panes");
        HBox hbox = new HBox(5, animButton, popPane, closeAll);

        control.setScene(new Scene(new StackPane(hbox), 200, 100 ));
        control.show();
    }


    private Animation createEnterBorderAnimation(Path borderFrame) {
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
    private Animation createEnterAnimation(Stage stage,
                                        Pane windowButtons,
                                        Pane mainTitleArea,
                                        Node leftAccent,
                                        Node leftTab,
                                        Path outerBorderFrame,
                                        Path mainBorderFrame) {

        SequentialTransition sequentialTransition = new SequentialTransition();

        Animation anim0 = createFadeAnim(stage.getScene().getRoot()); //createEnterRootAnim(1000, stage);
        ParallelTransition parallelTransition = new ParallelTransition();
        Animation anim1 = createEnterBorderAnimation(outerBorderFrame);
        Animation anim2 = createEnterBorderAnimation(mainBorderFrame);
        Animation anim3 = createFadeAnim(windowButtons);
        Animation anim4 = createFadeAnim(mainTitleArea);
        Animation anim5 = createFadeAnim(leftAccent);
        Animation anim6 = createFadeAnim(leftTab);
        parallelTransition.getChildren().addAll(anim1, anim2);

        sequentialTransition
                .getChildren()
                .addAll(anim0,
                    parallelTransition,
                    anim3,
                    anim4,
                    anim5,
                    anim6);

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

    private Animation createFadeAnim(Node view) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(view);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        return fadeTransition;
    }


    private Node createLeftAccent(AnchorPane root) {
        // create the accent shape left
        ShapedPath accentShape = ShapedPathBuilder.create(this.stage)
                .addStyleClass("window-accent-shape")
                .moveTo(20, 9)
                .horzSeg(35)
                .vertSeg(9+15)
                .lineSeg(15,9+15+20)
                .vertSeg(9+15)
                .lineSeg(20,9+15)
                .closeSeg()
                .build();

        root.getChildren().add(accentShape);
        return accentShape;
    }

    private Node createLeftTab(AnchorPane root) {
        ShapedPath leftTabShape = ShapedPathBuilder.create(this.stage)
                .addStyleClass("left-tab-shape")
                .moveTo(5, 30+3)
                .lineSeg(xTo(13).yTo(9+15))
                .vertSeg(yTo(9+140-13))
                .lineSeg(xTo(5).yTo(9+140-13-8))
                .closeSeg()
                .build();

        root.getChildren().add(leftTabShape);
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
        Rectangle b1 = new Rectangle();
        b1.setWidth(50);
        b1.setHeight(5);
        b1.setFill(Color.rgb(253,253,253, .8));

        Rectangle b2 = new Rectangle();
        b2.setWidth(50);
        b2.setHeight(5);
        b2.setFill(Color.rgb(235,235,80, .8));

        Rectangle b3 = new Rectangle();
        b3.setWidth(50);
        b3.setHeight(5);
        b3.setFill(Color.rgb(250,166,62, .8));

        buttonArea.getChildren().addAll(b1,b2, b3);

        AnchorPane.setTopAnchor(buttonArea, (double)(12-5f)/2);
        double buttonAreaRightAnchor = 5;
        AnchorPane.setRightAnchor(buttonArea, buttonAreaRightAnchor);

        windowHeader.getChildren().add(buttonArea);

        root.getChildren().add(windowHeader);
        return windowHeader;
    }
    private Pane createMainTitleArea(AnchorPane root) {

        AnchorPane mainTitleView = new AnchorPane();
        mainTitleView.setPrefHeight(70);
        //mainTitleView.getStyleClass().add("main-title-path");

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
        a1.yProperty().set(10);

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

        /*
        ext text1 = new Text("Big italic red text");
     text1.setFill(Color.RED);
     text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));
     Text text2 = new Text(" little bold blue text");
     text2.setFill(Color.BLUE);
     text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
     TextFlow textFlow = new TextFlow(text1, text2);

         */


        Text text1 = new Text("AIRCRAFT ");
        text1.setFill(Color.WHITE);
        text1.getStyleClass().add("main-title-text");

        Text text2 = new Text("GLOBAL POSITION");
        text2.setFill(Color.WHITE);
        text2.getStyleClass().add("main-title-text2");


        TextFlow textFlow = new TextFlow(text1, text2);
        AnchorPane.setLeftAnchor(textFlow, 20.0);
        AnchorPane.setTopAnchor(textFlow, 5.0);
        nestedPane.getChildren().add(textFlow);

        mainTitleView.getChildren().addAll(titlePath, nestedPane);

        root.getChildren().add(mainTitleView);
        return mainTitleView;

    }

    private Path createMainContentViewArea(AnchorPane root) {
        // Create a main content region
        // 1) create pane (transparent style)
        // 2) create path outline
        // 3) use path outline as clip region
        AnchorPane mainContentView = new AnchorPane();
        mainContentView.getStyleClass().add("main-content-view");
        AnchorPane.setTopAnchor(mainContentView, 90.0);
        double leftAnchor = 10 + 15;
        AnchorPane.setLeftAnchor(mainContentView, leftAnchor);
        double rightAnchor = 15;
        AnchorPane.setRightAnchor(mainContentView, rightAnchor);
        AnchorPane.setBottomAnchor(mainContentView, 10 + 15.0);
        root.getChildren().add(mainContentView);

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

        AnchorPane nestedPane = new AnchorPane(); // has the clipped

        Path mainContentInnerPathAsClip = new Path();
        mainContentInnerPathAsClip.getElements().addAll(mainContentInnerPath.getElements());
        mainContentInnerPathAsClip.setFill(Color.WHITE);
        nestedPane.setClip(mainContentInnerPathAsClip);

        mainContentView.getChildren().addAll(nestedPane, mainContentInnerPath);

        return mainContentInnerPath;
    }

    public void show() {
        stage.show();
    }

    private RESIZE_DIRECTION getCurrentResizeDirection() {
        int segmentIndex = segmentSelected.get();
        if (segmentIndex == -1) return NONE;
        return cursorSegmentArray[segmentIndex];
    }

    private void wireListeners() {
        Scene scene = stage.getScene();
        resizeWindowTracker = new ResizeWindowTracker(stage);

        resizeWindowTracker.setOnMousePressed((mouseEvent, wt) -> {
            // store anchor x,y of the stage
            wt.anchorStageXYCoordValue.set(new Point2D(stage.getX() , stage.getY()));

            // TODO Revisit code b/c this might be doing the same thing as line above.
            wt.stageXCoordValue.set(stage.getX());
            wt.stageYCoordValue.set(stage.getY());

            // anchor of the mouse screen x,y position.
            wt.anchorCoordValue.set(new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));

            // current width and height
            wt.anchorWidthSizeValue.set(stage.getWidth());
            wt.anchorHeightSizeValue.set(stage.getHeight());
            System.out.println("press mouseX = " + mouseEvent.getX() + " stageX = " + stage.getX());

            // current resize direction
            wt.currentResizeDirection.set(getCurrentResizeDirection());

            // current line segment
            wt.currentSegmentIndex.set(segmentSelected.get());
        });

        resizeWindowTracker.setOnMouseDragged((mouseEvent, wt) -> {
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
                scene.cursorProperty().set(cursorMap.get(direction));
            } else {
                scene.cursorProperty().set(Cursor.DEFAULT);
            }
        });

        // populate the lines for the outerframe.
        stage.onShownProperty().set(windowEvent -> {
            generateLineMap(outerFrame.getElements());

//            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
//
//            BufferedImage screencapture = null;
//            try {
//
//                screencapture = new Robot().createScreenCapture(
//                        new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
//                );
//            } catch (AWTException e) {
//                e.printStackTrace();
//            }
//
//            // Save as JPEG
//            File file = new File("screencapture_test"  + ".jpg");
//                    try {
//                        ImageIO.write(screencapture, "jpg", file);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }
        );
    }


    private void resizeNorth(MouseEvent mouseEvent, ResizeWindowTracker wt) {
        double screenY = mouseEvent.getScreenY();
        double distance = wt.anchorStageXYCoordValue.get().getY() - screenY;

        wt.stageYCoordValue.set(wt.anchorStageXYCoordValue.get().getY() - distance);
        double newHeight = wt.anchorHeightSizeValue.get() + distance;
        wt.resizeHeightValue.set(newHeight);
    }

    private void resizeSouth(MouseEvent mouseEvent, ResizeWindowTracker wt) {
        double y = mouseEvent.getScreenY();
        double newHeight = wt.anchorHeightSizeValue.get() + y - wt.anchorCoordValue.get().getY();
        wt.resizeHeightValue.set(newHeight);
        //System.out.println("newHeight " + newHeight + " dragging      xTo " + yTo + ", length " + (yTo - wt.anchorCoordValue.get().getY()));
    }

    private void resizeEast(MouseEvent mouseEvent, ResizeWindowTracker wt) {
        double x = mouseEvent.getScreenX();
        double newWidth = wt.anchorWidthSizeValue.get() + x - wt.anchorCoordValue.get().getX();
        wt.resizeWidthValue.set(newWidth);
        //System.out.println("newWidth " + newWidth + " dragging      xTo " + xTo + ", length " + (xTo - wt.anchorCoordValue.get().getX()));
    }

    private void resizeWest(MouseEvent mouseEvent, ResizeWindowTracker wt) {
        double screenX = mouseEvent.getScreenX();
        double offset = wt.currentSegmentIndex.intValue() == 8 ? 10 : 0; // TODO magic numbers fix.
        System.out.println("offset for segment " + offset);
        double distance = wt.anchorStageXYCoordValue.get().getX() - screenX + offset; // offset left side segment 8 (10 pixels)
        wt.stageXCoordValue.set(wt.anchorStageXYCoordValue.get().getX() - distance);

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

    private Path createFramePath(Stage stage) {
        // draw
        ShapedPath outerFrame = ShapedPathBuilder.create(stage)
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

        // Assign mouse cursor direction for each segment.
        cursorSegmentArray[0] = RESIZE_DIRECTION.N;
        cursorSegmentArray[1] = RESIZE_DIRECTION.NE;
        cursorSegmentArray[2] = RESIZE_DIRECTION.E;
        cursorSegmentArray[3] = RESIZE_DIRECTION.SE;
        cursorSegmentArray[4] = RESIZE_DIRECTION.S;
        cursorSegmentArray[5] = RESIZE_DIRECTION.S;
        cursorSegmentArray[6] = RESIZE_DIRECTION.S;
        cursorSegmentArray[7] = RESIZE_DIRECTION.SW;
        cursorSegmentArray[8] = RESIZE_DIRECTION.W;
        cursorSegmentArray[9] = RESIZE_DIRECTION.W;
        cursorSegmentArray[10] = RESIZE_DIRECTION.W;
        cursorSegmentArray[11] = RESIZE_DIRECTION.NW;
        cursorSegmentArray[12] = RESIZE_DIRECTION.NW;
        cursorSegmentArray[13] = RESIZE_DIRECTION.NW;
        return outerFrame;
    }
}

interface MousePressed {
    void pressed(MouseEvent mouseEvent, ResizeWindowTracker windowTracker);
}
interface MouseDragged {
    void dragged(MouseEvent mouseEvent, ResizeWindowTracker windowTracker);
}

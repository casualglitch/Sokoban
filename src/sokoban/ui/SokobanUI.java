package sokoban.ui;

import application.Main;
import application.Main.SokobanPropertyType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;
import application.Main.SokobanPropertyType;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.AnimationTimer;
import properties_manager.PropertiesManager;
import java.lang.Thread;
import java.util.Stack;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.sound.sampled.AudioSystem;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicTableUI.KeyHandler;

public class SokobanUI extends Pane {

    /**
     * The SokobanUIState represents the four screen states that are possible
     * for the Sokoban game application. Depending on which state is in current
     * use, different controls will be visible.
     */
    public enum SokobanUIState {

        SPLASH_SCREEN_STATE, PLAY_GAME_STATE, VIEW_STATS_STATE, VIEW_HELP_STATE,
        HANG1_STATE, HANG2_STATE, HANG3_STATE, HANG4_STATE, HANG5_STATE, HANG6_STATE,
    }

    // mainStage
    private Stage primaryStage;

    // mainPane
    private BorderPane mainPane;
    private BorderPane hmPane;

    // SplashScreen
    private ImageView splashScreenImageView;
    private StackPane splashScreenPane;
    private Label splashScreenImageLabel;
    private HBox levelSelectionPane;
    private ArrayList<Button> levelButtons;
    private Media audio;

    // NorthToolBar
    private HBox northToolbar;
    private Button backButton;
    private Button statsButton;
    private Button undoButton;
    private Button timeButton;
    private Label time = new Label();
    private Integer startTime = 0;

    // GamePane
    private Label SokobanLabel;
    private Button newGameButton;
    private HBox letterButtonsPane;
    private HashMap<Character, Button> letterButtons;
    private BorderPane gamePanel = new BorderPane();
    private GraphicsContext gc;
    private GridRenderer gr;
    Stack<int[][]> undo = new Stack<int[][]>();

    // images
    final Image wallImage = new Image("file:images/wall.png");
    final Image boxImage = new Image("file:images/box.png");
    final Image placeImage = new Image("file:images/place.png");
    final Image sokobanImage = new Image("file:images/Sokoban.png");

    //StatsPane
    private ScrollPane statsScrollPane;
    private JEditorPane statsPane;

    //HelpPane
    private BorderPane helpPanel;
    private JScrollPane helpScrollPane;
    private JEditorPane helpPane;
    private Button homeButton;
    private Pane workspace;

    // Padding
    private Insets marginlessInsets;

    // Image path
    private String ImgPath = "file:images/";

    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private SokobanEventHandler eventHandler;
    private SokobanErrorHandler errorHandler;
    private SokobanDocumentManager docManager;

    SokobanGameStateManager gsm;

    public SokobanUI() {
        gsm = new SokobanGameStateManager(this);
        eventHandler = new SokobanEventHandler(this);
        errorHandler = new SokobanErrorHandler(primaryStage);
        docManager = new SokobanDocumentManager(this);
        initMainPane();
        initSplashScreen();
    }

    public void SetStage(Stage stage) {
        primaryStage = stage;
    }

    public BorderPane GetMainPane() {
        return this.mainPane;
    }

    public SokobanGameStateManager getGSM() {
        return gsm;
    }

    public SokobanDocumentManager getDocManager() {
        return docManager;
    }

    public SokobanErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public JEditorPane getHelpPane() {
        return helpPane;
    }

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneWidth, paneHeigth);
        mainPane.setPadding(marginlessInsets);
    }

    public void initSplashScreen() {
        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props
                .getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        props.addProperty(SokobanPropertyType.INSETS, "5");
        String str = props.getProperty(SokobanPropertyType.INSETS);

        //splashScreenPane = new FlowPane();
        splashScreenPane = new StackPane();

        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);

        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        // move the label position to fix the pane
        splashScreenImageLabel.setLayoutX(-45);
        splashScreenPane.getChildren().add(splashScreenImageLabel);

        // GET THE LIST OF LEVEL OPTIONS
        ArrayList<String> levels = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelImages = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_IMAGE_NAMES);
        //ArrayList<String> levelFiles = props
        //        .getPropertyOptionsList(SokobanPropertyType.LEVEL_FILES);

        //levelSelectionPane = new HBox();
        //levelSelectionPane.setSpacing(10.0);
        //levelSelectionPane.setAlignment(Pos.CENTER);
        GridPane grid = new GridPane();
        grid.setHgap(3.0);
        grid.setAlignment(Pos.CENTER);
        // add key listener
        levelButtons = new ArrayList<Button>();
        for (int i = 0; i < levels.size(); i++) {

            // GET THE LIST OF LEVEL OPTIONS
            String level = levels.get(i);
            String levelImageName = levelImages.get(i);
            Image levelImage = loadImage(levelImageName);
            ImageView levelImageView = new ImageView(levelImage);

            // AND BUILD THE BUTTON
            Button levelButton = new Button();
            levelButton.setGraphic(levelImageView);

            // CONNECT THE BUTTON TO THE EVENT HANDLER
            levelButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    // TODO
                    //eventHandler.respondToSelectLevelRequest(level);
                    eventHandler.respondToNewGameRequest(level);
                }
            });
            Text label = new Text("LEVEL " + (i + 1));
            label.setFont(Font.font("Tahoma", FontWeight.BOLD, 39));
            // TODO
            //levelSelectionPane.getChildren().add(levelButton);
            // TODO: enable only the first level
            //levelButton.setDisable(true);
            if (i >= 5) {
                grid.add(levelButton, i - 5, 3);
                grid.add(label, i - 5, 2);
            } else {
                grid.add(levelButton, i, 1);
                grid.add(label, i, 0);
            }
        }
        splashScreenPane.getChildren().add(grid);
        splashScreenPane.setLayoutY(-200);
        mainPane.setCenter(splashScreenPane);
        //mainPane.setBottom(levelSelectionPane);
        //audio = new Media("http://www.123rf.com/audio_27651314_light-instrumental-music-with-keyboards-funk-guitar-bass-and-drum-bright-melody-loops-of-various-key.html");
        //playAudio();
    }

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initSokobanUI() {
        // FIRST REMOVE THE SPLASH SCREEN
        //mainPane.getChildren().clear();
        mainPane.setCenter(null);

        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        primaryStage.setTitle(title);

        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        initNorthToolbar();

        // OUR WORKSPACE WILL STORE EITHER THE GAME, STATS,
        // OR HELP UI AT ANY ONE TIME
        initWorkspace();
        //initGameScreen();
        //initStatsPane();
        //initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        changeWorkspace(SokobanUIState.PLAY_GAME_STATE);

    }

    /**
     * This function initializes all the controls that go in the north toolbar.
     */
    private void initNorthToolbar() {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new HBox();
        northToolbar.setStyle("-fx-background-color:lightgray");
        northToolbar.setAlignment(Pos.CENTER);
        northToolbar.setPadding(marginlessInsets);
        //northToolbar.setSpacing(10.0);

        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(northToolbar,
                SokobanPropertyType.BACK_IMG_NAME);
        //setTooltip(backButton, SokobanPropertyType.GAME_TOOLTIP);
        backButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.SPLASH_SCREEN_STATE);
            }
        });

        // MAKE AND INIT THE UNDO BUTTON
        undoButton = initToolbarButton(northToolbar,
                SokobanPropertyType.UNDO_IMG_NAME);
        //setTooltip(undoButton, SokobanPropertyType.HELP_TOOLTIP);
        undoButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.VIEW_HELP_STATE);
            }

        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME);
        //setTooltip(statsButton, SokobanPropertyType.STATS_TOOLTIP);

        statsButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.VIEW_STATS_STATE);
            }

        });

        // MAKE AND INIT THE TIME BUTTON
        timeButton = initToolbarButton(northToolbar,
                SokobanPropertyType.TIME_IMG_NAME);
        //setTooltip(timeButton, SokobanPropertyType.EXIT_TOOLTIP);
        timeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                //eventHandler.respondToExitRequest(primaryStage);
            }

        });

        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        mainPane.setTop(northToolbar);
        //mainPane.getChildren().add(northToolbar);
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     *
     * @param toolbar The toolbar for which to add the button.
     *
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     *
     * @return A constructed button initialized and added to the toolbar.
     */
    private Button initToolbarButton(HBox toolbar, SokobanPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);

        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }

    /**
     * The workspace is a panel that will show different screens depending on
     * the user's requests.
     */
    private void initWorkspace() {
        // THE WORKSPACE WILL GO IN THE CENTER OF THE WINDOW, UNDER THE NORTH
        // TOOLBAR
        workspace = new Pane();
        mainPane.setCenter(workspace);
        //mainPane.getChildren().add(workspace);
        //System.out.println("in the initWorkspace");
    }

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     */
    public void changeWorkspace(SokobanUIState uiScreen) {
        switch (uiScreen) {
            case SPLASH_SCREEN_STATE:
                mainPane.getChildren().clear();
                initSplashScreen();
                break;
            case VIEW_HELP_STATE:
                //TODO
                undo();
                break;
            case PLAY_GAME_STATE:
                initGameScreen();
                mainPane.setCenter(gamePanel); // or renderer??
                break;
            case VIEW_STATS_STATE:
                //TODO
                mainPane.setCenter(statsScrollPane);
                break;
            default:
        }
    }

    public void initGameScreen() {
        gr = new GridRenderer();
        gamePanel.setCenter(gr);
        workspace.getChildren().add(gamePanel);
        int sokoban = 4, wall = 1, box = 2, hole = 3, empty = 0;
        mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {

            }
        });
        gr.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me1) {
                int[][] grid = new int[gr.gridColumns][gr.gridRows];//newgrid
                for (int i = 0; i < gr.gridColumns; i++) {
                    for (int j = 0; j < gr.gridRows; j++) {
                        grid[i][j] = gr.grid[i][j];
                    }
                }
                // FIGURE OUT THE CORRESPONDING COLUMN & ROW
                double w = gr.getWidth() / gr.gridColumns;
                double col = me1.getX() / w;
                double h = gr.getHeight() / gr.gridRows;
                double row = me1.getY() / h;
                // GET THE VALUE IN THAT CELL
                int value1 = gr.grid[(int) col][(int) row];
                if (value1 == sokoban) { //sokoban
                    gr.setOnMouseReleased((MouseEvent me2) -> {
                        double w2 = gr.getWidth() / gr.gridColumns;
                        double col2 = me2.getX() / w2;
                        double h2 = gr.getHeight() / gr.gridRows;
                        double row2 = me2.getY() / h2;
                        //2nd value=up
                        if (value1 == gr.grid[(int) col2][(int) row2 + 1]) {
                            System.out.println("up");
                            outerloop:
                            for (int i = 0; i < gr.gridColumns; i++) {
                                for (int j = 0; j < gr.gridRows; j++) {
                                    if (gr.grid[i][j] == sokoban) {
                                        switch (gr.grid[i][j - 1]) {
                                            case 0: //empty, move up
                                                undo.push(grid);
                                                gr.grid[i][j - 1] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = empty; //then, soko = blank
                                                break outerloop;
                                            case 1: //wall
                                                playAudio(block);
                                                break outerloop; //don't move
                                            case 2: //box, move soko up & move box up
                                                if (gr.grid[i][j - 2] == hole || gr.grid[i][j - 2] == empty) { //2 spaces beyond avail
                                                    undo.push(grid);
                                                    gr.grid[i][j - 2] = gr.grid[i][j - 1]; //two ahead is box  
                                                    gr.grid[i][j - 1] = sokoban; //box is soko
                                                    gr.grid[i][j] = empty; //soko is empty or hole???
                                                } else if (gr.grid[i][j - 2] == box || gr.grid[i][j - 2] == wall) { //2 spaces is a box OR wall
                                                    playAudio(block);
                                                }
                                                break outerloop;
                                            case 3: //hole, move up
                                                undo.push(grid);
                                                gr.grid[i][j - 1] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = hole; //then, soko = hole
                                                break outerloop;
                                        }
                                    }
                                }
                            }
                            gr.repaint();
                            me1.consume();
                            me2.consume();
                        } //2nd value = down
                        else if (value1 == gr.grid[(int) col2][(int) row2 - 1]) {
                            System.out.println("down");
                            outerloop:
                            for (int i = 0; i < gr.gridColumns; i++) {
                                for (int j = 0; j < gr.gridRows; j++) {
                                    if (gr.grid[i][j] == sokoban) {
                                        switch (gr.grid[i][j + 1]) {
                                            case 0: //empty, move up
                                                undo.push(grid);
                                                gr.grid[i][j + 1] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = empty; //then, soko = blank
                                                break outerloop;
                                            case 1: //wall
                                                playAudio(block);
                                                break outerloop; //don't move
                                            case 2: //box, move soko up & move box up
                                                if (gr.grid[i][j + 2] == hole || gr.grid[i][j + 2] == empty) { //2 spaces beyond avail
                                                    undo.push(grid);
                                                    gr.grid[i][j + 2] = gr.grid[i][j + 1]; //two ahead is box  
                                                    gr.grid[i][j + 1] = sokoban; //box is soko
                                                    gr.grid[i][j] = empty; //soko is empty or hole???
                                                } else if (gr.grid[i][j + 2] == box || gr.grid[i][j + 2] == wall) { //2 spaces is a box OR wall
                                                    playAudio(block);
                                                }
                                                break outerloop;
                                            case 3: //hole, move up
                                                undo.push(grid);
                                                gr.grid[i][j + 1] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = hole; //then, soko = hole
                                                break outerloop;
                                        }
                                    }
                                }
                            }
                            gr.repaint();
                            me1.consume();
                            me2.consume();
                        } //2nd value = left
                        else if (value1 == gr.grid[(int) col2 + 1][(int) row2]) {
                            System.out.println("left");
                            outerloop:
                            for (int i = 0; i < gr.gridColumns; i++) {
                                for (int j = 0; j < gr.gridRows; j++) {
                                    if (gr.grid[i][j] == sokoban) {
                                        switch (gr.grid[i - 1][j]) {
                                            case 0: //empty, move up
                                                undo.push(grid);
                                                gr.grid[i - 1][j] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = empty; //then, soko = blank
                                                break outerloop;
                                            case 1: //wall
                                                playAudio(block);
                                                break outerloop; //don't move
                                            case 2: //box, move soko up & move box up
                                                if (gr.grid[i - 2][j] == hole || gr.grid[i - 2][j] == empty) { //2 spaces beyond avail
                                                    undo.push(grid);//gr.grid[i-2][j] <= box & gr.grid[i-1][j] <= soko
                                                    gr.grid[i - 2][j] = gr.grid[i - 1][j]; //two ahead is box  
                                                    gr.grid[i - 1][j] = sokoban; //box is soko
                                                    gr.grid[i][j] = empty; //soko is empty or hole???
                                                } else if (gr.grid[i - 2][j] == box || gr.grid[i - 2][j] == wall) { //2 spaces is a box OR wall
                                                    playAudio(block);
                                                }
                                                break outerloop;
                                            case 3: //hole, move up
                                                undo.push(grid);
                                                gr.grid[i - 1][j] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = hole; //then, soko = hole
                                                break outerloop;
                                        }
                                    }
                                }
                            }
                            gr.repaint();
                            me1.consume();
                            me2.consume();
                        } //2nd value = right
                        else if (value1 == gr.grid[(int) col2 - 1][(int) row2]) {
                            System.out.println("right");
                            outerloop:
                            for (int i = 0; i < gr.gridColumns; i++) {
                                for (int j = 0; j < gr.gridRows; j++) {
                                    if (gr.grid[i][j] == sokoban) {
                                        switch (gr.grid[i + 1][j]) {
                                            case 0: //empty, move up
                                                undo.push(grid);
                                                gr.grid[i + 1][j] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = empty; //then, soko = blank
                                                break outerloop;
                                            case 1: //wall
                                                playAudio(block);
                                                break outerloop; //don't move
                                            case 2: //box, move soko up & move box up
                                                if (gr.grid[i + 2][j] == hole || gr.grid[i + 2][j] == empty) { //2 spaces beyond avail
                                                    undo.push(grid);//gr.grid[i-2][j] <= box & gr.grid[i-1][j] <= soko
                                                    gr.grid[i + 2][j] = gr.grid[i + 1][j]; //two ahead is box  
                                                    gr.grid[i + 1][j] = sokoban; //box is soko
                                                    gr.grid[i][j] = empty; //soko is empty or hole???
                                                } else if (gr.grid[i + 2][j] == box || gr.grid[i + 2][j] == wall) { //2 spaces is a box OR wall
                                                    playAudio(block);
                                                }
                                                break outerloop;
                                            case 3: //hole, move up
                                                undo.push(grid);
                                                gr.grid[i + 1][j] = sokoban; //space up replaced by soko
                                                gr.grid[i][j] = hole; //then, soko = hole
                                                break outerloop;
                                        }
                                    }
                                }
                            }
                            gr.repaint();
                        }
                    });
                }
            }
        });
        //movement by arrow keys
        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                // TODO Auto-generated method stub
                int[][] grid = new int[gr.gridColumns][gr.gridRows]; //newgrid
                for (int i = 0; i < gr.gridColumns; i++) {
                    for (int j = 0; j < gr.gridRows; j++) {
                        grid[i][j] = gr.grid[i][j];
                    }
                }
                if (ke.getCode() == KeyCode.UP) {
                    System.out.println("up");
                    outerloop:
                    for (int i = 0; i < gr.gridColumns; i++) {
                        for (int j = 0; j < gr.gridRows; j++) {
                            if (gr.grid[i][j] == sokoban) {
                                switch (gr.grid[i][j - 1]) {
                                    case 0: //empty, move up
                                        undo.push(grid);
                                        gr.grid[i][j - 1] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = empty; //then, soko = blank
                                        break outerloop;
                                    case 1: //wall
                                        playAudio(block);
                                        break outerloop; //don't move
                                    case 2: //box, move soko up & move box up
                                        if (gr.grid[i][j - 2] == hole || gr.grid[i][j - 2] == empty) { //2 spaces beyond avail
                                            undo.push(grid);
                                            gr.grid[i][j - 2] = gr.grid[i][j - 1]; //two ahead is box  
                                            gr.grid[i][j - 1] = sokoban; //box is soko
                                            gr.grid[i][j] = empty; //soko is empty or hole???
                                        } else if (gr.grid[i][j - 2] == box || gr.grid[i][j - 2] == wall) { //2 spaces is a box OR wall
                                            playAudio(block);
                                        }
                                        break outerloop;
                                    case 3: //hole, move up
                                        undo.push(grid);
                                        gr.grid[i][j - 1] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = hole; //then, soko = hole
                                        break outerloop;
                                }
                            }
                        }
                    }
                    gr.repaint();
                }
                if (ke.getCode() == KeyCode.DOWN) {
                    System.out.println("down");
                    outerloop:
                    for (int i = 0; i < gr.gridColumns; i++) {
                        for (int j = 0; j < gr.gridRows; j++) {
                            if (gr.grid[i][j] == sokoban) {
                                switch (gr.grid[i][j + 1]) {
                                    case 0: //empty, move up
                                        undo.push(grid);
                                        gr.grid[i][j + 1] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = empty; //then, soko = blank
                                        break outerloop;
                                    case 1: //wall
                                        playAudio(block);
                                        break outerloop; //don't move
                                    case 2: //box, move soko up & move box up
                                        if (gr.grid[i][j + 2] == hole || gr.grid[i][j + 2] == empty) { //2 spaces beyond avail
                                            undo.push(grid);
                                            gr.grid[i][j + 2] = gr.grid[i][j + 1]; //two ahead is box  
                                            gr.grid[i][j + 1] = sokoban; //box is soko
                                            gr.grid[i][j] = empty; //soko is empty or hole???
                                        } else if (gr.grid[i][j + 2] == box || gr.grid[i][j + 2] == wall) { //2 spaces is a box OR wall
                                            playAudio(block);
                                        }
                                        break outerloop;
                                    case 3: //hole, move up
                                        undo.push(grid);
                                        gr.grid[i][j + 1] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = hole; //then, soko = hole
                                        break outerloop;
                                }
                            }
                        }
                    }
                    gr.repaint();
                }
                if (ke.getCode() == KeyCode.LEFT) {
                    System.out.println("left");
                    outerloop:
                    for (int i = 0; i < gr.gridColumns; i++) {
                        for (int j = 0; j < gr.gridRows; j++) {
                            if (gr.grid[i][j] == sokoban) {
                                switch (gr.grid[i - 1][j]) {
                                    case 0: //empty, move up
                                        undo.push(grid);
                                        gr.grid[i - 1][j] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = empty; //then, soko = blank
                                        break outerloop;
                                    case 1: //wall
                                        playAudio(block);
                                        break outerloop; //don't move
                                    case 2: //box, move soko up & move box up
                                        if (gr.grid[i - 2][j] == hole || gr.grid[i - 2][j] == empty) { //2 spaces beyond avail
                                            undo.push(grid);//gr.grid[i-2][j] <= box & gr.grid[i-1][j] <= soko
                                            gr.grid[i - 2][j] = gr.grid[i - 1][j]; //two ahead is box  
                                            gr.grid[i - 1][j] = sokoban; //box is soko
                                            gr.grid[i][j] = empty; //soko is empty or hole???
                                        } else if (gr.grid[i - 2][j] == box || gr.grid[i - 2][j] == wall) { //2 spaces is a box OR wall
                                            playAudio(block);
                                        }
                                        break outerloop;
                                    case 3: //hole, move up
                                        undo.push(grid);
                                        gr.grid[i - 1][j] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = hole; //then, soko = hole
                                        break outerloop;
                                }
                            }
                        }
                    }
                    gr.repaint();
                }
                if (ke.getCode() == KeyCode.RIGHT) {
                    System.out.println("right");
                    outerloop:
                    for (int i = 0; i < gr.gridColumns; i++) {
                        for (int j = 0; j < gr.gridRows; j++) {
                            if (gr.grid[i][j] == sokoban) {
                                switch (gr.grid[i + 1][j]) {
                                    case 0: //empty, move up
                                        undo.push(grid);
                                        gr.grid[i + 1][j] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = empty; //then, soko = blank
                                        break outerloop;
                                    case 1: //wall
                                        playAudio(block);
                                        break outerloop; //don't move
                                    case 2: //box, move soko up & move box up
                                        if (gr.grid[i + 2][j] == hole || gr.grid[i + 2][j] == empty) { //2 spaces beyond avail
                                            undo.push(grid);//gr.grid[i-2][j] <= box & gr.grid[i-1][j] <= soko
                                            gr.grid[i + 2][j] = gr.grid[i + 1][j]; //two ahead is box  
                                            gr.grid[i + 1][j] = sokoban; //box is soko
                                            gr.grid[i][j] = empty; //soko is empty or hole???
                                        } else if (gr.grid[i + 2][j] == box || gr.grid[i + 2][j] == wall) { //2 spaces is a box OR wall
                                            playAudio(block);
                                        }
                                        break outerloop;
                                    case 3: //hole, move up
                                        undo.push(grid);
                                        gr.grid[i + 1][j] = sokoban; //space up replaced by soko
                                        gr.grid[i][j] = hole; //then, soko = hole
                                        break outerloop;
                                }
                            }
                        }
                    }
                    gr.repaint();
                }
                if (ke.getCode() == KeyCode.U) {
                    undo();
                }
            }
        });
    }

    public GridRenderer getGrid() {
        return gr;
    }

    /**
     * This class renders the grid for us. Note that we also listen for mouse
     * clicks and key presses on it.
     */
    class GridRenderer extends Canvas {

        // PIXEL DIMENSIONS OF EACH CELL
        int cellWidth;
        int cellHeight;
        int gridColumns = 10;
        int gridRows = 10;
        int[][] grid;

        /**
         * Default constructor.
         */
        public GridRenderer() {
            this.setWidth(650);
            this.setHeight(650);
            grid = new int[gridColumns][gridRows];
            repaint();
        }

        public void repaint() {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            int w = (int) (this.getWidth() / gridColumns);
            int h = (int) (this.getHeight() / gridRows);

            gc = this.getGraphicsContext2D();

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            for (int i = 0; i < gridColumns; i++) {
                y = 0;
                for (int j = 0; j < gridRows; j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.WHITE);
                    switch (grid[i][j]) {
                        case 0:
                            gc.fillRect(x, y, w, h);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            gc.drawImage(boxImage, x, y, w, h);
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            gc.drawImage(sokobanImage, x, y, w, h);
                            break;
                    }

                    double xInc = (w / 2) - (10 / 2);
                    double yInc = (h / 2) + (10 / 4);
                    x += xInc;
                    y += yInc;
                    x -= xInc;
                    y -= yInc;

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
        }
    }

    public void undo() {
        if (undo.isEmpty() == false) {
            System.out.println("u");
            gr.grid = undo.pop();
            gr.repaint();
        }
    }

    final Media block = new Media(new File("audio/block.wav").toURI().toString());

    public void playAudio(Media media) {
        media = block;
        MediaPlayer audio = new MediaPlayer(media);
        audio.play();
    }
}

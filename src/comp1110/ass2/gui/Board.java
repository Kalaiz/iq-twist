package comp1110.ass2.gui;


import comp1110.ass2.Pieces;
import comp1110.ass2.StartingBoard;
import comp1110.ass2.TwistGame;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.*;
import java.util.stream.IntStream;

import static comp1110.ass2.StartingBoard.solNum;
import static comp1110.ass2.TwistGame.getFormalPieces;

public class Board extends Application {
    /*ToDo:
    1)Link task 11 to task 7
    2Task 10
    3)On completion Text should appear
    4)loading Screen
    5)Set image background
    6)Task 5 resetting the board
    7)Removal of extra board
    8)Board:createBoard improve current codes
    9)PPT
    10)Code cleanup & Fix Warnings
    */
    private static String specificSol = "";
    private static String gameState = "";//The game String
    private static String startingBoard = "";//The starting board string
    private static String solution="";
    private static final int DISPLAY_WIDTH = 1280;
    private static final int DISPLAY_HEIGHT = 649;
    private static final String URI_BASE = "assets/";

    private final Text completionText = new Text("Well done!");

    /*Game object*/
    TwistGame game = new TwistGame();

    /*NODE groups*/
    private Group root = new Group();
    private Group boardgrid = new Group();
    private Group pieces = new Group();
    private Group controls = new Group();
    private Group board=new Group();
    private Group outsides = new Group();

    /* Grid */
    private static GridPane grid = new GridPane();

    /* Default (home) x & y coordinates*/
    private static final double[] hxy = {100, 50, 100, 200, 100, 400, 740, 380, 340
                                          , 50, 340, 200, 340, 350, 540, 380};

    /* the difficulty slider */
    private final Slider difficulty = new Slider();

    /*Glow objects*/
    private Glow g1 = new Glow(0.5);
    private Glow g2 = new Glow();

    /*States whether game has started or not */
    private static boolean gamestart;



    private void createBoard(){
        Rectangle rect1 = new Rectangle(690, 2.5, 420, 215);
        Rectangle rect2 = new Rectangle(692.5, 5, 415, 210);
        //rect1.setFill(Color.GREY);
        rect2.setFill(Color.WHITE);
        rect1.setArcHeight(30);
        rect1.setArcWidth(30);
        rect2.setArcHeight(30);
        rect2.setArcWidth(30);
        rect1.toBack();
        rect2.toBack();
        for (int i = 0; i < 8; i ++) {
            for (int j = 0; j < 4; j ++) {
                Circle circle1 = new Circle(725 + 50 * i, 35 + 50 * j, 27);
                Circle circle2 = new Circle(725 + 50 * i, 35 + 50 * j, 25);
                Circle innercircle =new Circle(725 + 50 * i, 35 + 50 * j, 15);
                Circle innercircle2 =new Circle(725 + 50 * i, 35 + 50 * j, 13);
                circle1.setFill(Color.DARKGREY);
                circle2.setFill(Color.WHITE);
                innercircle.setFill(Color.LIGHTGREY);
                innercircle2.setFill(Color.WHITE);
                circle1.toBack();
                circle2.toBack();
                board.getChildren().add(circle1);
                board.getChildren().add(circle2);
                board.getChildren().add(innercircle);
                board.getChildren().add(innercircle2);
            }
        }
        outsides.getChildren().add(rect1);
        outsides.getChildren().add(rect2);



    }
    /*Sets up the board*/
    private void createBoardGrid() {
        boardgrid.getChildren().clear();
        for (int i = 0; i < 8; i++) {
            ColumnConstraints col = new ColumnConstraints(50);
            grid.getColumnConstraints().add(col);
        }
        for (int i = 0; i < 4; i++) {
            RowConstraints row = new RowConstraints(50);
            grid.getRowConstraints().add(row);
        }



        grid.setGridLinesVisible(false);
        grid.setLayoutX(700);
        grid.setLayoutY(10);
        boardgrid.getChildren().add(grid);
        //board.toBack();      //places the node it at the back
    }

    /*
     *Create a dialogue when the player start the game
     */
    private void instructions(){
        Stage dialogue = new Stage();
        dialogue.setTitle("Instructions");
        dialogue.initModality(Modality.APPLICATION_MODAL);
        dialogue.setMinWidth(500);
        dialogue.setMaxHeight(500);
        dialogue.getIcons().add((new Image((Viewer.class.getResource(URI_BASE + "e.png").toString()))));
        Button button = new Button("Close the widow");
        button.setOnAction(e -> dialogue.close());
        Text t1 = new Text("Right click for flip.");
        Text t2 = new Text("Scroll over the piece to rotate it.");
        Text t3 = new Text("To reset a piece,hover over it and press the middle button.");
        VBox layout = new VBox(10);
        layout.getChildren().addAll(t1, t2, t3, button);
        layout.setAlignment(Pos.CENTER);;
        Scene scene = new Scene(layout);
        scene.setFill(Color.WHITE);
        dialogue.setScene(scene);
        dialogue.showAndWait();
    }

    /*
     *Create the message to be displayed when the player completes the game
     */
    private void makeCompletion(){
        completionText.setFill(Color.BLACK);
        completionText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
        completionText.setLayoutX(DISPLAY_WIDTH/3);
        completionText.setLayoutY(DISPLAY_HEIGHT/2);
        completionText.setTextAlignment(TextAlignment.CENTER);
        completionText.toFront();
        completionText.setOpacity(1);
        root.getChildren().add(completionText);
    }


    /* Inner class for pieces*/
    class piece extends ImageView {
        int pieceType;//The actual ascii number.
        double defaultX;//Default x coordinate position on start of the game
        double defaultY;//Default y coordinate position on start of the game
        ImageView holder;

        piece(char pieceType) {
            holder = new ImageView();//Since grid needs a node
            Image img = (new Image(Viewer.class.getResource(URI_BASE + pieceType + ".png").toString()));
            holder.setImage(img);
            root.getChildren().add(holder);
            double height = img.getHeight() * 0.5;//Resizing the image
            double width = img.getWidth() * 0.5;
            holder.setFitHeight(height);
            holder.setFitWidth(width);
            this.pieceType = pieceType;// according to ascii encoding
            if (!(pieceType > 104)) {//pegs
                defaultX = hxy[2 * (pieceType - 97)];
                defaultY = hxy[(2 * (pieceType - 97)) + 1];
                holder.setX(defaultX);
                holder.setY(defaultY);
            }

        }

    }

    class eventPiece extends piece {
        String pieceInfo;
        int gridRow;//placement on the grid
        int gridCol;
        boolean flip;
        int rotate;

        void reset() {
            holder.setX(defaultX);
            holder.setY(defaultY);
            holder.setRotate(0);
            holder.setScaleY(1);
            holder.setTranslateX(0);
        }

        void delete() {
            holder.setImage(null);
        }

        eventPiece(String pieceInfo) {
            super(pieceInfo.charAt(0));
            gridCol = Integer.parseInt(pieceInfo.charAt(1) + "") - 1;
            //System.out.println(gridCol);
            gridRow = pieceInfo.charAt(2) - 65;
            //System.out.println(gridRow);
            int orientation = Integer.parseInt(pieceInfo.charAt(3) + "");
            flip = (orientation > 3);
            if (flip) {
                holder.setScaleY(-1);
            } else {
                holder.setScaleY(1);
            }
            rotate = (orientation > 4) ? 90 * (orientation - 4) : 90 * orientation;
            holder.setRotate(rotate);
            int[] csrs = imgModifier();
            grid.add(holder, gridCol, gridRow, csrs[0], csrs[1]);
        }

        private void decodePieces() {
            //As the string for the column (piece-encoding) starts from 1
            int col = gridCol + 1;
            int orientation = (flip) ? (int) ((holder.getRotate() / 90) + 4) : (int) (holder.getRotate() / 90);
            // Character of the piece + Column:number + Row:Alpha + orientation:number
            pieceInfo = Character.toString((char) pieceType) + col + ((char) (gridRow + 65)) + Integer.toString(orientation);

        }


        eventPiece(char piece) {
            super(piece);
            flip = false;//Declaring
            holder.setOnScroll(scroll -> {//Rotation on scroll
                if (!grid.getChildren().contains(holder)) {//If grid does not contain the piece .
                    rotate += 90;
                    rotate = (rotate >= 360) ? 0 : rotate;
                    holder.setRotate(rotate);
                }
            });

            holder.setOnMouseEntered(geffect -> { //Glow effect
                holder.setEffect(g1);

            });

            holder.setOnMouseExited(ageffect -> {//Anti glow effect
                holder.setEffect(g2);
            });

            holder.setOnMouseClicked(click -> {//Flip on right click
                if (click.getButton() == MouseButton.SECONDARY && !grid.getChildren().contains(holder)) {
                    if (flip) {
                        holder.setScaleY(1);
                        flip = false;
                    } else {
                        holder.setScaleY(-1);
                        flip = true;
                    }
                }
                else if(click.getButton()==MouseButton.MIDDLE&&grid.getChildren().contains(holder)){
                  resetPiecestr(pieceInfo);

                }
            });

            holder.setOnMouseDragged(drag -> {
                if (!grid.getChildren().contains(holder)) {
                    double cornerX = drag.getSceneX();
                    double cornerY = drag.getSceneY();
                    //Divide by 2 so to pull it by the center & using getScene for relative positioning
                    holder.setY(cornerY - holder.getFitHeight() / 2);
                    holder.setX(cornerX - holder.getFitWidth() / 2);
                    //Image remains non rotated according to Javafx(Grid).
                    //Piece G is an exception as it does not cause any offset error
                    double imgCornerx = ((rotate / 90) % 2 == 0 || pieceType == 103) ? holder.getX() : holder.getX() + holder.getFitWidth() / 2.5;
                    double imgCornery = ((rotate / 90) % 2 == 0 || pieceType == 103) ? holder.getY() : cornerY - holder.getFitWidth() / 2.25;
                    holder.setOnMouseReleased(released -> {
                        //not exactly the coordinates of the grid as to allow flexibility for the user
                        if (imgCornerx < 680 || imgCornerx > 1080 || imgCornery > 200 || imgCornery < 0) {
                            holder.setX(defaultX);
                            holder.setY(defaultY);
                        } else {// Image's top left corner.
                            setOnGrid(imgCornerx, imgCornery);
                        }
                    });
                }
            });

        }


        int[] imgModifier() {
            int rowspan = (int) holder.getImage().getHeight() / 100;
            int colspan = (int) holder.getImage().getWidth() / 100;
            //Switching the row and col span upon rotation
            int rs = ((rotate / 90) % 2 == 0) ? rowspan : colspan;
            int cs = ((rotate / 90) % 2 == 0) ? colspan : rowspan;
            int translateX = ((rotate / 90) % 2 == 0) ? 0 : -(int) (holder.getFitWidth() - holder.getFitHeight()) / 2;
            //to balance the offset created upon setting the image  on the grid
            holder.setTranslateX(translateX);
            int[] csrs = {cs, rs};
            return csrs;
        }


        void setOnGrid(double positionalX, double positionalY) {// Image's top left corner.
            // 50 - width/height of each grid
            // 695 & 5 - the approximate starting co-ordinates  of the grid
            gridCol = (int) (positionalX - 695) / 50;
            gridRow = (int) (positionalY - 5) / 50;
            int[] csrs = imgModifier();
            decodePieces();//Converting available data into piece encoding
            gameState += pieceInfo;//Concatenating the piece encoding into the game String
            //System.out.println(gameState);
            if (game.isPlacementStringValid(gameState)) {
                grid.add(holder, gridCol, gridRow, csrs[0], csrs[1]);
            } else {
                holder.setX(defaultX);
                holder.setY(defaultY);
                //updating the gameState string if the position is not valid.
                gameState = gameState.substring(0, gameState.length() - 4);
            }
        }
    }


    /*Creates all required pieces -- for the start of the game*/
    private void createPieces() {
        pieces.getChildren().clear();
        for (char ch = 'a'; ch <= 'h'; ch++) {//for only pieces not for pegs as pegs can't be placed by players
            pieces.getChildren().add(new eventPiece(ch));
        }
    }



    private void forceReset() {//Used for new game
        grid = new GridPane();
        Iterator im = pieces.getChildren().iterator();
        while (im.hasNext()) {
            eventPiece g = (eventPiece) im.next();
            g.delete();//Deleting the images formed by eventpieces
        }
        //pieces.getChildren().clear();//clears all the pieces in the node .


    }



    /**
     * Start a new game & clear the previous board
     */
    private void newGame() {
        Viewer access = new Viewer();
        TwistGame t = new TwistGame();
        //System.out.println(gamestart);
        if (gamestart) {
            forceReset();//clear the board and pieces using a seperate function
        }
        gamestart = true;
        specificSol = diffLevel(difficulty.getValue());
        //System.out.println(diffLevel(difficulty.getValue()));
        gameState = startingBoard;
        String placed = access.returner(startingBoard, 0);
        String unplaced = "";
        int[] output = IntStream.rangeClosed(97, 104).filter(i -> !placed.contains((char) i + "")).parallel().toArray();
        for (int i : output) {
            unplaced += (Character.toString((char) i));
        }//converting to String- not necessary
        for (int m = 0; m < unplaced.length(); m++) {
            pieces.getChildren().add(new eventPiece(unplaced.charAt(m)));
        }
        //System.out.println(startingBoard);
        List<String> listOfPieces = t.getFormalPieces(startingBoard);
        for (int i = 0; i < listOfPieces.size(); i++) {
            pieces.getChildren().add(new eventPiece(listOfPieces.get(i)));
        }
        createBoardGrid();
        //place all the pieces from  startingBoardplacement to the grid
        //update gameState & startingboard

    }

    private void  resetPiece(eventPiece p){
        grid.getChildren().remove(p.holder);
        pieces.getChildren().remove(p);
        pieces.getChildren().add(new eventPiece(((p).pieceInfo).charAt(0)));
    }

    private void  resetPiecestr(String pieceInfo){
        try {
        for(Node n:pieces.getChildren()){
            if (((((eventPiece) n).pieceInfo)) != null) {
            if(((eventPiece) n).pieceInfo.equals(pieceInfo)){
                grid.getChildren().remove(((eventPiece) n).holder);
                pieces.getChildren().remove(( n));
                pieces.getChildren().add(new eventPiece(((((eventPiece) n)).pieceInfo).charAt(0)));}
                gameState=gameState.replace(pieceInfo,"");
        }}

    }
    catch(ConcurrentModificationException e){}}


    private void resetgame() {
        try {
            gameState = startingBoard;
            Object[] arr = pieces.getChildren().toArray();//casue of concurrentModificationerror
            for (Object obj : arr) {
               // System.out.println((((eventPiece) obj).pieceInfo));
                if (((((eventPiece) obj).pieceInfo)) != null) {
                    if (!startingBoard.contains(((eventPiece) obj).pieceInfo)) {
                        //System.out.println(((eventPiece) obj).pieceInfo);
                    resetPiece((eventPiece )obj);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Error");
        }
    }




    /**
     * Create the controls that allow the game to be restarted and the difficulty
     * level set.
     * New Game - will create a new game board according to sliding level value
     */
    private void makeControls() {
        Button newGame = new Button("New Game");
        Button reset = new Button("Reset");
        reset.setLayoutX(DISPLAY_WIDTH / 4 + 30);
        reset.setLayoutY(DISPLAY_HEIGHT - 45);
        newGame.setLayoutX(DISPLAY_WIDTH / 4 + 100);
        newGame.setLayoutY(DISPLAY_HEIGHT - 45);
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                resetgame();
            }
        });
        newGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                newGame();

            }
        });

        controls.getChildren().add(newGame);
        controls.getChildren().add(reset);
        difficulty.setMin(0);
        difficulty.setMax(2);
        difficulty.setValue(0);
        difficulty.setShowTickLabels(true);
        difficulty.setShowTickMarks(true);
        difficulty.setMajorTickUnit(1);
        difficulty.setMinorTickCount(1);
        difficulty.setSnapToTicks(true);
        difficulty.setLayoutX(DISPLAY_WIDTH / 4 - 140);
        difficulty.setLayoutY(DISPLAY_HEIGHT - 40);
        controls.getChildren().add(difficulty);
        final Label difficultyCaption = new Label("Difficulty:");
        difficultyCaption.setTextFill(Color.GREY);
        difficultyCaption.setLayoutX(DISPLAY_WIDTH / 4 - 210);
        difficultyCaption.setLayoutY(DISPLAY_HEIGHT - 40);
        controls.getChildren().add(difficultyCaption);
        root.getChildren().add(controls);
    }


    /**
     * Sets the background filled with miniature sized pieces with a different opacity level
     * Miniature pieces must not be in the region of the grid
     * Obtains data about the starting positions so to allow miniature pieces to fill empty areas
     *
     * @param startingPlacement - The board state at the start of the game.
     */
    private void setBackground(String startingPlacement) {
        /*
         *IDEA- use the pieces group objects to know all offboard pieces
         *makes about 15 pieces
         *produce x and y coordinates randomly (updating starting x and y  and increasing upper bound of random value)
         *check if x and y coordinate are not in
         *                                       1)Grid
         *                                       2)Placement area of pieces (not on board)
         *
         *                                       */
        Viewer access = new Viewer();
        Random rnd = new Random();
        String onBoardPieces = access.returner(startingPlacement, 0);
        double rotate = rnd.nextInt(360);
        int piecePicker = rnd.nextInt(8);
        // offBoardPieces [] is going to contain the ascii encoding values of all pieces which are off board.
        int[] offBoardPieces = IntStream.rangeClosed(97, 104).filter(i -> !onBoardPieces.contains((char) i + "")).parallel().toArray();
        List<Integer> invalidArea = new ArrayList<>();
        for (int offboardVal : offBoardPieces) {
            //invalidArea.add((int))

        }
        double tempx = 0;
        double tempy = 0;

    }


    private void showCompletion(){
        int length = gameState.length();
        int numofpi = length/4;
        int numofpiece = 0;
        int i = 0;

        while (i < length){
            if (gameState.substring(i).charAt(0) < (char)105){
                numofpiece ++;
            }
            i += 4;
        }
        //System.out.println(numofpiece);
        if (numofpiece >= 8){
            makeCompletion();

        }
    }

    /*Start of JavaFX operations */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("IQ-twist");//sets the title name on the bar
        //sets the icon
        /*
        *  createpieces()
        *  getsolutions()
        *  pegAdder()   -->  add to hashMap
        *  add a loading screen until hashMap is initialized
        *  difficulty level alg (which should be created in StartingBoard.java, returns a starting board string)
        *           problem: find effecive way of finding n pieces & pegs from a collection
        *  when the user press the "new game" button, accroding to the diff level, run the alg over solutions
        */

        primaryStage.getIcons().add(new Image((Viewer.class.getResource(URI_BASE + "e.png").toString())));
        Scene scene = new Scene(root, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        createBoard();
        root.getChildren().add(outsides);
        root.getChildren().add(board);
        newGame();
        root.getChildren().add(boardgrid);
        root.getChildren().add(pieces);
        makeControls();
        instructions();
        //showCvb   ompletion();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String diffLevel(double level){
        StartingBoard sb = new StartingBoard();
        sb.pegAdder(sb.pieceCreator());
        startingBoard = sb.difficultyLevel(level);

        return sb.sol.get(solNum);
    }


  /*  // FIXME Task 8: Implement starting placements
    public static String makeBoard() {
//        StartingBoard obj = new StartingBoard();
//        Random rand = new Random();
//        String startboard = obj.difficultyStorage.get(rand.nextInt(75))[0];

       String startboard = "a7A7b6A7c1A3d2A6e2C3f3C2g4A7h6D0i6B0j2B0j1C0k3C0l4B0l5C0";

        return startboard;
    }*/

    // TESTING REASONS
    public static String makeBoard() {

        Random rand = new Random();
        String[] strs = {
                "c1A3d2A6e2C3f3C2g4A7h6D0i6B0j2B0j1C0k3C0l4B0l5C0",
                "c5A2d1B3e4A5f4C2g2B3h1A2i7D0j7A0k5B0k5C0l3A0l3D0",
                "c3A3d1A3e1C4f4B3g6B2h5D0i5A0j2B0j3C0k2C0k2D0l8C0l8D0",
                "c1B2d7B1e1C6f6A0g4A5h1A0j3B0j7D0k1C0k1D0l6B0l1A0",
                "c1B2d4C4e1C3f4A0g6A1h1A0j3B0j5C0",
                "c5A2d7B7e5B0f1A6g3A7h5D0i1B0j7A0j7B0k1A0k2B0l3B0l4C0",
                "c2D0d7B1e1A3f2A2g4B2h4A2i7B0j3D0j7D0k3A0l6A0",
                "c2D0d1A0e5B4f1B3g3A3h5A0k1B0k6B0l5A0l3C0",
                "c5C0d3A6e7A1f3C4g1B3h6D0j4B0k8B0k5D0l3C0",
                "c3A0d1A3e5C2f1C4g6B7h4B0k3D0k5D0l6C0",
                "d2A6e2C3f3C2g4A7h6D0i6B0j2B0j1C0k3C0l4B0l5C0",
                "d1B3e4A5f4C2g2B3h1A2i7D0j7A0k5B0k5C0l3A0l3D0",
                "d1A3e1C4f4B3g6B2h5D0i5A0j2B0j3C0k2C0k2D0l8C0l8D0",
                "d7B1e1C6f6A0g4A5h1A0j3B0j7D0k1C0k1D0l6B0l1A0",
                "d4C4e1C3f4A0g6A1h1A0j3B0j5C0",
                "d7B7e5B0f1A6g3A7h5D0i1B0j7A0j7B0k1A0k2B0l3B0l4C0",
                "d7B1e1A3f2A2g4B2h4A2i7B0j3D0j7D0k3A0l6A0",
                "d1A0e5B4f1B3g3A3h5A0k1B0k6B0l5A0l3C0",
                "d3A6e7A1f3C4g1B3h6D0j4B0k8B0k5D0l3C0",
                "d1A3e5C2f1C4g6B7h4B0k3D0k5D0l6C0",
                "e2C3f3C2g4A7h6D0i6B0j2B0j1C0k3C0l4B0l5C0",
                "e4A5f4C2g2B3h1A2i7D0j7A0k5B0k5C0l3A0l3D0",
                "e1C4f4B3g6B2h5D0i5A0j2B0j3C0k2C0k2D0l8C0l8D0",
                "e1C6f6A0g4A5h1A0j3B0j7D0k1C0k1D0l6B0l1A0",
                "e1C3f4A0g6A1h1A0j3B0j5C0",
                "e5B0f1A6g3A7h5D0i1B0j7A0j7B0k1A0k2B0l3B0l4C0",
                "e1A3f2A2g4B2h4A2i7B0j3D0j7D0k3A0l6A0",
                "e5B4f1B3g3A3h5A0k1B0k6B0l5A0l3C0",
                "e7A1f3C4g1B3h6D0j4B0k8B0k5D0l3C0",
                "e5C2f1C4g6B7h4B0k3D0k5D0l6C0",
                "c1A3d2A6e2C3f3C2g4A7h6D0j2B0j1C0k3C0l4B0l5C0",
                "a7A7b6A5c1A3d2A6e2C3f3C2g4A7h6D0",
                "a6A0b6B0c1A3d2A6e2C3f3C2g4A7h6D0",
                "a6B0b6C0c5A2e4A5f4C2h1A2i7D0j7A0k5B0k5C0l3A0",
                "a6B0b6C0c5A2d1B3e4A5f4C2g2B3h1A2",
                "a6B0b6C0c5A2d1B5e4A5f4C2g2B5h1A2"
        };
        int radius = strs.length;
        int number = rand.nextInt(radius);
        String startpoints = strs[number];
        return startpoints;
    }

    /*set opacity of selected pieces to a certain percentage  or
    use Blur effect for that certain piece (using setEffect) Use task 9 code for the solutions.*/
    // FIXME Task 10: Implement hints
    public static void hint() {

        String hint = "";

        if (specificSol.contains(gameState) && specificSol.length() > gameState.length()) {

            Random r = new Random();

            //get the gamestate string & specific solutionstring and convert them into lists, resort them to find the unplaced strings in specificSolL
            List<String> gameStateL = getFormalPieces(gameState.substring(0,specificSol.length()));
            Collections.sort(gameStateL);
            List<String> specificSolL = getFormalPieces(specificSol);
            Collections.sort(specificSolL);

            //randomly choose one piece in specificSolL which is shown as the hint
            int index = r.nextInt(specificSolL.size() - gameStateL.size()) + gameStateL.size();
            
            hint = specificSolL.get(index);
        }
    }


    //turn a String into a pi set
    public static Set<String> turnintoset(String placement) {
        //4 characters represents a pi
        int numofp = placement.length() / 4;
        Set<String> placesp = new HashSet<>();
        String pi = "";
        int i = 0;
        while (placesp.size() < numofp) {
            pi += placement.substring(i, i + 4);
            placesp.add(pi);
            pi = "";
            i += 4;
        }

        return placesp;
    }


//    public static void main(String[] args) {
//        String placement = "c1A3d2A6e2C3f3C2g4A7h6D0j2B0j1C0k3C0l4B0l5C0";
//        String pi = one_help_piece(placement);
//        System.out.println(pi);
//    }



}





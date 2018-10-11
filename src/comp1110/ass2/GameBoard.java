package comp1110.ass2;

import java.util.ArrayList;
import java.util.List;

import static comp1110.ass2.Pieces.hm;

/*
 *Class which creates a set of boards and does operation over them whenever needed.
 * 1)checkingBoard - specially meant for isPlacementStringValid method.
 * 2)actualBoard- specially meant for the running game.
 *Authorship:LingYu Xia (rotator)
 *           Kalai (Everything else)
 */
public class GameBoard {
    private String[][] checkingBoard = new String[4][8];
    private String[][] actualBoard = new String[4][8];
    private String checkingBoardName;
    private String actualBoardName;


    /**
     * Adds a piece to the respective board and updates it.
     *
     * @param piece piece String
     * @param bt    represents board-type(either actual or checking)
     */
    void pieceTobeAdded(String piece, String bt) {
        int hashMapKey= getHashmapkey(piece);
        int col = Character.getNumericValue(piece.charAt(1)) - 1;
        int row = piece.charAt(2) - 65;
        if (bt.equals("a")) {
            this.actualBoard = placer(actualBoard, hm.get(hashMapKey), row, col );
        } else {
            this.checkingBoard = placer(checkingBoard, hm.get(hashMapKey), row, col);
        }

    }

   private int getHashmapkey(String piece){
        char pName = piece.charAt(0);
        int orientationNo = Character.getNumericValue(piece.charAt(3));
        int hashMapKey = (pName > 104) ? pName - 104 + 63 : (pName - 97) * 8 + orientationNo;//(pname - 97)*8 to get the corresponding piece base number
        //Strong symmetric pieces dont have redudndant piece orientations
        if((hashMapKey>19&& hashMapKey<24 )||(hashMapKey>59 && hashMapKey<64))
        {hashMapKey-=4;}

return hashMapKey;
    }

    //piece must be valid placement
    //only for checking board
    void removepiece(String piece,char bt){
        int row= piece.charAt(2) -65;
        int col= Integer.parseInt(piece.charAt(1)+"") -1 ;
        //String[][] board= (bt=='c')? checkingBoard:actualBoard;
     /*    int hashmapkey =getHashmapkey(piece);
         String[][] piecearr = hm.get(hashmapkey);*/
         for(;row<4;row++){
             for(int cc=col;cc<8;cc++){
                 if(checkingBoard[row][cc].contains("p")&&checkingBoard[row][cc].length()>2){
                     int piecestrsval=(checkingBoard[row][cc].length()==4)?2:3;
                     //always be in format of orpr as pr will be always on board itself
                     checkingBoard[row][cc]=checkingBoard[row][cc].substring(piecestrsval);
                 }
                 else if (!checkingBoard[row][cc].equals("x")){
                     checkingBoard[row][cc]="x";
                 }
             }
         }


    }

  /*  String[][] bruteremover(List indices,char bt ){


    }*/
    void updateCBoardName(String checkingBoardName){
        this.checkingBoardName=checkingBoardName;
    }

    void updateABoardName(String actualBoardName){
        this.actualBoardName=actualBoardName;
    }

    String getCBoardName(){
        return  checkingBoardName;
    }

    String getABoardName(){
        return actualBoardName;
    }

    /**
     * Obtains the actual board
     *
     * @return Multidimensional array representing the actualBoard
     */
    String[][] getaboard() {
        return actualBoard;
    }

    /**
     * Obtains the checkingboard
     *
     * @return Multidimensional array representing the checkingBoard
     */
    String[][] getcboard() {
        return checkingBoard;
    }


    /**
     * Resets the respective Board(s)
     *
     * @param s which could represent
     *          "a"-actual board
     *          "c"-checking board
     *          "ac"-both the boards
     */
    void resetBoardvalues(String s) { //TODO-Try to implement Map function using streams
        int row = 4, col = 8;
        String[][] board = new String[4][8];
        if (s.equals("ac")) {
            resetBoardvalues("a");
            resetBoardvalues("c");
        }
        for (int trow = 0; trow < row; trow++) {
            for (int tcol = 0; tcol < col; tcol++) {
                    board[trow][tcol] = "x";
                }
            }
            if(s.equals("c") ){
                this.checkingBoard=board;
            }
            else{
                this.actualBoard=board;
            }
        }




    /**
     * Places the piece on the board multidimensional array
     * It will return the non modified board if the (placing array)
     * piecearr is larger than the board or if the input values
     * aren't valid.
     *
     * @param board    Non-modified board
     * @param piecearr Multidimensional array of the piece
     * @param row2     the row on which the top-most piece resides
     * @param col2     the column in which the left-most piece resides
     * @return (Updated) board or null if invalid position
     */
    public static String[][] placer(String[][] board, String[][] piecearr, int row2, int col2) {
        if (row2 < 0 || col2 < 0||row2>=board.length||col2>=board[0].length) {
            return null;
        }
        try {
        int prow = piecearr.length;
        int pcol = piecearr[0].length;
        int endr = prow + row2;
        int endc = pcol + col2;
         for (int cr = 0; row2 < endr; row2++, cr++) {
                int col2_temp = col2;
                for (int cc = 0; col2_temp < endc; col2_temp++, cc++) {
                    if (board[row2 ][col2_temp ] .equals("x")) {
                        board[row2 ][col2_temp ] = piecearr[cr][cc];
                    } else if (!piecearr[cr][cc].equals("x")) {// if the piece part is empty don't update the output board
                        board[row2 ][col2_temp ] = piecearr[cr][cc] + board[row2 ][col2_temp];
                    }
                }
            }
        }
        //if piecearr size is more than than board or the board has null value(i.e Values of the board not declared)
        catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
        return board;
    }

    /**
     * flip the array stuffs (not considering the holes in
     * pieces for now) and hence cannot take in c or h pieces.
     *
     * @param actualpiece multidimensional array (Respective piece)
     * @return a Flipped array(in respective to X-axis)
     */
    public static String[][] flipper(String[][] actualpiece) {
        int row = actualpiece.length;
        int col = actualpiece[0].length;
        String result[][] = new String[row][col];
        for (int i = 0; i <= row / 2; i++) {
            for (int j = 0; j < col; j++) {
                result[i][j] = actualpiece[row - (i + 1)][j];
                result[row - (i + 1)][j] = actualpiece[i][j];
            }
        }
        return result;
    }


    /**
     * rotates the array clockwise(90 degree)
     *
     * @param actualpiece2 multidimensional array (Respective piece)
     * @return a Rotated array
     *  author: Lingyu Xia
     */
    public static String[][] rotator(String[][] actualpiece2) {
        String[][] rotated = new String[actualpiece2[0].length][actualpiece2.length];
        for (int i = 0; i < actualpiece2[0].length; ++i) {
            for (int j = 0; j < actualpiece2.length; ++j) {
                rotated[i][j] = actualpiece2[actualpiece2.length - j - 1][i];
            }
        }
        return rotated;
    }


}

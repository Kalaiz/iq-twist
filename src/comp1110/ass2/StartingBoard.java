package comp1110.ass2;

import java.util.*;
/*Class which initialises the starting boards
 Authorships:LingYu Xia
             Kalai(pegcreator)*/


public class StartingBoard extends TwistGame{

    public static int solNum;
    public static List<String> sol = new ArrayList<>();
    private static HashMap<String, List<String>> storage=new HashMap<>();
    private static String[] initiator = {"a6B0b6C0c5A2d1B5e4A5f4C2g2B5h1A2",
                                         "a1A6b1B1c2D0d3B6e7A3f7B1g5B7h4A0",
                                         "a7A7b3B1c1A0d5A3e1C2f1B0g6B7h4D0",
                                         "a1C6b6A4c2D0d7B1e1A3f2A2g4B2h4A0"};
    private static final int[] pegDetails={1, 2, 2, 2};//dtat storage for Limitation on the number of pegs
    /*
     * 3 difficulty levels:
     * 1: Place 6 or 7 pegs / 4 or 5 pieces on board which may lead to a single solution, because pegs limited the color of pieces and may have fixed solutions
     * 2: Place 5 or 6 pegs/ 3 or 4 pieces on board which may have more possibilities but lead to a single solution
     * 3: Place 3 or 4 pegs /2 or 3 pieces on board which have the most possibilities but lead to the single answer
     *
     *                                                pgnos,piecenos,pgnos,piecenos...*/
    private static final int[] difficultyLevelDetails={6,4,5,3,4,2};


    /**Gets all pegs where ever possible and returns a board State string filled with pegs*
     * @param solution - Complete solution string from task 9 codes
     */
    public static List<String> pegAdder(String solution){
        boardcreator(solution);
        String[][] board = gobj.getaboard();
        List<String> pegs = new ArrayList<>();
        int[]pegD =pegDetails.clone();
        for(int row=0;row<board.length;row++){
            for(int col =0;col<board[0].length;col++){
                if(board[row][col].startsWith("o")){
                    int pgno=0;
                    switch(board[row][col]){
                        case "or":
                            break;
                        case "ob":
                            pgno=1;
                            break;
                        case "og":
                            pgno=2;
                            break;
                        case "oy":
                            pgno=3;
                    }
                    if(pegD[pgno]>0){
                        pegs.add(((char)(105+pgno))+Integer.toString((col+1))+((char)(row+65))+"0");
                        --pegD[pgno];
                    }}
            }
        }
        storage.put(solution,pegs);
        return pegs;
    }

    /**Creates a boardString such that it consist of 2 pieces
     * Produces the key for the Hashmap - storage
     * author: Lingyu Xia*/
    public static String pieceCreator(){

        Random r = new Random();
        List<String> choPie = new ArrayList<>();
        List<String> choFro = new ArrayList<>();    //The pieces from which we may choose the next piece
        int ranSol = r.nextInt(4);
        String str = initiator[ranSol];
        choFro = getFormalPieces(str);
        Collections.shuffle(choFro);
        choPie.add(choFro.get(0));
        choPie.add(choFro.get(1));

        StringBuilder sb = new StringBuilder();
        for (Object c : choPie){
            sb.append(c);
        }
        String[] s = getSolutions(sb.toString());
        for (int i = 0; i < s.length; i++) {
            sol.add(s[i]);
        }

        return sb.toString();
    }


    /**gives the difficulty level and a random solution string, find the values of pegs in hashMap
    * author:Lingyu Xia*/
    public static String difficultyLevel(double level){
        Random r = new Random();
        int ndv=r.nextInt(2);//ndv-non-deterministic value
        String startPlacement = "";
        int pieceNum ;
        int pegNum ;
        solNum = r.nextInt(sol.size());
        List<String> pegs = pegAdder(sol.get(solNum));
        //get the pieces & pegs number by difficulty level
        pegNum = difficultyLevelDetails[(int)(2 * level)];
        pieceNum = difficultyLevelDetails[(int)(2 * level + 1)];
        List<String> pie = getFormalPieces(sol.get(solNum));
        Collections.shuffle(pie);
        Collections.shuffle(pegs);

        for (int i = 0; i < pieceNum+ndv; i++) {
            startPlacement += pie.get(i);
        }

        for (int j = 0; j < pegNum+ndv; j++) {
            startPlacement += pegs.get(j);
        }

        return startPlacement;

    }







}

package edu.up.cs301.blokus;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.blokus.actions.SelectBlokOnSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectPieceTemplateAction;
import edu.up.cs301.blokus.actions.SelectValidBlokOnBoardAction;
import edu.up.cs301.blokus.pieces.PieceF;
import edu.up.cs301.blokus.pieces.PieceI1;
import edu.up.cs301.blokus.pieces.PieceI2;
import edu.up.cs301.blokus.pieces.PieceI3;
import edu.up.cs301.blokus.pieces.PieceI4;
import edu.up.cs301.blokus.pieces.PieceI5;
import edu.up.cs301.blokus.pieces.PieceL4;
import edu.up.cs301.blokus.pieces.PieceL5;
import edu.up.cs301.blokus.pieces.PieceN;
import edu.up.cs301.blokus.pieces.PieceO4;
import edu.up.cs301.blokus.pieces.PieceP;
import edu.up.cs301.blokus.pieces.PieceT;
import edu.up.cs301.blokus.pieces.PieceT4;
import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.blokus.pieces.PieceU;
import edu.up.cs301.blokus.pieces.PieceV3;
import edu.up.cs301.blokus.pieces.PieceV5;
import edu.up.cs301.blokus.pieces.PieceW;
import edu.up.cs301.blokus.pieces.PieceX;
import edu.up.cs301.blokus.pieces.PieceY;
import edu.up.cs301.blokus.pieces.PieceZ4;
import edu.up.cs301.blokus.pieces.PieceZ5;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * This game state initializes the master copy
 * It has a constructor which is used to make a deep copy
 * the deep copy is utilized throughout the game
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class BlokusGameState extends GameState implements Serializable
{

    private static final long serialVersionUID = 492017010L;

    // values defining the colors of the bloks on the 22x22 data board
    public static final int BARRIER = -2;
    public static final int EMPTY_BLOK = -1;
    public static final int PLAYER1_BLOK = 0;
    public static final int PLAYER2_BLOK = 1;
    public static final int PLAYER3_BLOK = 2;
    public static final int PLAYER4_BLOK = 3;

    // (0-3) according to the current player turn
    // each player ID matches that player's enumerated color
    private int playerTurn;

    //contains the 22x22 array of board bloks with current pieces
    private Blok[][] boardState;

    // contains the 7x7 array to track the selected piece's orientation
    private Blok[][] piecePreview;

    // these 3 iVars store the info necessary to place a piece on the board
    private PieceTemplate selectedPiece;
    private Blok selectedBoardBlok;
    private int selectedPieceBlokId;

    // 4x21 array, stores each player's enumerated remaining pieces
    private int[][] playerPieces;

    // use for master copy
    public BlokusGameState()
    {
        //initialize variables
        this.playerTurn = 0;

        this.selectedPiece = null;
        this.selectedBoardBlok = null;
        this.selectedPieceBlokId = -1;

        boardState = new Blok[22][22];
        boardState = initializeBoardArray(boardState);

        piecePreview = new Blok[7][7];
        resetPreview();

        playerPieces = new int[4][21];
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 21; j++)
            {
                playerPieces[i][j] = j;
            }
        }
    }

    public BlokusGameState(BlokusGameState orig)
    {
        this.playerTurn = orig.getPlayerTurn();
        this.boardState = copyBlokArray(orig.getBoardState());
        this.piecePreview = copyBlokArray(orig.getPiecePreview());
        this.playerPieces = copyPlayerPieces(orig.getPlayerPieces());

        this.selectedPiece = copySelectedPiece(orig.getSelectedPiece());
        this.selectedBoardBlok = copyBlok(orig.getSelectedBoardBlok());
        this.selectedPieceBlokId = orig.getSelectedPieceBlokId();
    }

    /**
     * initializeArray (when completed) should initialize all
     * the bloks for the game logic to use, including the
     * piece placement and legal move algorithms

     * @param initArray a 22 by 22 blok array
     * @return a 2D array of bloks w/ color EMPTY_BLOK (-1), except for the outline bloks
     *          which have color BARRIER (-2), and the corners of board which each are
     *          set as an individual player's color
     */
    public Blok[][] initializeBoardArray(Blok[][] initArray)
    {
        for(int i=1;i<21;i++)
        {
            for(int j=1;j<21;j++)
            {
                initArray[i][j] = new Blok(i, j, EMPTY_BLOK, EMPTY_BLOK);
            }
        }

        for(int i=0;i<22;i++)
        {
            initArray[0][i] = new Blok(0,i,BARRIER, BARRIER);

            initArray[i][0] = new Blok(i,0,BARRIER, BARRIER);

            initArray[i][21] = new Blok(i,21,BARRIER, BARRIER);

            initArray[21][i] = new Blok(21,i,BARRIER, BARRIER);
        }

        // set the corners to each player's color
        initArray[0][0].setColor(PLAYER1_BLOK);
        initArray[0][21].setColor(PLAYER2_BLOK);
        initArray[21][21].setColor(PLAYER3_BLOK);
        initArray[21][0].setColor(PLAYER4_BLOK);

        return initArray;
    }

    /**
     * searches the current board state for any corners that
     * the current player could possibly place a piece off of
     * and stores it in the array list
     *
     * @return a reference to the GameState's ArrayList<Blok> of valid corners
     */
    public ArrayList<Blok> getValidCorners(int playerId) {
        // stores the bloks on the board where current player can place a piece
        ArrayList<Blok> validCorners = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            for (int j = 1; j < 21; j++)
            {
                Blok b = boardState[i][j];
                if (b.getColor() == playerId)
                    checkSingleBlokCorners(playerId, b, validCorners);
            }
        }

        checkBoardCorners(validCorners);
        return validCorners;
    }

    /**
     * Check if any of the bloks at the corners of the given blok
     * are empty spaces, and if they are not adjacent to any bloks
     * of the same color as the current player. If those conditions
     * are met, add that corner blok to the validCorners list
     *
     * @param blok the blok on the board whose corners are to be checked
     */
    private void checkSingleBlokCorners(int playerID, Blok blok, ArrayList<Blok> validCorners)
    {
        int row = blok.getRow();
        int col = blok.getColumn();

        int rMod = 0;
        int cMod = 0;

        for (int k = 0; k < 4; k++) {
            switch (k) {
                case 0:
                    rMod = 1;
                    cMod = 1;
                    break;
                case 1:
                    rMod = -1;
                    cMod = 1;
                    break;
                case 2:
                    rMod = 1;
                    cMod = -1;
                    break;
                case 3:
                    rMod = -1;
                    cMod = -1;
                    break;
            }

            Blok checkBlok = boardState[row + rMod][col + cMod];

            /*
                add the checkBlok to the ArrayList of valid corners if:
                - the blok is not already in the array
                - the blok is an unnoccupied space
                - the blok is not adjacent to any bloks of your own color
             */
            if (!validCorners.contains(checkBlok))
            {
                if (checkBlok.getColor() == EMPTY_BLOK &&
                        checkAdjacentBloksOnBoard(playerID, checkBlok, boardState))
                {
                    validCorners.add(checkBlok);
                }
            }
        }
    }

    /**
     * add a corner of the board to the valid corners array if
     * it is valid for the current player
     *
     * @param validCorners the arraylist of Bloks that are on corners
     */
    private void checkBoardCorners(ArrayList<Blok> validCorners)
    {
        if (boardState[0][0].getColor() == playerTurn &&
                boardState[1][1].getColor() == EMPTY_BLOK)
            validCorners.add(boardState[1][1]);

        if (boardState[0][21].getColor() == playerTurn &&
                boardState[1][20].getColor() == EMPTY_BLOK)
            validCorners.add(boardState[1][20]);

        if (boardState[21][0].getColor() == playerTurn &&
                boardState[20][1].getColor() == EMPTY_BLOK)
            validCorners.add(boardState[20][1]);

        if (boardState[21][21].getColor() == playerTurn &&
                boardState[20][20].getColor() == EMPTY_BLOK)
            validCorners.add(boardState[20][20]);
    }

    /**
     * return an array list of the bloks on the given board to be colored
     * if the piece placement is valid, else return null
     *
     * @param selectedBoardBlok the chosen blok on the given board
     * @param selectedPiece the piece desired to place on the given board
     * @param selectedPieceBlokId the blok on the piece to overlap the chosen board blok
     * @param board the board to test the placement on
     * @return a list of bloks to color, or null if placement is invalid
     */
    public ArrayList<PieceBlok> prepareValidMove(int playerNum, Blok selectedBoardBlok,
                              PieceTemplate selectedPiece,
                              int selectedPieceBlokId,
                              Blok[][] board)
    {
        if (selectedBoardBlok == null ||
                selectedPiece == null ||
                selectedPieceBlokId == -1)
            return null;


        PieceBlok selectedPieceBlok = selectedPiece.getPieceShape()[selectedPieceBlokId];

        selectedPieceBlok.setRow(selectedBoardBlok.getRow());
        selectedPieceBlok.setCol(selectedBoardBlok.getColumn());

        ArrayList<PieceBlok> bloksToColor = new ArrayList<>();
        ArrayList<PieceBlok> temp = new ArrayList<>();

        bloksToColor.add(selectedPieceBlok);
        temp.addAll(bloksToColor);

        /*
            iterate over the the PieceBloks that define the shape of the selected
            piece, setting their row and column values to correspond with their
            future placement on the given board, or return null if ever a
            PieceBlok will be placed invalidly.
         */
        for (int i = 0; i < selectedPiece.getPieceShape().length; i++)
        {
            for (PieceBlok pb : bloksToColor)
            {
                if (board[pb.getRow()][pb.getColumn()].getColor() != EMPTY_BLOK)
                    return null;

                setBlokPlacement(temp, selectedPiece.getPieceShape(),
                        pb, pb.getAdjacencies());

                /*
                    check if any pieces bloks being planned to place
                    will be adjacent to already placed bloks of the same
                    color, in which case the placement is invalid
                */
                if (!checkAdjacentBloksOnBoard(playerNum, pb, board))
                    return null;
            }

            bloksToColor.addAll(temp);
        }

        return bloksToColor;
    }

    /**
     * method used to place a piece on the board
     */
    private void setBlokPlacement(ArrayList<PieceBlok> bloksToColor,
                                     PieceBlok[] pieceShape,
                                     PieceBlok curPieceBlok, int[] curAdj)
    {
        int id, rowMod, colMod;

        for (int i = 0; i < curAdj.length; i++)
        {
            id = curAdj[i];

            switch (i)
            {
                case 0: // left adjacency
                    rowMod = 0;
                    colMod = -1;
                    break;
                case 1: // top adjacency
                    rowMod = -1;
                    colMod = 0;
                    break;
                case 2: // right adjacency
                    rowMod = 0;
                    colMod = 1;
                    break;
                case 3: // bottom adjacency
                    rowMod = 1;
                    colMod = 0;
                    break;
                default: // default should never occur
                    rowMod = 0;
                    colMod = 0;
                    break;
            }

            if (id != PieceTemplate.NO_ADJ  && !bloksToColor.contains(pieceShape[id]))
            {
                pieceShape[id].setRow(curPieceBlok.getRow() + rowMod);
                pieceShape[id].setCol(curPieceBlok.getColumn() + colMod);

                bloksToColor.add(pieceShape[id]);
            }
        }
    }

    /**
     * Given a blok with coordinates set relative to the given board
     * (but not placed on the board), check if its coordinate locations
     * will place it adjacent to another blok of the current player's color.
     *
     * @param blok check this blok's adjacent bloks on the given board
     * @param board the board which the given blok is tested on
     * @return false if same color is adjacent to the blok, otherwise true
     */
    private boolean checkAdjacentBloksOnBoard(int playerID, Blok blok, Blok[][] board)
    {
        int row = blok.getRow();
        int col = blok.getColumn();

        int rMod = 0;
        int cMod = 0;

        for (int i = 0; i < 4; i++)
        {
            switch (i)
            {
                case 0:
                    rMod = -1;
                    cMod = 0;
                    break;
                case 1:
                    rMod = 0;
                    cMod = -1;
                    break;
                case 2:
                    rMod = 1;
                    cMod = 0;
                    break;
                case 3:
                    rMod = 0;
                    cMod = 1;
                    break;
            }

            if (board[row + rMod][col + cMod].getColor() == playerID)
                return false;
        }

        return true;
    }

    /**
     * This method is intended to be called using the returned result of the
     * prepareValidMove method. The piece stored in pieceBloks is
     * painted to the given board
     *
     * @param pieceBloks the ArrayList result given by the prepareValidMove method
     * @param targetBoard the board to paint the piece on
     */
    public void placePiece(ArrayList<PieceBlok> pieceBloks, Blok[][] targetBoard)
    {
        for (PieceBlok pb : pieceBloks)
        {
            int row = pb.getRow();
            int col = pb.getColumn();

            targetBoard[row][col].setColor(playerTurn);
            targetBoard[row][col].setId(pb.getId());
        }
    }

    /**
     * attempt to place the given piece on the board
     * at given location in given orientation
     *
     * @return true if the piece was placed, false if the placement is invalid
     */
    public boolean confirmPiecePlacement()
    {
        if (selectedPiece == null ||
                selectedPieceBlokId == -1 ||
                selectedBoardBlok == null)
        {
            return false;
        }

        ArrayList<PieceBlok> pieceToPlace = prepareValidMove(playerTurn, selectedBoardBlok,
                selectedPiece, selectedPieceBlokId, boardState);
        if (pieceToPlace != null) // if true, piece can be placed validly
        {
            placePiece(pieceToPlace, boardState);

            // "remove" the placed piece for the current player, so they can't replay it
            playerPieces[playerTurn][selectedPiece.getPieceId()] = -1;

            // reset all GameState values to prepare for the next player's turn
            selectedBoardBlok = null;
            selectedPiece = null;
            selectedPieceBlokId = -1;
            resetPreview();

            return true;
        }
        else
        {
            return false; // invalid move, given piece can't be placed under given conditions
        }
    }

    public void flipSelectedPiece()
    {
        if (selectedPiece != null)
        {
            selectedPiece.flip();
            updatePreview();
        }
    }

    public void rotateSelectedPiece()
    {
        if (selectedPiece != null)
        {
            selectedPiece.rotate();
            updatePreview();
        }
    }

    public void selectValidBlokOnBoard(SelectValidBlokOnBoardAction selectValidBlokAction)
    {
        int row = selectValidBlokAction.getSelectedBlok().getRow();
        int column = selectValidBlokAction.getSelectedBlok().getColumn();
        this.selectedBoardBlok = boardState[row][column];
    }

    public void selectBlokOnSelectedPiece(SelectBlokOnSelectedPieceAction selectPieceBlokAction)
    {
        this.selectedPieceBlokId = selectPieceBlokAction.getSelectedBlokId();
    }

    public void selectPieceTemplate(SelectPieceTemplateAction selectPieceAction)
    {
        int selectedPieceID = selectPieceAction.getSelectedPieceID();
        this.selectedPieceBlokId = -1;
        this.selectedPiece = getPieceFromID(selectedPieceID);
        updatePreview();
    }

    /**
     * store the selected piece on the preview board
     */
    private void updatePreview()
    {
        resetPreview();
        if (selectedPiece != null)
        {
            ArrayList<PieceBlok> pieceToPlace =
                    prepareValidMove(playerTurn, piecePreview[3][3], selectedPiece,
                    selectedPiece.getAnchor().getId(), piecePreview);
            placePiece(pieceToPlace, piecePreview);
        }
    }

    /**
     * set all bloks on the preview board to be empty bloks,
     * except the border bloks, which are set as barrier bloks
     */
    private void resetPreview()
    {
        int size = piecePreview.length;

        for (int i = 1; i < size-1; i++)
        {
            for (int j = 1; j < size-1; j++)
            {
                piecePreview[i][j] = new Blok(i, j, EMPTY_BLOK, EMPTY_BLOK);
            }
        }

        for (int i = 0; i < size; i++)
        {
            piecePreview[0][i] = new Blok(0, i, BARRIER, BARRIER);
            piecePreview[size-1][i] = new Blok(size-1, i, BARRIER, BARRIER);
            piecePreview[i][0] = new Blok(i, 0, BARRIER, BARRIER);
            piecePreview[i][size-1] = new Blok(i, size-1, BARRIER, BARRIER);
        }
    }

    public int getPlayerTurn()
    {
        return playerTurn;
    }

    public void setPlayerTurn(int newPlayerId)
    {
        this.playerTurn = newPlayerId;
    }

    public Blok[][] getBoardState()
    {
        return boardState;
    }

    public Blok[][] getPiecePreview()
    {
        return piecePreview;
    }

    public PieceTemplate getSelectedPiece()
    {
        return this.selectedPiece;
    }

    public Blok getSelectedBoardBlok()
    {
        return this.selectedBoardBlok;
    }

    public int getSelectedPieceBlokId()
    {
        return this.selectedPieceBlokId;
    }

    public int[][] getPlayerPieces()
    {
        return this.playerPieces;
    }

    /**
     * Creates a deep copy for the board state
     * @param origBoard the source board
     * @return deep copy of origBoard
     */
    public Blok[][] copyBlokArray(Blok[][] origBoard)
    {
        int boardSize = origBoard.length;

        Blok[][] copyBoard = new Blok[boardSize][boardSize];

        for (int row = 0; row < boardSize; row++)
        {
            for (int col = 0; col < boardSize; col++)
            {
                copyBoard[row][col] = copyBlok(origBoard[row][col]);
            }
        }

        return copyBoard;
    }

    /**
     * Creates a deep copy for individual Bloks
     * @param origBlok the source Blok
     * @return a deep copy of the source Blok
     */
    private Blok copyBlok(Blok origBlok)
    {
        if (origBlok == null)
        {
            return null;
        }
        int row = origBlok.getRow();
        int col = origBlok.getColumn();
        int color = origBlok.getColor();
        int id = origBlok.getId();

        return (new Blok(row, col, color, id));
    }

    /**
     * Creates a deep copy of the selected piece
     * @param piece the source PieceTemplate
     * @return a deep copy of the source PieceTemplate
     */
    private PieceTemplate copySelectedPiece(PieceTemplate piece)
    {
        if(piece == null)
        {
            return null;
        }

        PieceTemplate temp = getPieceFromID(piece.getPieceId());
        PieceBlok[] copyShape = temp.getPieceShape();
        PieceBlok[] origShape = piece.getPieceShape();

        for (int i = 0; i < origShape.length; i++)
        {
            copyShape[i].setRow(origShape[i].getRow());
            copyShape[i].setCol(origShape[i].getColumn());
            copyShape[i].setColor(origShape[i].getColor());
            copyShape[i].setId(origShape[i].getId());

            int[] copyAdj = copyShape[i].getAdjacencies();
            int[] origAdj = origShape[i].getAdjacencies();

            System.arraycopy(origAdj, 0, copyAdj, 0, origAdj.length);

            copyShape[i].setAdjacencies(copyAdj);
        }

        temp.setPieceShape(copyShape);
        return temp;
    }

    private int[][] copyPlayerPieces(int[][] origPieces)
    {
        int[][] copyPieces = new int[4][21];

        for (int i = 0; i < 4; i++)
        {
            System.arraycopy(origPieces[i], 0, copyPieces[i], 0, origPieces[i].length);
        }

        return copyPieces;
    }

    /**
     * Create a new piece indicated by pieceId
     *
     * @param pieceId the enumeration of the desired piece
     * @return a PieceTemplate child with the given pieceId
     */
    public PieceTemplate getPieceFromID(int pieceId)
    {
        PieceTemplate temp;

        switch (pieceId)
        {
            case 0:
                temp = new PieceI1();
                break;
            case 1:
                temp = new PieceI2();
                break;
            case 2:
                temp = new PieceV3();
                break;
            case 3:
                temp = new PieceI3();
                break;
            case 4:
                temp = new PieceO4();
                break;
            case 5:
                temp = new PieceT4();
                break;
            case 6:
                temp = new PieceI4();
                break;
            case 7:
                temp = new PieceL4();
                break;
            case 8:
                temp = new PieceZ4();
                break;
            case 9:
                temp = new PieceL5();
                break;
            case 10:
                temp = new PieceT();
                break;
            case 11:
                temp = new PieceY();
                break;
            case 12:
                temp = new PieceX();
                break;
            case 13:
                temp = new PieceU();
                break;
            case 14:
                temp = new PieceW();
                break;
            case 15:
                temp = new PieceP();
                break;
            case 16:
                temp = new PieceI5();
                break;
            case 17:
                temp = new PieceZ5();
                break;
            case 18:
                temp = new PieceN();
                break;
            case 19:
                temp = new PieceV5();
                break;
            case 20:
                temp = new PieceF();
                break;
            default:
                temp = null;
                break;
        }
        return temp;
    }

    public void setPlayerPieces(int[][] playerPieces)
    {
        this.playerPieces = playerPieces;
    }

    /**
     * report if the given player has any moves available
     * @param playerID the player
     * @return
     *      true if the player has at least one available move,
     *      false if the player has no available moves
     */
    public boolean playerCanMove(int playerID)
    {
        ArrayList<PieceTemplate> playablePieces = new ArrayList<>();
        int[] currentPieces = playerPieces[playerID];

        PieceTemplate tempPiece;

        for (int i = 0; i < 21; i++)
        {
            if (currentPieces[i] != -1)
            {
                tempPiece = getPieceFromID(currentPieces[i]);
                playablePieces.add(tempPiece);
            }
        }

        //get arrayList for specific playerId
        int pieceBlokID;
        PieceBlok[] curPieceShape;
        Blok curBoardBlok;

        ArrayList<Blok> validCorners = getValidCorners(playerID);

        //Iterate over avaliable corners
        if (validCorners != null && !validCorners.isEmpty())
        {
            for (int i = 0; i < validCorners.size(); i++) // for each valid corner blok on the board
            {
                curBoardBlok = validCorners.get(i);
                for (int j = 0; j < playablePieces.size(); j++) // for each piece the player has
                {
                    PieceTemplate ap = playablePieces.get(j);
                    curPieceShape = ap.getPieceShape();

                    for (int k = 0; k < curPieceShape.length; k++) // check each blok on piece
                    {
                        pieceBlokID = curPieceShape[k].getId();

                        // tests placing the piece, testing all rotated and flipped orientations as well
                        if (testPiecePlacement(playerID, curBoardBlok, ap, pieceBlokID, boardState))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public int[] getReducedPlayablePieces(int playerNum, int[] availablePieces, ArrayList<Blok> validCorners)
    {
        int[] reducedPieces = new int[21];
        System.arraycopy(availablePieces, 0, reducedPieces, 0, availablePieces.length);

        //Iterate over avaliable corners
        if (validCorners != null && !validCorners.isEmpty())
        {
            for (int j = 0; j < reducedPieces.length; j++) // for each piece the player has
            {
                if (reducedPieces[j] != -1)
                {
                    // if the piece is unplayable on the current boardstate
                    if (pieceIsUnplayable(playerNum, validCorners, j))
                    {
                        reducedPieces[j] = -1; // set it as unplayable
                    }
                }
            }
        }

        return reducedPieces;
    }

    private boolean pieceIsUnplayable(int playerNum, ArrayList<Blok> validCorners, int pieceID)
    {
        Blok curBoardBlok;
        PieceTemplate testPiece;
        PieceBlok[] curPieceShape;
        int pieceBlokID;

        for (int i = 0; i < validCorners.size(); i++) // for each valid corner blok on the board
        {
            curBoardBlok = validCorners.get(i);
            testPiece = getPieceFromID(pieceID);
            curPieceShape = testPiece.getPieceShape();

            for (int k = 0; k < curPieceShape.length; k++) // check each blok on piece
            {
                pieceBlokID = curPieceShape[k].getId();

                // tests placing the piece, testing all rotated and flipped orientations as well
                if (testPiecePlacement(playerNum, curBoardBlok,
                        testPiece, pieceBlokID, boardState))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * check all orientations of given piece with given blok on given board
     *
     * @param selectedBoardBlok reference point on given board
     * @param selectedPiece piece to test placement
     * @param pieceBlokId used to get the blok on the piece to overlap with the selected board blok
     * @param board the board to test piece placement on
     * @return
     *      false if the given piece cannot be placed with the given conditions
     *      in any orientation
     */
    public boolean testPiecePlacement(int playerNum, Blok selectedBoardBlok,
                                       PieceTemplate selectedPiece,
                                       int pieceBlokId,
                                       Blok[][] board)
    {
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 4; j++) {
                if (prepareValidMove(playerNum,
                        selectedBoardBlok,
                        selectedPiece,
                        pieceBlokId,
                        board) != null) {
                    return true;
                }
                selectedPiece.rotate();
            }
            selectedPiece.flip();
        } // piece returns to initial orientation after exiting this loop

        return false;
    }

    public void changeToNextPlayer()
    {
        playerTurn++;
        if (playerTurn == 4)
            playerTurn = 0;
    }
}


package edu.up.cs301.blokus;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.blokus.actions.ConfirmPiecePlacementAction;
import edu.up.cs301.blokus.actions.DoNothingAction;
import edu.up.cs301.blokus.actions.FlipSelectedPieceAction;
import edu.up.cs301.blokus.actions.RotateSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectBlokOnSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectPieceTemplateAction;
import edu.up.cs301.blokus.actions.SelectValidBlokOnBoardAction;
import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * @author Cole French
 * @author Evan Sterba
 */
public class BlokusSimpleComputerPlayer extends GameComputerPlayer {

    interface AIState {
        AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state);
    }

    private BlokusGameState gameState; //the received GameState from the LocalGame
    private GameAction curAction; //the current action that the AI will send to the LocalGame
    private AIState curState; //the current state in the state machine that the AI is executing
    public int rotateTracker; //tracks how much the selected piece has been reoriented
    public int pieceBlokTracker; //tracks which PieceBloks have been attempted on the selected piece
    public int[] playablePieces; //the piece IDs that the AI has not tried to place yet
    private ArrayList<Blok> playableBoardBloks; //the board spaces that the AI has not tried yet
    private boolean firstActionOfTurn; //used to reduce computations on AI's info received

    static Random r; //used to get random pieces, and board spaces

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public BlokusSimpleComputerPlayer(String name) {
        super(name);
        curState = AIStates.SetupStartOfTurn;
        r = new Random();

        this.playableBoardBloks = new ArrayList<>();

        this.pieceBlokTracker = 0;
        this.rotateTracker = 0;
        this.firstActionOfTurn = true;

        this.playablePieces = new int[21];
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof BlokusGameState)
        {
            this.gameState = (BlokusGameState)info;

            if (gameState.getPlayerTurn() == this.playerNum)
            {
                // if the current player has no available moves, skip his turn
                if (firstActionOfTurn && !gameState.playerCanMove(this.playerNum))
                {
                    game.sendAction(new DoNothingAction(this, true));
                }
                else
                {
                    sleep(50);
                    decideActionByState();
                    game.sendAction(curAction);
                }
            }
        }
    }

    /**
     * External Citation
     * Date: 4/30/2017
     * Problem: wanted better way to organize AI order of operations
     *
     * Resource:
     *      https://github.com/Team-Pronto-3070/Competition2015/blob/master/
     *      src/org/usfirst/frc/team3070/robot/ProntoLoader.java
     *
     * Solution: I used the code structure from my old robotics team to organize the states
     */
    /**
     * The state machine to control what the AI is attempting to do
     * at any moment in its turn
     */
    enum AIStates implements AIState {
        /**
         * reset AI's ivars on turn start; this state is only
         * executed once per turn
         */
        SetupStartOfTurn {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                AI.setAction(new DoNothingAction(AI, false));
                int[] currentPieces = state.getPlayerPieces()[AI.playerNum];
                AI.resetPlayablePieces(
                        state.getReducedPlayablePieces(AI.playerNum,
                                currentPieces, state.getValidCorners(AI.playerNum)));
                AI.rotateTracker = 0;
                AI.pieceBlokTracker = 0;
                AI.firstActionOfTurn = false;

                return SelectPiece;
            }
        },
        /**
         * randomly select a piece; the Simple AI will try placing one
         * piece at every valid corner until it has tried that piece
         * at every corner, at which point it will repeat the process
         * with a new piece chosen randomly. It will never select
         * the same piece twice in the same turn though.
         */
        SelectPiece {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                // reset valid corners
                AI.setPlayableCorners(state.getValidCorners(AI.playerNum));

                int[] AIPieces = AI.getPlayablePieces();
                int pieceIndex, pieceID;

                //find piece to play
                do {
                    //gets random number between 0 & 20
                    pieceIndex = r.nextInt(21);
                    pieceID = AIPieces[pieceIndex];
                } while (pieceID == -1);

                SelectPieceTemplateAction spta =
                        new SelectPieceTemplateAction(AI, pieceID);

                AI.setAction(spta);
                return SelectBoardBlok;
            }
        },
        /**
         * randomly select a valid blok on the board. Every time
         * all orientations of the selected piece have been attempted
         * and the piece still won't place, it selects a new
         * spot on the board. It will never select the same spot on the board
         * twice with the same piece selected.
         */
        SelectBoardBlok {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                if (AI.getPlayableCorners().isEmpty())
                {
                    AI.setAction(new DoNothingAction(AI, false));
                    AI.rotateTracker = 0;
                    AI.pieceBlokTracker = 0;
                    AI.setPieceUnplayable(state.getSelectedPiece().getPieceId());
                    return SelectPiece;
                }

                //gets arrayList of valid moves
                ArrayList<Blok> validBloks = AI.getPlayableCorners();

                Blok tempBlok = validBloks.get(r.nextInt(validBloks.size()));

                Blok selectedBoardBlok = state.getBoardState()[tempBlok.getRow()][tempBlok.getColumn()];

                SelectValidBlokOnBoardAction svboba =
                        new SelectValidBlokOnBoardAction(AI, selectedBoardBlok);

                AI.setAction(svboba);
                return SelectPieceBlok;
            }
        },
        /**
         * iterate through the selected piece's PieceBloks, trying to place
         * the piece with that PieceBlok selected at the given board space
         * in every possible orientation. If all PieceBloks have been attempted
         * at the current board space, select a new board space and repeat
         */
        SelectPieceBlok {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                PieceTemplate selectedPiece = state.getSelectedPiece();

                while (AI.pieceBlokTracker < selectedPiece.getPieceShape().length &&
                        !selectedPiece.getPieceShape()[AI.pieceBlokTracker].hasCorner())
                {
                    AI.pieceBlokTracker++;
                }

                if (AI.pieceBlokTracker == selectedPiece.getPieceShape().length)
                {
                    AI.setAction(new DoNothingAction(AI, false));
                    AI.pieceBlokTracker = 0;
                    AI.rotateTracker = 0;
                    AI.removePlayableBlok(state.getSelectedBoardBlok());
                    return SelectBoardBlok;
                }

                int selectedBlokID = AI.pieceBlokTracker;

                SelectBlokOnSelectedPieceAction sbospa =
                        new SelectBlokOnSelectedPieceAction(AI, selectedBlokID);

                AI.setAction(sbospa);
                return ConfirmPlacement;
            }
        },
        /**
         * rotates the selected piece 4 times, then flips it, then
         * rotates another 4 times. If the piece hasn't been placed
         * after that, select a new PieceBlok for the current piece
         * at the current board space
         */
        Rotate {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                GameAction tempAction = new DoNothingAction(AI, false);
                switch (AI.rotateTracker)
                {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        tempAction = new RotateSelectedPieceAction(AI);
                        break;
                    case 4:
                        tempAction = new FlipSelectedPieceAction(AI);
                        break;
                }

                AI.setAction(tempAction);
                AI.rotateTracker++;

                if (AI.rotateTracker == 9)
                {
                    AI.rotateTracker = 0;
                    AI.pieceBlokTracker++;

                    return SelectPieceBlok;
                }

                return ConfirmPlacement;
            }
        },
        /**
         * attempt to place the current piece at the current board space.
         * If successful, reset to prepare for whenever the next turn is.
         * If unsuccessful, reorient the current piece and try again.
         */
        ConfirmPlacement {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                Blok selectedBoardBlok = state.getSelectedBoardBlok();
                PieceTemplate selectedPiece = state.getSelectedPiece();
                int selectedPieceBlokId = state.getSelectedPieceBlokId();
                Blok[][] board = state.getBoardState();

                if (state.prepareValidMove(AI.playerNum, selectedBoardBlok, // if invalid move
                        selectedPiece,
                        selectedPieceBlokId,
                        board) == null)
                {
                    AI.setAction(new DoNothingAction(AI, false));

                    return Rotate;
                }
                else
                {
                    AI.setAction(new ConfirmPiecePlacementAction(AI));
                    AI.firstActionOfTurn = true;
                    return SetupStartOfTurn;
                }
            }
        }
    }

    private void decideActionByState()
    {
        curState = curState.checkState(this, gameState);
    }

    public void setAction(GameAction action)
    {
        this.curAction = action;
    }

    public int[] getPlayablePieces()
    {
        return this.playablePieces;
    }

    public void setPieceUnplayable(int pieceID)
    {
        this.playablePieces[pieceID] = -1;
    }

    public void resetPlayablePieces(int[] playerCurrentPieces)
    {
        System.arraycopy(playerCurrentPieces, 0, this.playablePieces,
                0, playerCurrentPieces.length);
    }

    public ArrayList<Blok> getPlayableCorners()
    {
        return this.playableBoardBloks;
    }

    public void setPlayableCorners(ArrayList<Blok> validCorners)
    {
        this.playableBoardBloks = validCorners;
    }

    /**
     * remove the given blok from pool of valid corners.
     * it needs to be removed this way b/c the Bloks in
     * the playableBoardBloks ArrayList are not from the
     * same GameStates, so the ArrayList will never contain
     * the reference to the given unplayable Blok
     *
     * @param unplayableBlok
     *      the Blok corresponding to the one stored in the
     *      ArrayList of valid corners, which both share
     *      the same row,column coordinates
     */
    public void removePlayableBlok(Blok unplayableBlok)
    {
        int row = unplayableBlok.getRow();
        int column = unplayableBlok.getColumn();

        int i; // i is used outside the for loop as well
        for (i = 0; i < playableBoardBloks.size(); i++)
        {
            Blok tempBlok = playableBoardBloks.get(i);

            if (tempBlok.getRow() == row && tempBlok.getColumn() == column)
                break;
        }

        this.playableBoardBloks.remove(i);
    }
}

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
 * The Blokus Advanced Computer Player is more advanced than the
 * simple computer player because it tries to play the largest
 * pieces first so that it can take over more territory
 * and be able to place more pieces in the later game.
 * Pieces are placed at randomly selected valid corners on the
 * board. The AI will place its selected piece at the first valid
 * move it can find, and will keep attempting to place pieces
 * until it finds a valid move.
 *
 * @author Cole French
 * @author Evan Sterba
 */
public class BlokusAdvancedComputerPlayer extends GameComputerPlayer{

    interface AIState {
        AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state);
    }

    private BlokusGameState gameState; //the received GameState from the LocalGame
    private GameAction curAction; //the current action that the AI will send to the LocalGame
    private AIState curState; //the current state in the state machine that the AI is executing
    public int rotateTracker; //tracks how much the selected piece has been reoriented
    public int pieceBlokTracker; //tracks which PieceBloks have been attempted on the selected piece
    public int[] playablePieces; //the piece IDs that the AI has not tried to place yet
    private ArrayList<Blok> playableBoardBloks; //the board spaces that the AI has not tried yet
    public boolean firstActionOfTurn; //used to reduce computations on AI's info received

    static Random r; //used to get random board spaces

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public BlokusAdvancedComputerPlayer(String name) {
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
        /*
        on start of turn all ints are set to their initial values
         */
        SetupStartOfTurn {
            @Override
            public AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state)
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
        /*
        in select piece the advanced AI cycles through avaliable pieces, selecting the largest one,
        it returns to this step if the piece it originally chooses doesn't fit anywhere
        if no pieces can be placed or no the ai has no pieces remaining it's turn is over
         */
        SelectPiece {
            @Override
            public AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state)
            {
                AI.setPlayableCorners(state.getValidCorners(AI.playerNum));

                int[] AIPieces = AI.getPlayablePieces();
                int pieceID = -1;

                for (int i = 20; i >= 0; i--)
                {
                    pieceID = AIPieces[i];

                    if (pieceID != -1)
                    {
                        break;
                    }
                }

                SelectPieceTemplateAction spta =
                        new SelectPieceTemplateAction(AI, pieceID);
                AI.setAction(spta);

                return SelectBoardBlok;
            }
        },
        /*
        Selects a blok on the board from the avaliable corners, if the selected piece doesn't
        fit at any of the spaces on the board it selects a new piece
         */
        SelectBoardBlok {
            @Override
            public AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state)
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
        /*
        runs through all the blok on the piece at the selected corners and sends information
        to the confirm action to be checked
         */
        SelectPieceBlok {
            @Override
            public AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state)
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
        /*
        rotates and flips the piece so all orientations are tried
         */
        Rotate {
            @Override
            public AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state)
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
                else
                    return ConfirmPlacement;
            }
        },
        /*
        confirm placement checks if the moves are valid, if a move is found to be valid
        that move is played and the ai returns to the turn setup step
         */
        ConfirmPlacement {
            @Override
            public AIState checkState(BlokusAdvancedComputerPlayer AI, BlokusGameState state)
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

    public void resetPlayablePieces(int[] playerPieces)
    {
        System.arraycopy(playerPieces, 0, this.playablePieces, 0, playerPieces.length);
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
     * remove playable is used to remove bloks that have been
     * tried so that we can know when a piece
     * has been tried at every valid space on the board
     * @param unplayableBlok the corresponding Blok to remove from the ArrayList
     */
    public void removePlayableBlok(Blok unplayableBlok)
    {
        int row = unplayableBlok.getRow();
        int column = unplayableBlok.getColumn();

        int i;

        for (i = 0; i < playableBoardBloks.size(); i++)
        {
            Blok tempBlok = playableBoardBloks.get(i);

            if (tempBlok.getRow() == row && tempBlok.getColumn() == column)
                break;
        }

        this.playableBoardBloks.remove(i);
    }
}

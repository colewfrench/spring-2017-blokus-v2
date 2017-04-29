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

    private BlokusGameState gameState;
    private GameAction curAction;
    private AIState curState;
    public int rotateTracker;
    public int pieceBlokTracker;
    public int[] playablePieces;
    private ArrayList<Blok> playableBoardBloks;

    static Random r;
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
                if (!gameState.playerCanMove(this.playerNum))
                {
                    game.sendAction(new DoNothingAction(this, true));
                }
                else
                {
                    sleep(45);
                    decideActionByState();
                    game.sendAction(curAction);
                }
            }
        }
    }

    enum AIStates implements AIState {
        SetupStartOfTurn {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                AI.setAction(new DoNothingAction(AI, false));
                AI.resetPlayablePieces(state.getPlayerPieces()[AI.playerNum]);
                AI.rotateTracker = 0;
                AI.pieceBlokTracker = 0;

                return SelectPiece;
            }
        },
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
        SelectPieceBlok {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                PieceTemplate selectedPiece = state.getSelectedPiece();

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
                        tempAction = new RotateSelectedPieceAction(AI);
                        break;
                    case 4:
                        tempAction = new FlipSelectedPieceAction(AI);
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        tempAction = new RotateSelectedPieceAction(AI);
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
        ConfirmPlacement {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                Blok selectedBoardBlok = state.getSelectedBoardBlok();
                PieceTemplate selectedPiece = state.getSelectedPiece();
                int selectedPieceBlokId = state.getSelectedPieceBlokId();
                Blok[][] board = state.getBoardState();

                if (state.prepareValidMove(selectedBoardBlok, // if invalid move
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

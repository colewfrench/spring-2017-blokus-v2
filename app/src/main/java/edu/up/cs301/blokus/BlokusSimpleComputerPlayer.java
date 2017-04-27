package edu.up.cs301.blokus;

import java.util.ArrayList;
import java.util.Hashtable;
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
 * Created by frenchco19 on 4/22/2017.
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
    public int[] unplayablePieces;
    private ArrayList<Blok> unplayableBoardBloks;

    static Random r;
    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public BlokusSimpleComputerPlayer(String name) {
        super(name);
        curState = AIStates.SelectBoardBlok;
        r = new Random();

        this.unplayableBoardBloks = new ArrayList<>();

        this.pieceBlokTracker = 0;
        this.rotateTracker = 0;

        this.unplayablePieces = new int[21];
    }

    // TODO change state to try one piece at every boardBlok
    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof BlokusGameState)
        {
            this.gameState = (BlokusGameState)info;

            // if the current player has no available moves, skip his turn
            if (!gameState.playerCanMove(this.playerNum))
            {
                game.sendAction(new DoNothingAction(this, true));
            }
            else if (gameState.getPlayerTurn() == this.playerNum)
            {
                sleep(50);
                decideAction();
                game.sendAction(curAction);
            }
        }
    }

    enum AIStates implements AIState {
        SelectBoardBlok {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                AI.resetUnplayablePieces(state.getPlayerPieces()[state.getPlayerTurn()]);
                //gets arrayList of valid moves
                ArrayList<Blok> cpValidMoves = state.getValidCorners(state.getPlayerTurn());

                ArrayList<Blok> unplayableMoves = AI.getUnplayableBloks();

                cpValidMoves.removeAll(unplayableMoves);

                Blok selectedBoardBlok = cpValidMoves.get(r.nextInt(cpValidMoves.size()));

                SelectValidBlokOnBoardAction svboba =
                        new SelectValidBlokOnBoardAction(AI, selectedBoardBlok);

                AI.setAction(svboba);
                return SelectPiece;
            }
        },
        SelectPiece {
            @Override
            public AIState checkState(BlokusSimpleComputerPlayer AI, BlokusGameState state)
            {
                if (AI.hasNoPiecesRemaining())
                {
                    AI.setAction(new DoNothingAction(AI, false));
                    AI.rotateTracker = 0;
                    AI.pieceBlokTracker = 0;
                    //AI.resetUnplayablePieces();
                    AI.addUnplayableBlok(state.getSelectedBoardBlok());
                    return SelectBoardBlok;
                }

                int[] AIPieces = state.getPlayerPieces()[state.getPlayerTurn()];
                int pieceIndex, pieceID;
                //find piece to play
                do {
                    //gets random number between 0 & 20
                    pieceIndex = r.nextInt(21);
                    pieceID = AIPieces[pieceIndex];
                } while (pieceID == -1 || AI.isPieceUnplayable(pieceIndex));

                SelectPieceTemplateAction spta =
                        new SelectPieceTemplateAction(AI, pieceID);

                AI.setAction(spta);
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
                    AI.pieceBlokTracker = 0;
                    AI.setAction(new DoNothingAction(AI, false));
                    AI.setPieceUnplayable(selectedPiece.getPieceId());
                    return SelectPiece;
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
                AIState tempState = ConfirmPlacement;
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

                AI.rotateTracker++;

                if (AI.rotateTracker == 9)
                {
                    AI.rotateTracker = 0;
                    tempState = SelectPieceBlok;
                    AI.pieceBlokTracker++;
                }

                AI.setAction(tempAction);
                return tempState;
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
                    // all pieces attempted at this boardBlok
                    if (AI.hasNoPiecesRemaining())
                    {
                        AI.pieceBlokTracker = 0;
                        AI.rotateTracker = 0;
                        AI.addUnplayableBlok(selectedBoardBlok);
                        return SelectBoardBlok;
                    }

                    // all pieceBloks attempted for this piece at this boardBlok
                    else if (AI.pieceBlokTracker == selectedPiece.getPieceShape().length)
                    {
                        AI.pieceBlokTracker = 0;
                        AI.rotateTracker = 0;
                        AI.setPieceUnplayable(selectedPiece.getPieceId());
                        return SelectPiece;
                    }
                    else
                    {
                        return Rotate;
                    }
                }
                else
                {
                    AI.setAction(new ConfirmPiecePlacementAction(AI));
                    AI.pieceBlokTracker = 0;
                    AI.rotateTracker = 0;
                    AI.clearUnplayableBloks();
                    return SelectBoardBlok;
                }
            }
        }
    }

    private void decideAction()
    {
        curState = curState.checkState(this, gameState);
    }

    public void setAction(GameAction action)
    {
        this.curAction = action;
    }

    public boolean isPieceUnplayable(int pieceIndex)
    {
        return (this.unplayablePieces[pieceIndex] == -1);
    }

    public void setPieceUnplayable(int pieceID)
    {
        this.unplayablePieces[pieceID] = -1;
    }

    public void resetUnplayablePieces(int[] playerPieces)
    {
        System.arraycopy(playerPieces, 0, this.unplayablePieces, 0, playerPieces.length);
    }

    public boolean hasNoPiecesRemaining()
    {
        for (int i = 0; i < 21; i++)
        {
            if (this.unplayablePieces[i] != -1)
                return false;
        }

        return true;
    }

    public ArrayList<Blok> getUnplayableBloks()
    {
        return this.unplayableBoardBloks;
    }

    public void addUnplayableBlok(Blok unplayableBlok)
    {
        this.unplayableBoardBloks.add(unplayableBlok);
    }

    public void clearUnplayableBloks()
    {
        this.unplayableBoardBloks.clear();
    }
}

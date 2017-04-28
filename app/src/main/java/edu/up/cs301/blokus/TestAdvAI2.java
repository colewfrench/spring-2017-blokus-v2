package edu.up.cs301.blokus;

/**
 * Created by sterba19 on 4/27/2017.
 */

import android.util.Log;

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

public class TestAdvAI2 extends GameComputerPlayer{

    interface AIState {
        AIState checkState(TestAdvAI2 AI, BlokusGameState state);
    }

    private BlokusGameState gameState;
    private GameAction curAction;
    private AIState curState;
    public int rotateTracker;
    public int pieceBlokTracker;
    public int[] playablePieces;
    private ArrayList<Blok> playableBoardBloks;
    public int sizeTracker;

    static Random r;
    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public TestAdvAI2(String name) {
        super(name);
        curState = AIStates.SetupStartOfTurn;
        r = new Random();

        this.playableBoardBloks = new ArrayList<>();

        this.pieceBlokTracker = 0;
        this.rotateTracker = 0;
        this.sizeTracker = 20;

        this.playablePieces = new int[21];
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
        SetupStartOfTurn {
            @Override
            public AIState checkState(TestAdvAI2 AI, BlokusGameState state)
            {
                AI.setAction(new DoNothingAction(AI, false));
                AI.resetPlayablePieces(state.getPlayerPieces()[AI.playerNum]);
                AI.rotateTracker = 0;
                AI.pieceBlokTracker = 0;
                AI.sizeTracker = 20;

                return SelectPiece;
            }
        },
        SelectPiece {
            @Override
            public AIState checkState(TestAdvAI2 AI, BlokusGameState state)
            {

                int[] AIPieces = state.getPlayerPieces()[state.getPlayerTurn()];
                int pieceID;
                //find piece to play
                do {
                    pieceID = AIPieces[AI.sizeTracker];

                    if (pieceID == -1)
                    {
                        AI.sizeTracker = AI.sizeTracker-1;
                    }
                } while (pieceID == -1 && AI.sizeTracker >= 0);

                if(AI.sizeTracker == -1)
                {
                    AI.setAction(new DoNothingAction(AI, true));
                }
                else
                {
                    SelectPieceTemplateAction spta =
                            new SelectPieceTemplateAction(AI, pieceID);

                    AI.setAction(spta);
                }
                return SelectBoardBlok;
            }
        },
        SelectBoardBlok {
            @Override
            public AIState checkState(TestAdvAI2 AI, BlokusGameState state)
            {
                if (AI.getPlayableCorners().isEmpty())
                {
                    AI.setAction(new DoNothingAction(AI, false));
                    AI.rotateTracker = 0;
                    AI.pieceBlokTracker = 0;
                    AI.sizeTracker = AI.sizeTracker-1;
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
            public AIState checkState(TestAdvAI2 AI, BlokusGameState state)
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

                Log.d("Valid Corners: ", "                      " + AI.getPlayableCorners().size());

                int selectedBlokID = AI.pieceBlokTracker;

                SelectBlokOnSelectedPieceAction sbospa =
                        new SelectBlokOnSelectedPieceAction(AI, selectedBlokID);

                AI.setAction(sbospa);
                return ConfirmPlacement;
            }
        },
        Rotate {
            @Override
            public AIState checkState(TestAdvAI2 AI, BlokusGameState state)
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
            public AIState checkState(TestAdvAI2 AI, BlokusGameState state)
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
        return (this.playablePieces[pieceIndex] == -1);
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
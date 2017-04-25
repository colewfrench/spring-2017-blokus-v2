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
public class BlokusTestAIPlayer extends GameComputerPlayer {

    interface AIState {
        AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state);
    }

    private BlokusGameState gameState;
    private AIStateMachine stateMachine;
    private GameAction curAction;
    private AIState curState;
    public int rotateTracker;

    private Hashtable<Integer,int[]> PlayedPieceBloks = new Hashtable<>();

    static Random r;
    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public BlokusTestAIPlayer(String name) {
        super(name);

        this.stateMachine = new AIStateMachine();
        curState = AIStates.SelectPiece;
        r = new Random();
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof BlokusGameState)
        {
            this.gameState = (BlokusGameState)info;

            // if the current player has no available moves, skip his turn
            if (!gameState.playerCanMove(this.playerNum))
            {
                game.sendAction(new DoNothingAction(this, true));
            } else if (gameState.getPlayerTurn() == this.playerNum)
            {
                periodic();

                game.sendAction(curAction);
            }
        }
    }

    enum AIStates implements AIState {
        SelectPiece {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                int[][] pieces = state.getPlayerPieces();
                int pieceIndex, pieceID;
                //find piece to play
                do {
                    //gets random number between 0 & 20
                    pieceIndex = r.nextInt(21);
                    pieceID = pieces[state.getPlayerTurn()][pieceIndex];
                } while (pieceID == -1);

                SelectPieceTemplateAction spta =
                        new SelectPieceTemplateAction(AI, pieceID);

                AI.setAction(spta);
                return SelectPieceBlok;
            }
        },
        SelectBoardBlok {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                //gets arrayList of valid moves
                ArrayList<Blok> cpValidMoves = state.getValidCorners(state.getPlayerTurn());

                Blok selectedBoardBlok = cpValidMoves.get(r.nextInt(cpValidMoves.size()));

                SelectValidBlokOnBoardAction svboba =
                        new SelectValidBlokOnBoardAction(AI, selectedBoardBlok);

                AI.setAction(svboba);
                return ConfirmPlacement;
            }
        },
        SelectPieceBlok {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                PieceTemplate selectedPiece = state.getSelectedPiece();

                PieceBlok[] bloksOnPiece = selectedPiece.getPieceShape();

                int selectedBlokID = r.nextInt(bloksOnPiece.length);

                SelectBlokOnSelectedPieceAction sbospa =
                        new SelectBlokOnSelectedPieceAction(AI, selectedBlokID);

                AI.setAction(sbospa);
                return SelectBoardBlok;
            }
        },
        Rotate {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
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
                        tempState = ConfirmPlacement;
                        break;
                    case 4:
                        tempAction = new FlipSelectedPieceAction(AI);
                        tempState = ConfirmPlacement;
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        tempAction = new RotateSelectedPieceAction(AI);
                        tempState = ConfirmPlacement;
                        break;
                }

                AI.rotateTracker++;

                if (AI.rotateTracker == 9)
                {
                    AI.rotateTracker = 0;
                    tempState = SelectPiece;
                }


                AI.setAction(tempAction);
                return tempState;
            }
        },
        ConfirmPlacement {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                Blok selectedBoardBlok = state.getSelectedBoardBlok();
                PieceTemplate selectedPiece = state.getSelectedPiece();
                int selectedPieceBlokId = state.getSelectedPieceBlokId();
                Blok[][] board = state.getBoardState();

                if (state.prepareValidMove(selectedBoardBlok,
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
                    return SelectPiece;
                }
            }
        }
    }

    private void periodic()
    {
        curState = curState.checkState(this, gameState);
    }

    public void setAction(GameAction action)
    {
        this.curAction = action;
    }
}

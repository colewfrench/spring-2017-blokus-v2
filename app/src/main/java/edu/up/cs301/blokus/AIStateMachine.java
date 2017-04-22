package edu.up.cs301.blokus;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.blokus.actions.ConfirmPiecePlacementAction;
import edu.up.cs301.blokus.actions.SelectBlokOnSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectPieceTemplateAction;
import edu.up.cs301.blokus.actions.SelectValidBlokOnBoardAction;
import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GameComputerPlayer;

/**
 * Created by frenchco19 on 4/22/2017.
 */
public class AIStateMachine {

    interface AIState {
        AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state);
    }

    static Random r;

    public AIStateMachine()
    {
        r = new Random();
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
                    pieceIndex = 0;//r.nextInt(21);
                    pieceID = pieces[state.getPlayerTurn()][pieceIndex];
                } while (pieceID == -1);

                SelectPieceTemplateAction spta =
                        new SelectPieceTemplateAction(AI, pieceID);

                AI.setAction(spta);
                return SelectBoardBlok;
            }
        },
        SelectBoardBlok {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                ArrayList<Blok> cpValidMoves = state.getValidCorners(state.getPlayerTurn());

                Blok selectedBoardBlok = cpValidMoves.get(r.nextInt(cpValidMoves.size()));

                SelectValidBlokOnBoardAction svboba =
                        new SelectValidBlokOnBoardAction(AI, selectedBoardBlok);

                AI.setAction(svboba);
                return SelectPieceBlok;
            }
        },
        SelectPieceBlok {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                PieceTemplate selectedPiece = state.getSelectedPiece();

                PieceBlok[] bloksOnPiece = selectedPiece.getPieceShape();

                int selectedBlokID = 0;

                SelectBlokOnSelectedPieceAction sbospa =
                        new SelectBlokOnSelectedPieceAction(AI, selectedBlokID);

                AI.setAction(sbospa);
                return ConfirmPlacement;
            }
        },
        Rotate {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                return ConfirmPlacement;
            }
        },
        Flip {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                return ConfirmPlacement;
            }
        },
        ConfirmPlacement {
            @Override
            public AIState checkState(BlokusTestAIPlayer AI, BlokusGameState state)
            {
                AI.setAction(new ConfirmPiecePlacementAction(AI));
                return SelectPiece;
            }
        }
    }
}

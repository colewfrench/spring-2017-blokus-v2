package edu.up.cs301.blokus;

/**
 * Created by lowa19 on 3/5/2017.
 */

import android.util.Log;

import edu.up.cs301.blokus.actions.ConfirmPiecePlacementAction;
import edu.up.cs301.blokus.actions.DoNothingAction;
import edu.up.cs301.blokus.actions.FlipSelectedPieceAction;
import edu.up.cs301.blokus.actions.RotateSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectBlokOnSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectPieceTemplateAction;
import edu.up.cs301.blokus.actions.SelectValidBlokOnBoardAction;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameState;


/**
 * A class that represents the state of a game. In our counter game, the only
 * relevant piece of information is the value of the game's counter. The
 * CounterState object is therefore very simple.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 */
public class BlokusLocalGame extends LocalGame {

    // the game's state
    private BlokusGameState gameState;

    /**
     * can this player move
     *
     * @return true if the given player has any available move
     */
    @Override
    protected boolean canMove(int playerIdx) {
        return (gameState.getPlayerTurn() == playerIdx &&
                gameState.playerCanMove(playerIdx));
    }

    /**
     * This ctor should be called when a new counter game is started
     */
    public BlokusLocalGame() {
        this.gameState = new BlokusGameState();
    }

    /**
     *
     */
    @Override
    protected boolean makeMove(GameAction action) {
            if (action instanceof FlipSelectedPieceAction) {
                gameState.flipSelectedPiece();
            }
            if (action instanceof RotateSelectedPieceAction) {
                gameState.rotateSelectedPiece();
            }
            if (action instanceof SelectBlokOnSelectedPieceAction) {
                gameState.selectBlokOnSelectedPiece((SelectBlokOnSelectedPieceAction) action);
            }
            if (action instanceof ConfirmPiecePlacementAction) {
                if (gameState.confirmPiecePlacement()) {
                    // piece was successfully placed; go to next player turn
                    gameState.changeToNextPlayer();
                    gameState.getValidCorners(gameState.getPlayerTurn());
                }
                // else, current piece cannot be placed, continue current turn
            }
            if (action instanceof SelectPieceTemplateAction) {
                gameState.selectPieceTemplate((SelectPieceTemplateAction) action);
            }
            if (action instanceof SelectValidBlokOnBoardAction) {
                gameState.selectValidBlokOnBoard((SelectValidBlokOnBoardAction) action);
            }

            if (action instanceof DoNothingAction) {
                if (((DoNothingAction) action).passMyTurn()) {
                    gameState.changeToNextPlayer();
                    gameState.getValidCorners(gameState.getPlayerTurn());
                }
            }

        return true;
    }//makeMove

    /**
     * send the updated state to a given player
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        BlokusGameState copyState = new BlokusGameState(gameState);
        p.sendInfo((GameState)copyState);

    }//sendUpdatedSate

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return
     * 		a message that tells who has won the game, or null if the
     * 		game is not over
     */
    @Override
    protected String checkIfGameOver() {
        Log.d("entered", "checking game over");
        for(int i=0; i<4; i++)
        {
            if (gameState.playerCanMove(i))
            {
                return null;
            }
        }

        Log.d("Game is Over", "True");

        Blok[][] boardState = gameState.getBoardState();
        int[] pointsPerPlayer = new int[4];
        int[][] playerPieces = gameState.getPlayerPieces();
        int maxPoints = 0;
        int winner = 0;
        String scoreMessage = "";

        // give players bonus points if they placed all their pieces
        int bonusPoints;
        for(int i=0; i<4; i++)
        {
            bonusPoints = 20;

            for (int j = 0; j < 21; j++)
            {
                if (playerPieces[i][j] != -1)
                {
                    bonusPoints = 0;
                    break;
                }
            }

            pointsPerPlayer[i] = bonusPoints;
        }

        for(int j = 1; j < 21; j++)
        {
            for(int k = 1; k < 21; k++)
            {
                Blok curBlok = boardState[j][k];
                switch (curBlok.getColor())
                {
                    case 0:
                        pointsPerPlayer[0]++;
                        break;
                    case 1:
                        pointsPerPlayer[1]++;
                        break;
                    case 2:
                        pointsPerPlayer[2]++;
                        break;
                    case 3:
                        pointsPerPlayer[3]++;
                        break;
                }
            }
        }


        for(int i = 0; i < 4; i++)
        {
            if(pointsPerPlayer[i]> maxPoints)
            {
                Log.d("Player" + (i+1) + "Score", "" + pointsPerPlayer[i]);
                maxPoints = pointsPerPlayer[i];
                winner = i;
            }
        }

        switch(winner)
        {
            case 0:
                scoreMessage = "Player 1 Wins!";
                break;

            case 1:
                scoreMessage = "Player 2 Wins!";
                break;

            case 2:
                scoreMessage = "Player 3 Wins!";
                break;

            case 3:
                scoreMessage = "Player 4 Wins!";
                break;
        }

        return scoreMessage;
    }

}// class CounterLocalGame

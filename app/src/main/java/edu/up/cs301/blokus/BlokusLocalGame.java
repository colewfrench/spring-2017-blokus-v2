package edu.up.cs301.blokus;

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
public class
BlokusLocalGame extends LocalGame {

    // the game's state
    private BlokusGameState gameState;

    /**
     * can this player move
     *
     * @return true if the given player has any available move
     */
    @Override
    protected boolean canMove(int playerIdx) {
        return (gameState.getPlayerTurn() == playerIdx);
    }

    /**
     * This ctor should be called when a new Blokus game is started
     */
    public BlokusLocalGame() {
        this.gameState = new BlokusGameState();
    }

    /**
     * execute the appropriate operation on the master gamestate
     * depending on the given move
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
        BlokusGameState copyState = new BlokusGameState(gameState);
        for(int i=0; i<4; i++)
        {
            copyState.setPlayerTurn(i);
            if (copyState.playerCanMove(i))
            {
                return null;
            }
        }

        String scoreMessage = "============\n GAME OVER\n============\n\n";

        Blok[][] boardState = copyState.getBoardState();
        int[] pointsPerPlayer = new int[4];
        int[][] playerPieces = copyState.getPlayerPieces();
        int maxPoints = 0;
        int winner = 0;

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
                maxPoints = pointsPerPlayer[i];
                winner = i;
            }
        }

        for (int i = 0; i < 4; i++)
        {
            switch (i)
            {
                case 0:
                    scoreMessage += "[Green] ";
                    break;
                case 1:
                    scoreMessage += "[Orange] ";
                    break;
                case 2:
                    scoreMessage += "[Purple] ";
                    break;
                case 3:
                    scoreMessage += "[Blue] ";
                    break;
            }

            scoreMessage = scoreMessage + this.playerNames[i] + "'s Score: "
                    + pointsPerPlayer[i] + "\n";
        }

        switch(winner)
        {
            case 0:
                scoreMessage += "\n" + this.playerNames[0];
                break;

            case 1:
                scoreMessage += "\n" + this.playerNames[1];
                break;

            case 2:
                scoreMessage += "\n" + this.playerNames[2];
                break;

            case 3:
                scoreMessage += "\n" + this.playerNames[3];
                break;
        }

        return scoreMessage + " wins!";
    }

}// class CounterLocalGame

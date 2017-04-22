package edu.up.cs301.blokus;

import edu.up.cs301.blokus.actions.DoNothingAction;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by frenchco19 on 4/22/2017.
 */
public class BlokusTestAIPlayer extends GameComputerPlayer {

    BlokusGameState gameState;
    AIStateMachine stateMachine;
    GameAction curAction;
    AIStateMachine.AIState curState;

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public BlokusTestAIPlayer(String name) {
        super(name);

        this.stateMachine = new AIStateMachine();
        curState = AIStateMachine.AIStates.SelectPiece;
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
                return;
            }

            if (gameState.getPlayerTurn() == this.playerNum)
            {
                periodic();

                game.sendAction(curAction);
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

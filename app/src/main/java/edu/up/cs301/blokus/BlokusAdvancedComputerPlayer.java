package edu.up.cs301.blokus;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

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
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by lowa19 on 3/5/2017.
 */




public class BlokusAdvancedComputerPlayer extends GameComputerPlayer {

    private int pieceID, actionTracker = 0, rotationTracker = 0, sizeTracker = 20;
    private BlokusGameState state;
    private Random r = new Random();

    /**
     * Constructor for objects of class CounterComputerPlayer1
     *
     * @param name
     * 		the player's name
     *
     */
    public BlokusAdvancedComputerPlayer(String name) {
        // invoke superclass constructor
        super(name);

    }

    /**
     * callback method--game's state has changed
     *
     * @param info
     * 		the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        //IT RECEIVES THE INFO
        if (info instanceof BlokusGameState) {
            this.state = (BlokusGameState) info;

            if (state.getPlayerTurn() == playerNum)
            {
                switch (actionTracker) {
                    case 0:
                        selectPiece(state.getPlayerPieces());
                        actionTracker = 1;
                        break;
                    case 1:
                        selectBlokOnPiece();
                        actionTracker = 2;
                        break;
                    case 2:
                        boardPlacement();
                        actionTracker = 3;
                        break;
                    case 3:
                        if (rotationTracker == 9) {
                            actionTracker = 0;
                            rotationTracker = 0;
                            sizeTracker--;
                            game.sendAction(new DoNothingAction(this,false));
                        } else {
                            rotateAndFlip();
                            rotationTracker++;
                            actionTracker = 4;
                        }
                        break;
                    case 4:
                        if (moveIsValid()) {
                            actionTracker = 0;
                            rotationTracker = 0;
                            sizeTracker = 20;
                        } else {
                            actionTracker = 0;
                        }
                        playRandom();
                        break;
                    default:
                        actionTracker = 0;
                        rotationTracker = 0;
                        game.sendAction(new DoNothingAction(this,false));
                        break;
                }
            }
        }
    }

    public void playRandom()
    {
        ConfirmPiecePlacementAction cppa = new ConfirmPiecePlacementAction(this);

        game.sendAction(cppa);
    }

    /**
     * this works
     * @param pieces
     */
    public void selectPiece(int[][] pieces)
    {
        int pieceIndex;
        //TODO think of way to change between 5,4,3,2,1
        //TODO error if all pieces of a size have been played (i.e. all equal -1)
        //find piece to play
        do {
            pieceIndex = sizeTracker;
            if(pieceIndex == -1)
            {
                game.sendAction(new DoNothingAction(this, true));
            }
            pieceID = pieces[this.playerNum][pieceIndex];
            Log.d("Player",Integer.toString(state.getPlayerTurn()));
            Log.d("Size Tracker", Integer.toString(sizeTracker));
            Log.d("PieceID",Integer.toString(pieceID));
            if(pieceID ==-1)
            {
                sizeTracker--;
            }
        } while (pieceID == -1);

        SelectPieceTemplateAction spta =
                new SelectPieceTemplateAction(this, pieceID);

        game.sendAction(spta);
    }

    public void selectBlokOnPiece()
    {
        PieceTemplate selectedPiece = state.getSelectedPiece();

        PieceBlok[] bloksOnPiece = selectedPiece.getPieceShape();

        int selectedBlokID = r.nextInt(bloksOnPiece.length);

        SelectBlokOnSelectedPieceAction sbospa =
                new SelectBlokOnSelectedPieceAction(this,selectedBlokID);

        game.sendAction(sbospa);
    }

    public void boardPlacement()
    {
        //gets arrayList of valid moves
        ArrayList<Blok> cpValidMoves = state.getValidCorners();

        int blokID = r.nextInt(cpValidMoves.size());

        //select random move
        Blok selectedBoardBlok = cpValidMoves.get(blokID);

        SelectValidBlokOnBoardAction svboba =
                new SelectValidBlokOnBoardAction(this, selectedBoardBlok);

        game.sendAction(svboba);
    }

    private boolean moveIsValid()
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
            return false;
        }

        return true;
    }

    private void rotateAndFlip()
    {
        PieceTemplate selectedPiece = state.getSelectedPiece();
        RotateSelectedPieceAction rotateAction =
                new RotateSelectedPieceAction(this);

        FlipSelectedPieceAction flipAction =
                new FlipSelectedPieceAction(this);
        switch(rotationTracker)
        {
            case 0:
            case 1:
            case 2:
            case 3:
                game.sendAction(rotateAction); // rotate 4 in 4 cases
                break;
            case 4:
                game.sendAction(flipAction); // flip
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                game.sendAction(rotateAction); // rotate in 4 cases
                break;
            case 9:
                break;
        }
    }
}


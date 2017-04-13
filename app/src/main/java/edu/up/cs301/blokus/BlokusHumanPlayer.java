package edu.up.cs301.blokus;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import edu.up.cs301.blokus.actions.ConfirmPiecePlacementAction;
import edu.up.cs301.blokus.actions.FlipSelectedPieceAction;
import edu.up.cs301.blokus.actions.RotateSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectBlokOnSelectedPieceAction;
import edu.up.cs301.blokus.actions.SelectPieceTemplateAction;
import edu.up.cs301.blokus.actions.SelectValidBlokOnBoardAction;
import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;


/**
 * Created by lowa19 on 3/5/2017.
 */
public class BlokusHumanPlayer extends GameHumanPlayer {

    private GameMainActivity activity;

    // constant value for number of pieces
    private static final int NUM_PIECES = 21;

    // static constants to set up board
    private static final int BOARD_SIZE = 20;
    private static final int TILE_SIZE = 76; // in pixels
    private static final int PREVIEW_BOARD_SIZE = 5;
    private static final int SAMPLE_PIECE_TILE_SIZE = 62; // in pixels

    //global variables for buttons
    private Button rotateButton;
    private Button confirmButton;
    private Button flipButton;

    //Array for Piece Buttons
    private PieceButton[] pieceButtons;
    private BoardButton[][] boardButtons;
    private PieceDisplayButton[][] displayButtons;
    private LinearLayout pieceLayout;

    private PieceTemplate selectedPiece = null;
    private Blok[][] previewBoard = null;

    BlokusGameState newState;

    /**
     * constructor
     *
     * @param name the name of the player
     */
    public BlokusHumanPlayer(String name) {
        super(name);
    }

    @Override
    public View getTopView() {
        return activity.findViewById(R.id.top_gui_layout);
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof BlokusGameState)
        {
            //enablePlayerInput();
            newState = (BlokusGameState)info;

            if (newState.getBoardState() != null) {
                updateGUIBoard();
            }

            this.previewBoard = newState.getPiecePreview();
            this.selectedPiece = newState.getSelectedPiece();

            if (previewBoard != null) {
                updatePreviewBoard();
            }

            updatePieceButtons();

            rotateButton.setBackgroundColor(Color.YELLOW);
            flipButton.setBackgroundColor(Color.YELLOW);
            updateConfirmButton();
        }

        /*
        if (info instanceof NotYourTurnInfo)
        {
            //disablePlayerInput();
            confirmButton.setBackgroundColor(Color.RED);
            rotateButton.setBackgroundColor(Color.RED);
            flipButton.setBackgroundColor(Color.RED);
        }
        */
    }

    private void enablePlayerInput()
    {
        setBoardListeners(new BlokButtonListener());
        setPieceListeners(new PieceButtonListener());
        setPreviewListeners(new BlokButtonListener());
        setControlListeners(new PieceControlListener());
    }

    private void disablePlayerInput()
    {
        setBoardListeners(null);
        setPieceListeners(null);
        setPreviewListeners(null);
        setControlListeners(null);
    }

    private void setBoardListeners(View.OnClickListener l)
    {
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            for (int j = 0; j < BOARD_SIZE; j++)
            {
                boardButtons[i][j].setOnClickListener(l);
            }
        }
    }

    private void setPieceListeners(View.OnClickListener l)
    {
        for (int i = 0; i < pieceButtons.length; i++)
        {
            pieceButtons[i].setOnClickListener(null);
        }
    }

    private void setPreviewListeners(View.OnClickListener l)
    {
        for (int i = 0; i < PREVIEW_BOARD_SIZE; i++)
        {
            for (int j = 0; j < PREVIEW_BOARD_SIZE; j++)
            {
                displayButtons[i][j].setOnClickListener(l);
            }
        }
    }

    private void setControlListeners(View.OnClickListener l)
    {
        confirmButton.setOnClickListener(l);
        rotateButton.setOnClickListener(l);
        flipButton.setOnClickListener(l);
    }

    public void setAsGui(GameMainActivity activity) {
        this.activity = activity;

        /**
         * External Citation
         *      Date: 2/17/2017
         *      Problem: Wanted the app screen to start in fullscreen
         *               without the title bar.
         *
         *      Resource: http://stackoverflow.com/questions/2862528/how-to-hide-app-title-in-android
         *      Solution: Used the example code from this post.
         */
        // set app to start fullscreen in portrait
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity.setContentView(R.layout.blokus_human_player);

        //Sets background color and gets references for the layout containing the board
        LinearLayout mainBoardLayout = (LinearLayout)activity.findViewById(R.id.boardLayout);
        mainBoardLayout.setBackgroundColor(Color.BLACK);

        // initialize the Confirm, Rotate, and Flip buttons
        initPlayerControlButtons();

        // create a 20x20 grid of image buttons to use as the board
        initGameBoard(mainBoardLayout);

        // initialize the buttons that allow piece seletion
        initPieceButtons();

        // create a 5x5 grid that allows the player
        // to preview the orientation of their selected piece
        initPiecePreviewDisplay();

    }

    /**
     *
     */
    private void initPlayerControlButtons()
    {
        rotateButton = (Button)activity.findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new PieceControlListener());

        confirmButton = (Button)activity.findViewById(R.id.ConfirmButton);
        confirmButton.setOnClickListener(new PieceControlListener());

        flipButton = (Button)activity.findViewById(R.id.flipButton);
        flipButton.setOnClickListener(new PieceControlListener());
    }

    /**
     *
     * @param mainBoardLayout the square layout containing the board
     */
    private void initGameBoard(LinearLayout mainBoardLayout)
    {
        LinearLayout[] boardLayouts = new LinearLayout[BOARD_SIZE];
        //Parameters for setting height and width of board layout
        LinearLayout.LayoutParams boardLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TILE_SIZE);

        //Fills boardLayouts with horizontal Layouts
        //i.e. the rows that buttons will fill
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            boardLayouts[i] = new LinearLayout(activity);
            boardLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
            boardLayouts[i].setGravity(Gravity.CENTER_HORIZONTAL);
            boardLayouts[i].setLayoutParams(boardLayoutParams);

            mainBoardLayout.addView(boardLayouts[i]);
        }

        //Creates Button array, and buttonParams array
        boardButtons = new BoardButton[BOARD_SIZE][BOARD_SIZE];
        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(TILE_SIZE, TILE_SIZE);

        //Fills horizontal Layouts with buttons
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            for (int j = 0; j < BOARD_SIZE; j++)
            {
                boardButtons[i][j] = new BoardButton(activity, i, j, false);
                boardButtons[i][j].setLayoutParams(buttonParams);

                /**
                 * External Citation
                 *      Date: 2/17/2017
                 *      Problem: Need to know how to set the image on an imageButton and
                 *               have the image fit on the imageButton
                 *
                 *      Resources: http://stackoverflow.com/questions/2617969/how-can-i-change-the-images-
                 *                 on-an-imagebutton-in-android-when-using-a-ontouchlis
                 *
                 *                 https://developer.android.com/reference/android/widget/ImageButton.html
                 *      Solution: Used the StackOverflow post code snippet and the ImageButton documentation
                 *                to learn the correct methods neccessary to fit the image onto the button
                 */

                boardButtons[i][j].setBackgroundResource(R.drawable.empty_blok);
                boardLayouts[i].addView(boardButtons[i][j]);
                boardButtons[i][j].setOnClickListener(new BlokButtonListener());
            }
        }
    }

    /**
     *  Fills the array with a reference to each PieceButton in order
     *  to be able to programmatically manipulate the buttons later
     */
    private void initPieceButtons()
    {
        pieceLayout = (LinearLayout)activity.findViewById(R.id.pieceLayout);
        pieceButtons = new PieceButton[NUM_PIECES];

        for (int i = 0; i < pieceLayout.getChildCount(); i++)
        {
            pieceButtons[i] = (PieceButton)pieceLayout.getChildAt(i);
            pieceButtons[i].setOnClickListener(new PieceButtonListener());
            pieceButtons[i].setIsHighlighted(false);

            pieceButtons[i].setButtonPiece(i);
            // the piece ID corresponding to the piece displayed on the button
        }
    }

    /**
     *
     */
    private void initPiecePreviewDisplay()
    {
        LinearLayout pieceDisplay = (LinearLayout)activity.findViewById(R.id.previewLayout);
        LinearLayout[] pieceDisplayLayouts = new LinearLayout[PREVIEW_BOARD_SIZE];

        LinearLayout.LayoutParams pieceDisplayLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        SAMPLE_PIECE_TILE_SIZE);

        // add the horizontal layouts to the display layout
        for (int i = 0; i < PREVIEW_BOARD_SIZE; i++)
        {
            pieceDisplayLayouts[i] = new LinearLayout(activity);
            pieceDisplayLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
            pieceDisplayLayouts[i].setGravity(Gravity.CENTER_HORIZONTAL);
            pieceDisplayLayouts[i].setLayoutParams(pieceDisplayLayoutParams);
            pieceDisplay.addView(pieceDisplayLayouts[i]);
        }

        displayButtons = new PieceDisplayButton[PREVIEW_BOARD_SIZE][PREVIEW_BOARD_SIZE];

        LinearLayout.LayoutParams displayButtonParams =
                new LinearLayout.LayoutParams(
                        SAMPLE_PIECE_TILE_SIZE, SAMPLE_PIECE_TILE_SIZE);

        // add buttons to the display layouts
        for (int i = 0; i < PREVIEW_BOARD_SIZE; i++)
        {
            for (int j = 0; j < PREVIEW_BOARD_SIZE; j++)
            {
                displayButtons[i][j] = new PieceDisplayButton(activity, i, j, false);
                displayButtons[i][j].setLayoutParams(displayButtonParams);

                displayButtons[i][j].setBackgroundResource(R.drawable.empty_blok);

                pieceDisplayLayouts[i].addView(displayButtons[i][j]);
                displayButtons[i][j].setOnClickListener(new BlokButtonListener());
            }
        }
    }

    /**
     *
     */
    private void updateGUIBoard()
    {
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            for (int j = 0; j < BOARD_SIZE; j++)
            {
                boardButtons[i][j].setColor(newState.getBoardState()[i+1][j+1].getColor());
                boardButtons[i][j].setImageResource(android.R.color.transparent);

                switch(boardButtons[i][j].getColor())
                {
                    case 0: // Green
                        boardButtons[i][j].setBackgroundResource(R.drawable.player1_blok);
                        break;
                    case 1: // Orange
                        boardButtons[i][j].setBackgroundResource(R.drawable.player2_blok);
                        break;
                    case 2: // Purple
                        boardButtons[i][j].setBackgroundResource(R.drawable.player3_blok);
                        break;
                    case 3: // Blue
                        boardButtons[i][j].setBackgroundResource(R.drawable.player4_blok);
                        break;
                }
            }
        }

        //Displays valid moves
        //go through the board and check for valid movies
        for (Blok b : newState.getValidCorners())
        {
            // decrease by 1 b/c bloks from validMoves are stored on a 22x22 board
            displayAsButtonSelected(boardButtons[b.getRow()-1][b.getColumn()-1], 15);
        }

    }

    /**
     *
     */
    private void updatePreviewBoard()
    {
        for (int i = 0; i < PREVIEW_BOARD_SIZE; i++)
        {
            for (int j = 0; j < PREVIEW_BOARD_SIZE; j++)
            {
                displayButtons[i][j].setBackgroundResource(R.drawable.empty_blok);
                displayButtons[i][j].setActivated(false);

                switch(previewBoard[i+1][j+1].getColor())
                {
                    case 0: // Green
                        displayButtons[i][j].setBackgroundResource(R.drawable.player1_blok);
                        displayButtons[i][j].setActivated(true);
                        break;
                    case 1: // Orange
                        displayButtons[i][j].setBackgroundResource(R.drawable.player2_blok);
                        displayButtons[i][j].setActivated(true);
                        break;
                    case 2: // Purple
                        displayButtons[i][j].setBackgroundResource(R.drawable.player3_blok);
                        displayButtons[i][j].setActivated(true);
                        break;
                    case 3: // Blue
                        displayButtons[i][j].setBackgroundResource(R.drawable.player4_blok);
                        displayButtons[i][j].setActivated(true);
                        break;
                }

                if (displayButtons[i][j].isSelected())
                {
                    displayAsButtonSelected(displayButtons[i][j], 10);
                }
                else
                {
                    displayButtons[i][j].setImageResource(android.R.color.transparent);
                }
            }
        }
    }

    private void updatePieceButtons()
    {
        int[] curPieces = newState.getPlayerPieces()[newState.getPlayerTurn()];
        for (int i = 0; i < curPieces.length; i++)
        {
            if (curPieces[i] == -1)
            {
                pieceLayout.removeView(pieceButtons[i]);
            }
        }
    }

    private void updateConfirmButton()
    {
        Blok blok = newState.getSelectedBoardBlok();
        PieceTemplate piece = newState.getSelectedPiece();
        int pieceBlokId = newState.getSelectedPieceBlokId();

        if (blok == null || piece == null || pieceBlokId == -1)
        {
            confirmButton.setBackgroundColor(Color.RED);
            return;
        }

        if (newState.piecePlacementIsValid(
                blok, piece, pieceBlokId, newState.getBoardState()))
        {
            confirmButton.setBackgroundColor(Color.GREEN);
        }
        else
        {
            confirmButton.setBackgroundColor(Color.RED);
        }
    }

    private void displayAsButtonSelected(ImageButton button, int padding)
    {
        button.setImageResource(R.drawable.circle_check);
        button.setPadding(padding,padding,padding,padding);
        button.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    //Listener for BoardButtons
    public class BlokButtonListener implements Button.OnClickListener {

        public void onClick(View v)
        {
            // one of the buttons on the 20x20 grid
            if (v instanceof BoardButton)
            {
                int row = ((BoardButton) v).getRow() + 1;
                int col = ((BoardButton) v).getCol() + 1;
                Blok selectedBlok = newState.getBoardState()[row][col];

                if (blokIsValid(selectedBlok))
                {
                    game.sendAction(new SelectValidBlokOnBoardAction(
                            BlokusHumanPlayer.this, selectedBlok));
                }
            }

            // one of the buttons on the 5x5 grid
            if ((v instanceof PieceDisplayButton) && (v.isActivated()))
            {
                int row = ((PieceDisplayButton) v).getRow();
                int col = ((PieceDisplayButton) v).getCol();
                int id = previewBoard[row+1][col+1].getId();
                // because previewBoard is 7x7 and displayButtons is 5x5

                if (selectedPiece != null)
                {
                    Log.d("selected", "id: " + id);
                    resetSelectedDisplayButtons();
                    displayButtons[row][col].setSelected(true);
                    SelectBlokOnSelectedPieceAction selPieceBlok =
                            new SelectBlokOnSelectedPieceAction(BlokusHumanPlayer.this, id);
                    game.sendAction(selPieceBlok);
                }
            }
        }
    }

    private boolean blokIsValid(Blok selectedBlok)
    {
        ArrayList<Blok> validCorners = newState.getValidCorners();

        return validCorners.contains(selectedBlok);
    }

    /**
     *
     */
    private void resetSelectedDisplayButtons()
    {
        for (int i = 0; i < displayButtons.length; i++)
        {
            for (int j = 0; j < displayButtons.length; j++)
            {
                displayButtons[i][j].setSelected(false);
            }
        }
    }

    public class PieceControlListener implements Button.OnClickListener {

        public void onClick(View v) {
            if (v == confirmButton)
            {
                resetSelectedDisplayButtons();
                updatePreviewBoard();
                ConfirmPiecePlacementAction confirm =
                        new ConfirmPiecePlacementAction(BlokusHumanPlayer.this);
                game.sendAction(confirm);
            }

            if (v == rotateButton)
            {
                resetSelectedDisplayButtons();
                RotateSelectedPieceAction rotate =
                        new RotateSelectedPieceAction(BlokusHumanPlayer.this);
                game.sendAction(rotate);
            }

            if (v == flipButton)
            {
                resetSelectedDisplayButtons();
                FlipSelectedPieceAction flip =
                        new FlipSelectedPieceAction(BlokusHumanPlayer.this);
                game.sendAction(flip);
            }
        }
    }

    //PieceButtonListener is used by PieceButtons to highlight on click
    public class PieceButtonListener implements Button.OnClickListener {

        public void onClick(View v) {
            if (((PieceButton)v).isHighlighted()) //if the button selected is already highlighted
            {
                v.setBackgroundResource(R.drawable.button_border);
                ((PieceButton) v).setIsHighlighted(false);
                selectedPiece = null;
                resetSelectedDisplayButtons();
                SelectPieceTemplateAction sel =
                        new SelectPieceTemplateAction(BlokusHumanPlayer.this, -1);

                game.sendAction(sel);
            }
            else if( !((PieceButton) v).isHighlighted() && ((PieceButton) v).getButtonPiece() != -1) //if the button selected is not highlighted
            {
                for (PieceButton pb: pieceButtons) //unhighlight other buttons
                {
                    if (pb.isHighlighted())
                    {
                        pb.setIsHighlighted(false);
                        pb.setBackgroundResource(R.drawable.button_border);
                    }
                }
                v.setBackgroundResource(R.drawable.highlighted_button_border);
                ((PieceButton) v).setIsHighlighted(true);
                resetSelectedDisplayButtons();
                int selectedPieceID = ((PieceButton) v).getButtonPiece();
                SelectPieceTemplateAction sel =
                        new SelectPieceTemplateAction(BlokusHumanPlayer.this, selectedPieceID);

                game.sendAction(sel);
            }
        }
    }
}


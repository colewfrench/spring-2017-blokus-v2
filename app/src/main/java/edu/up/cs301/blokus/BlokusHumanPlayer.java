package edu.up.cs301.blokus;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import edu.up.cs301.blokus.actions.ConfirmPiecePlacementAction;
import edu.up.cs301.blokus.actions.DoNothingAction;
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


/**
 * Contains the GUI and behavior definitions for the HumanPlayer
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class BlokusHumanPlayer extends GameHumanPlayer {

    private static final int SELECTED_CORNER_PADDING = 15;
    private static final int SELECTED_PREVIEW_BLOK_PADDING = 10;

    // constant value for number of pieces
    private static final int NUM_PIECES = 21;

    // static constants to set up board
    private static final int BOARD_SIZE = 20;
    private static final int TILE_SIZE = 76; // in pixels
    private static final int PREVIEW_BOARD_SIZE = 5;
    private static final int SAMPLE_PIECE_TILE_SIZE = 62; // in pixels

    private GameMainActivity activity;

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
    private Blok[][] gameBoard = null;
    private Blok[][] previewBoard = null;
    private Blok selectedBoardBlok = null;
    private int selectedPieceBlokID = -1;

    private BlokusGameState newState;

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
        return activity.findViewById(R.id.boardContainer);
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof BlokusGameState)
        {
            this.newState = (BlokusGameState)info;
            this.gameBoard = newState.copyBlokArray(newState.getBoardState());
            this.previewBoard = newState.getPiecePreview();
            this.selectedPiece = newState.getSelectedPiece();
            this.selectedBoardBlok = newState.getSelectedBoardBlok();
            this.selectedPieceBlokID = newState.getSelectedPieceBlokId();

            if (newState.getPlayerTurn() == this.playerNum)
            {
                // if the current player has no available moves, skip his turn
                if (!newState.playerCanMove(this.playerNum))
                {
                    game.sendAction(new DoNothingAction(this, true));
                }
                else
                {
                    enablePlayerInput();
                    rotateButton.setBackgroundColor(Color.YELLOW);
                    flipButton.setBackgroundColor(Color.YELLOW);
                    updateConfirmButton();
                }
            }
            else
            {
                disablePlayerInput();
                rotateButton.setBackgroundColor(Color.RED);
                flipButton.setBackgroundColor(Color.RED);
                confirmButton.setBackgroundColor(Color.RED);
            }

            updateGUIBoard();
            updatePreviewBoard();
            updatePieceButtons();
        }
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
            pieceButtons[i].setOnClickListener(l);
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

        // initialize the buttons that allow piece selection
        initPieceButtons();

        // create a 5x5 grid that allows the player
        // to preview the orientation of their selected piece
        initPiecePreviewDisplay();
    }

    /**
     * gets the references to the rotate, flip, and confirm buttons,
     * and sets their listeners.
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
     * setup the 20x20 board of buttons and set all of their listeners
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
                 *
                 *      Solution: Used the StackOverflow post code snippet and the ImageButton documentation
                 *                to learn the correct methods necessary to fit the image onto the button
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
     * set up the 5x5 board of buttons and set their listeners
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
     * update the 20x20 button array to reflect any changes
     * in the board stored in the GameState
     */
    private void updateGUIBoard()
    {
        resetSelectedBoardSpaces(boardButtons);
        setBoardBlokSelected();

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            for (int j = 0; j < BOARD_SIZE; j++)
            {
                boardButtons[i][j].setColor(newState.getBoardState()[i+1][j+1].getColor());
                boardButtons[i][j].setImageResource(android.R.color.transparent);

                // display the piece's preview placement on the board
                switch (gameBoard[i+1][j+1].getColor())
                {
                    case 0:
                        boardButtons[i][j].setBackgroundResource(
                                R.drawable.player1_blok_preview);
                        break;
                    case 1:
                        boardButtons[i][j].setBackgroundResource(
                                R.drawable.player2_blok_preview);
                        break;
                    case 2:
                        boardButtons[i][j].setBackgroundResource(
                                R.drawable.player3_blok_preview);
                        break;
                    case 3:
                        boardButtons[i][j].setBackgroundResource(
                                R.drawable.player4_blok_preview);
                        break;
                    default:
                        boardButtons[i][j].setBackgroundResource(R.drawable.empty_blok);
                        break;
                }

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
        for (Blok b : newState.getValidCorners(newState.getPlayerTurn()))
        {
            int row = b.getRow() - 1;
            int col = b.getColumn() - 1;
            if (boardButtons[row][col].isSelected())
            {
                displayAsSelectedCorner(boardButtons[row][col], SELECTED_CORNER_PADDING);
            }
            // don't overwrite the selected board square indicator
            else if (!boardButtons[b.getRow()-1][b.getColumn()-1].isSelected())
            {
                // decrease by 1 b/c bloks from validMoves are stored on a 22x22 board
                displayAsValidCorner(boardButtons[b.getRow() - 1][b.getColumn() - 1], 15);
            }
        }
    }

    /**
     * update the 5x5 button array to reflect any changes in the
     * board stored in the GameState
     */
    private void updatePreviewBoard()
    {
        resetSelectedBoardSpaces(displayButtons);
        setPieceBlokSelected();

        for (int i = 0; i < PREVIEW_BOARD_SIZE; i++)
        {
            for (int j = 0; j < PREVIEW_BOARD_SIZE; j++)
            {
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
                    default:
                        displayButtons[i][j].setBackgroundResource(
                                R.drawable.preview_background_blok);
                        break;
                }

                if (displayButtons[i][j].isSelected())
                {
                    displayAsValidCorner(displayButtons[i][j], SELECTED_PREVIEW_BLOK_PADDING);
                }
                else
                {
                    displayButtons[i][j].setImageResource(android.R.color.transparent);
                }
            }
        }
    }

    /**
     * display only the pieces that the player has not played
     * yet as available to be placed on the board
     */
    private void updatePieceButtons()
    {
        pieceLayout.removeAllViews();
        for (int i = 0; i < 21; i++)
        {
            pieceLayout.addView(pieceButtons[i]);
        }

        int[] curPieces = newState.getPlayerPieces()[this.playerNum];
        for (int i = 0; i < curPieces.length; i++)
        {
            if (curPieces[i] == -1)
            {
                pieceLayout.removeView(pieceButtons[i]);
            }
        }
    }

    /**
     * set the confirm button as green if the player
     * has selected all of the necessary information
     * and the chosen move is valid;
     * otherwise set the confirm button to be red.
     * Also draws a preview of the move on the 20x20
     * board if the current move is valid
     */
    private void updateConfirmButton()
    {
        if (selectedBoardBlok == null ||
                selectedPiece == null ||
                selectedPieceBlokID == -1)
        {
            confirmButton.setBackgroundColor(Color.RED);
            return;
        }

        ArrayList<PieceBlok> move = newState.prepareValidMove(this.playerNum,
            selectedBoardBlok, selectedPiece, selectedPieceBlokID, gameBoard);
        if (move != null)
        {
            newState.placePiece(move, gameBoard);
            confirmButton.setBackgroundColor(Color.GREEN);
        }
        else
        {
            confirmButton.setBackgroundColor(Color.RED);
        }
    }

    /**
     * draws the image on the given button with the
     * specified padding.
     *
     * @param button the button to draw image to
     * @param padding the padding to apply to the image
     */
    private void displayAsValidCorner(ImageButton button, int padding)
    {
        button.setImageResource(R.drawable.circle_check);
        button.setPadding(padding,padding,padding,padding);
        button.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    /**
     * draws the image on the given button with the
     * specified padding.
     *
     * @param button the button to draw image to
     * @param padding the padding to apply to the image
     */
    private void displayAsSelectedCorner(ImageButton button, int padding)
    {
        button.setImageResource(R.drawable.circle_target);
        button.setPadding(padding,padding,padding,padding);
        button.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private boolean blokIsValid(Blok selectedBlok)
    {
        ArrayList<Blok> validCorners = newState.getValidCorners(newState.getPlayerTurn());

        return validCorners.contains(selectedBlok);
    }

    /**
     * de-selects all buttons on the given array of buttons
     * (either the preview board or the game board)
     */
    private void resetSelectedBoardSpaces(BoardButton[][] targetBoard)
    {
        for (int i = 0; i < targetBoard.length; i++)
        {
            for (int j = 0; j < targetBoard.length; j++)
            {
                targetBoard[i][j].setSelected(false);
            }
        }
    }

    private void setBoardBlokSelected()
    {
        if (selectedBoardBlok != null)
        {
            int row = this.selectedBoardBlok.getRow();
            int col = this.selectedBoardBlok.getColumn();

            // -1 bc boardButtons is 20x20 and selectedBoardBlok's coordinates
            // are based on a 22x22 array
            boardButtons[row-1][col-1].setSelected(true);
        }
    }

    private void setPieceBlokSelected()
    {
        for (int i = 0; i < displayButtons.length; i++)
        {
            for (int j = 0; j < displayButtons.length; j++)
            {
                if (this.selectedPieceBlokID >= 0 &&
                        this.selectedPieceBlokID == previewBoard[i+1][j+1].getId())
                {
                    displayButtons[i][j].setSelected(true);
                }
            }
        }
    }

    //Listener for BoardButtons
    public class BlokButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View v)
        {
            // one of the buttons on the 20x20 grid
            if (v instanceof BoardButton)
            {
                int row = ((BoardButton) v).getRow();
                int col = ((BoardButton) v).getCol();
                Blok selectedBlok = newState.getBoardState()[row+1][col+1];

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
                    if (selectedPiece.getPieceShape()[id].hasCorner())
                    {
                        SelectBlokOnSelectedPieceAction selPieceBlok =
                                new SelectBlokOnSelectedPieceAction(BlokusHumanPlayer.this, id);
                        game.sendAction(selPieceBlok);
                    }
                }
            }
        }
    }

    // Listener for the buttons to orient or place a piece on the board
    public class PieceControlListener implements Button.OnClickListener {

        public void onClick(View v) {
            //resetSelectedBoardSpaces(displayButtons);

            if (v == confirmButton)
            {
                resetSelectedBoardSpaces(boardButtons);
                ConfirmPiecePlacementAction confirm =
                        new ConfirmPiecePlacementAction(BlokusHumanPlayer.this);
                game.sendAction(confirm);
            }

            if (v == rotateButton)
            {
                RotateSelectedPieceAction rotate =
                        new RotateSelectedPieceAction(BlokusHumanPlayer.this);
                game.sendAction(rotate);
            }

            if (v == flipButton)
            {
                FlipSelectedPieceAction flip =
                        new FlipSelectedPieceAction(BlokusHumanPlayer.this);
                game.sendAction(flip);
            }
        }
    }

    //Listener for the buttons used to select a piece
    public class PieceButtonListener implements Button.OnClickListener {

        public void onClick(View v) {
            resetSelectedBoardSpaces(displayButtons);

            //if the button selected is already highlighted
            if (((PieceButton)v).isHighlighted())
            {
                v.setBackgroundResource(R.drawable.button_border);
                ((PieceButton) v).setIsHighlighted(false);
                selectedPiece = null;
                SelectPieceTemplateAction sel =
                        new SelectPieceTemplateAction(BlokusHumanPlayer.this, -1);

                game.sendAction(sel);
            }
            //if the button selected is not highlighted
            else if( !((PieceButton) v).isHighlighted() && ((PieceButton) v).getButtonPiece() != -1)
            {
                resetHighlightedPieceButtons(); //unhighlight other buttons

                v.setBackgroundResource(R.drawable.highlighted_button_border);
                ((PieceButton) v).setIsHighlighted(true);
                int selectedPieceID = ((PieceButton) v).getButtonPiece();
                SelectPieceTemplateAction selectPiece =
                        new SelectPieceTemplateAction(BlokusHumanPlayer.this, selectedPieceID);

                game.sendAction(selectPiece);
            }
        }
    }

    private void resetHighlightedPieceButtons()
    {
        for (PieceButton pb: pieceButtons)
        {
            if (pb.isHighlighted())
            {
                pb.setIsHighlighted(false);
                pb.setBackgroundResource(R.drawable.button_border);
            }
        }
    }
}


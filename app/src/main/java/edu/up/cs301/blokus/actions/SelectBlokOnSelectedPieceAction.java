package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.blokus.PieceBlok;
import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lowa19 on 3/5/2017.
 */
public class SelectBlokOnSelectedPieceAction extends GameAction implements Serializable{

    private static final long serialVersionUID = 492017005L;

    private int selectedBlokId;

    /**
     * constructor
     * @author Adrian Low
     * @author Cole French
     * @author Devin Ajimine
     * @author Evan Sterba
     * @param player the player who created the action
     * @param selectedBlokId the blok ID on the piece that was selected
     *Player selects the corner on the Piece preview screen that will martch up according to the corner that is on the board
     */
    public SelectBlokOnSelectedPieceAction(GamePlayer player, int selectedBlokId)
    {
        super(player);

        this.selectedBlokId = selectedBlokId;
    }

    public int getSelectedBlokId()
    {
        return this.selectedBlokId;
    }
}


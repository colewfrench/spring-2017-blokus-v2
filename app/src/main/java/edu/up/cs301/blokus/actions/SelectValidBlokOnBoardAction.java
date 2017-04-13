package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.blokus.Blok;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lowa19 on 3/5/2017.
 */
public class SelectValidBlokOnBoardAction extends GameAction implements Serializable{

    private static final long serialVersionUID = 492017003L;

    private Blok selectedBlok;

    /**
     * constructor
     * @author Adrian Low
     * @author Cole French
     * @author Devin Ajimine
     * @author Evan Sterba
     * @param player the player who created the action
     * @param selectedBlok the blok that was selected within the valid
     *                    moves array
     */
    public SelectValidBlokOnBoardAction(GamePlayer player, Blok selectedBlok) {
        super(player);

        this.selectedBlok = selectedBlok;
    }

    public Blok getSelectedBlok()
    {
        return this.selectedBlok;
    }
}


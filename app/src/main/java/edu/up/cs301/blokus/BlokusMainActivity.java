package edu.up.cs301.blokus;


import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * BlokusMainActivity class
 * initializes the Blokus board and sets all of the listeners
 * necessary for interacting with the GUI elements
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class BlokusMainActivity extends GameMainActivity {

    // the port number that this game will use when playing over the network
    private static final int PORT_NUMBER = 2234;

    /**
     * Create the default configuration for this game:
     * - one human player vs. three simple computer players
     * - minimum of 4 players, maximum of 4
     * - two kinds of computer players and one kind of human player available
     *
     * @return
     * 		the new configuration object, representing the default configuration
     */
    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // a human player player type (player type 0)
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new BlokusHumanPlayer(name);
            }});

        // a computer player type (player type 1)
        playerTypes.add(new GamePlayerType("Simple Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new BlokusSimpleComputerPlayer(name);
            }});

        // a computer player type (player type 2)
        playerTypes.add(new GamePlayerType("Advanced Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new BlokusAdvancedComputerPlayer(name);
            }});

        // Create a game configuration class for Counter:
        // - player types as given above
        // - Only Four Players
        // - name of game is "Blokus"
        // - port number as defined above
        GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Blokus",
                PORT_NUMBER);

        // Add the default players to the configuration
        defaultConfig.addPlayer("Adrian", 0); // player 1: a human player
        defaultConfig.addPlayer("Cole", 1); // player 2: a computer player
        defaultConfig.addPlayer("Devin", 1); // player 3: a computer player
        defaultConfig.addPlayer("Evan", 1); // player 4: a computer player

        // Set the default remote-player setup:
        // - player name: "Remote Player"
        // - IP code: (empty string)
        // - default player type: human player
        defaultConfig.setRemoteData("Remote Player", "", 0);

        // return the configuration
        return defaultConfig;
    }//createDefaultConfig

    /**
     * create a local game
     *
     * @return
     * 		the local game, a counter game
     */
    @Override
    public LocalGame createLocalGame() {
        return new BlokusLocalGame();
    }
}

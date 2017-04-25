package com.example.frenchco19.blokus;

import org.junit.Test;

import edu.up.cs301.blokus.BlokusGameState;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void pCMTest() throws Exception {
        BlokusGameState state = new BlokusGameState();

        int[][] pieces = new int[4][21];

        for (int i = 0; i < 21; i++)
        {
            pieces[0][i] = -1;
        }

        //pieces[0][12] = 12;
        state.setPlayerPieces(pieces);

        assertEquals(state.playerCanMove(0), false);
    }
}
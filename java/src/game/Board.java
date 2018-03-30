/*  This file is part of PegboardApp.
 *
 *  Copyright 2012 Bryan Bueter
 *
 *  PegboardApp is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PegboardApp is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PegboardApp.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package game;

import event.*;
import java.util.*;

// A peg board
public class Board {
    private Peg[] pegs;
    private Peg[][] directions;

    private Peg emptyPeg;
    private Vector moves;

    private ObjectHandler eventHandler = null;

    public static final int PEGS = 15;

    // Directions in clockwise order
    public static final int TOPLEFT = 0;
    public static final int TOPRIGHT = 1;
    public static final int RIGHT = 2;
    public static final int BOTRIGHT = 3;
    public static final int BOTLEFT = 4;
    public static final int LEFT = 5;

    // Constructor, initialization routines
    public Board() {
        eventHandler = new ObjectHandler(this);

        initComponents();
    }
    private void initComponents() {
        // Initialize the pegs
        pegs = new Peg[PEGS];
        for (int index=0; index<PEGS; index++) {
            pegs[index] = new Peg(index);
            pegs[index].fill();
        }

        // Each array contains which peg points int the direciton
        // (TOPLEFT, TOPRIGHT, RIGHT, BOTRIGHT, LEFT)
        Peg[][] initDirections= {
            {null,     null,     null,     pegs[2],  pegs[1],  null},
            {null,     pegs[0],  pegs[2],  pegs[4],  pegs[3],  null},
            {pegs[0],  null,     null,     pegs[5],  pegs[4],  pegs[1]},
            {null,     pegs[1],  pegs[4],  pegs[7],  pegs[6],  null},
            {pegs[1],  pegs[2],  pegs[5],  pegs[8],  pegs[7],  pegs[3]},
            {pegs[2],  null,     null,     pegs[9],  pegs[8],  pegs[4]},
            {null,     pegs[3],  pegs[7],  pegs[11], pegs[10], null},
            {pegs[3],  pegs[4],  pegs[8],  pegs[12], pegs[11], pegs[6]},
            {pegs[4],  pegs[5],  pegs[9],  pegs[13], pegs[12], pegs[7]},
            {pegs[5],  null,     null,     pegs[14], pegs[13], pegs[8]},
            {null,     pegs[6],  pegs[11], null,     null,     null},
            {pegs[6],  pegs[7],  pegs[12], null,     null,     pegs[10]},
            {pegs[7],  pegs[8],  pegs[13], null,     null,     pegs[11]},
            {pegs[8],  pegs[9],  pegs[14], null,     null,     pegs[12]},
            {pegs[9],  null,     null,     null,     null,     pegs[13]},
        };
        directions = initDirections;
        emptyPeg = null;
        moves = new Vector();
    }

    // Public interface
    public ObjectHandler eventHandler() { return eventHandler; }

    // Fill in all the pegs, awaiting the blank spot to be picked
    public void reset() {
        for (int peg=0; peg<PEGS; peg++) {
            pegs[peg].fill();
        }

        moves.clear();
        emptyPeg = null;
        eventHandler.callback();
    }

    // Reset any highlighted pegs
    public void clear() {
        for (int peg=0; peg<PEGS; peg++) {
            if ( pegs[peg].state() != Peg.EMPTY ) {
                pegs[peg].fill();
            }
        }

        eventHandler.callback();
    }

    // Actually pick the empty peg
    public void setEmptyPeg(Peg peg) {
        if ( peg == null ) { return; }
        
        for (int index=0; index<PEGS; index++) {
            if ( pegs[index] == peg ) {
                peg.empty();
                eventHandler.callback();

                emptyPeg = peg;
                return;
            }
        }
    }

    // Return the empty peg
    public Peg getEmptyPeg() { return emptyPeg; }

    // Attempt to make a move, putting Peg1 into Peg2 and removing the jumped peg
    public boolean jump(int peg1, int peg2) {

        if ( canJump(peg1, peg2) ) {
            // Make the jump
            int direction = direction(peg1, peg2);

            pegs[peg1].empty();
            pegs[peg2].fill();
            directions[peg1][direction].empty();

            moves.add(new Integer(peg1));
            moves.add(new Integer(peg2));

            eventHandler.callback();
            return true;
        }
        return false;
    }

    // Return the peg requested by index
    public Peg peg(int peg) {
        if ( peg < 0 || peg >= PEGS ) { return null; }

        return pegs[peg];
    }

    // Highlight the peg selected
    public void selectPeg(Peg peg) {
        for (int index=0; index<PEGS; index++) {
            if ( pegs[index] == peg ) {
                clear();

                peg.select();
                eventHandler.callback();

                return;
            }
        }
    }

    // Show a move on the board, both the peg jumping and the peg jumped
    public void showMove(int peg1, int peg2) {
        if ( peg1 >= PEGS || peg1 < 0 || peg2 >= PEGS || peg2 < 0 ) { return; }
        if ( ! canJump(peg1, peg2) ) { return; }

        int direction = direction(peg1, peg2);
        int peg3 = directions[peg1][direction].value();

        pegs[peg1].select();
        pegs[peg3].jump();

        eventHandler.callback();
    }

    // Determine if a jump from peg1 into peg2 is possible
    private boolean canJump(int peg1, int peg2) {
        if ( peg1 >= PEGS || peg1 < 0 || peg2 >= PEGS || peg2 < 0 ) { return false; }

        // Find the direction from Peg1 to Peg2
        int direction = direction(peg1, peg2);
        if ( direction < 0 ) { return false; }

        if ( direction >= 0 && direction <= LEFT ) {
            // Make sure this peg has something in it
            if ( pegs[peg1].state() == Peg.EMPTY ) {
                return false;
            }

            // Make sure the peg we are jumping is not empty
            if ( directions[peg1][direction].state() == Peg.EMPTY ) {
                return false;
            }

            // Verify the peg we are going to land in is empty
            if ( pegs[peg2].state() != Peg.EMPTY ) {
                return false;
            }

            // All tests pass, return true
            return true;
        }

        return false;
    }

    // Determine the direction from one peg to the next.  Maximum of two hops
    private int direction(int peg1, int peg2) {
        if ( peg1 >= PEGS || peg1 < 0 || peg2 >= PEGS || peg2 < 0 ) { return -1; }

        // Find the direction from Peg1 to Peg2
        int direction = 0;
        while (direction <= LEFT ) {
            // Find adjacent pegs
            if ( directions[peg1][direction] == pegs[peg2] ) {
                return direction;
            }
            
            // Find pegs that are one hop away
            if ( directions[peg1][direction] != null ) {
                Peg adjPeg = directions[peg1][direction];
                if ( directions[adjPeg.value()][direction] != null) {
                    if (directions[adjPeg.value()][direction] == pegs[peg2]) {
                        return direction;
                    }
                }
            }

            direction++;
        }

        return -1;
    }

    // Determine if this board has any moves left
    public boolean hasMovesLeft() {
        for (int peg=0; peg<PEGS; peg++) {
            for (int direction=0; direction<=LEFT; direction++) {
                if ( directions[peg][direction] != null ) {
                    int pegTo = directions[peg][direction].value();
                    if ( directions[pegTo][direction] != null ) {
                        if ( canJump(peg, directions[pegTo][direction].value()) ) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    // Number of pegs still filled on the board
    public int pegsLeft() {
        int count = 0;

        for (int index=0; index<pegs.length; index++) {
            if ( pegs[index].state() == Peg.FILLED ) {
                count++;
            }
        }

        return count;
    }

    // Keep track of the moves and return them if requested
    public Object[] moves() {
        return moves.toArray();
    }

    // Remove the last move from the board
    public void undo() {
        clear();
        if ( moves.size() == 0 ) { return; }
        
        Integer pegTo = (Integer) moves.lastElement();
        moves.remove(moves.size()-1);

        Integer pegFrom = (Integer) moves.lastElement();
        moves.remove(moves.size()-1);

        int direction = direction(pegFrom, pegTo);

        pegs[pegTo].empty();
        pegs[pegFrom].fill();
        directions[pegFrom][direction].fill();

        eventHandler.callback();
    }
}

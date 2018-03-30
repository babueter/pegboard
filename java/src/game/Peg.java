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

// Single peg on a board
public class Peg {
    private int value;
    private int state;

    public static final int EMPTY = 0;
    public static final int FILLED = 1;
    public static final int SELECTED = 2;
    public static final int TOBEJUMPED = 3;

    protected Peg(int initValue) {
        value = initValue;
        state = EMPTY;
    }

    // Public access for accessing our values
    public int state() { return state; }
    public int value() { return value; }

    // Protected access for changing our state
    protected void empty() { state = EMPTY; }
    protected void fill() { state = FILLED; }
    protected void select() {
        if ( state == FILLED ) {
            state = SELECTED;
        }
    }
    protected void jump() {
        if ( state == FILLED ) {
            state = TOBEJUMPED;
        }
    }

}

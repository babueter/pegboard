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

package gui;

import event.*;
import data.*;
import game.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Main interface for running this board
public class GuiPegboardApp extends JFrame implements ObjectListener, ActionListener {
    private Dawg solutions;     // All solutions
    private Dawg currentMove;   // Current solution path

    // Game and GUI objects
    private Board board;
    private GuiBoard guiBoard;
    private JLabel messageLabel;
    private JLabel solutionsLabel;
    private int state = PICKING_EMPTY;

    // Keep track of the peg we are jumping from
    private Peg fromPeg = null;

    // States of the game
    public static final int PICKING_EMPTY = 0;
    public static final int WAITING = 1;
    public static final int JUMPING = 2;
    public static final int FINISHED = 3;

    // Factors for translating peg nubmers after rotation.
    // factors[peg][# of rotations]
    public static final Integer[][] factors = {
        {0, 14, 10},
        {1, 9, 11},
        {2, 13, 6},
        {3, 5, 12},
        {4, 8, 7},
        {5, 12, 3},
        {6, 2, 13},
        {7, 4, 8},
        {8, 7, 4},
        {9, 11, 1},
        {10, 0, 14},
        {11, 1, 9},
        {12, 3, 5},
        {13, 6, 2},
        {14, 10, 0},};

    // Number of rotations required to position the peg into the starting 5 (0-4)
    public static final Integer[] rotations = {
        0, 0, 0, 0, 0,
        2, 1, 1, 2, 2,
        1, 1, 1, 2, 2,};

    // Start the app
    public GuiPegboardApp() {
        // Master solutions
        solutions = new Dawg(getClass().getResourceAsStream("/data/solutions.dawg"));

        initComponents();
    }

    // Init the GUI componenents
    public void initComponents() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Game components
        board = new Board();
        guiBoard = new GuiBoard(board);
        guiBoard.eventHandler().register(this);

        messageLabel = new JLabel("Select the peg to be empty.");
        JLabel solutionsTextLabel = new JLabel("Solutions left:");
        solutionsLabel = new JLabel("0");

        JPanel buttonBox = new JPanel();
        JButton resetButton = new JButton("Start Over");
        JButton hintButton = new JButton("Suggest");
        JButton undoButton = new JButton("Undo Move");

        buttonBox.add(undoButton);
        buttonBox.add(hintButton);
        buttonBox.add(resetButton);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.NONE;
        c1.anchor = GridBagConstraints.PAGE_END;
        c1.gridx = 0;

        GridBagConstraints c2 = (GridBagConstraints) c1.clone();
        c2.fill = GridBagConstraints.HORIZONTAL;

        JSeparator separator1 = new JSeparator();
        JSeparator separator2 = new JSeparator();

        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(guiBoard, c1);
        this.getContentPane().add(separator1, c2);
        this.getContentPane().add(messageLabel, c1);
        this.getContentPane().add(separator2, c2);
        this.getContentPane().add(solutionsTextLabel, c1);
        this.getContentPane().add(solutionsLabel, c1);
        this.getContentPane().add(buttonBox, c1);
        this.pack();

        resetButton.setActionCommand("reset");
        hintButton.setActionCommand("hint");
        undoButton.setActionCommand("undo");

        resetButton.addActionListener(this);
        hintButton.addActionListener(this);
        undoButton.addActionListener(this);

        this.setTitle("Peg Board Game");
        this.setResizable(false);
    }


    // Instead of having a DAWG with every possible move, we have a DAWG with moves for
    // blank pegs in 0-4.  For the other pegs we rotate the board clockwise and translate
    // the pegs to the new position.

    // rotatePeg takes a peg from the game and converts it to a peg in the DAWG
    public int rotatedPeg(int peg) {
        if ( board.getEmptyPeg() == null ) { return peg; }

        return factors[peg][rotations[board.getEmptyPeg().value()]];
    }

    // unRotatePeg takes a peg from the DAWG and converts it to a peg in the game
    public int unRotatedPeg(int peg) {
        if ( board.getEmptyPeg() == null ) { return peg; }

        int reverseRotations = 3 - rotations[board.getEmptyPeg().value()];

        if ( reverseRotations == 3 ) {
            return peg;
        }
        return factors[peg][reverseRotations];
    }

    // Main function, start the thread
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuiPegboardApp().setVisible(true);
            }
        });
    }

    // Set the number of solutions based on the current position of the DAWG
    public void updateSolutionsLabel() {
        if (currentMove != null) {
            solutionsLabel.setText("" + currentMove.solutions());
        } else {
            solutionsLabel.setText("0");
        }
    }

    // guiBoard was clicked, capture the peg and take action based on the current state
    public void objectUpdated(ObjectHandler o) {
        // We dont watch any other type of objects but...
        if ( o.getEntity() instanceof GuiBoard ) {

            // We havnt yet picked an empty peg, make the selected peg empty
            if ( state == PICKING_EMPTY ) {
                Peg selectedPeg = guiBoard.lastPegClicked();
                if (selectedPeg != null) {
                    board.setEmptyPeg(selectedPeg);
                    state = WAITING;

                    currentMove = solutions.child(rotatedPeg(selectedPeg.value()));
                    messageLabel.setText("Select the peg to move");

                    updateSolutionsLabel();
                }

            // Waiting to start a move, highlight the peg selected and move to JUMPING state
            } else if ( state == WAITING ) {
                fromPeg = guiBoard.lastPegClicked();
                if ( fromPeg == null ) { return; }

                if (fromPeg.state() != Peg.EMPTY) {
                    board.selectPeg(fromPeg);
                    messageLabel.setText("Select where to jump");
                    state = JUMPING;
                }

            // Chosing a spot to land in after selecting a peg.
            } else if ( state == JUMPING ) {
                Peg toPeg = guiBoard.lastPegClicked();
                if ( toPeg != null ) {
                    if (board.jump(fromPeg.value(), toPeg.value())) {
                        // Advance the currentMove DAWG
                        if (currentMove != null) {
                            currentMove = currentMove.child(rotatedPeg(fromPeg.value()));
                        }
                        if (currentMove != null) {
                            currentMove = currentMove.child(rotatedPeg(toPeg.value()));
                        }

                        updateSolutionsLabel();
                    }
                }

                // Change state back to WAITING no matter what was clicked
                board.clear();
                state = WAITING;

                // Still moves left, continue with the game
                if ( board.hasMovesLeft() ) {
                    messageLabel.setText("Select the peg to move");

                // End of the game, decide if they won or not
                } else {
                    if ( board.pegsLeft() == 1 ) {
                        messageLabel.setText("You Win!");
                    } else {
                        messageLabel.setText("You Lose!");
                    }
                    state = FINISHED;
                }
            }
        }
    }

    // One of the buttons was clicked, take action depending on which one.
    public void actionPerformed(ActionEvent e) {

        // Reset the board to the picking an empty peg state
        if ( e.getActionCommand().equals("reset") ) {
            state = PICKING_EMPTY;
            board.reset();
            currentMove = null;

            messageLabel.setText("Select the peg to be empty.");
            updateSolutionsLabel();

        // Reveal the best move on the board, if one exists
        } else if ( e.getActionCommand().equals("hint") ) {
            if ( currentMove != null && board.hasMovesLeft() ) {
                // Walk the DAWG to find the best starting peg
                Dawg bestChild1 = currentMove.child();
                Dawg child = currentMove.child();
                while ( child != null ) {
                    if ( child.solutions() > bestChild1.solutions() ) {
                        bestChild1 = child;
                    }
                    child = child.sibling();
                }

                // Walk the DAWG to find the best landing spot
                Dawg bestChild2 = bestChild1.child();
                child = bestChild2;
                while ( child != null ) {
                    if ( child.solutions() > bestChild1.solutions() ) {
                        bestChild1 = child;
                    }
                    child = child.sibling();
                }

                // Reveal move if it exists
                if ( bestChild1 != null && bestChild2 != null ) {
                    board.showMove(unRotatedPeg(bestChild1.value()), unRotatedPeg(bestChild2.value()));
                }
            }

        // Undo the last move made, update the currentMove DAWG as well.
        } else if ( e.getActionCommand().equals("undo") ) {
            board.undo();
            if ( board.getEmptyPeg() == null ) { return; }

            currentMove = solutions.child(rotatedPeg(board.getEmptyPeg().value()));
            Object[] moves = board.moves();
            int count = 0;

            // Walk the moves array and update the currentMove
            while ( count < moves.length && currentMove != null ) {
                currentMove = currentMove.child(rotatedPeg((Integer)moves[count]));
                count++;
            }

            updateSolutionsLabel();
            messageLabel.setText("Select the peg to move");
        }
    }

}

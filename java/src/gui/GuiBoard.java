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
import game.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import javax.imageio.*;

// Gui representation of a Board object
public class GuiBoard extends Canvas implements MouseListener, ObjectListener {
    private Board board;
    private ObjectHandler eventHandler = null;

    // Keep track of the last peg clicked
    private Peg lastPegClicked = null;

    private static final int preferredHeight = 234;
    private static final int preferredWidth = 315;

    private static final int preferredPegHeight = 76;
    private static final int preferredPegWidth = 35;

    // This is [row][col] for x,y translation.  Returns the peg clicked, or -1
    private static final int[][] pegClickMatrix = {
        { -1, -1, -1, -1,  0, -1, -1, -1, -1},
        { -1, -1, -1,  1,  0,  2, -1, -1, -1},
        { -1, -1,  3,  1,  4,  2,  5, -1, -1},
        { -1,  6,  3,  7,  4,  8,  5,  9, -1},
        { 10,  6, 11,  7, 12,  8, 13,  9, 14},
        { 10, -1, 11, -1, 12, -1, 13, -1, 14},
    };

    // Images of a page indexed by the state value
    private BufferedImage[] pegImages;

    public GuiBoard(Board initBoard) {
        board = initBoard;
        board.eventHandler().register(this);

        this.setSize(preferredWidth, preferredHeight);

        try {
            BufferedImage[] initPegImages = {
                ImageIO.read(getClass().getResourceAsStream("/images/pegEmpty.PNG")),
                ImageIO.read(getClass().getResourceAsStream("/images/pegFilled.PNG")),
                ImageIO.read(getClass().getResourceAsStream("/images/pegSelected.PNG")),
                ImageIO.read(getClass().getResourceAsStream("/images/pegToBeJumped.PNG")),};
            pegImages = initPegImages;

        } catch (IOException e) {
            System.exit(0);
        }

        addMouseListener(this);
        eventHandler = new ObjectHandler(this);
        repaint();
    }

    // Event handler for this object
    public ObjectHandler eventHandler() {
        if ( eventHandler == null ) { eventHandler = new ObjectHandler(this); }

        return eventHandler;
    }
    public Peg lastPegClicked() { return lastPegClicked; }

    @Override
    public void paint(Graphics g) {
        // Paint a buffered image to be scaled to current window
        java.awt.Image bufferImage = createImage(preferredWidth, preferredHeight);
        paintPreferredSize(bufferImage.getGraphics());

        // Scale the current window size
        int width = getWidth();
        int height = getHeight();
        ReplicateScaleFilter scale = new ReplicateScaleFilter(width, height);
        FilteredImageSource fis = new FilteredImageSource(bufferImage.getSource(), scale);
        Image scaledImage = createImage(fis);

        g.drawImage(scaledImage, 0, 0, null);
    }
    private void paintPreferredSize(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        // Draw each peg on the board
        float boardCenter = preferredWidth / 2;
        float pegCenter = preferredPegWidth / 2;
        float pegMiddle = preferredPegHeight / 2;

        // Row 1 has just peg1
        drawPeg(g, board.peg(0), Math.round(boardCenter-pegCenter), 0);

        // Row 2 has peg2, peg3
        drawPeg(g, board.peg(1), Math.round(boardCenter-pegCenter-preferredPegWidth), Math.round(pegMiddle));
        drawPeg(g, board.peg(2), Math.round(boardCenter+pegCenter), Math.round(pegMiddle));

        // Row 3 has peg4 - peg6
        drawPeg(g, board.peg(3), Math.round(boardCenter-pegCenter-preferredPegWidth*2), Math.round(pegMiddle*2));
        drawPeg(g, board.peg(4), Math.round(boardCenter-pegCenter), Math.round(pegMiddle*2));
        drawPeg(g, board.peg(5), Math.round(boardCenter+pegCenter+preferredPegWidth), Math.round(pegMiddle*2));

        // Row 4 has peg7 - peg10
        drawPeg(g, board.peg(6), Math.round(boardCenter-pegCenter-preferredPegWidth*3), Math.round(pegMiddle*3));
        drawPeg(g, board.peg(7), Math.round(boardCenter-pegCenter-preferredPegWidth), Math.round(pegMiddle*3));
        drawPeg(g, board.peg(8), Math.round(boardCenter+pegCenter), Math.round(pegMiddle*3));
        drawPeg(g, board.peg(9), Math.round(boardCenter+pegCenter+preferredPegWidth*2), Math.round(pegMiddle*3));

        // Row 5 has peg11 - peg15
        drawPeg(g, board.peg(10), Math.round(boardCenter-pegCenter-preferredPegWidth*4), Math.round(pegMiddle*4));
        drawPeg(g, board.peg(11), Math.round(boardCenter-pegCenter-preferredPegWidth*2), Math.round(pegMiddle*4));
        drawPeg(g, board.peg(12), Math.round(boardCenter-pegCenter), Math.round(pegMiddle*4));
        drawPeg(g, board.peg(13), Math.round(boardCenter+pegCenter+preferredPegWidth), Math.round(pegMiddle*4));
        drawPeg(g, board.peg(14), Math.round(boardCenter+pegCenter+preferredPegWidth*3), Math.round(pegMiddle*4));

    }
    private void drawPeg(Graphics g, Peg peg, int x, int y) {
        Graphics2D g2 = (Graphics2D)g;

        g2.drawImage(pegImages[peg.state()], null, x, y);
    }

    @Override
    public void update(Graphics g) { paint(g); }

    // Return the peg object that cooresponds to the x,y coordinates, null otherwise
    private Peg pegClicked(int x, int y) {
        int column = x/preferredPegWidth;
        int row = y/(preferredPegHeight/2);

        if ( column < 0 || row < 0 || column > 8 || row > 5 ) { return null; }

        return board.peg(pegClickMatrix[row][column]);
    }

    // Store last peg clicked, callback listening objects
    public void mouseClicked(MouseEvent e) {
        // Translate x and y coordinates to scale
        int x = e.getX();
        int y = e.getY();

        x /= (float)this.getWidth()/preferredWidth;
        y /= (float)this.getHeight()/preferredHeight;

        lastPegClicked = pegClicked(x, y);
        eventHandler.callback();
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    // Board object was updated, repaint
    public void objectUpdated(ObjectHandler o) {
        this.repaint();
    }
}

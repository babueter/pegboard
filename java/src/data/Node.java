/*  This file is part of Wordplay Trainer.
 *
 *  Copyright 2012 Bryan Bueter
 *
 *  Wordplay Trainer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Wordplay Trainer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Wordplay Trainer.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package data;

// Basic node template for a tree data structure.
public class Node <T> {
    private T sibling;
    private T child;
    private int value;
    private boolean terminal;

    public Node () {
        sibling = null;
        child = null;
        value = 0x0;
        terminal = false;

    }
    public int value() { return value; }
    protected void value(int newValue) {
        value = newValue;
    }

    public boolean hasSibling() {
        if ( sibling == null ) {
            return false;
        }
        return true;
    }
    public T sibling() { return sibling; }
    protected void sibling(T siblingNode) {
        sibling = siblingNode;
    }

    public boolean hasChild() {
        // Does this node even have a child
        if ( child == null ) {
            return false;
        }
        return true;
    }
    public boolean hasChild(int childValue) {
        // Does this node have a child of childValue
        T nextChild = child;
        while ( nextChild != null ) {
            if ( ((Node)nextChild).value == childValue ) { return true; }
            nextChild = (T) ((Node)nextChild).sibling();
        }
        return false;
    }

    public T child() {
        return child;
    }
    public T child(int childValue) {
        // Return the node value of the child with the value indicated, null otherwise
        T nextChild = child;
        while ( nextChild != null ) {
            if ( ((Node)nextChild).value == childValue ) { return nextChild; }
            nextChild = (T) ((Node)nextChild).sibling();
        }

        return null;
    }
    protected void child(T childNode) {
        child = childNode;
    }

    public boolean isTerminal() { return terminal; }
    protected void terminal(Boolean value) {
        terminal = value;
    }
    
    public void print() {
        System.out.println(this.toString()+" ("+value+") : ("+sibling+"/"+child+")");
    }
}


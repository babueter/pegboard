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

package event;

// Must be implemented if you wish to get called back from ObjectHandler
public interface ObjectListener {
    public void objectUpdated(ObjectHandler o);
}

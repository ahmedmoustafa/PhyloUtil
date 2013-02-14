/*
 * $Id: Path.java,v 1.1.1.1 2007/05/29 16:11:41 ahmed Exp $
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package phyloutil;

import java.util.ArrayList;

/**
 * Path from the root of a tree to some other node in the tree. It is simply an
 * {@link ArrayList} of {@link Integer}s (the ids) of the nodes in the path.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.1.1.1 $
 */

public final class Path extends ArrayList<Integer> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3219999536658557694L;

	public boolean contains(int id) {
		int size = this.size();
		int index = 0;
		boolean found = false;
		while (!found && index < size) {
			if (get(index).intValue() == id) {
				found = true;
			} else {
				index++;
			}
		}
		return found;
	}
}
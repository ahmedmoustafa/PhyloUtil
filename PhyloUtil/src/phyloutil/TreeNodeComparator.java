/*
 * $Id: TreeNodeComparator.java,v 1.1.1.1 2007/05/29 16:11:41 ahmed Exp $
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

import java.util.Comparator;

/**
 * A comparator between between two nodes in a tree.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.1.1.1 $
 */
public class TreeNodeComparator implements Comparator<TreeNode> {

	/**
	 * Compares between two subtrees based on their branch lengths.
	 * 
	 * @param t1
	 *            The first node
	 * @param t2
	 *            The second node
	 * 
	 * @return 0 if <code>t1</code> = <code>t2</code>, 1 if <code>t1</code>
	 *         &gt; <code>t2</code>, or -1 if <code>t1</code> &lt;
	 *         <code>t2</code>
	 */
	public int compare(TreeNode t1, TreeNode t2) {

		float len1 = t1.getLength();

		float len2 = t2.getLength();

		if (len1 > len2) {
			return 1;
		} else if (len1 < len2) {
			return -1;
		} else {
			return 0;
		}
	}
}
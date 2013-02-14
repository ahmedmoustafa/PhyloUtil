/*
 * $Id: NewickParser.java,v 1.5 2007/06/29 15:11:15 ahmed Exp $
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

import phyloutil.misc.NewickConstants;

/**
 * A parser to <a
 * href="http://evolution.genetics.washington.edu/phylip/newick_doc.html">Newick
 * (8:45) tree standard format</a>.
 * 
 * Currently, it is assumed that no whitespaces are included in the text of the
 * tree.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.5 $
 */

public final class NewickParser {

	/**
	 * Parses a tree string into a {@link TreeNode}
	 * 
	 * @param str
	 *            The string to parse
	 * @return {@link TreeNode} representation of the tree string
	 */
	public static TreeNode parse(String str) {
		if (str.charAt(str.length() - 1) == NewickConstants.SEMI_COLON) {
			// Remove the semicolon at the end of the tree syntax
			String str2 = str.substring(0, str.length() - 1);
			return parse(str2, null);
		} else {
			return null;
		}
	}

	/**
	 * Parses a tree string into a {@link TreeNode}
	 * 
	 * @param _str
	 *            The string to parse
	 * @param parent
	 *            The parent of the tree of the tree to parse
	 * @return {@link TreeNode} representation of the tree string
	 */
	public static TreeNode parse(String _str, TreeNode parent) {

		String str = _str.trim();

		TreeNode tree = new TreeNode();

		if (parent == null) {
			tree.setLevel(TreeNode.DEFAULT_ROOT_LEVEL);
			tree.setId(TreeNode.DEFAULT_ROOT_ID);
		} else {
			tree.setLevel(parent.getLevel() + 1);
			tree.setId(parent.getMaximumId() + 1);
		}

		// Null parent means this is the root of the tree
		tree.setParent(parent);

		if (str.indexOf(NewickConstants.RIGHT_PARENTHESIS) == -1) {

			// Terminal node
			int colon = str.lastIndexOf(NewickConstants.COLON);
			if (colon != -1) {

				float length = Float.parseFloat(str.substring(colon + 1, str.length()));

				tree.setLength(length);

				String label = str.substring(0, colon);
				tree.setLabel(label);
			} else {
				String label = str.substring(0, str.length());
				tree.setLabel(label);
			}

			return tree;
		}

		int right = 0;
		int left = 0;

		int start = str.indexOf(NewickConstants.RIGHT_PARENTHESIS);
		int end = str.lastIndexOf(NewickConstants.LEFT_PARENTHESIS);

		float length = 0;
		String label = "";

		boolean dummy = false;

		if (end < str.length() - 1) {

			// Internal (dummy?) node

			dummy = true;

			int colon = str.indexOf(NewickConstants.COLON, end);
			if (colon != -1) {
				length = Float.parseFloat(str.substring(colon + 1, str.length()));
				label = str.substring(end + 1, colon);
			} else {
				label = str.substring(end + 1, str.length());
			}

		}

		String substr = str.substring(start + 1, end);

		int index = 0;

		for (int i = 0, n = substr.length(); i < n; i++) {

			char c = substr.charAt(i);

			// If within single quotes, all characters are skipped.
			if (c == NewickConstants.RIGHT_PARENTHESIS) {

				// Count the number of opening parentheses
				left++;

			} else if (c == NewickConstants.LEFT_PARENTHESIS) {

				// Count the number of closing parentheses
				right++;

			} else if (c == NewickConstants.COMMA) {

				// There is a split (branch)
				if (right - left == 0) {

					// It has to be between whole sub-trees
					// I.E. The number of closing parentheses should match the
					// number of opening parentheses
					String leftstr = substr.substring(index, i);

					TreeNode child = parse(leftstr, tree);

					tree.addChild(child);

					index = i + 1;

				}
			}
		}

		if (dummy) {
			tree.setLabel(label);
			tree.setLength(length);
		}

		String remaining = substr.substring(index, substr.length());
		TreeNode child = parse(remaining, tree);
		tree.addChild(child);

		return tree;
	}
}
/*
 * $Id: TreeNode.java,v 1.7 2008/12/15 21:04:51 ahmed Exp $
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import phyloutil.misc.Config;
import phyloutil.misc.NewickConstants;

/**
 * A generic tree node that can be root, internal or leaf (operational taxonomic
 * unit "OTU") node.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.7 $
 */
public class TreeNode {

	/**
	 * Default root node id
	 */
	public static final int DEFAULT_ROOT_ID = 0;

	/**
	 * Default root node level
	 */
	public static final int DEFAULT_ROOT_LEVEL = 0;

	/**
	 * Default branch length
	 */
	public static final float DEFAULT_BRANCH_LENGTH = 0;

	/**
	 * Invalid node id (for example, node not found)
	 */
	public static final int INVALID_NODE_ID = -1;

	/**
	 * Parent node. The parent is null then the node is the root of the tree.
	 */
	private TreeNode parent = null;

	/**
	 * Children (subtrees). If children is null, then the node is leaf.
	 */
	private Collection<TreeNode> children = null;

	/**
	 * Node label. If the node is leaf, then the label is the taxon name,
	 * otherwise, it is the name of an internal or the bootstrap support value
	 * for that internal node.
	 */
	private String label = "";

	/**
	 * Branch length. Default value is zero
	 */
	private float length = 0;

	/**
	 * Unique <em>generated</em> identifier for the node.
	 */
	private int id = DEFAULT_ROOT_ID;

	/**
	 * Node level
	 */
	private int level = DEFAULT_ROOT_LEVEL;

	/**
	 * Default constructor
	 */
	public TreeNode() {
		super();
	}

	/**
	 * Adds a child to the tree
	 * 
	 * @param child
	 *            A child to be added
	 */
	public void addChild(TreeNode child) {
		if (children == null) {
			this.children = new ArrayList<TreeNode>();
		}

		boolean found = false;
		Iterator<TreeNode> iterator = this.children.iterator();
		while (iterator.hasNext() && !found) {
			if (iterator.next() == child) {
				found = true;
			}
		}

		if (!found) {
			this.children.add(child);
		}

		if (child.getParent() != this) {
			child.setParent(this);
		}
	}

	/**
	 * Returns an iterator to the children
	 * 
	 * @return an iterator to the children
	 */
	public Iterable<TreeNode> getChildren() {
		return this.children;
	}

	/**
	 * Returns an iterorator to the sorted children
	 * 
	 * @return an iterorator to the sorted children
	 */
	public Iterable<TreeNode> getSortedChildren() {
		List<TreeNode> sorted = new ArrayList<TreeNode>();
		for (TreeNode node : this.children) {
			sorted.add(node);
		}
		Collections.sort(sorted, new TreeNodeComparator());
		return sorted;
	}

	/**
	 * Returns the number of children
	 * 
	 * @return the number of children
	 */
	public int getChildrenCount() {
		if (children == null) {
			return 0;
		} else {
			return this.children.size();
		}
	}

	/**
	 * Returns the label of the node
	 * 
	 * @return the label of the node
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the node
	 * 
	 * @param label
	 *            The label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the length of the node branch
	 * 
	 * @return the length of the node branch
	 */
	public float getLength() {
		return length;
	}

	/**
	 * Sets the length of the node branch
	 * 
	 * @param length
	 *            The length to set
	 */
	public void setLength(float length) {
		this.length = length;
	}

	/**
	 * Returns the node id
	 * 
	 * @return int The node id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the node id
	 * 
	 * @param id
	 *            The node id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the node level
	 * 
	 * @return int The node level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the node level
	 * 
	 * @param level
	 *            The node level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns true if the nodes does not have any children
	 * 
	 * @return <code>true</code> if the nodes does not have any children
	 *         otherwise <code>false</code>
	 */
	public boolean isLeaf() {
		return children == null || children.size() == 0;
	}

	/**
	 * Returns the parent of the node
	 * 
	 * @return {@link TreeNode} The parent of the node
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Sets the parent of the node
	 * 
	 * @param parent
	 *            The parent to set
	 */
	public void setParent(TreeNode parent) {
		this.parent = parent;

		if (parent != null) {
			boolean found = false;
			if (parent.getChildren() != null) {
				Iterator<TreeNode> iterator = parent.getChildren().iterator();
				while (iterator.hasNext() && !found) {
					if (iterator.next() == this) {
						found = true;
					}
				}
			}
			if (!found) {
				parent.addChild(this);
			}
		}
	}

	/**
	 * Returns true if this the root node of the tree (i.e. it does not have a
	 * parent node)
	 * 
	 * @return true if this the root node of the tree
	 */
	public boolean isRoot() {
		return this.parent == null;
	}

	/**
	 * Performs a depth-first search (DFS) starting at the current node
	 * 
	 * @return <code>String</code> A string representation of the tree
	 */
	public String depthFirstSearch() {

		StringBuffer buffer = new StringBuffer();

		if (!this.isLeaf()) {
			Iterator<TreeNode> iterator = this.getSortedChildren().iterator();

			TreeNode node = null;

			buffer.append(NewickConstants.RIGHT_PARENTHESIS);

			if (iterator.hasNext()) {
				// Get the first child here and handle the rest in the while
				// loop in order to avoid checking the requirement of adding a
				// comma or not
				node = iterator.next();
				buffer.append(node.depthFirstSearch());
			}

			while (iterator.hasNext()) {
				node = iterator.next();
				buffer.append(NewickConstants.COMMA);
				buffer.append(node.depthFirstSearch());
			}

			buffer.append(NewickConstants.LEFT_PARENTHESIS);
		}

		buffer.append(this.label);
		buffer.append(NewickConstants.COLON);
		buffer.append(this.length);

		if (this.isRoot()) {
			buffer.append(NewickConstants.SEMI_COLON);
		}

		return buffer.toString();
	}

	/**
	 * Returns a string representation of the tree
	 * 
	 * @return <code>String</code> A string representation of the tree
	 */
	public String toString() {
		return this.depthFirstSearch();
	}

	/**
	 * Processes the tree and sets the id and level for each in the tree
	 */
	public void preprocess() {
		preprocess(DEFAULT_ROOT_ID, DEFAULT_ROOT_LEVEL);
	}

	/**
	 * Processes the tree and sets the id and level for each in the tree
	 * 
	 * @param id
	 *            The node id to set
	 * @param level
	 *            The node level to set
	 * @return int The node id
	 */
	public int preprocess(int id, int level) {
		this.setId(id);
		this.setLevel(level);

		int childId = id;

		if (!this.isLeaf()) {
			for (TreeNode node : this.children) {
				childId = node.preprocess(childId + 1, level + 1);
			}
		}
		return childId++;
	}

	/**
	 * Retrieve a node using its id
	 * 
	 * @param id
	 *            The node id
	 * @return {@link TreeNode} that has the specified id
	 */
	public TreeNode getNode(int id) {

		if (this.id == id) {

			return this;

		} else {

			if (!this.isLeaf()) {

				for (TreeNode node : this.getSortedChildren()) {

					TreeNode node2 = node.getNode(id);

					if (node2 != null) {
						return node2;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Returns the maximum node id under this node
	 * 
	 * @return int The maximum node id under this node
	 */
	public int getMaximumId() {

		if (this.isLeaf()) {

			return this.id;

		} else {

			int maximumId = Integer.MIN_VALUE;
			int currentId = Integer.MIN_VALUE;

			for (TreeNode child : this.children) {

				currentId = child.getMaximumId();

				if (currentId > maximumId) {
					maximumId = currentId;
				}
			}

			return currentId;
		}
	}

	/**
	 * Returns an ordered list representing the path from the root to this node.
	 * Each node in the path is represented by its id integer value.
	 * 
	 * @return An ordered list representing the path from the root to this node
	 */
	public Path getPathFromRoot() {

		Stack<Integer> stack = new Stack<Integer>();

		TreeNode node = this;

		while (!node.isRoot()) {
			stack.push(node.getId());
			node = node.getParent();
		}

		// This should be the root node
		stack.push(node.getId());

		Path path = new Path();
		while (!stack.isEmpty()) {
			path.add(stack.pop());
		}

		return path;
	}

	/**
	 * Returns an ordered list representing the path from the root to this node.
	 * Each node in the path is represented by its id integer value.
	 * 
	 * @return An ordered list representing the path from the root to this node
	 */
	public Path getPathFromInternal(TreeNode internal) {

		Stack<Integer> stack = new Stack<Integer>();

		TreeNode node = this;

		while (node.getId() != internal.getId()) {
			stack.push(node.getId());
			node = node.getParent();
		}

		// This should be the root node
		stack.push(node.getId());

		Path path = new Path();
		while (!stack.isEmpty()) {
			path.add(stack.pop());
		}

		return path;
	}

	/**
	 * Removes a child
	 * 
	 * @param child
	 *            Child to be removed
	 */
	public void removeChild(TreeNode child) {

		if (!this.isLeaf() && this.children.contains(child)) {
			this.children.remove(child);
		}
	}

	/**
	 * Removes a child
	 * 
	 * @param child
	 *            Child to be removed
	 */
	public void removeChild(int child) {

		if (!this.isLeaf()) {

			TreeNode target = null;

			Iterator<TreeNode> iterator = this.children.iterator();

			while (target == null && iterator.hasNext()) {

				TreeNode node = iterator.next();

				if (node.getId() == child) {
					target = node;
				}

			}

			if (target != null) {
				this.children.remove(target);
			}
		}
	}

	/**
	 * Returns the number of terminal nodes in a tree
	 * 
	 * @return the number of terminal nodes in a tree
	 */
	public int size() {
		if (this.isLeaf()) {
			return 1;
		} else {
			int sum = 0;

			for (TreeNode child : this.children) {
				sum += child.size();
			}
			return sum;
		}
	}

	/**
	 * Returns all leaves under this node
	 * 
	 * @return all leaves under this node
	 */
	public List<TreeNode> getLeaves() {

		List<TreeNode> leaves = new ArrayList<TreeNode>();

		if (this.isLeaf()) {
			leaves.add(this);
		} else {
			for (TreeNode child : this.getChildren()) {
				leaves.addAll(child.getLeaves());
			}
		}

		return leaves;

	}

	/**
	 * Extracts a taxon string from the node label
	 * 
	 * @return {@link String} taxon
	 */
	public String getTaxon() {
		return getTaxon(false);
	}

	/**
	 * Extracts a taxon string from the node label
	 * 
	 * @param labelOnNoMatch
	 *            Flag to return the node label if no match
	 * @return {@link String} taxon
	 */
	public String getTaxon(boolean labelOnNoMatch) {

		Matcher matcher = Config.getInstance().getRegexp().matcher(this.label);

		if (matcher.matches()) {

			String taxon = matcher.group(1);

			return taxon;

		} else {

			if (labelOnNoMatch) {
				return this.label;
			} else {
				return null;
			}
		}
	}

	/**
	 * Finds a node with a specific label
	 * 
	 * @param label
	 *            {@link String} query label
	 * @return {@link TreeNode} with query label
	 */
	public TreeNode find(String query) {
		return find(this, query, false);
	}

	/**
	 * Finds a node with a specific label
	 * 
	 * @param query
	 *            {@link String} query label
	 * @param regexp
	 *            whether to find the exact query or use it a pattern to match
	 * @return {@link TreeNode} with query label
	 */
	public TreeNode find(String query, boolean regexp) {
		return find(this, query, regexp);
	}

	/**
	 * Finds a node with a specific label
	 * 
	 * @param tree
	 *            {@link TreeNode} root of tree
	 * @param label
	 *            {@link String} query label
	 * @return {@link TreeNode} with query label
	 */
	public static TreeNode find(TreeNode tree, String label) {
		return find(tree, label, false);
	}

	/**
	 * Finds a node with a specific label
	 * 
	 * @param tree
	 *            {@link TreeNode} root of tree
	 * @param query
	 *            {@link String} query label
	 * @param regexp
	 *            whether to find the exact query or use it a pattern to match
	 * @return {@link TreeNode} with query label
	 */
	public static TreeNode find(TreeNode tree, String query, boolean regexp) {
		String label = tree.getLabel();
		if (regexp) {
			if (Pattern.matches(query, label)) {
				return tree;
			}
		} else {
			// if (label.equalsIgnoreCase(query)) {
			if (label.startsWith(query)) {
				return tree;
			}
		}

		if (tree.isLeaf()) {
			return null;
		}

		for (TreeNode child : tree.children) {
			TreeNode node = TreeNode.find(child, query, regexp);
			if (node != null) {
				return node;
			}
		}

		return null;
	}

	/**
	 * Returns the root of the tree of a node
	 * 
	 * @return Root {@link TreeNode}
	 */
	public TreeNode getRoot() {

		if (this.isRoot()) {
			return this;
		}

		TreeNode parent = this.getParent();
		while (!parent.isRoot()) {
			parent = parent.getParent();
		}

		return parent;
	}

	/**
	 * Returns depth first search {@link Iterable}
	 * 
	 * @return Depth first search {@link Iterable}
	 */
	public Iterable<TreeNode> iterator() {
		List<TreeNode> list = new ArrayList<TreeNode>();
		Stack<TreeNode> stack = new Stack<TreeNode>();
		stack.add(this);

		while (!stack.isEmpty()) {

			TreeNode node = stack.pop();

			if (node.isLeaf()) {
				list.add(node);
			} else {
				for (TreeNode child : node.getChildren()) {
					stack.push(child);
				}
			}
		}

		return list;
	}
	
	/**
	 * 
	 * Performs a depth-first search (DFS) starting at the current node
	 * 
	 * @return <code>String</code> A string representation of the tree
	 */
	public Iterable<TreeNode> getNodes() {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		nodes.add(this);
		if (!this.isLeaf()) {
			Iterator<TreeNode> iterator = this.getSortedChildren().iterator();
			TreeNode node = null;
			while (iterator.hasNext()) {
				node = iterator.next();
				Iterable<TreeNode> nodes2 = node.getNodes();
				for (TreeNode node2 : nodes2) {
					nodes.add(node2);
				}
			}
		}
		return nodes;
	}
}
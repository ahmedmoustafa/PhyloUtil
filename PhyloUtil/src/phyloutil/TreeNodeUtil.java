/*
 * $Id: TreeNodeUtil.java,v 1.26 2012/09/25 12:43:52 ahmed Exp $
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.misc.Commons;
import phyloutil.misc.Config;
import phyloutil.misc.Distance;
import phyloutil.misc.NewickConstants;
import phyloutil.misc.SingleLogger;

/**
 * Utility methods for general {@link TreeNode} processing.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.26 $
 */
public final class TreeNodeUtil {

	/**
	 * Logger
	 */
	private static final Logger logger = SingleLogger.getLogger();

	/**
	 * Checks whether the set of all OTUs is a subset of taxa
	 * 
	 * @param node
	 *            Tree to check it OTUs
	 * @param taxa
	 *            Set of taxa to check aganist
	 * 
	 * @return True if the set of all OTUs is a subset of taxa, otherwise false
	 */
	public static boolean belongs(TreeNode node, Set<String> taxa) {
		if (node.isLeaf()) {

			String taxon = node.getTaxon();
			return taxa.contains(taxon);

		} else {

			for (TreeNode child : node.getChildren()) {
				if (!belongs(child, taxa)) {
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * Checks whether each group of taxa is represented at least by one OTU
	 * 
	 * @param node
	 *            Tree to check it OTUs
	 * @param taxa
	 *            Sets of taxa to check
	 * 
	 * @return True if the group of taxa is represented in the tree, otherwise
	 *         false
	 */
	public static int contains(TreeNode node, Set<String> taxa) {
		if (node.isLeaf()) {

			String taxon = node.getTaxon();
			if (taxa.contains(taxon)) {
				return 1;
			} else {
				return 0;
			}

		} else {

			int sum = 0;

			for (TreeNode child : node.getChildren()) {
				sum += contains(child, taxa);
			}

			return sum;
		}
	}

	/**
	 * Checks whether each group of taxa is represented at least by one OTU
	 * 
	 * @param node
	 *            Tree to check it OTUs
	 * @param query
	 *            Query OTU to check
	 * 
	 * @return True query OTU is in the tree, otherwise false
	 */
	public static boolean contains(TreeNode node, String query) {

		if (query == null) {
			return true;
		}

		if (node.isLeaf()) {
			String OTU = node.getLabel();
			return query.equalsIgnoreCase(OTU);
		} else {
			for (TreeNode child : node.getChildren()) {
				if (contains(child, query)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Checks whether a tree has a common branch holding groups of taxa
	 * 
	 * @param tree
	 *            Tree to search in for a common branch
	 * @param taxa
	 *            Groups of taxa (labels) to search for a common branch
	 * @param query
	 *            Query OTU that must exist in the monophyletic clade
	 * @return Node id of the internal node rooting the taxa
	 */
	public static TreeNode getMonophyleticNode(TreeNode tree, List<List<String>> taxa, String query) {

		return getMonophyleticNode(tree, taxa, true, query);

	}

	/**
	 * Checks whether a tree has a common branch holding groups of taxa
	 * 
	 * @param tree
	 *            Tree to search in for a common branch
	 * @param taxa
	 *            Groups of taxa (labels) to search for a common branch
	 * @param exclusive
	 *            Validation type indicator
	 * @param query
	 *            Query OTU that must exist in the monophyletic clade
	 * @return Node id of the internal node rooting the taxa
	 */
	public static TreeNode getMonophyleticNode(TreeNode tree, List<List<String>> taxa, boolean exclusive, String query) {

		Set<String> extended = new HashSet<String>();

		List<Set<String>> sets = new ArrayList<Set<String>>();

		for (Iterable<String> list : taxa) {

			Set<String> set = new HashSet<String>();

			for (String taxon : list) {
				extended.add(taxon);
				set.add(taxon);
			}

			sets.add(set);
		}

		Collection<TreeNode> nodes = getNodesByTaxa(tree, extended);

		if (exclusive) {

			int lca = TreeNode.DEFAULT_ROOT_ID;

			if (!nodes.isEmpty()) {
				lca = lca(nodes);
			}

			if (lca != TreeNode.INVALID_NODE_ID) {

				TreeNode node = tree.getNode(lca);

				if (belongs(node, extended)) {

					for (Set<String> set : sets) {

						if (contains(node, set) == 0) {

							return null;

						}

					}

					return node;

				}
			}

			return null;

		} else {

			Set<Integer> exlcuded = new HashSet<Integer>();

			for (TreeNode node : nodes) {

				int id = hasMonophyly(node, extended, sets, exlcuded, query);

				if (id != TreeNode.INVALID_NODE_ID) {

					return tree.getNode(id);

				}

			}

			return null;
		}
	}

	/**
	 * Checks whether a tree has a common branch holding groups of taxa
	 * 
	 * @param tree
	 *            Tree to search in for a common branch
	 * @param taxa
	 *            Groups of taxa (labels) to search for a common branch
	 * @param query
	 *            Query OTU that must exist in the monophyletic clade
	 * @return Node id of the internal node rooting the taxa
	 */
	public static Collection<TreeNode> getAllMonophyleticNodes(TreeNode tree, List<List<String>> taxa, String query) {

		return getAllMonophyleticNodes(tree, taxa, true, query);

	}

	/**
	 * Checks whether a tree has a common branch holding groups of taxa
	 * 
	 * @param tree
	 *            Tree to search in for a common branch
	 * @param taxa
	 *            Groups of taxa (labels) to search for a common branch
	 * @param exclusive
	 *            Validation type indicator
	 * @param query
	 *            Query OTU that must exist in the monophyletic clade
	 * @return Node id of the internal node rooting the taxa
	 */
	public static Collection<TreeNode> getAllMonophyleticNodes(TreeNode tree, List<List<String>> taxa, boolean exclusive, String query) {

		Set<TreeNode> monophyletic = new HashSet<TreeNode>();

		Set<String> extended = new HashSet<String>();

		List<Set<String>> sets = new ArrayList<Set<String>>();

		for (Iterable<String> list : taxa) {

			Set<String> set = new HashSet<String>();

			for (String taxon : list) {
				extended.add(taxon);
				set.add(taxon);
			}

			sets.add(set);
		}

		Collection<TreeNode> nodes = getNodesByTaxa(tree, extended);

		if (exclusive) {

			int lca = TreeNode.DEFAULT_ROOT_ID;

			if (!nodes.isEmpty()) {
				lca = lca(nodes);
			}

			if (lca != TreeNode.INVALID_NODE_ID) {

				TreeNode node = tree.getNode(lca);

				TreeNode target = node;

				if (belongs(target, extended)) {

					for (Set<String> set : sets) {

						if (contains(target, set) == 0) {

							return null;

						}

					}

					if (Config.getInstance().isQueryRequired()) {
						if (!contains(node, query)) {
							return null;
						}
					}

					return getAllMonophyleticNodes(node, taxa, false, query);

				}
			}

			return null;

		} else {

			Set<Integer> exlcuded = new HashSet<Integer>();

			for (TreeNode node : nodes) {

				int id = hasMonophyly(node, extended, sets, exlcuded, query);

				if (id != TreeNode.INVALID_NODE_ID) {
					TreeNode node2 = tree.getNode(id);
					if (Config.getInstance().isQueryRequired()) {
						if (contains(node2, query)) {
							monophyletic.add(node2);
						}
					} else {
						monophyletic.add(node2);
					}

				}

			}

			if (monophyletic.isEmpty()) {
				return null;
			} else {
				return monophyletic;
			}
		}
	}

	/**
	 * Returns node id if the node is valid, otherwise returns -1
	 * 
	 * @param node
	 *            Node to validate
	 * @param extended
	 *            Extended list of taxa
	 * @param sets
	 *            Taxa in groups
	 * @param excluded
	 *            List of ids of exlcuded nodes (already visited)
	 * @param query
	 *            Query OTU that must exist in the monophyletic clade
	 * @return If valid node, returns node id; otherwise, returns -1
	 */
	public static int hasMonophyly(TreeNode node, Set<String> extended, List<Set<String>> sets, Set<Integer> excluded, String query) {

		if (excluded.contains(node.getId())) {

			return TreeNode.INVALID_NODE_ID;

		}

		if (belongs(node, extended)) {

			boolean fullCoverage = true;

			Iterator<Set<String>> iterator = sets.iterator();

			while (fullCoverage && iterator.hasNext()) {

				int count = contains(node, iterator.next());

				if (count < Config.getInstance().getMinimumGroupSize()) {

					fullCoverage = false;

				}

			}

			if (fullCoverage) {

				return node.getId();

			} else {
				if (!node.isRoot()) {

					return hasMonophyly(node.getParent(), extended, sets, excluded, query);

				} else {

					excluded.add(node.getId());

					return TreeNode.INVALID_NODE_ID;
				}
			}

		} else {

			excluded.add(node.getId());

			return TreeNode.INVALID_NODE_ID;

		}
	}

	/**
	 * Returns a {@link Set} of {@link String} of taxa in a {@link TreeNode}
	 * 
	 * @param node
	 *            {@link TreeNode}
	 * @return A {@link Set} of {@link String} of taxa in a {@link TreeNode}
	 */
	public static Set<String> getTaxa(TreeNode node) {
		Set<String> taxa = new HashSet<String>();

		if (node.isLeaf()) {

			String taxon = node.getTaxon();

			if (!taxa.contains(taxon)) {

				taxa.add(taxon);

			}

		} else {

			for (TreeNode child : node.getChildren()) {

				taxa.addAll(getTaxa(child));

			}

		}

		return taxa;
	}

	/**
	 * Returns a {@link Set} of {@link String} of all taxa in a file
	 * 
	 * @param file
	 *            Input file
	 * @return A {@link Set} of {@link String} of all taxa in a file
	 * @throws Exception
	 */
	public static Set<String> getTaxa(File file) throws Exception {

		if (file.isFile()) {

			try {
				TreeNode tree = load(file);
				return getTaxa(tree);
			} catch (Exception e) {
				logger.warning("Failed loading tree from " + file.getName() + ": " + e.getMessage());
				return null;
			}

		} else if (file.isDirectory()) {

			Set<String> taxa = new HashSet<String>();

			for (String name : file.list()) {

				String full = file + Commons.getFileSeparator() + name;

				File file2 = new File(full);

				try {
					taxa.addAll(getTaxa(file2));
				} catch (Exception e) {
					logger.warning("Failed getting taxa from " + file2.getName() + ": " + e.getMessage());
				}

			}

			return taxa;

		} else {
			logger.warning("Invalid file:" + file.getName());
			return null;
		}

	}

	/**
	 * Returns all OTU nodes with labels matching the set of taxa
	 * 
	 * @param tree
	 *            Tree to search in
	 * @param taxa
	 *            Taxa to search for
	 * 
	 * @return Collection of the OTU {@link TreeNode}
	 */
	public static Collection<TreeNode> getNodesByTaxa(TreeNode tree, Set<String> taxa) {
		return getNodesByTaxa(tree, taxa, true);
	}

	/**
	 * Returns all OTU nodes with labels matching the set of taxa
	 * 
	 * @param node
	 *            Tree to search in
	 * @param taxa
	 *            Taxa to search for
	 * @param equal
	 *            Flag to search of equality or non-equality
	 * 
	 * @return Collection of the OTU {@link TreeNode}
	 */
	public static Collection<TreeNode> getNodesByTaxa(TreeNode node, Set<String> taxa, boolean equal) {

		List<TreeNode> list = new ArrayList<TreeNode>();

		if (node.isLeaf()) {

			String taxon = node.getTaxon();

			if (taxa.contains(taxon) == equal) {
				list.add(node);
			}

		} else {

			for (TreeNode child : node.getChildren()) {
				list.addAll(getNodesByTaxa(child, taxa, equal));
			}

		}

		return list;
	}

	/**
	 * Loads a tree from a text file
	 * 
	 * @param file
	 *            Tree text file
	 * @return {@link TreeNode} root of the tree (midpoint?)
	 * @throws Exception
	 */
	public static TreeNode load(File file) throws Exception {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));

			StringBuffer buffer = new StringBuffer();

			String line = null;

			while ((line = reader.readLine()) != null) {

				buffer.append(line.trim());

			}

			String tree = buffer.toString();

			int index = tree.indexOf(NewickConstants.SEMI_COLON);

			if (index != -1) {
				tree = tree.substring(0, index);
			}

			return NewickParser.parse(tree, null);

		} catch (Exception exception) {
			String msg = "Failed loading tree from " + file + ": " + exception.getMessage();
			logger.log(Level.SEVERE, msg, exception);
			throw new Exception(msg, exception);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception silent) {
					String msg = "Failed closing input file: " + silent.getMessage();
					logger.log(Level.WARNING, msg, silent);
				}
			}
		}
	}

	/**
	 * Returns the lowest common ancestor (LCA) for a list of nodes
	 * 
	 * @param nodes
	 *            The list of tree nodes
	 * @return The id (<code>int</code>) of the LCA
	 * @see TreeNode
	 */
	public static int lca(Iterable<TreeNode> nodes) {

		List<Path> paths = new ArrayList<Path>();
		for (TreeNode node : nodes) {
			paths.add(node.getPathFromRoot());
		}

		int node = TreeNode.INVALID_NODE_ID;

		int row = paths.size();
		int col = 0;

		for (Path path : paths) {

			int n = path.size();

			if (n > col) {
				col = n;
			}
		}

		int[][] hubs = new int[row][col];

		// Initialize the hubs array with -1
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				hubs[i][j] = -1;
			}
		}

		// Set the values of the existing points
		for (int i = 0; i < row; i++) {
			Path path = paths.get(i);
			for (int j = 0, n = path.size(); j < n; j++) {
				hubs[i][j] = path.get(j);
			}
		}

		boolean match = true;

		for (int j = 0; j < col && match; j++) {

			int node2 = hubs[0][j];

			for (int i = 1; i < row && match; i++) {

				if (hubs[i][j] != node2) {
					match = false;
				}

			}

			if (match) {
				node = node2;
			}
		}

		return node;
	}

	/**
	 * Reroots a tree based on an outgroup node.
	 * 
	 * @param outgroup
	 *            {@link TreeNode} outgroup node
	 * @return Rerooted {@link TreeNode}
	 */
	public static TreeNode reroot(TreeNode outgroup) {
		TreeNode rerooted = _reroot(outgroup);
		rerooted.preprocess();
		return rerooted;
	}

	/**
	 * Reroots a tree based on an outgroup node.
	 * 
	 * @param outgroup
	 *            {@link TreeNode} outgroup node
	 * @return Rerooted {@link TreeNode}
	 */
	private static TreeNode _reroot(TreeNode outgroup) {

		if (outgroup.isRoot()) {
			return copy(outgroup, null);
		}

		TreeNode parent = outgroup.getParent();

		TreeNode _outgroup = copy(outgroup, null);

		TreeNode _root = null;
		TreeNode _sister = new TreeNode();

		if (_outgroup.isLeaf()) {
			_root = new TreeNode();
			_root.addChild(_outgroup);
			_outgroup.setLength(outgroup.getLength() / 2);
			_root.addChild(_sister);
			_sister.setLength(outgroup.getLength() / 2);
		} else {
			_root = _outgroup;
			_root.addChild(_sister);
			_sister.setParent(_root);
			_sister.setLength(outgroup.getLength() + _sister.getLength());
		}

		for (TreeNode child : parent.getChildren()) {
			if (child != outgroup) {
				TreeNode copy = copy(child, null);
				_sister.addChild(copy);
			}
		}

		TreeNode grandParent = parent.getParent();

		if (grandParent != null) {
			grandParent.removeChild(parent);

			float parentBranchLength = parent.getLength();
			TreeNode rerootedGrandParent = reroot(grandParent);
			rerootedGrandParent.setLength(parentBranchLength);
			_sister.addChild(rerootedGrandParent);

			rerootedGrandParent.setLabel(parent.getLabel());
			// rerootedGrandParent.setLabel(grandParent.getLabel());

			grandParent.addChild(parent);
		}

		// _sister.setLabel(parent.getLabel());

		return _root;
	}

	/**
	 * Returns a copy of a {@link TreeNode}
	 * 
	 * @param root
	 *            {@link TreeNode} to start coping from
	 * @param exclude
	 *            {@link TreeNode} to exclude while coping
	 * 
	 * @return {@link TreeNode} copy of the root node
	 */
	public static TreeNode copy(TreeNode root, TreeNode exclude) {

		TreeNode copy = new TreeNode();

		if (root == exclude) {
			return copy;
		}

		copy.setId(root.getId());
		copy.setLabel(root.getLabel());
		copy.setLength(root.getLength());
		copy.setLevel(root.getLevel());

		if (!root.isLeaf()) {

			for (TreeNode child : root.getChildren()) {
				if (child != exclude) {
					TreeNode node = copy(child, exclude);
					node.setParent(copy);
					copy.addChild(node);
				}
			}
		}

		return copy;
	}

	/**
	 * Finds a leaf node with a taxon not included within a list of taxa
	 * 
	 * @param node
	 *            {@link TreeNode} root of a tree to search for an outgroup
	 * @param taxa
	 *            List of taxa (ingroup)
	 * @return {@link TreeNode} outgroup leaf node or null if no outgroup is
	 *         found
	 */
	public static TreeNode findOutgroup(TreeNode node, List<Set<String>> taxa) {
		Set<String> set = new HashSet<String>();
		for (Set<String> subset : taxa) {
			set.addAll(subset);
		}
		return findOutgroup(node, set);
	}

	/**
	 * Finds a leaf node with a taxon not included within a list of taxa
	 * 
	 * @param node
	 *            {@link TreeNode} root of a tree to search for an outgroup
	 * @param taxa
	 *            List of taxa (ingroup)
	 * @return {@link TreeNode} outgroup leaf node or null if no outgroup is
	 *         found
	 */
	public static TreeNode findOutgroup(TreeNode node, Set<String> taxa) {
		if (node.isLeaf()) {
			String taxon = node.getTaxon();

			if (!taxa.contains(taxon)) {
				return node;
			} else {
				return null;
			}
		} else {
			for (TreeNode child : node.getChildren()) {
				TreeNode outgroup = findOutgroup(child, taxa);
				if (outgroup != null) {
					return outgroup;
				}
			}
			return null;
		}
	}

	/**
	 * Returns the average number of copies per species
	 * 
	 * @param root
	 *            {@link TreeNode} root of a tree
	 * @return The average number of copies per species (decimal number)
	 */
	public static float getAverageNumberOfCopies(TreeNode root) {

		Stack<TreeNode> stack = new Stack<TreeNode>();
		Map<String, Integer> counts = new HashMap<String, Integer>();

		stack.push(root);

		while (!stack.isEmpty()) {

			TreeNode node = stack.pop();

			if (node.isLeaf()) {

				String key = node.getTaxon();

				int value = 1;

				if (counts.containsKey(key)) {
					value += counts.get(key);
				}

				counts.put(key, value);
			} else {

				for (TreeNode child : node.getChildren()) {
					stack.push(child);
				}
			}
		}

		int sum = 0;

		for (String taxon : counts.keySet()) {
			sum += counts.get(taxon);
		}

		float average = sum / (float) counts.size();

		return average;
	}

	/**
	 * 
	 * @param node
	 * @param min
	 * @return
	 */
	public static TreeNode clean(TreeNode node, float min) {

		if (!node.isLeaf()) {

			float bootstrap = 0;
			String label = node.getLabel();

			try {
				bootstrap = Float.parseFloat(label);
			} catch (Exception e) {
				String msg = "Failed parsing internal label: " + e.getMessage();
				logger.log(Level.WARNING, msg);
			}

			if (bootstrap < min) {
				node.setLabel("");
			}

			for (TreeNode child : node.getChildren()) {
				clean(child, min);
			}
		}

		return node;
	}

	/**
	 * 
	 * @param tree
	 * @param filename
	 * @throws Exception
	 */
	public static void save(TreeNode tree, String filename) throws Exception {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write(tree.toString());
			writer.newLine();
		} catch (Exception e) {
			String msg = "Failed saving tree: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					String msg = "Failed closing writer: " + e.getMessage();
					logger.log(Level.WARNING, msg, e);
				}
			}
		}
	}

	/**
	 * Returns the distance between two nodes in a tree
	 * 
	 * @param node1
	 *            Node one
	 * @param node2
	 *            Node two
	 * @return
	 */
	public static Distance getDistance(TreeNode node1, TreeNode node2) {

		List<TreeNode> nodes = new ArrayList<TreeNode>();

		nodes.add(node1);
		nodes.add(node2);

		int lca = lca(nodes);

		float length1 = 0;
		int depth1 = 0;

		TreeNode parent = node1;
		while (parent.getId() != lca) {
			depth1++;
			length1 += parent.getLength();
			parent = parent.getParent();
		}

		float length2 = 0;
		int depth2 = 0;

		parent = node2;
		while (parent.getId() != lca) {
			depth2++;
			length2 += parent.getLength();
			parent = parent.getParent();
		}

		int depth = depth1 + depth2;
		float length = length1 + length2;

		return new Distance(depth, length);
	}

	/**
	 * Returns the farthest node from a query node
	 * 
	 * @param node
	 *            query {@link TreeNode}
	 * @return farthest {@link TreeNode}
	 */
	public static TreeNode getFarthest(TreeNode node) {
		TreeNode root = node.getRoot();

		TreeNode furthest = null;

		int depth = Integer.MIN_VALUE;
		float length = Float.MIN_VALUE;

		for (TreeNode current : root.getLeaves()) {
			if (current != node && current.isLeaf()) {
				Distance distance = getDistance(node, current);

				if (distance.getDepth() >= depth) {
					if (distance.getLength() > length) {
						furthest = current;
						depth = distance.getDepth();
						length = distance.getLength();
					}
				}
			}
		}
		return furthest;
	}

	/**
	 * Returns the nearest node from a query node
	 * 
	 * @param node
	 *            query {@link TreeNode}
	 * @return nearest {@link TreeNode}
	 */
	public static TreeNode getNearest(TreeNode node) {
		Set<String> empty = new HashSet<String>();
		return getNearest(node, empty);
	}

	/**
	 * Returns the nearest node from a query node
	 * 
	 * @param node
	 *            query {@link TreeNode}
	 * @return nearest {@link TreeNode}
	 */

	/**
	 * Returns the nearest node from a query node
	 * 
	 * 
	 * @param node
	 *            query {@link TreeNode}
	 * @param skip
	 *            nodes to skip {@link Set}
	 * @return nearest {@link TreeNode}
	 */

	public static TreeNode getNearest(TreeNode node, Set<String> skipset) {
		TreeNode root = node.getRoot();
		TreeNode parent = node;
		TreeNode nearest = null;

		do {
			int depth = Integer.MAX_VALUE;
			float length = Float.MAX_VALUE;

			parent = parent.getParent();

			for (TreeNode current : parent.getLeaves()) {
				if (current != node && current.isLeaf()) {

					boolean found = false;
					String skip = "";
					Iterator<String> iterator = skipset.iterator();
					while (!found && iterator.hasNext()) {
						skip = iterator.next();
						if (current.getLabel().contains(skip)) {
							found = true;
						}
					}

					if (!found) {
						Distance distance = getDistance(node, current);

						if (distance.getDepth() < depth) {
							nearest = current;
							depth = distance.getDepth();
							length = distance.getLength();
						} else if (distance.getDepth() == depth) {
							if (distance.getLength() < length) {
								nearest = current;
								depth = distance.getDepth();
								length = distance.getLength();
							}
						}
					}
				}
			}

		} while (nearest == null && root != parent);

		return nearest;
	}

	/**
	 * Reroots a tree based on an outgroup node.
	 * 
	 * @param outgroup
	 *            {@link TreeNode} outgroup node
	 * @return Rerooted {@link TreeNode}
	 */
	@SuppressWarnings("unused")
	private static TreeNode _reroot_backup(TreeNode outgroup) {

		if (outgroup.isRoot()) {
			return copy(outgroup, null);
		}

		TreeNode parent = outgroup.getParent();

		TreeNode _outgroup = copy(outgroup, null);

		TreeNode _root = null;
		TreeNode _sister = new TreeNode();

		if (_outgroup.isLeaf()) {
			_root = new TreeNode();
			_root.addChild(_outgroup);
			_outgroup.setLength(outgroup.getLength() / 2);
			_root.addChild(_sister);
			_sister.setLength(outgroup.getLength() / 2);
		} else {
			_root = _outgroup;
			_root.addChild(_sister);
			_sister.setParent(_root);
			_sister.setLength(outgroup.getLength() + _sister.getLength());
		}

		_sister.setLabel(parent.getLabel());

		for (TreeNode child : parent.getChildren()) {
			if (child != outgroup) {
				TreeNode copy = copy(child, null);
				_sister.addChild(copy);
			}
		}

		TreeNode grandParent = parent.getParent();

		if (grandParent != null) {
			grandParent.removeChild(parent);

			float parentBranchLength = parent.getLength();
			TreeNode rerootedGrandParent = reroot(grandParent);
			rerootedGrandParent.setLength(parentBranchLength);
			_sister.addChild(rerootedGrandParent);

			// rerootedGrandParent.setLabel(parent.getLabel());
			rerootedGrandParent.setLabel(grandParent.getLabel());

			grandParent.addChild(parent);
		}

		return _root;
	}

	public static TreeNode removeNode(TreeNode node) {
		TreeNode parent = node.getParent();
		TreeNode root = node.getRoot();

		if (parent != null) {

			if (parent.isRoot()) {
				if (parent.getChildrenCount() > 2) {
					parent.removeChild(node);
					return parent;
				} else {
					TreeNode child = null;
					for (TreeNode child2 : parent.getChildren()) {
						if (!child2.equals(node)) {
							child = child2;
						}
					}
					child.setParent(null);
					return child;
				}
			} else {
				parent.removeChild(node);
				if (parent.getChildrenCount() == 0) {
					removeNode(parent);
				} else if (parent.getChildrenCount() == 1) {
					TreeNode child = null;
					for (TreeNode child2 : parent.getChildren()) {
						if (!child2.equals(node)) {
							child = child2;
						}
					}
					if (child != null) {
						child.setLength(parent.getLength() + child.getLength());
						parent.getParent().addChild(child);
						child.setParent(parent.getParent());
						parent.getParent().removeChild(parent);
					}
				} else {
					System.err.println("Error: parent with more than 1 child : " + parent.getChildrenCount());
					System.err.println(parent);
					System.exit(1);
				}
			}
			return root;
		} else {
			return null;
		}
	}

	public static TreeNode removeNode(TreeNode tree, List<String> list) {

		TreeNode reduced = tree;

		for (String name : list) {
			TreeNode node = reduced.find(name);
			if (node != null) {
				reduced = removeNode(node);
			}
		}
		return reduced;
	}

	/**
	 * Transforms branch lengths by taking log10
	 * 
	 * @param root
	 *            {@link TreeNode} root of the tree
	 * 
	 * @return {@link TreeNode} a log10-transformed version of the original tree
	 */

	public static TreeNode transform(TreeNode root) {
		return transform(root, 1);
	}

	/**
	 * Transforms branch lengths by taking log10
	 * 
	 * @param root
	 *            {@link TreeNode} root of the tree
	 * 
	 * @param scale
	 *            scaling the branch lengths before the log10-transformation
	 * 
	 * @return {@link TreeNode} a log10-transformed version of the original tree
	 */

	public static TreeNode transform(TreeNode root, float scale) {
		TreeNode log10 = new TreeNode();

		log10.setId(root.getId());
		log10.setLabel(root.getLabel());
		log10.setLevel(root.getLevel());

		float lengthOriginal = root.getLength();
		float length = lengthOriginal > 0 ? (float) Math.log10(scale * lengthOriginal) : 0;

		log10.setLength(length);

		if (!root.isLeaf()) {
			for (TreeNode child : root.getChildren()) {
				TreeNode node = transform(child, scale);
				node.setParent(log10);
				log10.addChild(node);
			}
		}
		return log10;
	}
}
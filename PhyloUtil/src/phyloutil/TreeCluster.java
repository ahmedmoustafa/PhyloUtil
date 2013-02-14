/*
 * $Id: TreeCluster.java,v 1.5 2007/07/23 03:58:26 ahmed Exp $
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.misc.SingleLogger;

/**
 * A cluster of overlapping phylogenetic trees.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.5 $
 */

public class TreeCluster {

	/**
	 * Logger
	 */
	private static final Logger logger = SingleLogger.getLogger();

	/**
	 * Default minimum overlap
	 */
	public static final int DEFAULT_MINIMUM_OVERLAP = 1;

	/**
	 * Set of trees file names in the cluster
	 */
	private final Set<String> trees = new HashSet<String>();

	/**
	 * Set of taxa in all trees in the cluster
	 */
	private final Set<String> taxa = new HashSet<String>();

	/**
	 * Returns true if cluster contains a specific taxon
	 * 
	 * @param taxon
	 *            Taxon to be checked for inclusion
	 * @return True if cluster contain a specific taxon, otherwise returns false
	 */
	public boolean contains(String taxon) {
		return this.taxa.contains(taxon);
	}

	/**
	 * Returns true if cluster contains at least one taxon from a list of taxa
	 * 
	 * @param taxa
	 *            List of taxa to be checked
	 * @return True if cluster contains at least one taxon from a list of taxa,
	 *         otherwise returns false
	 */
	public boolean contains(Iterable<String> taxa) {
		return contains(taxa, DEFAULT_MINIMUM_OVERLAP);
	}

	/**
	 * Returns true if cluster contains at least one taxon from a list of taxa
	 * 
	 * @param taxa
	 *            List of taxa to be checked
	 * @param minimumOverlap
	 *            Minimum number of overlapping taxa
	 * @return True if cluster contains at least minimum number of overlapping
	 *         taxa from a list of taxa, otherwise returns false
	 */
	public boolean contains(Iterable<String> taxa, int minimumOverlap) {
		int count = 0;
		for (String taxon : taxa) {
			if (contains(taxon)) {
				count++;
				if (count >= minimumOverlap) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if cluster contains at least one taxon from a tree
	 * 
	 * @param tree
	 *            {@link TreeNode} to be checked
	 * @return True if cluster contains at least one taxon from a tree,
	 *         otherwise returns false
	 */
	public boolean contains(TreeNode tree) {
		return contains(tree, DEFAULT_MINIMUM_OVERLAP);
	}

	/**
	 * Returns true if cluster contains at least one taxon from a tree
	 * 
	 * @param tree
	 *            {@link TreeNode} to be checked
	 * @param minimumOverlap
	 *            Minimum number of overlapping taxa
	 * @return True if cluster contains at least minimum number of overlapping
	 *         taxa from a tree, otherwise returns false
	 */
	public boolean contains(TreeNode tree, int minimumOverlap) {
		Iterable<TreeNode> iterable = tree.iterator();
		List<String> taxa = new ArrayList<String>();
		for (TreeNode node : iterable) {
			taxa.add(node.getLabel());
		}
		return contains(taxa, minimumOverlap);
	}

	/**
	 * Returns true if cluster contains at least one taxon from a tree file
	 * 
	 * @param file
	 *            Tree file to be checked
	 * @return True if cluster contains at least one taxon from a tree file,
	 *         otherwise returns false
	 */
	public boolean contains(File file) {
		return contains(file, DEFAULT_MINIMUM_OVERLAP);
	}

	/**
	 * Returns true if cluster contains at least one taxon from a tree file
	 * 
	 * @param file
	 *            Tree file to be checked
	 * @param minimumOverlap
	 *            Minimum number of overlapping taxa
	 * @return True if cluster contains at least minimum number of overlapping
	 *         taxa from a tree file, otherwise returns false
	 */
	public boolean contains(File file, int minimumOverlap) {
		try {
			TreeNode tree = TreeNodeUtil.load(file);
			return contains(tree, minimumOverlap);
		} catch (Exception exception) {
			String msg = "Failed loading tree: " + exception.getMessage();
			logger.log(Level.SEVERE, msg, exception);
		}
		return false;
	}

	/**
	 * Adds a tree to cluster
	 * 
	 * @param file
	 *            {@link File} to be added
	 * @param tree
	 *            {@link TreeNode} to be added
	 */
	public void add(File file, TreeNode tree) {

		this.trees.add(file.getName());

		for (TreeNode node : tree.iterator()) {
			taxa.add(node.getLabel());
		}
	}

	/**
	 * Adds a file to cluster
	 * 
	 * @param file
	 *            {@link File} to be added
	 */
	public void add(File file) {
		try {
			TreeNode tree = TreeNodeUtil.load(file);
			add(file, tree);
		} catch (Exception exception) {
			String msg = "Failed adding '" + file.getName() + "': " + exception.getMessage();
			logger.log(Level.SEVERE, msg, exception);
		}
	}

	/**
	 * Checks if two clusters overlap with at least one taxon
	 * 
	 * @param other
	 *            {@link TreeCluster}
	 * @return true if two clusters overlap with at least one taxon, otherwise
	 *         returns false
	 */
	public boolean overlaps(TreeCluster other) {
		return overlaps(other, DEFAULT_MINIMUM_OVERLAP);
	}

	/**
	 * Checks if two clusters overlap with a minimum number of overlapping taxa
	 * 
	 * @param other
	 *            Other {@link TreeCluster}
	 * @param minimumOverlap
	 *            Minimum number of overlapping taxa
	 * @return True if two clusters overlap with a minimum number of overlapping
	 *         taxa, otherwise returns false
	 */
	public boolean overlaps(TreeCluster other, int minimumOverlap) {
		return contains(other.taxa, minimumOverlap);
	}

	/**
	 * Merges two clusters into one cluster
	 * 
	 * @param other
	 *            {@link TreeCluster} to be merged
	 */
	public void merge(TreeCluster other) {

		for (String tree : other.trees) {
			this.trees.add(tree);
		}

		for (String taxon : other.taxa) {
			this.taxa.add(taxon);
		}

	}

	/**
	 * Returns a list of the tree files in the cluster
	 * 
	 * @return A list of the tree files in the cluster
	 */
	public Iterable<String> getFiles() {
		return this.trees;
	}

	/**
	 * Returns a list of the taxa in the cluster
	 * 
	 * @return A list of the taxa in the cluster
	 */
	public Iterable<String> getTaxa() {
		return this.taxa;
	}

	/**
	 * Returns the cluster size
	 * 
	 * @return The cluster size
	 */
	public int size() {
		return this.trees.size();
	}
}
/*
 * $Id: PhyloSortRunner.java,v 1.4 2008/12/11 13:57:32 ahmed Exp $
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

package phyloutil.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.NewickParser;
import phyloutil.TreeNode;
import phyloutil.TreeNodeUtil;

/**
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.4 $
 */

public class PhyloSortRunner {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(PhyloSortRunner.class.getName());

	/**
	 * Config
	 */
	private final Config config = Config.getInstance();

	/**
	 * Trees
	 */
	private List<TreeNode> trees = null;

	/**
	 * Taxa lists
	 */
	private List<List<String>> taxa = null;

	/**
	 * Taxa lists
	 */
	private List<String> queries = null;

	/**
	 * Taxa set
	 */
	private Set<String> set = new HashSet<String>();

	/**
	 * 
	 * @param trees
	 */
	public void setTrees(List<String> trees) {
	      this.trees = new ArrayList<TreeNode>();
	      for (String str : trees) {
	          try {
	              TreeNode treeNode = NewickParser.parse(str);
	              this.trees.add(treeNode);
	          } catch (Exception e) {
	              this.trees.add(null);
	              logger.info("Failed to parse string: " + str);
	          }
	      }
	  }

	/**
	 * 
	 * @param taxa
	 */
	public void setTaxa(List<List<String>> taxa) {
		this.taxa = taxa;
		for (List<String> list : this.taxa) {
			set.addAll(list);
		}
	}

	/**
	 * 
	 * @param queries
	 */
	public void setQueries(List<String> queries) {
		this.queries = new ArrayList<String>();
		for (String query : queries) {
			this.queries.add(query);
		}
	}

	/**
	 * 
	 * @param trees
	 * @param taxa
	 * @return
	 */
	public List<Boolean> run(List<String> trees, List<List<String>> taxa) {
		this.setTrees(trees);
		this.setTaxa(taxa);
		return run();
	}

	/**
	 * 
	 * @param trees
	 * @param taxa
	 * @param queries
	 * @return
	 */
	public List<Boolean> run(List<String> trees, List<List<String>> taxa, List<String> queries) {
		this.setTrees(trees);
		this.setTaxa(taxa);
		this.setQueries(queries);
		return run();
	}

	/**
	 * 
	 * @return
	 */
	public List<Boolean> run() {
		List<Boolean> results = new ArrayList<Boolean>();
		long start = System.currentTimeMillis();
		logger.info("Started sorting trees...");
		int failCounter = 0;
		if (queries == null) {
			for (TreeNode tree : this.trees) {
				if (tree != null) {
				results.add(runOneTree(tree));
				} else {
					failCounter++;
					results.add(false);
				}
			}
		} else {
			for (int i = 0, n = trees.size(); i < n; i++) {
				results.add(runOneTree(trees.get(i), queries.get(i)));
			}
		}
		long end = System.currentTimeMillis();
		logger.info("Finished sorting " + trees .size() + " trees in " + (end - start) + " milliseconds");
		logger.info("Length of results: " + results.size());
		if (failCounter > 0) {
			logger.info("Failed to sort / parse " + failCounter + " trees.");
		}
		return results;
	}

	/**
	 * 
	 * @param tree
	 * @return
	 */
	private boolean runOneTree(TreeNode tree) {
		return runOneTree(tree, null);
	}

	/**
	 * 
	 * @param tree
	 * @param query
	 *            Query OTU that must exist in the monophyletic clade
	 * @return
	 */
	private boolean runOneTree(TreeNode tree, String query) {

		boolean result = false;

		int size = Config.DEFAULT_MINIMUM_NUMBER_OF_TAXA;

		if (config.isMaximumNumberOfTaxaOn() || config.isMinimumNumberOfTaxaOn()) {
			size = tree.size();
		}

		float copies = Config.DEFAULT_MAXIMUM_AVERAGE_NUMBER_OF_COPIES;

		if (config.isMaximumAverageNumberOfCopiesOn()) {
			copies = TreeNodeUtil.getAverageNumberOfCopies(tree);
		}

		if ((!config.isMinimumNumberOfTaxaOn() || (config.isMinimumNumberOfTaxaOn() && size >= config.getMinimumNumberOfTaxa()))
				&& (!config.isMaximumNumberOfTaxaOn() || (config.isMaximumNumberOfTaxaOn() && size <= config.getMaximumNumberOfTaxa()))
				&& (!config.isMaximumAverageNumberOfCopiesOn() || (config.isMaximumAverageNumberOfCopiesOn() && copies <= config.getMaximumAverageNumberOfCopies()))) {

			Collection<TreeNode> monophyletic = null;
			TreeNode tree2 = tree;

			if (Config.getInstance().isOutgroup()) {
				TreeNode outgroup = TreeNodeUtil.findOutgroup(tree, set);
				if (outgroup != null) {
					TreeNode rerooted = TreeNodeUtil.reroot(outgroup);
					tree2 = rerooted;
				}
			}

			monophyletic = TreeNodeUtil.getAllMonophyleticNodes(tree2, this.taxa, config.isExclusive(), query);

			if (monophyletic != null) {

				if (config.isMinimumBootstrapSupportOn()) {

					Iterator<TreeNode> iterator = monophyletic.iterator();
					boolean found = false;

					while (iterator.hasNext() && !found) {

						TreeNode node = iterator.next();

						float bootstrap = 0;// Float.MAX_VALUE;

						if (!node.isLeaf()) {
							try {
								String label = node.getLabel();
								bootstrap = Float.parseFloat(label);
							} catch (Exception silent) {
								String msg = "Failed parsing bootstrap from tree: " + silent.getMessage();
								logger.log(Level.WARNING, msg);
							}
						} else {
							logger.warning("Matching monophyletic clade is single node. There is no bootstrap.");
						}

						if (bootstrap >= config.getMinimumBootstrapSupport()) {
							result = true;
						}
					}
				} else {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param maximumAverageNumberOfCopies
	 */
	public void setMaximumAverageNumberOfCopies(float maximumAverageNumberOfCopies) {
		config.setMaximumAverageNumberOfCopies(maximumAverageNumberOfCopies);
	}

	/**
	 * 
	 * @return
	 */
	public float getMaximumAverageNumberOfCopies() {
		return config.getMaximumAverageNumberOfCopies();
	}

	/**
	 * 
	 * @param maximumNumberOfTaxa
	 */
	public void setMaximumNumberOfTaxa(int maximumNumberOfTaxa) {
		config.setMaximumNumberOfTaxa(maximumNumberOfTaxa);
	}

	/**
	 * 
	 * @return
	 */
	public int getMaximumNumberOfTaxa() {
		return config.getMaximumNumberOfTaxa();
	}

	/**
	 * 
	 * @param minimumBootstrapSupport
	 */
	public void setMinimumBootstrapSupport(float minimumBootstrapSupport) {
		config.setMinimumBootstrapSupport(minimumBootstrapSupport);
	}

	/**
	 * 
	 * @return
	 */
	public float getMinimumBootstrapSupport() {
		return config.getMinimumBootstrapSupport();
	}

	/**
	 * 
	 * @param minimumNumberOfTaxa
	 */
	public void setMinimumNumberOfTaxa(int minimumNumberOfTaxa) {
		config.setMinimumNumberOfTaxa(minimumNumberOfTaxa);
	}

	/**
	 * 
	 * @return
	 */
	public int getMinimumNumberOfTaxa() {
		return config.getMinimumNumberOfTaxa();
	}

	/**
	 * 
	 * @param mode
	 * @throws Exception
	 */
	public void setMode(String mode) throws Exception {
		config.setMode(mode);
	}

	/**
	 * 
	 * @return
	 */
	public String getMode() {
		return config.getMode();
	}

	/**
	 * 
	 * @param outgroup
	 */
	public void setOutgroup(boolean outgroup) {
		config.setOutgroup(outgroup);
	}

	/**
	 * 
	 * @return
	 */
	public boolean getOutgroup() {
		return config.isOutgroup();
	}
	
	/**
	 * 
	 * @param query required
	 */
	public void setQueryRequired(boolean queryRequired) {
		config.setQueryRequired(queryRequired);
	}

	/**
	 * 
	 * @return
	 */
	public boolean getQueryRequired() {
		return config.isQueryRequired();
	}
	

	/**
	 * 
	 * @param regexp
	 */
	public void setRegexp(String regexp) {
		config.setRegexp(regexp);
	}

	/**
	 * 
	 * @return
	 */
	public String getRegexp() {
		return config.getRegexp().toString();
	}

	/**
	 * 
	 * @param regexp
	 */
	public void setFilenameRegexp(String regexp) {
		config.setFilenameRegexp(regexp);
	}

	/**
	 * 
	 * @return
	 */
	public String getFilenameRegexp() {
		return config.getFilenameRegexp().toString();
	}

	/**
	 * 
	 * @param file
	 */
	public void setConfig(File file) throws Exception {
		Config.load(file);
	}

	/**
	 * 
	 * @param filename
	 */
	public void setConfig(String filename) throws Exception {
		Config.load(filename);
	}

	/**
	 * 
	 * @param properties
	 */
	public void setConfig(Properties properties) throws Exception {
		Config.load(properties);
	}

	/**
	 * 
	 * @return
	 */
	public String getConfig() {
		return config.toString();
	}
}

/*
 * $Id: PhyloSort.java,v 1.42 2012/04/30 04:48:05 ahmed Exp $
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import phyloutil.misc.CombinationGenerator;
import phyloutil.misc.Commons;
import phyloutil.misc.Config;
import phyloutil.misc.SingleLogger;

/**
 * Phylogenetic trees sorting tool
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.42 $
 */
public class PhyloSort {

	/**
	 * Logger
	 */
	private static final Logger logger = SingleLogger.getLogger();

	/**
	 * Returns {@link List} of {@link String} of all taxa in all trees in a
	 * directory
	 * 
	 * @param directory
	 *            Input directory
	 * @return {@link List} of {@link String} of all taxa in all trees in a
	 *         directory
	 */
	public static List<String> loadTaxa(File directory) {

		Set<String> taxa = new HashSet<String>();

		for (String file : directory.list()) {

			String path = directory + Commons.getFileSeparator() + file;

			File in = new File(path);

			if (in.isFile() && in.canRead()) {

				logger.info("Loading taxa from tree file \"" + file + "\"");

				try {
					taxa.addAll(TreeNodeUtil.getTaxa(in));
				} catch (Exception e) {
					String msg = "Failed getting taxa from tree file \"" + in.getName() + "\": " + e.getMessage();
					logger.log(Level.SEVERE, msg, e);
				} catch (Error error) {
					String msg = "Failed getting taxa from tree file \"" + in.getName() + "\": " + error.getMessage();
					logger.log(Level.SEVERE, msg, error);
				}

				logger.info("Finished loading taxa from tree file \"" + file + "\"");

			}
		}

		List<String> sorted = new ArrayList<String>();

		for (String taxon : taxa) {
			if (taxon != null && taxon.trim().length() != 0) {
				sorted.add(taxon);
			}
		}

		logger.info("Loaded " + sorted.size() + " taxa from " + directory.list().length + " files in " + directory.getPath());

		Collections.sort(sorted);

		logger.info("Finished loading taxa");

		if (sorted.size() == 0) {
			logger.warning("Found no taxa");
		}

		return sorted;
	}

	/**
	 * Loads groups of taxa from a text file
	 * 
	 * @param filename
	 *            Text file name to load taxa groups from
	 * @return List of lists of groups
	 * @throws Exception
	 */
	public static List<List<String>> loadGroups(String filename) throws Exception {

		BufferedReader reader = null;

		try {

			logger.info("Loading groups from " + filename + "...");

			reader = new BufferedReader(new FileReader(filename));
			String line = null;
			StringTokenizer tokenizer = null;

			List<List<String>> supergroup = new ArrayList<List<String>>();
			List<String> group = null;

			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (!line.startsWith("#") && line.length() > 0) {

					group = new ArrayList<String>();

					tokenizer = new StringTokenizer(line, ", \t\r\n:");

					while (tokenizer.hasMoreTokens()) {

						group.add(tokenizer.nextToken());

					}

					supergroup.add(group);
				}
			}

			logger.info("Finished loading groups from " + filename);

			return supergroup;

		} catch (Exception e) {

			String msg = "Failed loading groups from " + filename + ": " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);

		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (Exception silent) {
					String msg = "Failed closing groups file " + filename + ": " + silent.getMessage();
					logger.log(Level.WARNING, msg, silent);
				}
			}

		}
	}

	/**
	 * Loads groups of taxa from a text file
	 * 
	 * @param filename
	 *            Text file name to load taxa groups from
	 * @return List of lists of groups
	 * @throws Exception
	 */
	public static Map<String, List<String>> loadGroupsWithNames(String filename) throws Exception {

		BufferedReader reader = null;

		try {

			logger.info("Loading groups from " + filename + "...");

			reader = new BufferedReader(new FileReader(filename));
			String line = null;
			StringTokenizer tokenizer = null;

			Map<String, List<String>> supergroup = new HashMap<String, List<String>>();
			List<String> group = null;

			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (!line.startsWith("#") && line.length() > 0) {

					group = new ArrayList<String>();

					tokenizer = new StringTokenizer(line, ", \t\r\n:");

					String name = null;

					if (tokenizer.hasMoreTokens()) {
						name = tokenizer.nextToken();
					}

					while (tokenizer.hasMoreTokens()) {

						group.add(tokenizer.nextToken());

					}

					supergroup.put(name, group);
				}
			}

			logger.info("Finished loading groups from " + filename);

			return supergroup;

		} catch (Exception e) {

			String msg = "Failed loading groups from " + filename + ": " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);

		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (Exception silent) {
					String msg = "Failed closing groups file " + filename + ": " + silent.getMessage();
					logger.log(Level.WARNING, msg, silent);
				}
			}

		}
	}

	/**
	 * Automatically sorts trees in different categories.
	 * 
	 * @param infolder
	 * @param base
	 * @param optionalGroups
	 * @param optionalNames
	 * @return
	 * @throws Exception
	 */
	public static Map<List<String>, Integer> autoSort(String infolder, String base, final List<List<String>> requiredGroups, List<String> requiredNames, final List<List<String>> optionalGroups,
			List<String> optionalNames) throws Exception {

		Map<List<String>, Integer> counts = new HashMap<List<String>, Integer>();
		StringBuffer buffer = new StringBuffer();
		String runID = null;
		int count = -1;

		// Start with the optional groups included
		for (int r = optionalGroups.size(), n = optionalGroups.size(); r > 0; r--) {

			CombinationGenerator generator = new CombinationGenerator(n, r);

			while (generator.hasMore()) {

				int[] combination = generator.getNext();

				if (combination.length > 0) {

					List<List<String>> list = new ArrayList<List<String>>();
					List<String> runNames = new ArrayList<String>();

					for (int i = 0, m = requiredGroups.size(); i < m; i++) {
						list.add(requiredGroups.get(i));
						runNames.add(requiredNames.get(i));
					}

					for (int i = 0; i < combination.length; i++) {
						list.add(optionalGroups.get(combination[i]));
						runNames.add(optionalNames.get(combination[i]));
					}

					Collections.sort(runNames);
					buffer = new StringBuffer();
					if (runNames.size() > 0) {
						buffer.append(runNames.get(0));
						for (int k = 1, m = runNames.size(); k < m; k++) {
							buffer.append("_");
							buffer.append(runNames.get(k));
						}
					}

					runID = buffer.toString();

					String outfolder = base + Commons.getFileSeparator() + runID;
					new File(outfolder).mkdir();
					count = sort(infolder, outfolder, list);
					counts.put(runNames, count);

					if (count == 0) {
						//new File(outfolder).delete();
					}
				}
			}
		}

		// Final run with the required groups only (i.e., no optional groups)
		List<String> sortedRequiredNames = new ArrayList<String>();
		sortedRequiredNames.addAll(requiredNames);
		Collections.sort(sortedRequiredNames);
		buffer = new StringBuffer();
		if (sortedRequiredNames.size() > 0) {
			buffer.append(sortedRequiredNames.get(0));
			for (int i = 1, n = sortedRequiredNames.size(); i < n; i++) {
				buffer.append("_");
				buffer.append(sortedRequiredNames.get(i));
			}
			runID = buffer.toString();
			String outfolder = base + Commons.getFileSeparator() + runID;
			new File(outfolder).mkdir();
			count = sort(infolder, outfolder, requiredGroups);
			counts.put(sortedRequiredNames, count);

			if (count == 0) {
				//new File(outfolder).delete();
			}
		}

		return counts;
	}

	/**
	 * Automatically sorts trees in different categories.
	 * 
	 * @param infolder
	 * @param base
	 * @param optionalGroups
	 * @param optionalNames
	 * @return
	 * @throws Exception
	 */
	public static Map<List<String>, Integer> autoSort2(String infolder, String base, final List<List<String>> requiredGroups, List<String> requiredNames, final List<List<String>> optionalGroups,
			List<String> optionalNames) throws Exception {

		Map<List<String>, Integer> counts = new HashMap<List<String>, Integer>();
		StringBuffer buffer = new StringBuffer();
		String runID = null;
		int count = -1;

		// Start with the optional groups included
		for (int i = 0, n = optionalGroups.size(); i < n; i++) {

			List<List<String>> list = new ArrayList<List<String>>();
			List<String> runNames = new ArrayList<String>();

			for (int j = 0, m = requiredGroups.size(); j < m; j++) {
				list.add(requiredGroups.get(j));
				runNames.add(requiredNames.get(j));
			}

			Collections.sort(runNames);
			buffer = new StringBuffer();
			if (runNames.size() > 0) {
				buffer.append(runNames.get(0));
				for (int k = 1, m = runNames.size(); k < m; k++) {
					buffer.append("_");
					buffer.append(runNames.get(k));
				}
			}

			list.add(optionalGroups.get(i));
			runNames.add(optionalNames.get(i));

			buffer.append("_");
			buffer.append(optionalNames.get(i));

			runID = buffer.toString();

			String outfolder = base + Commons.getFileSeparator() + runID;
			new File(outfolder).mkdir();
			count = sort(infolder, outfolder, list);
			counts.put(runNames, count);

			if (count == 0) {
				//new File(outfolder).delete();
			}
		}

		// Final run with the required groups only (i.e., no optional groups)
		List<String> sortedRequiredNames = new ArrayList<String>();
		sortedRequiredNames.addAll(requiredNames);
		Collections.sort(sortedRequiredNames);
		buffer = new StringBuffer();
		if (sortedRequiredNames.size() > 0) {
			buffer.append(sortedRequiredNames.get(0));
			for (int i = 1, n = sortedRequiredNames.size(); i < n; i++) {
				buffer.append("_");
				buffer.append(sortedRequiredNames.get(i));
			}
			runID = buffer.toString();
			String outfolder = base + Commons.getFileSeparator() + runID;
			new File(outfolder).mkdir();
			count = sort(infolder, outfolder, requiredGroups);
			counts.put(sortedRequiredNames, count);

			if (count == 0) {
				//new File(outfolder).delete();
			}
		}

		return counts;
	}

	/**
	 * Sorts trees from an input folder to an output folder based on a set of
	 * query taxa
	 * 
	 * @param infolder
	 *            Input folder
	 * @param outfolder
	 *            Output folder
	 * @param taxa
	 *            Groups of taxa
	 * @return Number of sorted (matching) trees
	 * @throws Exception
	 */
	public static int sort(String infolder, String outfolder, final List<List<String>> taxa) throws Exception {
		try {

			File idir = new File(infolder);
			String[] files = idir.list();

			File odir = new File(outfolder);
			if (!odir.exists()) {
				odir.mkdir();
			}

			int current = 0;
			int match = 0;
			int total = files.length;

			long start1 = System.currentTimeMillis();

			if (taxa.isEmpty()) {
				logger.severe("Empty query taxa!");
			}

			Set<String> set = new HashSet<String>();
			for (List<String> list : taxa) {
				set.addAll(list);
			}

			logger.info("Started sorting trees...");

			Config config = Config.getInstance();

			if (config.isMinimumBootstrapSupportOn()) {

				for (String filename : files) {

					Matcher matcher = Config.getInstance().getFilenameRegexp().matcher(filename);
					if (matcher.matches()) {

						String query = matcher.group(1);

						String infullpath = infolder + Commons.getFileSeparator() + filename;
						String outfullpath = outfolder + Commons.getFileSeparator() + filename;

						File infile = new File(infullpath);

						if (infile.isFile() && infile.canRead()) {

							long start2 = System.currentTimeMillis();

							current++;

							logger.info(current + " / " + total + ": " + filename + " ... ");

							TreeNode tree = TreeNodeUtil.load(infile);

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
										// System.out.println ("Rerooted: " + rerooted.toString());
										tree2 = rerooted;
									}
								}

								monophyletic = TreeNodeUtil.getAllMonophyleticNodes(tree2, taxa, config.isExclusive(), query);

								if (monophyletic != null) {

									Iterator<TreeNode> iterator = monophyletic.iterator();
									boolean found = false;

									while (iterator.hasNext() && !found) {

										TreeNode node = iterator.next();

										float bootstrap = 0;

										if (!node.isLeaf()) {
											try {
												String label = node.getLabel();
												bootstrap = Float.parseFloat(label);
											} catch (Exception silent) {
												String msg = "Failed parsing bootstrap from tree: " + silent.getMessage();
												logger.log(Level.WARNING, msg, silent);
											}
										} else {
											logger.warning("Matching monophyletic clade is single node. There is no bootstrap.");
										}

										if (bootstrap >= config.getMinimumBootstrapSupport()) {
											if (config.getOnMatchAction().equalsIgnoreCase(Config.PROPERTY_ON_MATCH_ACTION_COPY)) {
												File outfile = new File(outfullpath);
												Commons.copy(infile, outfile);
											} else if (config.getOnMatchAction().equalsIgnoreCase(Config.PROPERTY_ON_MATCH_ACTION_MOVE)) {
												File outfile = new File(outfullpath);
												Commons.move(infile, outfile);
											}
											found = true;
											match++;
										}

									}

								}

							}

							long end2 = System.currentTimeMillis();

							logger.info("Finished processing " + filename + " in " + (end2 - start2) + " milliseconds");
						}
					}
				}

			} else {

				for (String filename : files) {
					Matcher matcher = Config.getInstance().getFilenameRegexp().matcher(filename);
					if (matcher.matches()) {

						String query = matcher.group(1);

						String infullpath = infolder + Commons.getFileSeparator() + filename;
						String outfullpath = outfolder + Commons.getFileSeparator() + filename;

						File infile = new File(infullpath);

						if (infile.isFile() && infile.canRead()) {

							long start2 = System.currentTimeMillis();

							current++;

							logger.info(current + " / " + total + ": " + filename + " ... ");

							TreeNode tree = TreeNodeUtil.load(infile);

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
										// System.out.println ("Rerooted: " + rerooted.toString());
										tree2 = rerooted;
									}
								}

								monophyletic = TreeNodeUtil.getAllMonophyleticNodes(tree2, taxa, config.isExclusive(), query);

								if (monophyletic != null) {

									if (config.getOnMatchAction().equalsIgnoreCase(Config.PROPERTY_ON_MATCH_ACTION_COPY)) {
										File outfile = new File(outfullpath);
										Commons.copy(infile, outfile);
									} else if (config.getOnMatchAction().equalsIgnoreCase(Config.PROPERTY_ON_MATCH_ACTION_MOVE)) {
										File outfile = new File(outfullpath);
										Commons.move(infile, outfile);
									}

									match++;
								}

							}

							long end2 = System.currentTimeMillis();

							logger.info("Finished processing " + filename + " in " + (end2 - start2) + " milliseconds");
						}
					}
				}

			}

			long end1 = System.currentTimeMillis();

			logger.info("Processed: " + current + " trees");
			logger.info("Found: " + match + " matching trees");

			logger.info("Finished sorting trees in " + (end1 - start1) + " milliseconds");

			// writeReport(infolder, outfolder, total, match, taxa);

			return match;
		} catch (Exception e) {

			String msg = "Failed sorting trees: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg);

		}
	}

	/**
	 * Write a summary report
	 * 
	 * @param in
	 *            Input folder
	 * @param out
	 *            Output folder
	 * @param total
	 *            Number of processed trees (from input folder)
	 * @param match
	 *            Number of matching tree (went to output folder)
	 * @param taxa
	 *            Groups of query taxa
	 */
	@SuppressWarnings("unused")
	private static void writeReport(String in, String out, int total, int match, final List<List<String>> taxa) {

		Config config = Config.getInstance();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date();
		String timestamp = simpleDateFormat.format(now);

		StringBuffer buffer = new StringBuffer();

		buffer.append("PhyloSort Report (" + timestamp + ")");
		buffer.append(Commons.getLineSeparator());
		buffer.append("----------------");
		buffer.append(Commons.getLineSeparator());
		buffer.append(Commons.getLineSeparator());

		buffer.append("Input folder = " + in);
		buffer.append(Commons.getLineSeparator());
		buffer.append("Output folder = " + out);
		buffer.append(Commons.getLineSeparator());
		buffer.append(Commons.getLineSeparator());

		buffer.append("Number of processed trees = " + total);
		buffer.append(Commons.getLineSeparator());
		buffer.append("Number of matching trees = " + match);
		buffer.append(Commons.getLineSeparator());

		buffer.append(Commons.getLineSeparator());
		buffer.append("Taxa groups");
		buffer.append(Commons.getLineSeparator());
		buffer.append("-----------");
		buffer.append(Commons.getLineSeparator());

		for (List<String> group : taxa) {
			if (group.size() > 0) {
				Iterator<String> iterator = group.iterator();
				buffer.append("Group: " + iterator.next());
				while (iterator.hasNext()) {
					String taxon = iterator.next();
					buffer.append(", " + taxon);
				}
				buffer.append(Commons.getLineSeparator());
			}
		}

		buffer.append(Commons.getLineSeparator());
		buffer.append("Filters");
		buffer.append(Commons.getLineSeparator());
		buffer.append("-------");
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_ROOT + " = " + config.getRoot());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_QUERY_REQUIRED + " = " + config.getQueryRequired());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MODE + " = " + config.getMode());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MINIMUM_BOOTSTRAP_SUPPORT + " = " + config.getMinimumBootstrapSupport());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MAXIMUM_NUMBER_OF_TAXA + " = " + config.getMaximumNumberOfTaxa());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MINIMUM_NUMBER_OF_TAXA + " = " + config.getMinimumNumberOfTaxa());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MAXIMUM_AVERAGE_NUMBER_OF_COPIES + " = " + config.getMaximumAverageNumberOfCopies());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_ON_MATCH_ACTION + " = " + config.getOnMatchAction());
		buffer.append(Commons.getLineSeparator());

		buffer.append(Commons.getLineSeparator());
		buffer.append("Settings");
		buffer.append(Commons.getLineSeparator());
		buffer.append("--------");
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_ON_MATCH_ACTION + " = " + config.getOnMatchAction());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_REGEXP + " = " + config.getRegexp());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_FILENAME_REGEXP + " = " + config.getFilenameRegexp());
		buffer.append(Commons.getLineSeparator());

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(out + Commons.getFileSeparator() + "phylosort-report-" + timestamp + ".txt"));
			writer.write(buffer.toString());
		} catch (Exception exception) {
			String msg = "Failed writing report: " + exception.getMessage();
			logger.log(Level.SEVERE, msg, exception);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					String msg = "Failed closing report: " + e.getMessage();
					logger.log(Level.SEVERE, msg, e);
				}
			}
		}

	}
}
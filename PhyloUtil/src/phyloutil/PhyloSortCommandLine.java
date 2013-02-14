/*
 * $Id: PhyloSortCommandLine.java,v 1.10 2009/11/24 14:50:54 ahmed Exp $
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.misc.Commons;
import phyloutil.misc.Config;
import phyloutil.misc.SingleLogger;

/**
 * Command line interface for PhyloSort.
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.10 $
 */
public class PhyloSortCommandLine {

	private static final Logger logger = SingleLogger.getLogger();

	/**
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		System.out.println("PhyloSort - phylogenetic trees sorting");

		String version = Commons.getCurrentVersion();
		if (version != null) {
			System.out.println("Version " + version);
		}

		String build = Commons.getBuildTimestamp();
		if (build != null) {
			System.out.println("Build " + build);
		}

		String config = System.getProperty(Config.SYSTEM_PROPERTY_PHYLOSORT_CONFIG);
		if (config != null) {
			try {
				Config.load(config);
			} catch (Exception e) {
				String msg = "Failed loading config: " + config;
				logger.log(Level.WARNING, msg, e);
				logger.info("Using default settings.");
			}
		} else {

			File file = new File(Config.DEFAULT_PHYLOSORT_FILE_NAME);
			if (file.exists()) {

				try {
					logger.info("Found config file " + Config.DEFAULT_PHYLOSORT_FILE_NAME + " in the current directory...");
					Config.load(file.getPath());
				} catch (Exception e) {
					String msg = "Failed loading config file " + file.getName() + ": " + e.getMessage();
					logger.log(Level.WARNING, msg, e);
					logger.info("Using default settings.");
				}

			} else {
				logger.info("Using default settings.");
			}
		}

		if (args.length == 1) {

			try {
				String filename = args[0];
				File file = new File(filename);
				List<String> taxa = PhyloSort.loadTaxa(file);

				for (String taxon : taxa) {
					System.out.println(taxon);
				}

			} catch (Exception e) {
				String msg = "Failed loading taxa: " + e.getMessage();
				logger.log(Level.SEVERE, msg, e);
				System.exit(1);
			}

		} else if (args.length == 3) {
			try {
				String ifolder = args[0]; // Folder with input trees
				String ofolder = args[1]; // Folder for output trees
				String igroups = args[2]; // File with input groups (taxa)

				List<List<String>> groups = PhyloSort.loadGroups(igroups);

				PhyloSort.sort(ifolder, ofolder, groups);

			} catch (Exception e) {
				String msg = "Failed sorting trees: " + e.getMessage();
				logger.log(Level.SEVERE, msg, e);
				System.exit(1);
			}
		} else if (args.length == 4) {
			try {
				String inputFolder = args[0]; // Folder with input trees
				String outputFolder = args[1]; // Folder for output trees
				String requiredGroupsFilename = args[2]; // File with
															// required
															// input groups
															// (taxa)
				String optionalGroupsFilename = args[3]; // File with
															// optional
															// input groups
															// (taxa)

				Map<String, List<String>> required = PhyloSort.loadGroupsWithNames(requiredGroupsFilename);

				List<List<String>> requiredGroups = new ArrayList<List<String>>();
				List<String> requiredNames = new ArrayList<String>();

				for (String key : required.keySet()) {
					requiredNames.add(key);
					requiredGroups.add(required.get(key));
				}

				Map<String, List<String>> optional = PhyloSort.loadGroupsWithNames(optionalGroupsFilename);

				List<List<String>> optionalGroups = new ArrayList<List<String>>();
				List<String> optionalNames = new ArrayList<String>();

				for (String key : optional.keySet()) {
					optionalNames.add(key);
					optionalGroups.add(optional.get(key));
				}

				Map<List<String>, Integer> counts = PhyloSort.autoSort(inputFolder, outputFolder, requiredGroups, requiredNames, optionalGroups, optionalNames);
				System.out.println("Total number of combinations: " + counts.size());
				for (List<String> key : counts.keySet()) {
					int value = counts.get(key);
					System.out.print(value);
					for (String group : key) {
						System.out.print(Commons.TAB + group);
					}
					System.out.println();
				}

			} catch (Exception e) {
				String msg = "Failed sorting trees: " + e.getMessage();
				logger.log(Level.SEVERE, msg, e);
				System.exit(1);
			}
		} else {
			logger.severe("Invalid number of arguments: " + args.length);
			printUsage();
			System.exit(1);
		}
	}

	/**
	 * Prints the syntax for using JAligner
	 */
	private static void printUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Commons.getLineSeparator());
		buffer.append("Usage:");
		buffer.append(Commons.getLineSeparator());
		buffer.append("------");
		buffer.append(Commons.getLineSeparator());
		buffer.append("[1] java -jar phylosort.jar <infolder> <outfolder> <taxafile>");
		buffer.append(Commons.getLineSeparator());
		buffer.append("[2] java -jar phylosort.jar");
		buffer.append(Commons.getLineSeparator());
		buffer.append("[1] java -Dphylosort.config=<configfile> -jar phylosort.jar <infolder> <outfolder> <taxafile>");
		buffer.append(Commons.getLineSeparator());
		buffer.append("[2] java -Dphylosort.config=<configfile> -jar phylosort.jar");
		buffer.append(Commons.getLineSeparator());
		buffer.append("\n");
		logger.info(buffer.toString());
	}
}
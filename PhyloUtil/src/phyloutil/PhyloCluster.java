/*
 * $Id: PhyloCluster.java,v 1.2 2008/07/08 16:08:58 ahmed Exp $
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.misc.Commons;

/**
 * Phylogenetic trees clustering tool
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.2 $
 */
public class PhyloCluster {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(PhyloCluster.class.getName());

	/**
	 * Tree cluster name prefix
	 */
	private static final String CLUSTER_PREFIX = "cluster";

	/**
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		if (args.length == 3) {
			try {
				String ifolder = args[0]; // Folder with input trees
				String ofolder = args[1]; // Folder for output trees
				int minimum = Integer.parseInt(args[2]); // Minimum number of
				// overlapping OTUs

				int in = new File(ifolder).list().length;
				int out = cluster(ifolder, ofolder, minimum).size();
				
				
				System.out.println (ifolder + Commons.TAB + in + Commons.TAB + out);

			} catch (Exception e) {
				String msg = "Failed clustering trees: " + e.getMessage();
				logger.log(Level.SEVERE, msg, e);
				System.exit(1);
			}
		} else {
			logger.severe("Invalid number of arguments: " + args.length);
			System.exit(1);
		}
	}

	/**
	 * Clusters trees from an input folder
	 * 
	 * @param infolder
	 *            Input folder
	 * @param outfolder
	 *            Output folder
	 * @return Clustered trees
	 * @throws Exception
	 */
	public static List<TreeCluster> cluster(String infolder, String outfolder) throws Exception {
		return cluster(infolder, outfolder, TreeCluster.DEFAULT_MINIMUM_OVERLAP);
	}

	/**
	 * Clusters trees from an input folder
	 * 
	 * @param infolder
	 *            Input folder
	 * @param outfolder
	 *            Output folder
	 * @param minimumOverlap
	 *            Minimum number of overlapping taxa
	 * @return Clustered trees
	 * @throws Exception
	 */
	public static List<TreeCluster> cluster(String infolder, String outfolder, int minimumOverlap) throws Exception {
		try {

			File idir = new File(infolder);
			String[] files = idir.list();

			File odir = new File(outfolder);
			if (!odir.exists()) {
				odir.mkdir();
			}

			int total = 0;

			long start1 = System.currentTimeMillis();

			List<TreeCluster> clusters = new ArrayList<TreeCluster>();

			logger.info("Started clustering trees...");

			// Load all trees and put each tree in a cluster by itself
			for (String filename : files) {
				String infullpath = infolder + Commons.getFileSeparator() + filename;
				File infile = new File(infullpath);
				if (infile.isFile() && infile.canRead()) {
					long start2 = System.currentTimeMillis();
					total++;
					logger.info("Processing: " + filename + " ... ");
					TreeCluster cluster = new TreeCluster();
					cluster.add(infile);
					clusters.add(cluster);
					long end2 = System.currentTimeMillis();
					logger.info("Finished processing " + filename + " in " + (end2 - start2) + " milliseconds");
				}
			}

			boolean finished = true;
			int round = 0;

			// Start clustering
			do {
				logger.info("Clustering round # " + ++round);
				finished = true;

				List<TreeCluster> remove = new ArrayList<TreeCluster>();

				for (int i = 0, n = clusters.size(); i < n - 1; i++) {

					TreeCluster cluster1 = clusters.get(i);

					for (int j = i + 1; j < n; j++) {

						TreeCluster cluster2 = clusters.get(j);

						if (cluster1.overlaps(cluster2, minimumOverlap)) {
							// Merge two overlapping clustering
							cluster1.merge(cluster2);
							remove.add(cluster2);
							finished = false;
						}
					}
				}
				// Remove clusters that have been merged into others
				clusters.removeAll(remove);
			} while (!finished);

			logger.info("Finished clustering in " + round + " rounds(s)");

			long end1 = System.currentTimeMillis();

			logger.info("Processed: " + total + " trees");
			logger.info("Found: " + clusters.size() + " clusters");

			logger.info("Finished clustering trees in " + (end1 - start1) + " milliseconds");

			StringBuffer buffer = new StringBuffer();
			buffer.append(CLUSTER_PREFIX);
			for (int i = 0, n = new Integer(clusters.size()).toString().length(); i < n; i++) {
				buffer.append("0");
			}

			NumberFormat numberFormat = new DecimalFormat(buffer.toString());
			
			logger.info("Writing clusters to destination folder...");

			int i = 1;
			for (TreeCluster cluster : clusters) {

				String id = numberFormat.format(i);

				File dir = new File(outfolder + Commons.getFileSeparator() + id);
				dir.mkdir();

				for (String tree : cluster.getFiles()) {

					File ifile = new File(infolder + Commons.getFileSeparator() + tree);
					File ofile = new File(dir.getPath() + Commons.getFileSeparator() + tree);

					Commons.copy(ifile, ofile);
				}

				i++;
			}
			
			logger.info("Finished writing clusters to destination folder");

			return clusters;

		} catch (Exception exception) {
			String msg = "Failed clustering trees: " + exception.getMessage();
			logger.log(Level.SEVERE, msg, exception);
			throw new Exception(msg);
		}
	}
}
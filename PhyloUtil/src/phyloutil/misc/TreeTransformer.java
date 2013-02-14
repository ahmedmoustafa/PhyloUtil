package phyloutil.misc;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.TreeNode;
import phyloutil.TreeNodeUtil;

public class TreeTransformer {

	private static Logger logger = Logger.getLogger(TreeTransformer.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println("Invalid number of arguments: " + args.length);
			System.err.println("Expected: infile outfile");
			System.exit(1);
		}

		try {

			String ifile = args[0];
			String ofile = args[1];

			TreeNode itree = TreeNodeUtil.load(new File(ifile));

			System.out.println(itree.toString());

			Iterable<TreeNode> nodes = itree.getNodes();

			float sum = 0;
			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;
			int count = 0;

			for (TreeNode node : nodes) {
				System.out.println(node.getId() + "\t" + node.getLabel() + "\t" + node.getLength());
				sum += node.getLength();

				if (node.getLength() != 0 && node.getLength() < min) {
					min = node.getLength();
				}

				if (node.getLength() > max) {
					max = node.getLength();
				}

				count++;
			}

			float avg = sum / count;

			System.out.println("Min: " + min);
			System.out.println("Max: " + max);
			System.out.println("Avg: " + avg);

			float offset = (float) Math.log10(min);

			System.out.println(min + "\t" + offset);

			if (offset < 0) {
				offset = -offset;
			} else {
				offset = 0;
			}

			System.out.println(min + "\t" + offset);

			TreeNode otree = TreeNodeUtil.transform(itree, (float)1e3);

			TreeNodeUtil.save(otree, ofile);

		} catch (Exception e) {
			String msg = "Failed tranforming tree: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			System.exit(1);
		}
	}
}

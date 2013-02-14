package phyloutil.misc;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import phyloutil.TreeNode;
import phyloutil.TreeNodeUtil;

public class TreeCleaner {

	private static Logger logger = Logger.getLogger(TreeCleaner.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.err.println ("Invalid number of arguments: " + args.length);
			System.err.println ("Expected: infile outfile min");
			System.exit(1);
		}

		try {
			String in = args[0];
			String out = args[1];
			float min = Float.parseFloat(args[2]);

			TreeNode tree1 = TreeNodeUtil.load(new File(in));
			TreeNode tree2 = TreeNodeUtil.clean(tree1, min);

			TreeNodeUtil.save(tree2, out);

		} catch (Exception e) {
			String msg = "Failed cleaning tree: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			System.exit(1);
		}
	}
}

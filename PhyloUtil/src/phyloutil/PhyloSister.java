package phyloutil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


public class PhyloSister {

	public static void main(String[] args) {
		try {
			
			String filename = args[0];
			String nodename = args[1];
			
			Set<String> skipset = new HashSet<String>();
			
			if (args.length == 3) {
				String skip = args[2];
				StringTokenizer tokenizer = new StringTokenizer(skip, ",-");
				while (tokenizer.hasMoreTokens()) {
					skipset.add(tokenizer.nextToken());
				}
			}
			
			TreeNode tree = TreeNodeUtil.load(new File(filename));
			TreeNode node = tree.find(nodename);
			TreeNode sibling = TreeNodeUtil.getNearest(node, skipset);

			System.out.println (nodename + "\t" + (sibling != null? sibling.getLabel() : ""));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

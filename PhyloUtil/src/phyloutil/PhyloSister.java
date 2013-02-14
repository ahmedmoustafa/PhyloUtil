package phyloutil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class PhyloSister {

	public static void main(String[] args) {
		String filename = null;
		String nodename = null;

		try {

			filename = args[0];
			nodename = args[1];

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

			String bootstrap = "";

			if (sibling != null) {

				List<TreeNode> list = new ArrayList<TreeNode>();
				list.add(node);
				list.add(sibling);

				int id = TreeNodeUtil.lca(list);
				TreeNode lca = tree.getNode(id);

				if (lca != null) {
					String _bootstrap = lca.getLabel();

					if (_bootstrap == "") {
						bootstrap = "0.0";
					} else {
						bootstrap = _bootstrap;
					}

				}

			}

			System.out.println(nodename + "\t" + (sibling != null ? sibling.getLabel() : "") + "\t" + bootstrap);

		} catch (Exception e) {
			System.err.println("Error in " + filename);
			e.printStackTrace();

		}
	}
}

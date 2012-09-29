package phyloutil.misc;

import java.io.File;

import phyloutil.TreeNode;
import phyloutil.TreeNodeUtil;

public class TestDistance {

	public static void main(String[] args) {
		try {
			String filename = args[0];
			String _node = args[1];

			TreeNode tree = TreeNodeUtil.load(new File(filename));

			System.out.println("Tree: " + tree.toString());

			TreeNode node = tree.find(_node);

			TreeNode farthest = TreeNodeUtil.getFarthest(node);

			System.out.println ("Farthest: " + farthest.getLabel());
			
			TreeNode nearest = TreeNodeUtil.getNearest(node);
			
			System.out.println ("Nearest: " + nearest.getLabel());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

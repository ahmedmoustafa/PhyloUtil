package phyloutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PhyloReducer {

	public static void main(String[] args) {
		try {
			
			String treefilename = args[0];
			String listfilename = args[1];
			
			BufferedReader in = new BufferedReader(new FileReader(listfilename));
			
			List<String> list = new ArrayList<String>();
			String line = null;
			while ((line = in.readLine()) != null) {
				list.add(line);
			}
			
			TreeNode original = TreeNodeUtil.load(new File(treefilename));
			
			System.out.println ("Before:");
			System.out.println (original.toString());
			
			TreeNode reduced = TreeNodeUtil.removeNode(original, list);
			
			System.out.println ("After:");
			System.out.println (reduced.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

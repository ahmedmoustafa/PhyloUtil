package phyloutil.misc;

import phyloutil.NewickParser;
import phyloutil.TreeNode;
import phyloutil.TreeNodeUtil;

public class Test {

	public static void main(String[] args) {
		
		// String str = "(((A:5,B:5)10:2,(C:4,D:4)20:1)30:3,E:10);";
		
		// String str = "(e:3,((a:3,b:4)10:1,(f:4,g:2)40:4)50:3,(c:2,d:3)20:2);";
		
		String str = "(((a:1, b:2)50:2,c:1)60:1,d:2):1;";
		
		TreeNode tree = NewickParser.parse(str);
		
		System.out.println(tree.toString());
		
		TreeNode outgroup = tree.find("a");
		
		TreeNode rerooted = TreeNodeUtil.reroot(outgroup);
		
		System.out.println(rerooted);
		
	}

}

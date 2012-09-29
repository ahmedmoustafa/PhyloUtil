/*
 * $Id: PhyloSortRunnerExample.java,v 1.2 2008/04/11 19:29:23 ahmed Exp $
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

package phyloutil.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.2 $
 */

public class PhyloSortRunnerExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String tree1 = "(((a,b),c),(e,d));";
		String tree2 = "(((e,b),c),(a,d));";
		String tree3 = "(((a,b),c)75.0,(e,d));";
		
		List<String> trees = new ArrayList<String>();
		trees.add(tree1);
		trees.add(tree2);
		trees.add(tree3);

		List<String> list1 = new ArrayList<String>();
		list1.add("a");
		List<String> list2 = new ArrayList<String>();
		list2.add("b");
		List<String> list3 = new ArrayList<String>();
		list3.add("c");
		
		List<List<String>> taxa = new ArrayList<List<String>>();
		taxa.add(list1);
		taxa.add(list2);
		taxa.add(list3);
		
		PhyloSortRunner runner = new PhyloSortRunner();
		runner.setTrees(trees);
		runner.setTaxa(taxa);
		
		System.out.println ("Testing without boostrap...");
		List<Boolean> results = runner.run();
		int i = 1;
		for (Boolean result:results) {
			System.out.println ("Tree " + i++ + ": " + result);
		}
		System.out.println ("Finished testing");
		System.out.println ("Config...");
		System.out.println (runner.getConfig());
		
		System.out.println ("Testing with boostrap...");
		runner.setMinimumBootstrapSupport(50);
		results = runner.run();
		i = 1;
		for (Boolean result:results) {
			System.out.println ("Tree " + i++ + ": " + result);
		}
		System.out.println ("Finished testing");
		
		System.out.println ("Config...");
		System.out.println (runner.getConfig());
	}
}
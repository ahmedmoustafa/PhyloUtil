/*
 * $Id: CombinationGenerator.java,v 1.2 2008/06/27 17:17:29 ahmed Exp $
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

/**
 * A utility class to enumarate all possible combinations. Adopted from Michael
 * Gilleland's implementation (http://www.merriampark.com/comb.htm).
 * 
 * @author Michael Gilleland
 * @author Ahmed Moustafa
 * @version $Revision: 1.2 $
 */

public class CombinationGenerator {

	private int[] a;
	private int n;
	private int r;
	private long remaining;
	private long total;

	/**
	 * Constructor
	 * 
	 */
	public CombinationGenerator(int n, int r) {
		this.n = n;
		this.r = r;
		a = new int[r];
		long nfact = factorial(n);
		long rfact = factorial(r);
		long dfact = factorial(n - r);
		total = nfact / (rfact * dfact);
		reset();
	}

	/**
	 * Resets
	 * 
	 */
	public void reset() {
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		remaining = total;
	}

	/**
	 * Returns number of combinations not yet generated
	 * 
	 */
	public long getRemaining() {
		return remaining;
	}

	/**
	 * Checks whether there more combinations
	 * 
	 */
	public boolean hasMore() {
		return remaining > 0;
	}

	/**
	 * Returns total number of combinations
	 * 
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Computes factorial
	 * 
	 */
	private static long factorial(int n) {
		long fact = 1;
		for (int i = n; i > 1; i--) {
			fact = fact * i;
		}
		return fact;
	}

	/**
	 * Generates next combination (algorithm from Rosen p. 286)
	 * 
	 */
	public int[] getNext() {

		if (remaining == total) {
			remaining--;
			return a;
		}

		int i = r - 1;
		while (a[i] == n - r + i) {
			i--;
		}
		a[i] = a[i] + 1;
		for (int j = i + 1; j < r; j++) {
			a[j] = a[i] + j - i;
		}

		remaining--;
		return a;

	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		int r = Integer.parseInt(args[1]);

		CombinationGenerator generator = new CombinationGenerator(n, r);

		while (generator.hasMore()) {
			int[] list = generator.getNext();
			for (int i = 0; i < list.length; i++) {
				System.out.print(list[i] + "\t");
			}
			System.out.println();
		}
	}
}
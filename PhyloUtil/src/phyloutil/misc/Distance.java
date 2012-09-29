package phyloutil.misc;

public class Distance {

	/**
	 * 
	 */
	private int depth = 0;
	
	/**
	 * 
	 */
	private float length = 0;

	/**
	 * 
	 * @param depath
	 * @param length
	 */
	public Distance(int depath, float length) {
		super();
		this.depth = depath;
		this.length = length;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDepth() {
		return depth;
	}
	
	/**
	 * 
	 * @param Depth
	 */
	public void setDepth(int Depth) {
		this.depth = Depth;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getLength() {
		return length;
	}
	
	/**
	 * 
	 * @param length
	 */
	public void setLength(float length) {
		this.length = length;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return "(" + depth + ", " + length + ")";
	}
}

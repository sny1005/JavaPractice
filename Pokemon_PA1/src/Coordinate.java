/**
 * This class represents the coordinate system using rows and cols
 * @author SNYMac
 *
 */
public class Coordinate {
	int row, col;
	
	public Coordinate(int r, int c) {
		row = r;
		col = c;
	}
	
	public int getRow(){ return row; }
	
	public int getCol(){ return col; }
	
	/**
	 * for printing the path in desired format
	 */
	public String toString(){ return "<" + row + "," + col + ">"; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

}

package se.cenote.bobo.domain;



public class Interval implements Comparable<Interval>{
	
	private int start;
	private int end;
	
	public Interval(){}
	
	public Interval(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getLength(){
		return (end-start) + 1;
	}
	
	public boolean intersects(Interval other){
		boolean result = false;
		
		if(other.start >= this.start && other.start < this.end){
			result = true;
		}
		else if(this.start >= other.start && this.start < other.end){
			result = true;
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

	@Override
	public int compareTo(Interval other) {
		int c = this.start - other.start;
		return c;
	}

	@Override
	public String toString() {
		return "Interval(start=" + start + ", end=" + end + ")";
	}
	
}

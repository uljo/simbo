package se.cenote.bobo.domain;

public class Chromosom extends Interval {

	private int id;
	
	public Chromosom(int id){
		super(-1, -1);
		this.id = id;
	}
	
	public Chromosom(int id, int start, int end){
		super(start, end);
		this.id = id;
	}
	
	public int getId(){
		return id;
	}

	@Override
	public String toString() {
		return "CromosomRegion [id=" + id + ", getStart()=" + getStart()
				+ ", getEnd()=" + getEnd() + "]";
	}
	
	
}

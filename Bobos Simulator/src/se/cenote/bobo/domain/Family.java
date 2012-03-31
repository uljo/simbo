package se.cenote.bobo.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Family {
	
	private int id;
	
	private Map<Chromosom, List<Integer>> blockLengthByChromosom;
	
	
	public Family(int id){
		this(id, null, null);
	}
	
	public Family(int id, Chromosom chrom, int[] blockLength){
		this.id = id;
		
		this.blockLengthByChromosom = new HashMap<Chromosom, List<Integer>>();
		
		if(chrom != null && blockLength != null){
			List<Integer> blocks = new ArrayList<Integer>();
			for(int l : blockLength){
				blocks.add(l);
			}
			this.blockLengthByChromosom.put(chrom, blocks);
		}
	}
	
	public void addLength(Chromosom chrom, int len){
		List<Integer> blocks = blockLengthByChromosom.get(chrom);
		if(blocks == null){
			blocks = new ArrayList<Integer>();
			blockLengthByChromosom.put(chrom, blocks);
		}
		blocks.add(len);
	}
	
	public int getId(){
		return id;
	}
	
	public List<Chromosom> getChromosomsKeys(){
		return new ArrayList<Chromosom>(blockLengthByChromosom.keySet());
	}
	
	public List<Integer> getBlockLengths(Chromosom chromosom){
		return blockLengthByChromosom.get(chromosom);
	}
	
	@Override
	public String toString(){
		return "family(id=" + getId() + ", chromosoms=" + getChromosomsKeys().size() + ")";
	}

}

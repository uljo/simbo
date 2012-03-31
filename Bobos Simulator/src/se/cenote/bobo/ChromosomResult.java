package se.cenote.bobo;

import java.util.Map;

import se.cenote.bobo.domain.Chromosom;

public class ChromosomResult{
	
	private Chromosom chrom;
	
	private Map<Integer, Integer> map;
	private int maxCount;
	
	public ChromosomResult(Chromosom chrom, Map<Integer, Integer> map){
		this.chrom = chrom;
		
		this.map = map;
		maxCount = getMaxCount(map);
	}
	
	public String getKey(){
		return "K-" + chrom.getId();
	}
	
	public Chromosom getChromosom(){
		return chrom;
	}
	
	public int getMaxCount(){
		return maxCount;
	}
	
	public int getSum(int count){
		Integer value = map.get(count);
		return value == null ? 0 : value;
	}
	
	private int getMaxCount(Map<Integer, Integer> map){
		int max = 0;
		for(Integer count : map.keySet()){
			max = Math.max(max, count);
		}
		return max;
	}
}
package se.cenote.bobo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.cenote.bobo.domain.Chromosom;

public class TotalResult{
	
	private int itr;
	
	private Map<Chromosom, ChromosomResult> resultByChromosom;
	
	private Map<Chromosom, Integer> keyMaxMap;
	private Map<Chromosom, Integer>  scoreMaxMap;
	
	public TotalResult(int iterations, Map<Chromosom, Map<Integer, Integer>> overlapsByChromosomAndNumber){
		
		this.itr = iterations;
		
		resultByChromosom = new HashMap<Chromosom, ChromosomResult>();
		
		for(Chromosom chrom : overlapsByChromosomAndNumber.keySet()){
			Map<Integer, Integer> overlapsByNumber = overlapsByChromosomAndNumber.get(chrom);
			ChromosomResult chromResult = new ChromosomResult(chrom, overlapsByNumber);
			resultByChromosom.put(chrom, chromResult);
		}
		
		this.keyMaxMap = new HashMap<Chromosom, Integer>();
		this.scoreMaxMap = new HashMap<Chromosom, Integer>();
		
		for(Chromosom crom : resultByChromosom.keySet()){
			ChromosomResult chromResult = resultByChromosom.get(crom);
			keyMaxMap.put(crom, chromResult.getMaxCount());
			
			int scoreMax = 0;
			for(int key = 1; key <= chromResult.getMaxCount(); key++){
				int sum = chromResult.getSum(key);
				scoreMax = Math.max(scoreMax, sum);
			}
			
			scoreMaxMap.put(crom, scoreMax);
		}
	}
	
	public int getIterations(){
		return itr;
	}
	
	public int getKeyMax(Chromosom crom){
		return keyMaxMap.get(crom);
	}
	
	public int getScoreMax(Chromosom crom){
		return scoreMaxMap.get(crom);
	}
	
	public int getScore(Chromosom crom, int key){
		ChromosomResult chromosomResult = resultByChromosom.get(crom);
		Integer value = chromosomResult.getSum(key);
		return value == null ? 0 : value;
	}

	public List<Chromosom> getCromosoms() {
		return new ArrayList<Chromosom>(resultByChromosom.keySet());
	}
}
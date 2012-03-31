package se.cenote.bobo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import se.cenote.bobo.domain.Chromosom;
import se.cenote.bobo.domain.Family;
import se.cenote.bobo.domain.Interval;

public class Simulator {
	
	private int iterations;
	
	private Map<Chromosom, Map<Integer, Integer>> totalOverlapsByChromosom;

	private Random random;
	
	private SimListener listener;
	private boolean debug;

	public Simulator(int iterations, SimListener listener, boolean debug){
		this.iterations = iterations;
		this.listener = listener;
		this.debug = debug;
	}
	
	public void start(){
		debug("[start] Starting..., iterations=" + iterations);

		random = new Random();
		
		totalOverlapsByChromosom = new HashMap<Chromosom, Map<Integer, Integer>>();
		
		for(int i = 0; i < iterations; i++){
			runIteration(i);
		}
	}
	
	private void runIteration(int i){
		debug("[runIteration] Start iteration: " + i);
		
		Map<Chromosom, List<Interval>> placedBlocksByChromosom = placeBlocksByRandom();
		
		Map<Chromosom, Map<Integer, Integer>> overlapsByChromosom = countOverlaps(placedBlocksByChromosom);

		addTotals(overlapsByChromosom);
		
		if(listener != null){
			listener.update(i, iterations);
			debug("[start]   Called listener.");
		}
		
		debug("[start] End iteration: " + i);
	}
	
	public TotalResult getResult(){
		return new TotalResult(iterations, totalOverlapsByChromosom);
	}
	
	private Map<Chromosom, Map<Integer, Integer>> countOverlaps(Map<Chromosom, List<Interval>> placedBlocksByChromosom){
		
		Map<Chromosom, Map<Integer, Integer>> result = new HashMap<Chromosom, Map<Integer, Integer>>();
		
		for(Chromosom crom : placedBlocksByChromosom.keySet()){
			
			List<Interval> placedBlocks = placedBlocksByChromosom.get(crom);
			
			Map<Integer, Integer> overlapByNumber = OverlappCounter.countOverlaps(placedBlocks);
			result.put(crom, overlapByNumber);
		}
		return result;
	}
	
	/**
	 * 
	 * @param overlapsByChromosom current overlap count ordered by chromosom and number
	 */
	private void addTotals(Map<Chromosom, Map<Integer, Integer>> overlapsByChromosom){
		
		for(Chromosom crom : overlapsByChromosom.keySet()){
			
			Map<Integer, Integer> overlaps = overlapsByChromosom.get(crom); 
			
			Map<Integer, Integer> totalMap = totalOverlapsByChromosom.get(crom);
			if(totalMap == null){
				totalMap = new HashMap<Integer, Integer>();
				totalOverlapsByChromosom.put(crom, totalMap);
			}
			
			for(int key : overlaps.keySet()){
				
				int sum = overlaps.get(key);
				
				Integer prevSum = totalMap.get(key);
				if(prevSum == null){
					prevSum = 0;
				}
				totalMap.put(key, (prevSum + sum) );
			}
		}
	}
	
	private Map<Chromosom, List<Interval>> placeBlocksByRandom(){
		
		Map<Chromosom, List<Interval>> blocksByChromosom = new HashMap<Chromosom, List<Interval>>();
		
		for(Chromosom chrom : App.getInstance().getChromosoms()){
			
			List<Family> families = App.getInstance().getFamilies(chrom);
			if(families != null){
				List<Interval> placedBlocks = placeBlocksByRandom(chrom, families);
				blocksByChromosom.put(chrom, placedBlocks);
			}
		}
		return blocksByChromosom;
	}
	
	private List<Interval> placeBlocksByRandom(Chromosom chrom, List<Family> families){
		
		List<Interval> placedBlocks = new ArrayList<Interval>();
		
		for(Family family : families){
			List<Interval> placedFamilyBlocks = placeFamilyBlocksByRandom(chrom, family);
			placedBlocks.addAll(placedFamilyBlocks);
		}
		Collections.sort(placedBlocks);
		
		return placedBlocks;
	}
	
	private List<Interval> placeFamilyBlocksByRandom(Chromosom chrom, Family fam){
		debug("[placeFamilyBlocksByRandom] Processing chrom: " + chrom + ", family: " + fam);
		
		List<Interval> placedFamilyBlocks = new ArrayList<Interval>();
		List<Integer> blockLengths = fam.getBlockLengths(chrom);
		
		while(placedFamilyBlocks.size() < blockLengths.size()){
			
			int len = blockLengths.get(placedFamilyBlocks.size());
			int start = getRandomStartPosition(len, chrom);
			int end = start + len - 1;
			Interval interval = new Interval(start, end);
			
			if(!intersects(interval, placedFamilyBlocks) && end <= chrom.getEnd()){
				placedFamilyBlocks.add(interval);
			}
			else{
				debug("[placeFamilyBlocksByRandom] Random region " + interval + " invalid within cromosom: " + chrom);
			}
		}
		return placedFamilyBlocks;
	}
	
	private static boolean intersects(Interval interval, List<Interval> intervals){
		boolean result = false;
		for(Interval curr : intervals){
			if(interval.intersects(curr)){
				result = true;
				break;
			}
		}
		return result;
	}
	
	private int getRandomStartPosition(int length, Interval chromosom){
		int cromeLength = chromosom.getLength();
		int availableLength = cromeLength - length;
		if(availableLength < 10){
			debug("getRandomStartPosition] available=" + availableLength + ", length=" + length + ", cromosom=" + chromosom);
		}
		return random.nextInt(availableLength + 1) + chromosom.getStart();
	}
	
	private void debug(String msg){
		if(debug)
			System.out.println(msg);
	}
}

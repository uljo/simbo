package se.cenote.bobo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import se.cenote.bobo.domain.Interval;

class OverlappCounter{
	
	public OverlappCounter(){
		
	}
	
	static Map<Integer, Integer> countOverlaps(List<Interval> intervals){
		
		Map<Integer, List<Interval>> map = new HashMap<Integer, List<Interval>>();
		
		int start = Integer.MAX_VALUE;
		int end = -1;
		
		SortedSet<Integer> posList = new TreeSet<Integer>();
		for(Interval interval : intervals){
			posList.add(interval.getStart());
			posList.add(interval.getEnd());
			
			start = Math.min(start, interval.getStart());
			end = Math.max(end, interval.getEnd());
		}
		
		Interval currRegion = null;
		int pos = start;
		int prevCount = 0;
		
		for(Iterator<Integer> it = posList.iterator(); it.hasNext(); ){
			
			pos = it.next();
			
			int count = getOverlapCount(pos, intervals); // 1..33
			
			boolean shrink = count <= prevCount;
			
			if(prevCount > 0){
				int end2 = count > prevCount ? pos-1 : pos;
				currRegion.setEnd(end2);
				add(currRegion, map, prevCount);
			}
			
			if(count > 1 || prevCount == 0){
				int start2 = (shrink ? pos+1 : pos);
				currRegion = new Interval(start2, -1);
				prevCount = shrink ? count-1 : count;
			}
			else{
				currRegion = null;
				prevCount = 0;
			}
		}
		if(currRegion != null){
			currRegion.setEnd(pos);
			add(currRegion, map, prevCount);
		}
		
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for(int key : map.keySet()){
			List<Interval> list = map.get(key);
			result.put(key, list.size());
		}
		
		return result;
	}
	
	
	private static int getOverlapCount(int pos, List<Interval> intervals){
		int count = 0;
		for(Interval interval : intervals){
			if(interval.getStart() > pos){
				break;
			}
			else if(interval.getStart() <= pos && interval.getEnd() >= pos){
				count++;
			}
		}
		return count;
	}
	
	private static void add(Interval interval, Map<Integer, List<Interval>> map, int prevCount){
		List<Interval> list = map.get(prevCount);
		if(list == null){
			list = new ArrayList<Interval>();
			map.put(prevCount, list);
		}
		list.add(interval);
		
		//if(map.values().size() < 5)
		//	System.out.println("[add] Adding overlap: " + region + ", count=" + prevCount);
	}
}
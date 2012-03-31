package se.cenote.bobo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.cenote.bobo.domain.Chromosom;
import se.cenote.bobo.domain.Family;
import se.cenote.bobo.gui.MainFrame;

public class App {
	
	private static final App INSTANCE = new App();
	
	
	private List<Chromosom> chromosoms;
	private Map<Integer, Chromosom> chromosomsById;
	
	private List<Family> families;
	private Map<Integer, Family> familyById;
	private Map<Chromosom, List<Family>> familiesByChromosom;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame.start();
	}
	
	private App(){
		
	}
	
	public static final App getInstance(){
		return INSTANCE;
	}
	
	public List<Family> getFamilies(){
		return families;
	}
	
	public List<Family> getFamilies(Chromosom chrom){
		return familiesByChromosom.get(chrom);
	}
	
	public List<Chromosom> getChromosoms(){
		return chromosoms;
	}
	
	public List<Family> loadFile(File file){
		
		chromosoms = new ArrayList<Chromosom>();
		chromosomsById = new HashMap<Integer, Chromosom>();
		
		families = new ArrayList<Family>();
		familyById = new HashMap<Integer, Family>();
		familiesByChromosom = new HashMap<Chromosom, List<Family>>();
		
		BufferedReader reader = null;
		int count = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			boolean chromePart = true;
			
			while((line = reader.readLine()) != null ){
				count++;
				
				if(line.trim().length() == 0){
					chromePart = false;
				}
				
				if(chromePart){
					processChromePart(line);
				}
				else{
					processFamilyPart(line);
				}
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("[loadFile] read " + count + " lines from file: " + file);
		return families;
	}

	private void processChromePart(String line) {
		
		if(line != null && line.trim().length() > 0 && !line.startsWith("Crom")){
			
			String[] parts = line.split("\t");
			
			int id = Integer.parseInt(parts[0]);
			int start = (int)(Double.parseDouble(parts[1]) * 1000);
			int end = (int)(Double.parseDouble(parts[2]) * 1000);
			
			Chromosom chrom = new Chromosom(id, start, end);
			chromosoms.add(chrom);
			chromosomsById.put(id, chrom);
		}
	}

	private void processFamilyPart(String line) {
		
		if(line != null && line.trim().length() > 0 && !line.startsWith("Fam")){
			
			String[] parts = line.split("\t");
			
			int id = Integer.parseInt(parts[0]);
			int chromId = Integer.parseInt(parts[1]);
			
			Chromosom chrom = chromosomsById.get(chromId);
			if(chrom == null){
				chrom = new Chromosom(chromId);
			}
			
			double lenTemp = Double.parseDouble(parts[2]);
			int len = (int)(lenTemp * 1000);
			
			Family family = familyById.get(id);
			if(family == null){
				family = new Family(id);
				families.add(family);
				familyById.put(family.getId(), family);
				
				List<Family> families = familiesByChromosom.get(chrom);
				if(families == null){
					families = new ArrayList<Family>();
					familiesByChromosom.put(chrom, families);
				}
				families.add(family);
			}
			family.addLength(chrom, len);
			
		}
	}

}

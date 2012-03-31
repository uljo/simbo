package se.cenote.bobo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import se.cenote.bobo.App;
import se.cenote.bobo.ChromosomResult;
import se.cenote.bobo.TotalResult;
import se.cenote.bobo.domain.Chromosom;
import se.cenote.bobo.domain.Family;

public class ListDetailPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;

	private final DecimalFormat FORMAT = new DecimalFormat("#,###");
	
	private JScrollPane scroll;
	private JList chromList;
	private KeyListModel chromListModel;
	
	private DetailPanel detailPanel;
	
	private Map<String, ChromosomResult> resultByChrom;
	
	
	public ListDetailPanel(){
		initComponents();
		layoutComponents();
	}
	
	public void init(TotalResult totalResult){
		resultByChrom.clear();
		
		List<Chromosom> chroms = totalResult.getCromosoms();
		Collections.sort(chroms, new Comparator<Chromosom>() {
			@Override
			public int compare(Chromosom c1, Chromosom c2) {
				return c1.getId() - c2.getId();
			}
		});
		
		for(Chromosom crom : chroms){
			
			Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
			for(int i = 0; i < totalResult.getKeyMax(crom); i++){
				int key = i + 1;
				int s = totalResult.getScore(crom, key);
				scoreMap.put(key, s);
			}
			ChromosomResult chromosomResult = new ChromosomResult(crom, scoreMap);
			resultByChrom.put(chromosomResult.getKey(), chromosomResult);
		}
		
		List<String> keys = new ArrayList<String>();
		for(Chromosom chrom : chroms){
			keys.add("K-" + chrom.getId());
		}
		chromListModel.update(keys);
		
		chromList.setSelectedIndex(0);

		chromList.requestFocusInWindow();
	}
	
	private void showDetail(String key){
		ChromosomResult chromosomResult = resultByChrom.get(key);
		detailPanel.update(chromosomResult);
	}

	private void initComponents() {
		
		resultByChrom = new HashMap<String, ChromosomResult>();

		chromListModel = new KeyListModel();
		chromList = new JList(chromListModel);
		chromList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				String key = (String)chromList.getSelectedValue();
				showDetail(key);
			}
		});
		chromList.setFocusable(true);
		
		detailPanel = new DetailPanel();
	}

	private void layoutComponents() {
		setLayout(new BorderLayout());
		
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		p.add(new JLabel("Chroms:"), BorderLayout.NORTH);
		
		scroll = new JScrollPane(chromList);
		Border b1 = BorderFactory.createEmptyBorder(6, 0, 0, 0);
		Border b2 = BorderFactory.createLineBorder(Color.GRAY);
		scroll.setBorder(BorderFactory.createCompoundBorder(b1, b2));
		p.add(scroll, BorderLayout.CENTER);
		add(p, BorderLayout.WEST);
		
		add(detailPanel, BorderLayout.CENTER);
	}
	
	class DetailPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
		
		private JLabel chromLbl;
		private JLabel chromLengthLbl;
		
		private JTable familyTbl;
		private FamilyTableModel familyModel;
		
		private JTable resultTbl;
		private ResultTableModel resultModel;
		
		public DetailPanel(){
			initComponents();
			layoutComponents();
		}
		
		private void initComponents(){
			
			chromLbl = new JLabel("");
			chromLengthLbl = new JLabel("");
			
			familyModel = new FamilyTableModel();
			familyTbl = new JTable(familyModel);
			familyTbl.setDefaultRenderer(Integer.class, new NumberCellRenderer());
			familyTbl.setDefaultRenderer(Float.class, new NumberCellRenderer());
			familyTbl.setDefaultRenderer(String.class, new TextCellRenderer());
			
			resultModel = new ResultTableModel();
			resultTbl = new JTable(resultModel);
			resultTbl.setDefaultRenderer(Integer.class, new NumberCellRenderer());
			resultTbl.setDefaultRenderer(String.class, new TextCellRenderer());
			
		}
		
		private void layoutComponents(){
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			JPanel chromosomPanel = new JPanel(new BorderLayout());
			
			JPanel p = new JPanel();
			JLabel l1 = new JLabel("Chromosom:");
			l1.setFont(getFont().deriveFont(Font.BOLD));
			p.add(l1);
			p.add(chromLbl);
			JLabel l2 = new JLabel("Total length:");
			l2.setFont(getFont().deriveFont(Font.BOLD));
			p.add(l2);
			p.add(chromLengthLbl);
			
			chromosomPanel.add(p, BorderLayout.NORTH);
			
			JScrollPane scroll = new JScrollPane(resultTbl);
			scroll.setPreferredSize(new Dimension(100, 130));
			chromosomPanel.add(scroll, BorderLayout.SOUTH);
			
			add(chromosomPanel, BorderLayout.NORTH);
			
			add(new JScrollPane(familyTbl), BorderLayout.CENTER);
		}
		
		public void update(ChromosomResult chromosomResult){
			String key = chromosomResult.getKey();
			chromLbl.setText(key);
			
			Chromosom chrom = chromosomResult.getChromosom();
			
			String text = FORMAT.format(chrom.getLength());
			chromLengthLbl.setText(text);
			
			familyModel.update(chrom);
			
			resultModel.update(chromosomResult);
			
		}
		
		class ResultTableModel extends AbstractTableModel{
			
			private static final long serialVersionUID = 1L;
			
			private final String[] HEADERS = new String[]{"Overlap by num of families", "Total count"};
			private ChromosomResult chromosomResult;
			
			public ResultTableModel(){
			}
			
			public void update(ChromosomResult chromosomResult){
				this.chromosomResult = chromosomResult;
				fireTableDataChanged();
			}
			
			@Override
			public String getColumnName(int column) {
				return HEADERS[column];
			}

			public int getColumnCount() {
				return HEADERS.length;
			}

			public int getRowCount() {
				return chromosomResult != null ? chromosomResult.getMaxCount() : 0;
			}

			public Object getValueAt(int row, int col) {
				Object value = null;
				if(chromosomResult != null){
					if(col == 0){
						value = row + 1;
					}
					else{
						value = chromosomResult.getSum(row+1);
					}
				}
				return value;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex == 0){
					return String.class;
				}
				else{
					return Integer.class;
				}
			}
			
		}
		
		class FamilyTableModel extends AbstractTableModel{
			
			private static final long serialVersionUID = 1L;
			
			private final String[] HEADERS = new String[]{"Family Id", "Block length", "% of Chrom length"};
			private Chromosom chrom;
			private List<FamilyItem> items;
			
			public FamilyTableModel(){
			}
			
			public void update(Chromosom chrom){
				this.chrom = chrom;
				
				List<Family> families = App.getInstance().getFamilies(chrom);
				Collections.sort(families, new Comparator<Family>() {
					@Override
					public int compare(Family f1, Family f2) {
						return f1.getId() - f2.getId();
					}
				});
				
				
				items = new ArrayList<FamilyItem>();
				for(Family family : families){
					String id = "F-" + family.getId();
					for(int blockLength : family.getBlockLengths(chrom)){
						float percent = blockLength/(float)chrom.getLength();
						FamilyItem item = new FamilyItem(id, blockLength, percent);
						items.add(item);
					}
				}
				
				
				fireTableDataChanged();
			}
			
			@Override
			public String getColumnName(int column) {
				return HEADERS[column];
			}

			public int getColumnCount() {
				return HEADERS.length;
			}

			public int getRowCount() {
				return items != null ? items.size() : 0;
			}

			public Object getValueAt(int row, int col) {
				Object value = null;
				if(items != null){
					FamilyItem item = items.get(row);
					if(col == 0){
						value = item.getId();
					}
					else if(col == 1){
					    value = item.getBlockLength();
					}
					else{
						value = item.getPercent();
					}
				}
				return value;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex == 0){
					return String.class;
				}
				else if(columnIndex == 1){
					return Integer.class;
				}
				else{
					return Float.class;
				}
			}



			class FamilyItem{
				
				private String id;
				private int blockLength;
				private float percent;
				
				public FamilyItem(String id, int blockLength, float percent) {
					this.id = id;
					this.blockLength = blockLength;
					this.percent = percent;
				}
				
				public String getId() {
					return id;
				}
				public int getBlockLength() {
					return blockLength;
				}
				public float getPercent() {
					return percent;
				}
			}
			
		}
	}
	
	class KeyListModel extends DefaultListModel{
		
		private static final long serialVersionUID = 1L;
		
		private List<String> list;
		
		public KeyListModel(){
		}
		
		public void update(List<String> list){
			this.list = list;
			int listSize = list != null ? list.size() : 0;
			
			fireContentsChanged(this, 0, listSize);
		}

		@Override
		public Object getElementAt(int index) {
			return list.get(index);
		}

		@Override
		public int getSize() {
			return list != null ? list.size() : 0;
		}
	}
}

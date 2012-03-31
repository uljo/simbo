package se.cenote.bobo.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import se.cenote.bobo.App;
import se.cenote.bobo.SimListener;
import se.cenote.bobo.Simulator;
import se.cenote.bobo.TotalResult;
import se.cenote.bobo.domain.Chromosom;
import se.cenote.bobo.domain.Family;
import se.cenote.bobo.gui.MainPanel.FamilyPanel.FamilyInfo;

public class MainPanel extends JPanel{

	private CardLayout cardLayout;
	
	private StartPanel startPanel;
	private IterPanel iterPanel;
	private FamilyPanel familyPanel;
	private WorkPanel workPanel;
	private ResultPanel resultPanel;
	
	private Font headerFont = getFont().deriveFont(Font.BOLD, 18);
	
	
	public MainPanel(){
		
		cardLayout = new CardLayout();
        setLayout(cardLayout);
        
        startPanel = new StartPanel();
        iterPanel = new IterPanel();
        familyPanel = new FamilyPanel();
        workPanel = new WorkPanel();
        resultPanel = new ResultPanel();
        
        add(startPanel, "start");
        add(familyPanel, "family");
        add(iterPanel, "iter");
        add(workPanel, "work");
        add(resultPanel, "result");
        
        cardLayout.show(this, "start");
	}
	
	public void showStart(){
		setView("start");
	}
	
	public void showFamily(){
		familyPanel.prepare();
		setView("family");
	}
	
	public void showIter(){
		setView("iter");
		iterPanel.setFocus();
	}
	
	public void showWork(){
		
		int itr = iterPanel.getItr();
		List<Family> families = getApp().getFamilies();
		if(itr > 0 && !families.isEmpty()){
			setView("work");
			workPanel.start(itr);
		}
	}
	
	public void showResult(TotalResult totalResult){
		resultPanel.init(totalResult);
		setView("result");
	}
	
	public void setView(String name){
		cardLayout.show(this, name);
	}
	
	
	private App getApp(){
		return App.getInstance();
	}
	
	class StartPanel extends JPanel{
		
		public StartPanel(){
			
			setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
		
			setLayout(new BorderLayout());
		
			JButton btn = new JButton("Start");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					showFamily();
				}
			});
			Font font = getFont().deriveFont(Font.BOLD, 24);
			
			JLabel h = new JLabel("S I M U L A T E");
			h.setFont(font);
			JPanel hp = new JPanel();
			hp.add(h);
			add(hp, BorderLayout.NORTH);
			
			
			java.net.URL imageURL = StartPanel.class.getResource("/dna_2.jpg");
			ImageIcon img = new ImageIcon(imageURL);
			//ImageIcon img = new ImageIcon("images/dna_2.jpg");
			add(new JLabel(img));
			
			JPanel p = new JPanel();
			p.add(btn);
			add(p, BorderLayout.SOUTH);
		}

	}
	
	class FamilyPanel extends JPanel{
		
		private JTable cromTbl;
		private CromModel cromModel;
		
		private JTable familyTbl;
		private FamilyModel familyModel;
		
		private JButton fileBtn;
		private JButton okBtn;
		
		public FamilyPanel(){
			
			setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
			
			setLayout(new BorderLayout());
			
			fileBtn = new JButton("Load...");
			fileBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser chooser = new JFileChooser();
					int result = chooser.showOpenDialog(FamilyPanel.this);
					if(result == JFileChooser.APPROVE_OPTION){
						File file = chooser.getSelectedFile();
						
						getApp().loadFile(file);
						
						cromModel.update(getApp().getChromosoms());
						
						familyModel.update(getApp().getFamilies());
						
						if(familyModel.getRowCount() > 0){
							fileBtn.setText("Change..");
							okBtn.setEnabled(true);
						}
						else{
							fileBtn.setText("Load..");
							okBtn.setEnabled(false);
						}
					}
				}
			});
			
			okBtn = new JButton("Ok");
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					showIter();
				}
			});
			
			cromModel = new CromModel();
			cromTbl = new JTable(cromModel);
			cromTbl.setDefaultRenderer(Integer.class, new NumberCellRenderer());
			cromTbl.setDefaultRenderer(String.class, new TextCellRenderer());
			
			familyModel = new FamilyModel();
			familyTbl = new JTable(familyModel);
			familyTbl.setDefaultRenderer(Integer.class, new ChromIdCellRenderer());
			familyTbl.setDefaultRenderer(String.class, new TextCellRenderer());

			JLabel header = new JLabel("Indata");
			header.setFont(headerFont);
			JPanel hp = new JPanel();
			hp.add(header);
			add(hp, BorderLayout.NORTH);
			
			JTabbedPane tabbedPane = new JTabbedPane();
			JPanel p1 = new JPanel(new BorderLayout());
			p1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			p1.add(new JScrollPane(cromTbl), BorderLayout.CENTER);
			tabbedPane.addTab("Chromosoms", p1);
			
			JPanel p2 = new JPanel(new BorderLayout());
			p2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			p2.add(new JScrollPane(familyTbl), BorderLayout.CENTER);
			tabbedPane.addTab("Families", p2);
			
			add(tabbedPane, BorderLayout.CENTER);
			
			JPanel p = new JPanel();
			p.add(fileBtn);
			p.add(okBtn);
			add(p, BorderLayout.SOUTH);
		}
		
		public void prepare(){
			if(familyModel.getRowCount() > 0){
				fileBtn.setText("Change..");
				okBtn.setEnabled(true);
				
			}
			else{
				fileBtn.setText("Load..");
				okBtn.setEnabled(false);
			}
		}


		class ChromIdCellRenderer extends NumberCellRenderer{
			
			private static final long serialVersionUID = 1L;
			private final Color MISS_COLOR = Color.red;
			
			public ChromIdCellRenderer(){
				setOpaque(true);
			}

			@Override
			public Component getTableCellRendererComponent(JTable tbl,
					Object value, boolean isSelected, boolean hasFocus, int row, int col) {
				
				super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
				
				if(col == 1){
					String id = (String)value;
					boolean missingChr = true;
					for(Chromosom chrom : getApp().getChromosoms()){
						if(id.equals("K-" + chrom.getId())){
							missingChr = false;
							break;
						}
					}
					if(missingChr){
						setForeground(MISS_COLOR);
					}

				}
				
				return this;
			}
			
			private String format(int value){
				String t = Integer.toString(value);
				int len = t.length();
				if(len > 3){
					return t.substring(0, len-3) + "," + t.substring(len-3);
				}
				else{
					return t;
				}
			}
			
		}
		
		class CromModel extends AbstractTableModel{
					
			private List<Chromosom> croms;
					
			private String[] headers = {"Cromosom id", "Start", "End"};
			
			public void update(List<Chromosom> croms){
				
				this.croms = croms;
				
				fireTableDataChanged();
			}
			
			@Override
			public int getColumnCount() {
				return headers.length;
			}
			
			@Override
			public String getColumnName(int index) {
				return headers[index];
			}

			@Override
			public int getRowCount() {
				return croms == null ? 0 : croms.size();
			}

			@Override
			public Object getValueAt(int row, int col) {
				Object value = null;
				Chromosom crom = croms.get(row);
				switch(col){
					case 0:
						value = "K-" + crom.getId();
						break;
					case 1:
						value = crom.getStart();
						break;
					case  2:
						value = crom.getEnd();
						break;
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
		
		class FamilyModel extends AbstractTableModel{
			
			private List<FamilyInfo> familyInfos;
			
			private String[] headers = {"Family id", "Chromosom id", "Block length"};
			
			public void update(List<Family> families){
				
				Collections.sort(families, new Comparator<Family>() {
					@Override
					public int compare(Family f1, Family f2) {
						return f1.getId() - f2.getId();
					}
				});
				
				familyInfos = new ArrayList<FamilyInfo>();
				if(families != null){
					for(Family fam : families){
						for(Chromosom chrom : fam.getChromosomsKeys()){
							for(Integer len : fam.getBlockLengths(chrom)){
								familyInfos.add(new FamilyInfo(fam.getId(), chrom.getId(), len));
							}
						}
					}
				}
				
				fireTableDataChanged();
			}
			
			@Override
			public int getColumnCount() {
				return headers.length;
			}
			
			@Override
			public String getColumnName(int index) {
				return headers[index];
			}

			@Override
			public int getRowCount() {
				return familyInfos == null ? 0 : familyInfos.size();
			}

			@Override
			public Object getValueAt(int row, int col) {
				Object value = null;
				FamilyInfo famInfo = familyInfos.get(row);
				switch(col){
					case 0:
						value = "F-" + famInfo.getId();
						break;
					case 1:
						value = "K-" + famInfo.getChr();
						break;
					case  2:
						value = famInfo.getLen();
						break;
				}
				return value;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex > 1){
					return Integer.class;
				}
				else{
					return String.class;
				}
			}
			
		}
		
		class FamilyInfo{
			private int id;
			private int chr;
			private int len;
			public FamilyInfo(int id, int chr, int len) {
				super();
				this.id = id;
				this.chr = chr;
				this.len = len;
			}
			public int getId() {
				return id;
			}
			public int getChr() {
				return chr;
			}
			public int getLen() {
				return len;
			}
		}
	}
	
	class IterPanel extends JPanel{
		
		private JTextField inputFld;
		
		public IterPanel(){
			
			setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
		
			setLayout(new BorderLayout());
			
			inputFld = new JTextField(6);
		
			JButton btn = new JButton("Ok");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					int itr = getItr();
					if(itr > 0){
						showWork();
					}
				}
			});
			
			JPanel top = new JPanel();
			top.add(new JLabel("Num of iterations:"));
			top.add(inputFld);
			add(top, BorderLayout.CENTER);
			
			JPanel p = new JPanel();
			p.add(btn);
			add(p, BorderLayout.SOUTH);
		}
		
		public void setFocus(){
			inputFld.requestFocus();
		}
		
		public int getItr(){
			int value = 0;
			String t = inputFld.getText();
			if(t != null && t.length() > 0){
				value = Integer.parseInt(t);
			}
			return value;
		}
	}
	
	class WorkPanel extends JPanel implements SimListener{
		
		public final static int ONE_SECOND = 1000;
		
		private JProgressBar progressBar;
		
		private Simulator simulator;
		
		public WorkPanel(){
			
			setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
			
			setLayout(new BorderLayout());
			
			JLabel header = new JLabel("Calculating...");
			header.setFont(headerFont);
			
			JPanel headerPanel = new JPanel();
			headerPanel.add(header);
			add(headerPanel, BorderLayout.NORTH);
			
			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
	        progressBar.setStringPainted(true);
	        
	        JPanel p = new JPanel();
	        p.add(progressBar);
			add(p, BorderLayout.CENTER);
		}
		
		public void start(int itr){

			progressBar.setMinimum(0);
			progressBar.setMaximum(itr);
			progressBar.setValue(0);
			
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			simulator = new Simulator(itr, this, false);
			Thread t = new Thread(){
				@Override
				public void run() {
					simulator.start();
					done();
				}
			};
			t.start();
		}
		
		public void done(){
			setCursor(null);
			progressBar.setValue(progressBar.getMinimum());
			
			TotalResult totalResult = simulator.getResult();
			
			showResult(totalResult);
		}

		@Override
		public void update(int currentItr, int totalItr) {
			progressBar.setValue(currentItr);
		}
	}
	
	class ResultPanel extends JPanel{
		
		private JLabel header;
		//private ResultGraphPanel graphPanel;
		private ListDetailPanel graphPanel;
		
		public ResultPanel(){
			
			setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
			
			setLayout(new BorderLayout());
			
			header = new JLabel("Resultat: ");
			header.setFont(headerFont);
			
			JPanel topPanel = new JPanel();
			//topPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
			//topPanel.setBackground(Color.WHITE);
			topPanel.add(header);
			add(topPanel, BorderLayout.NORTH);
			
			//graphPanel = new ResultGraphPanel();
			graphPanel = new ListDetailPanel();
			add(graphPanel, BorderLayout.CENTER);
			
			JButton btn = new JButton("Back");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					showStart();
				}
			});
			
			JPanel botPanel = new JPanel();
			botPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			botPanel.add(btn);
			add(botPanel, BorderLayout.SOUTH);
		}
		
		public void init(TotalResult totalResult) {
			header.setText("Result: " + totalResult.getIterations() + " iterations");
			graphPanel.init(totalResult);
		}
	}
	
	
	static class ResultGraphPanel extends JPanel{
		
		private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.000");

		private final Font smallFont = getFont().deriveFont(8);
		
		private TotalResult totalResult;
		private int maxY;
		private int maxX;
		
		public ResultGraphPanel(){
			
		}

		public void init(TotalResult totalResult) {
			this.totalResult = totalResult;
			
			maxY = 0; //score.getScoreMax();
			maxX = 0; //score.getKeyMax();
			
			
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			g2.setColor(getBackground());
			g2.fillRect(0, 0, getWidth(), getHeight());
			
			int border = 12;
			int cols = maxX;
			
			
			float colW = (getWidth()-(border*2))/cols;
			float colH = getHeight()-(border*2);
			
			int x = border;
			int y = border;
			
			int y3 = getHeight();
					
			for(int i = 1; i <= cols; i++){
				g2.setColor(Color.YELLOW);
				g2.fillRect(x, y, (int)colW, (int)colH);
				
				/*
				int h2 = i*10;
				int y2 = (int)(colH - h2);
				g2.setColor(Color.RED);
				g2.fillRect(x, y2, (int)colW, h2);
				*/
				
				g2.setColor(Color.BLACK);
				g2.drawRect(x, y, (int)colW, (int)colH);
				
				// draw column id
				g2.drawString(Integer.toString(i), x + (colW/2), y3);
				
				
				int total = 0; //score.getScore(i);
				
				Font font = getFont();
				// draw total score
				g2.setFont(smallFont);
				g2.drawString(Integer.toString(total), x + 3, y3 - 15);
				
				// draw total/itr score
				g2.setFont(font);
				double real = total/(double)totalResult.getIterations();
				
				String text = FORMATTER.format(real);
				g2.drawString(text, x + 3, y+15);
				
				x += colW;
			}
		}
		
		
	}
}

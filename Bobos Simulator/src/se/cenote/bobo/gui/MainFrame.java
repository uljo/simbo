package se.cenote.bobo.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {
	
	private static String NIMBUS_LOOK = "Nimbus";
	
	private String TITLE = "Bobbo Simulator 4.0";
	private int WIDTH = 600;
	private int HEIGHT = 600;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		start();
	}
	
	public MainFrame(){
		setTitle(TITLE);
		setSize(WIDTH, HEIGHT);
		
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel mainPanel = createMainPanel();
		getContentPane().add(mainPanel);
	}
	
	private JPanel createMainPanel() {
		return new MainPanel();
	}

	public static void start(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		
	    SwingUtilities.invokeLater(new Runnable() {
	    	public void run() {
	    		try{
		    		for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    			//System.out.println("L&F " + info.getName());
	    		        if(NIMBUS_LOOK.equals(info.getName())) {
	    		            UIManager.setLookAndFeel(info.getClassName());
	    		            break;
	    		        }
	    		    }
	    		}
	    		catch(Exception e) {
	    			e.printStackTrace();
	    		}
	    		
	    		MainFrame frame = new MainFrame();
	    	  	frame.setVisible(true);
	    	}
	    });
	}

}

package se.cenote.bobo.gui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class NumberCellRenderer extends JLabel implements TableCellRenderer{
	
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat FORMAT = new DecimalFormat("#,###");
	private static final DecimalFormat FORMAT_DEC = new DecimalFormat("#,###.00");
	
	private static final Color bgOdd = UIManager.getColor("Table.alternateRowColor");
	private static final Color bgEven = Color.WHITE;
	private static final Color bgSelected = UIManager.getColor("Table[Enabled+Selected].textBackground");
	
	private static final Color fg = UIManager.getColor("Table.textForeground");
	private static final Color fgSelected = UIManager.getColor("Table[Enabled+Selected].textForeground");
	
	
	public NumberCellRenderer(){
		setOpaque(true);
		setHorizontalAlignment(JLabel.RIGHT);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
	}

	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String text = "";
		if(value instanceof Integer){
			int num = (Integer)value;
			text = FORMAT.format(num);
			setHorizontalAlignment(JLabel.RIGHT);
		}
		else if(value instanceof Float){
			float num = (Float)value;
			text = FORMAT_DEC.format(num*100);
			setHorizontalAlignment(JLabel.RIGHT);
		}
		else{
			setHorizontalAlignment(JLabel.LEFT);
		}
		setText(text);
		
		if(isSelected){
			setBackground(bgSelected);
			setForeground(fgSelected);
		}
		else{
			setForeground(fg);
            if(row % 2 == 0) {
                setBackground(bgEven);
            } 
            else {
                setBackground(bgOdd);
            }
		}

		return this;
	}
	
}
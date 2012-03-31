package se.cenote.bobo.gui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class TextCellRenderer extends JLabel implements TableCellRenderer{
	
	private static final long serialVersionUID = 1L;
	
	private static final Color bgOdd = UIManager.getColor("Table.alternateRowColor");
	private static final Color bgEven = Color.WHITE;
	private static final Color bgSelected = UIManager.getColor("Table[Enabled+Selected].textBackground");
	
	private static final Color fg = UIManager.getColor("Table.textForeground");
	private static final Color fgSelected = UIManager.getColor("Table[Enabled+Selected].textForeground");
	
	
	public TextCellRenderer(){
		setOpaque(true);
		setHorizontalAlignment(JLabel.LEFT);
		setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
	}

	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		String text = "";
		if(value instanceof String){
			text = (String)value;
		}
		else if(value != null){
			text = value.toString();
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
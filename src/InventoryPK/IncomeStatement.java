package InventoryPK;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IncomeStatement {
    
    static public JTabbedPane tPane = new JTabbedPane();
    
    public static JTabbedPane incomeStatementPane() {
        
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
	tPane.addTab("Sales",Sales.sales());
	tPane.addTab("COGS",panel2);
        tPane.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                
                if(tPane.getSelectedIndex() == 0)
                   Main.statusBar.setText("Reports","Sales", "");
                if(tPane.getSelectedIndex() == 1)
                   Main.statusBar.setText("Reports","COGS", "");
            }
            
        });
                
		return tPane;	
	}

}

package InventoryPK;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Reports {
    
    static public JTabbedPane tPane = new JTabbedPane();
    
    public static JTabbedPane reportsPane() {
        
        JPanel panel = new JPanel();
		
	tPane.addTab("Income statement", IncomeStatement.incomeStatementPane());
	tPane.addTab("Balance sheet",panel);
        tPane.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                
                if(tPane.getSelectedIndex() == 0 &&
                        IncomeStatement.tPane.getSelectedIndex() == 0)
                   Main.statusBar.setText("Reports","Sales", "");
                if(tPane.getSelectedIndex() == 1 &&
                        IncomeStatement.tPane.getSelectedIndex() == 1)
                   Main.statusBar.setText("Reports","COGS", "");
                if(tPane.getSelectedIndex() == 1)
                    Main.statusBar.setText("Reports", "Balance sheet", "");

            }
            
        });
                
		return tPane;	
	}

}

package InventoryPK;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Database {
    
    public static JTabbedPane tPane;
	
    public static JTabbedPane Database() {
		
        tPane = new JTabbedPane();
        tPane.addTab("Items",Items.itemPanel());
        tPane.addTab("Customers", Customers.customers());
        tPane.addTab("Vendors", Vendors.vendors());
	
        tPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
            
                if(tPane.getSelectedIndex() == 0) 
                    Items.updateSumTotalDisPrice();
                if(tPane.getSelectedIndex() == 1) 
                    Main.statusBar.setText("Database","Customers", "");
                if(tPane.getSelectedIndex() == 2) 
                    Main.statusBar.setText("Database","Vendors", "");
            }
        });
                
        return tPane;	
    }
}

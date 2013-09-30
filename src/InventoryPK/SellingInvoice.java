package InventoryPK;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SellingInvoice {
		

    public static JTabbedPane tPane;
    
    public static JTabbedPane sellingInvoicePanel() {
        
        tPane = new JTabbedPane();
        tPane.addTab("Registered invoices",
                SInvoice_RegisteredInvoices.RegisteredInvoice());
        tPane.addTab("New invoice",
                SInvoice_NewInvoice.NewInvoice(Main.mainFrame));
         tPane.addTab("Returns",
                SInvoice_Returns.returns());
        
         tPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                
                if(tPane.getSelectedIndex() == 0)
                   Main.statusBar.setText("Selling Invoice","Registered Invoices", "");
                if(tPane.getSelectedIndex() == 1)
                   Main.statusBar.setText("Selling Invoice","New Invoice", "");
                if(tPane.getSelectedIndex() == 2)
                   Main.statusBar.setText("Selling Invoice","Returns", "");
            }
            
        });
        
	return tPane;	
	}

}

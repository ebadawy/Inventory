package InventoryPK;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PurchasingInvoice {
    
    static public JTabbedPane tPane = new JTabbedPane();
    
    public static JTabbedPane purchasingInvoicePanel() {
		
	tPane.addTab("Registered invoices",PInvoice_RegisteredInvoices.RegisteredInvoice());
	tPane.addTab("New invoice",PInvoice_NewInvoice.NewInvoice(Main.mainFrame));
        tPane.addTab("Returns",PInvoice_Returns.returns());

        tPane.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                
                if(tPane.getSelectedIndex() == 0)
                   Main.statusBar.setText("Purchaing Invoice","Registered Invoices", "");
                if(tPane.getSelectedIndex() == 1)
                   Main.statusBar.setText("Purchaing Invoice","New Invoice", "");
                 if(tPane.getSelectedIndex() == 2)
                   Main.statusBar.setText("Purchaing Invoice","Returns", "");
            }
            
        });
                
		return tPane;	
	}

}

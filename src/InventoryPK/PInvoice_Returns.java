
package InventoryPK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class PInvoice_Returns {
    
    static Connection conn;
    static boolean itemAdded;
    static double netTotalPrice;
    static JLabel lInvoiceID, lItem, lQuantity, lVendor;
    static JTextField tfInvoiceID, tfQuantity, tfVendor;
    static JComboBox cbItem;
    static JButton btnAdd, btnRemove, btnRemoveAll, btnSubmit;
    
    static Object[][] dataResult;
    static Object[] columns = {"","Item", "Quantity",
        "price/Item", "Total Price"};
    static DefaultTableModel dTableModel = 
            new DefaultTableModel(dataResult, columns) {
        public Class getColumnClass(int column){
            Class returnValue;
	
            if((column >= 0) && (column < getColumnCount())) 
                returnValue = getValueAt(0, column).getClass();
            else 
                returnValue = Object.class;
            return returnValue;
        }
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public static JTable returnsTable;
    
    public static JScrollPane returns() {
        
        lInvoiceID = new JLabel("InvoiceID");
        lItem = new JLabel("Item");
        lQuantity = new JLabel("Quantity");
        lVendor = new JLabel("Vendor");
        
        tfInvoiceID = new JTextField(4);
        tfQuantity = new JTextField(4);
        tfVendor = new JTextField(16);
        tfVendor.setEditable(false);
        
        cbItem = new JComboBox();
        cbItem.setEditable(true);
        new ComboBoxOption_PReturns(cbItem);
        
        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");
        btnRemoveAll = new JButton("Remove All");
        btnSubmit = new JButton("Submit");
        
        returnsTable = new JTable(dTableModel);
        returnsTable.setRowHeight(returnsTable.getRowHeight() + 16);
        JScrollPane returnsTableSP = new JScrollPane(returnsTable);
        TableColumn column = null;
        for(int i = 0; i < returnsTable.getColumnCount(); i++) {
            column = returnsTable.getColumnModel().getColumn(i);
            if(i == 0)
                column.setPreferredWidth(1);
            else
                column.setPreferredWidth(150);
        }
        
        JPanel inputPanel1 = new JPanel();
        
        inputPanel1.add(lInvoiceID);
        inputPanel1.add(tfInvoiceID);
        inputPanel1.add(lItem);
        inputPanel1.add(cbItem);
        inputPanel1.add(lQuantity);
        inputPanel1.add(tfQuantity);
                
        JPanel vendorPanel= new JPanel();
        vendorPanel.add(lVendor);
        vendorPanel.add(tfVendor);
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        btnPanel.add(btnRemoveAll);
        
        JPanel inputPanel2 = new JPanel(new BorderLayout());
        inputPanel2.add(btnPanel, BorderLayout.EAST);
        inputPanel2.add(vendorPanel, BorderLayout.WEST);
        
        JPanel topPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        topPanel.add(inputPanel1);
        topPanel.add(inputPanel2);
        
        JPanel submitPanel = new JPanel(new BorderLayout());
        submitPanel.add(btnSubmit, BorderLayout.EAST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(returnsTableSP, BorderLayout.CENTER);
        mainPanel.add(submitPanel, BorderLayout.SOUTH);
        JScrollPane mainPanelSP = new JScrollPane(mainPanel);

        tfInvoiceID.getDocument().addDocumentListener(new DocumentListener() {
            
            public void changedUpdate(DocumentEvent e) {
                loadData();
            }
            
            public void insertUpdate(DocumentEvent e) {
                loadData();
            }
            
            public void removeUpdate(DocumentEvent e) {
                loadData();
            }
            
        });
        
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addAction();
            }
            
        });
        
        btnRemoveAll.addActionListener(new ActionListener() {
			
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(returnsTable.getRowCount() == 0)
                    Main.infoPane.append(Main.tp, Color.RED,
                            "There is no item to delete\n");
                else {
		int rest = JOptionPane.showConfirmDialog(null,
                        "Are you sure, you want to remove all items?",
                        "New Invoice", JOptionPane.YES_NO_OPTION);
                
		switch(rest) {
		case JOptionPane.YES_OPTION :
                            dTableModel.setRowCount(1);
                            dTableModel.removeRow(0);
                            break;
                        case JOptionPane.NO_OPTION:
                            break;
                    }
                }
            }
        });
        
        btnRemove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(returnsTable.getRowCount() == 0)
                    Main.infoPane.append(Main.tp, Color.RED,
                            "There is no item to delete\n");
                else
                    removeAction();
            }
        });
        
        returnsTable.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

                if(e.getKeyChar() == '') {
                    
                    if(returnsTable.getRowCount() == 0)
                        Main.infoPane.append(Main.tp, Color.RED,
                                "There is no item to delete\n");
                    else
                        removeAction();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
            }
            
        });
        
        btnSubmit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                if(tfInvoiceID.getText().equals(""))
                    Main.infoPane.append(Main.tp, Color.RED,
                            "Enter the InvoiceID\n");
                
                else  if(returnsTable.getRowCount() == 0)
                    Main.infoPane.append(Main.tp, Color.RED,
                            "The invoice is empty!\n");
                
                else {
                    int reslt = JOptionPane.showConfirmDialog(null,
                            "After this step the invoice will be unchangeable,"
                            + " do you want to continue?",
                            "Finish", JOptionPane.YES_NO_OPTION);
					
                    switch(reslt) {
                        
                        case JOptionPane.YES_OPTION :
                            submitAction();
                            break;
				
                        case JOptionPane.NO_OPTION :
                            break;
                    }
                }
            }
        });
        
        return mainPanelSP;
        
    }
    
    public static void loadData() {
        try {
            int invoiceID = Integer.parseInt(tfInvoiceID.getText());

            cbItem.removeAllItems();
            cbItem.addItem("");

            tfVendor.setEditable(true);
            tfVendor.setText("");
            try {
                
                Class.forName("com.mysql.jdbc.Driver");
                String username = LogIn.username;
                String password = LogIn.password;
                String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();

                String vendor = "", invoiceType = "1";
                
                String sql = "SELECT vendorName"
                        + " FROM registered_pInvoice"
                        + " WHERE invoiceID = " + invoiceID + ";";

                ResultSet rset = stmt.executeQuery(sql);
                
                while(rset.next()) {
                    vendor = rset.getString(1);
                    break;
                }
                
                sql = "SELECT invoiceType FROM pInvoice s,"
                            + " registered_pInvoice r"
                            + " WHERE s.invoiceDate = r.invoiceDate"
                            + " AND invoiceId = " + invoiceID + " ;";
                                
                rset = stmt.executeQuery(sql);
                
                while(rset.next()) 
                    invoiceType = rset.getString(1);
                
                int len = vendor.length();
                boolean valid = true;
                String test = "";
                try {
                    test = vendor.substring(len-8, len);
                }catch (StringIndexOutOfBoundsException e) {
                    valid = true;
                }
                System.out.println(invoiceType);
                if(invoiceType.equals("Return")|| test.equals("(Edited)")) {
                    Main.infoPane.append(Main.tp, Color.RED,
                            "Can't make a return for this invoiceID (" 
                            + invoiceID + ")\n");
                    
                    valid = false;
                }
                if(valid) {
                    tfVendor.setText(vendor);
                    sql = "SELECT itemName FROM pInvoice s,"
                            + " registered_pInvoice r"
                            + " WHERE s.invoiceDate = r.invoiceDate"
                            + " AND invoiceId = " + invoiceID + " ;";
                    rset = stmt.executeQuery(sql);
                    while(rset.next()) {
                        cbItem.addItem(rset.getString(1));
                    }
                }

                rset.close();		
                conn.close();
                stmt.close();
                tfVendor.setEditable(false);
            } catch (SQLException e1) {
            Main.infoPane.append(Main.tp, Color.RED,
                    "Can't connect to the database\n");
            e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            }
        }catch (NumberFormatException e) {
            tfVendor.setText("");
            cbItem.removeAllItems();
        }
    }
    
    public static void addAction() {
        
        String itemName = (String) cbItem.getSelectedItem();
        
        int testQuantity = 0;
        int requiredQuantity = 0;
        boolean quantityExists = true;
        int invoiceID = 0;
	
        try { 
            invoiceID = Integer.parseInt(tfInvoiceID.getText());
            requiredQuantity = Integer.parseInt(tfQuantity.getText());
            for(int i = 0; i < returnsTable.getRowCount(); i++) {
                if(returnsTable.getValueAt(i, 1) == "")
                    continue;
                if(returnsTable.getValueAt(i, 1) == itemName) {
                    requiredQuantity += (int) returnsTable.getValueAt(i , 2);
                    break; 
                }
            }
        } catch (NumberFormatException ne) {
            /*the code will handle it down below*/
        }
	
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String username = LogIn.username;
            String password = LogIn.password;
            String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
            conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            String sql = "SELECT quantity FROM pInvoice s,"
                    + " registered_pInvoice r"
                    + " WHERE s.invoiceDate = r.invoiceDate"
                    + " AND invoiceId = " + invoiceID 
                    + " AND itemName = '" + itemName + "' ;";
            
            stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(sql);
            while(rset.next()){ 
                testQuantity = rset.getInt(1);
            }
	
            if(requiredQuantity > testQuantity) {
                
                Main.infoPane.append(Main.tp, Color.RED,
                        "The Quantity value can't exceed " 
                        + testQuantity + " for '" 
                        + itemName + "'\n");
                
                quantityExists = false;
            }
            rset.close();
            conn.close();
            stmt.close();
        } catch(SQLException ex) {
            ex.printStackTrace();	
        } catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if(quantityExists) {            
	
            if(returnsTable.getRowCount() != 0 && itemAdded)
                dTableModel.removeRow(returnsTable.getRowCount()-1);
            
            boolean validQuantityValue = true;
            int sQuantity = 0;
            
            try { 
                sQuantity = Integer.parseInt(tfQuantity.getText());
		
            } catch (NumberFormatException ne) {
                validQuantityValue = false;
                itemAdded = false;
                
                if(!tfQuantity.getText().isEmpty())                
                    Main.infoPane.append(Main.tp, Color.RED,
                            "Bad formation for the Quantity value\n");
                else 
		
                    Main.infoPane.append(Main.tp, Color.RED,
                            "Enter the Quantity value\n");
            }
            
            String sItem;
            double sPrice = 0, sTotalPrice;
            sItem = (String) cbItem.getSelectedItem();	
            boolean itemExists = false;
            int itemExistsAt = 0;
	
            for(int i = 0; i < returnsTable.getRowCount(); i++) 
                if(returnsTable.getValueAt(i, 1).toString() == sItem) {
                    itemExists = true;
                    itemExistsAt = i;
                    break;
                }
		
            if(!itemExists && sQuantity <= 0 && validQuantityValue) {
                Main.infoPane.append(Main.tp, Color.RED,
                        "Quantity value must be 1 or more\n");
                itemAdded = false;
            }
            else if(itemExists && sQuantity == 0 && validQuantityValue) {
                Main.infoPane.append(Main.tp, Color.RED,
                        "Quantity value must be 1 or more\n");
                itemAdded = false;
            }
            else if(validQuantityValue){
	
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    String username = LogIn.username;
                    String password = LogIn.password;
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                    conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();
                    String sql = "SELECT disPrice FROM items "
                            + "WHERE itemName = '" + sItem + "';";
                    stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery(sql);
                    while(rset.next())
                        sPrice =
                                Double.parseDouble(
                                new DecimalFormat(".##").
                                format(rset.getDouble(1)));
		
                    sTotalPrice = Double.parseDouble(
                            new DecimalFormat(".##").
                            format(sPrice * sQuantity)); 
                    
                    rset.close();
                    conn.close();
                    stmt.close();
                    
                    int num = 0;
                    try {
                        num =(int) returnsTable.
                                getValueAt(returnsTable.getRowCount()-1, 0)+1;
                    } catch(Exception ex) {
                        num = 1;
                    }
		 if(!itemExists) {
                        Object[] tempRow = 
                                new Object[]{num, sItem,
                                    sQuantity, sPrice, sTotalPrice};
                        
                        dTableModel.addRow(tempRow);
                    } else {
                        int oldQuantity;
                        double  oldTotalPrice;
                        oldQuantity = 
                                (int) returnsTable.
                                getValueAt(itemExistsAt, 2);
                        
                        oldTotalPrice = 
                                (double) returnsTable.
                                getValueAt(itemExistsAt, 4);
                        
                        if(oldQuantity+sQuantity > 0) {
                            returnsTable.setValueAt
                                    (oldQuantity+sQuantity, itemExistsAt,
                                    2);
                            
                            returnsTable.setValueAt
                                    ((oldQuantity+sQuantity) * sPrice,
                                    itemExistsAt, 4);
                        } else 
                            Main.infoPane.append(Main.tp,Color.RED,
                                    "Quantity value must be 1 or more\n");			
                    }
                    netTotalPrice = 0;
                            
                    for(int i = 0; i <  returnsTable.getRowCount(); i++) 
                        netTotalPrice += (double) returnsTable.getValueAt(i, 4);
			
                    netTotalPrice = 
                            Double.parseDouble(
                            new DecimalFormat(".##").
                            format(netTotalPrice));
		
                    Object[] FinalRow = new Object[]{"","","","",netTotalPrice};
                    dTableModel.addRow(FinalRow);
                    
                    DefaultTableCellRenderer centerRenderer = 
                            new DefaultTableCellRenderer();
                    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
                    for(int i = 0; i < returnsTable.getColumnCount(); i++)
                        returnsTable.getColumnModel().
                                getColumn(i).setCellRenderer(centerRenderer);
                    
                    itemAdded = true;
                } catch(SQLException ex) {
                    ex.printStackTrace();
                } catch(ClassNotFoundException ex) {
                    ex.printStackTrace();
                }                
            }
        }
    }
    
    public static void removeAction() {
        try {
            boolean isEmbtyCell = 
                    returnsTable.getValueAt(returnsTable.getSelectedRow(),
                    1).toString().equals("");
            
            if(isEmbtyCell) {
                System.out.println("can't remove it!");
            } else {
                if(returnsTable.getRowCount() == 2){                   
                    try {
                        dTableModel.removeRow(returnsTable.getSelectedRow());
                        dTableModel.removeRow(0);
                    } catch(IndexOutOfBoundsException ee) {
                        Main.infoPane.append(Main.tp, Color.RED,
                                "No item selected\n");
                    }
		   } else {                
                    try {                      
                        dTableModel.removeRow(returnsTable.getSelectedRow());                       
                        dTableModel.removeRow(returnsTable.getRowCount()-1);
                        int num = 0;
                        for(int i = 0; i <= returnsTable.getRowCount()-1; i++) 
                            returnsTable.setValueAt(++num, i, 0);
                        netTotalPrice = 0;
                        for(int i = 0; i <  returnsTable.getRowCount(); i++) 
                            netTotalPrice += 
                                    (double) returnsTable.getValueAt(i, 4);
                        netTotalPrice = 
                                Double.parseDouble(
                                new DecimalFormat(".##").
                                format(netTotalPrice));
                        
                        Object[] FinalRow = 
                                new Object[]{"","","","",netTotalPrice};
                        dTableModel.addRow(FinalRow);
                        
                    } catch(IndexOutOfBoundsException ee) {
                        Main.infoPane.append(Main.tp, Color.RED,
                                "No item selected\n");
                    }
                }
            }
        }catch(ArrayIndexOutOfBoundsException e) {
            Main.infoPane.append(Main.tp, Color.RED, "No item selected\n");
        }
    }
    
    public static void submitAction() {
        
		
		
		
        double invoiceValue =
                (double) returnsTable.getValueAt
                (returnsTable.getRowCount()-1,4);
        
        
        Date date = new Date();        
        SimpleDateFormat fotmatDate = 
                new SimpleDateFormat("E yy.MM.dd 'at' hh:mm:ssa");
	String tempDate = fotmatDate.format(date);
        String invoiceDate = tempDate.replace("AM", "am").replace("PM","pm");
	
        try {
                
            Class.forName("com.mysql.jdbc.Driver");
            String username = LogIn.username;
            String password = LogIn.password;
            String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
            conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            ResultSet rset = null;
            String addItem, updateItem;
            String vendorName, itemName;
            
            int quantity; 
            int nQuantity = 0;
            
            double price = 0, totalPrice, disPrice = 0;
            double nTotalPrice, nDisPrice, disPercentage = 0, nTotalDisPrice ;
		
            vendorName = (String) tfVendor.getText();
	
            for(int i = 0; i < returnsTable.getRowCount(); i++) {
                if(returnsTable.getValueAt(i, 1).toString().length() == 0)
                        continue;
                
                itemName = (String) returnsTable.getValueAt(i,1);
                quantity = (int) returnsTable.getValueAt(i,2);
                totalPrice = (double) returnsTable.getValueAt(i,4);
                
                for(int k = 0; k < Items.itemTable.getRowCount(); k++)
                    if(Items.itemTable.getValueAt(k, 1).equals(itemName)) {
                        price = (double) Items.itemTable.getValueAt(k,3);
                        disPrice = (double) Items.itemTable.getValueAt(k,6);
                        break;
                  
                    }

                    
                //adding item to the invoice
                addItem = "INSERT INTO pInvoice VALUES ('" 
                        + vendorName + "', '" + itemName 
                        + "', "+ quantity + ", " + disPrice 
                        + ", " + totalPrice + ", '" 
                        + invoiceDate + "');";
			
                stmt.executeUpdate(addItem);
		
                //updating the item in the database
                  
                rset = stmt.executeQuery("SELECT quantity, disPercentage"
                        + "  FROM items WHERE itemName = '" + itemName + "';");
                    
                while(rset.next()) {
                    nQuantity = rset.getInt(1) - quantity;
                    disPercentage = rset.getDouble(2);
                }
                
                nTotalPrice = 
                        Double.parseDouble(
                        new DecimalFormat(".##").
                        format(nQuantity * price));
                
                nTotalDisPrice =
                        Double.parseDouble(
                        new DecimalFormat(".##").
                        format(nQuantity * disPrice));

                    updateItem = "UPdATE items SET quantity = " 
                            + nQuantity 
                            + ", totalPrice = " 
                            + nTotalPrice 
                            + ", totalDisPrice = " 
                            + nTotalDisPrice 
                            + " WHERE itemName = '" 
                            + itemName + "';";
                    stmt.executeUpdate(updateItem);
		
                }
            
            //update vendor balance 
            double vendorBalance = 0;
            double debit = 0;
            double credit = 0;
            
            String getBalance = "SELECT balance,"
                    + " dr, cr "
                    + "FROM vendors "
                    + "WHERE vendorName = '" 
                    + vendorName + "';";
                
            rset = stmt.executeQuery(getBalance);
            while(rset.next()) {
                vendorBalance = rset.getDouble(1);
                debit = rset.getDouble(2);
                credit = rset.getDouble(3);
            }
            
            if(invoiceValue > vendorBalance) {
            
                String editCashBalance = "INSERT INTO cash VALUES (" 
                        + invoiceValue + ", 0, 'dr', '" 
                        + vendorName + "', '" + invoiceDate 
                        + "', 'purchasing invoice(Return)');";

                stmt.executeUpdate(editCashBalance);

                Object[] tempRow = new Object[]{invoiceDate, 0, invoiceValue};
                Cash.dTableModel.addRow(tempRow);

            } else {
                debit -= invoiceValue;
                vendorBalance = debit - credit;
                
                String updateBalance = "UPDATE vendors SET balance = " 
                        + vendorBalance + ", dr = "
                        + debit + ", cr = " 
                        + credit +" WHERE vendorName = '" 
                        + vendorName + "';";
                
                stmt.executeUpdate(updateBalance);
            }
            
            String sql = "INSERT INTO registered_pInvoice VALUES (NULL, '" +
                    vendorName + "', 'Return', '" + invoiceDate + "');";
		
            stmt.executeUpdate(sql);
            
            int finalRowCount = 
                    PInvoice_RegisteredInvoices.
                    rPInvoiceTable.getRowCount()+1;
            Object[] tempRow = 
                    new Object[]
                    {finalRowCount, vendorName , "Return", invoiceDate};
            
            PInvoice_RegisteredInvoices.dTableModel.addRow(tempRow);
            
              
            vendorName += "(Edited)";
            int invoiceID = Integer.parseInt(tfInvoiceID.getText());
            sql = "UPDATE registered_pInvoice SET vendorName = '"
                    + vendorName + "' WHERE invoiceID = " + invoiceID + ";";
            stmt.executeUpdate(sql);
            
            String lInvoiceDate = "";
            for(int i = 0;
                    i <= PInvoice_RegisteredInvoices.rPInvoiceTable.getRowCount();
                    i++) {
                int id = 
                        (int)PInvoice_RegisteredInvoices.
                        rPInvoiceTable.getValueAt(i, 0);
                if(id == invoiceID) {
                    PInvoice_RegisteredInvoices.
                        rPInvoiceTable.setValueAt(vendorName, i, 1);
                    lInvoiceDate = 
                            (String)PInvoice_RegisteredInvoices.
                            rPInvoiceTable.getValueAt(i, 3);
                    break;
                }
            }
          
            sql = "UPDATE pInvoice SET vendorName = '"
                    + vendorName + "' WHERE invoiceDate = '" 
                    + lInvoiceDate + "';";
            
            stmt.executeUpdate(sql);
            
            Main.infoPane.append(Main.tp, new Color(10,100,10),
                    "The invoice is successfully created,"
                    + " ckeck it in the registered invoices list.\n");
                
            rset.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e1) {
            Main.infoPane.append(Main.tp, Color.RED,
                    "Can't connect to the database\n");
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        dTableModel.setRowCount(0);
    }
}

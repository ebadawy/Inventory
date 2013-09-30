package InventoryPK;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Items {
	
    static Connection conn = null;
    static JLabel lItem, lPrice, lDisPercentage;
    static JTextField tfItem, tfPrice, tfDisPercentage;
    static JButton addItem, removeItem, refresh;
    static Object[][] dataResult;
    static Object[] columns = {"","Item", "Quantity",
        "Price", "Total Price", "Discount",
        "Price After Discount", "Total Price After Discount"};
    
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
            if(column == 0 || column == 1 || 
                    column == 2 || column == 4 || 
                    column == 6 || column == 7)
                return false;
            return true;
        }
    };

    public static JTable itemTable = new JTable(dTableModel);

    public static JScrollPane itemPanel() {
        
        loadData();	
        
        itemTable.setAutoCreateRowSorter(true);
        itemTable.setRowHeight(itemTable.getRowHeight() + 16);
        
        JScrollPane itemTableSP = new JScrollPane(itemTable);        
        TableColumn column = null;
        
        for(int i = 0; i < itemTable.getColumnCount(); i++) {
            column = itemTable.getColumnModel().getColumn(i);
            if(i == 0)
                column.setPreferredWidth(56);
            else
                column.setPreferredWidth(150);
        }
        
        addItem = new JButton("Add");
        removeItem = new JButton("Remove");
        refresh = new JButton("Refresh");    
        
        addItem.setToolTipText("Add a new item");
        removeItem.setToolTipText("Remove selected item");
        refresh.setToolTipText("Refresh items database");
        
        lItem = new JLabel("Item");
        lPrice = new JLabel("Price");        
        lDisPercentage = new JLabel("Discount");              
       
        tfItem = new JTextField(19);
        tfPrice = new JTextField(9);
        tfDisPercentage = new JTextField(9);
	
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(0,1,2,2));
        JPanel textPanel = new JPanel();
        JPanel btmPanel1 = new JPanel();
        JPanel btmPanel = new JPanel(new BorderLayout());
        
        textPanel.add(lItem);
        textPanel.add(tfItem);
        textPanel.add(lPrice);
        textPanel.add(tfPrice);
        textPanel.add(lDisPercentage);
        textPanel.add(tfDisPercentage);
        
        btmPanel1.add(addItem);
        btmPanel1.add(removeItem);
        btmPanel1.add(refresh);
        btmPanel.add(btmPanel1, BorderLayout.EAST);
	
        inputPanel.add(textPanel);
        inputPanel.add(btmPanel);
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(itemTableSP, BorderLayout.CENTER);
        JScrollPane mainPanelSP = new JScrollPane(mainPanel);
        
        addItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                addAction();	
            }
        });
        
        addItem.addKeyListener(new KeyListener() {
	
            @Override
            public void keyPressed(KeyEvent e) {  
                
                if(e.getKeyCode() == 10) {
                    addAction();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent arg0) {
            
            }
            
            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        
        removeItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeAction();
            }
        });

        refresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Main.infoPane.append(Main.tp, Color.BLACK,
                        "Loading data...\n");
                double ts1 = System.currentTimeMillis();
                dTableModel.setRowCount(1);
                dTableModel.removeRow(0);
                loadData();
                double ts2 = System.currentTimeMillis();
                double T = Double.parseDouble(
                        new DecimalFormat(".###").
                        format((ts2 - ts1) / 1000));
                Main.infoPane.append(Main.tp,
                        new Color(10, 100, 10),
                        "Data has been successfully loaded (" 
                        + T + " sec)\n");
            }
        });
        
        tfItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tfPrice.requestFocus();
            }
        });
        tfPrice.addActionListener(new ActionListener() {
	
            @Override
            public void actionPerformed(ActionEvent e) {
                tfDisPercentage.requestFocus();
            }
        });
	
        tfDisPercentage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addItem.requestFocus();
            }
        });
        
        itemTable.addKeyListener(new KeyListener() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                
                if(e.getKeyCode() == 10) {
                    
                    Object sItem = 
                            itemTable.getValueAt
                            (itemTable.getSelectedRow(), 1);
                    
                    int sQuantity = 
                            (int) itemTable.getValueAt
                            (itemTable.getSelectedRow(), 2);
                    
                    double sPrice = 
                            (double) itemTable.getValueAt
                            (itemTable.getSelectedRow(), 3);
                    
                    double sTotalPrice = 
                            Double.parseDouble
                            (new DecimalFormat(".##").
                            format(sQuantity * sPrice));
                    
                    double sDisPercentage = 
                            (double) itemTable.getValueAt
                            (itemTable.getSelectedRow(), 5);
                   
                    double sDisPrice = 
                            Double.parseDouble
                            (new DecimalFormat(".##").
                            format(sPrice * (1-sDisPercentage)));
                   
                    double stotalDisPrice = 
                            Double.parseDouble
                            (new DecimalFormat(".##").
                            format(sQuantity * sDisPrice));
                    
                    try{
                        Class.forName("com.mysql.jdbc.Driver");
                        String username = LogIn.username;
                        String password = LogIn.password;
                        String url = "jdbc:mysql://" + LogIn.ip 
                                + "/inventory";
                        
                        conn = DriverManager.getConnection
                                (url, username, password);
                        
                        Statement stmt = conn.createStatement();
                        
                        String updateItem = "UPdATE items SET quantity = " 
                                + sQuantity + ", price = " 
                                + sPrice + ", totalPrice = " 
                                + sTotalPrice + ", disPercentage = " 
                                + sDisPercentage + ", disPrice = " 
                                + sDisPrice + ", totalDisPrice = "
                                + stotalDisPrice + " WHERE itemName = '" 
                                + sItem + "';";
                        
                        stmt.executeUpdate(updateItem);
                        Main.infoPane.append
                                (Main.tp, new Color(10, 100, 10), "new " 
                                + sItem + "'s valuse has been updated\n");
                        
                        conn.close();
                        stmt.close();
                        
                        itemTable.setValueAt
                                (sQuantity, itemTable.getSelectedRow(), 2);
                        
                        itemTable.setValueAt
                                (sPrice, itemTable.getSelectedRow(), 3);
                        
                        itemTable.setValueAt
                                (sTotalPrice, itemTable.getSelectedRow(), 4);
                       
                        itemTable.setValueAt
                                (sDisPercentage, itemTable.getSelectedRow(), 5);
                        
                        itemTable.setValueAt
                                (sDisPrice, itemTable.getSelectedRow(), 6);
                       
                        itemTable.setValueAt
                                (stotalDisPrice, itemTable.getSelectedRow(), 7);
                        
                        updateSumTotalDisPrice();
                    
                    } catch(SQLException sqlex) {
                        sqlex.printStackTrace();
                    } catch(ClassNotFoundException cex) {
                        cex.printStackTrace();
                    }
		
                } else if(e.getKeyCode() == 127) {
                    removeAction();
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }
            
            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        
        return mainPanelSP;
    }
    
    public static void loadData() {
    
        Statement stmt;
	
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String username = LogIn.username;
            String password = LogIn.password;
            String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            String selectStuff = "Select * From items;";
            ResultSet rset = stmt.executeQuery(selectStuff);
	
            Object[] tempRow;
            int count = 0;
            while(rset.next()) {                
		
                int quantity = rset.getInt(2);
                double price = rset.getDouble(3);
                double totalPrice = rset.getDouble(4);
                double disPercentage = rset.getDouble(5);
                double disPrice = rset.getDouble(6);
                double totalDisPrice = rset.getDouble(7);
		
                price = Double.parseDouble(                        
                        new DecimalFormat(".##").
                        format(price));
			
                totalPrice = Double.parseDouble(                
                        new DecimalFormat(".##").                        
                        format(totalPrice));
			
                disPercentage = Double.parseDouble(
                        new DecimalFormat(".##").
                        format(disPercentage));
			
                disPrice = Double.parseDouble(
                        new DecimalFormat(".##").
                        format(disPrice));
                        
                totalDisPrice = Double.parseDouble(
                        new DecimalFormat(".##").
                        format(totalDisPrice));                
		
                tempRow = 
                        new Object[]{ ++count,
                            rset.getString(1),
                            quantity, price,
                            totalPrice,
                            disPercentage,
                            disPrice,
                            totalDisPrice};
                            		
                dTableModel.addRow(tempRow);
            }
	
            rset.close();
            conn.close();
            
            //Center the data in the table
            DefaultTableCellRenderer centerRenderer = 
                    new DefaultTableCellRenderer();
            
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            
            for(int i = 0; i < itemTable.getColumnCount(); i++)
                itemTable.getColumnModel().getColumn(i).
                        setCellRenderer( centerRenderer );            
	
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
	
        }catch(NullPointerException e2) {
        }
    }
        
    public static void addAction() {
   
        try{
        
            double sDisPercentage = 
                    Double.parseDouble(tfDisPercentage.getText());
            
            String sItem = tfItem.getText();
            
            if(sDisPercentage > 1 || sDisPercentage < 0)
                Main.infoPane.append(Main.tp, Color.RED,
                        "Discount value must be between 0 & 1\n");
                else {
                int sQuantity = 0;
                double sPrice = Double.parseDouble(tfPrice.getText());
                double sTotalPrice = sPrice * sQuantity;
                double sDisPrice = sPrice * (1-sDisPercentage);
                double sTotalDisPrice = sDisPrice * sQuantity;
                
                int num = 0;
                try {
                    num = (int) itemTable.getValueAt
                            (itemTable.getRowCount()-1, 0)+1;
                    
                } catch(Exception ex) {
                    num = 1;
                }

                Object[] tempRow = 
                        new Object[]{num, sItem,
                            sQuantity, sPrice, sTotalPrice,                           
                            sDisPercentage, sDisPrice, sTotalDisPrice};
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    String username = LogIn.username;
                    String password = LogIn.password;                        
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";                        
                    conn = DriverManager.getConnection(url,
                            username, password);
                    Statement stmt = conn.createStatement();
                    String itemSearch = "SELECT * from items where "
                            + "itemName = '" + sItem.trim() + "';";
                    stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery(itemSearch);
                    if(rset.next())
                        Main.infoPane.append(Main.tp, Color.RED,
                                "Multiple item defined.\n");
                    else {
                        String sqlQuery = "INSERT INTO items VALUES ('" 
                                + sItem + "', " + sQuantity + ", " 
                                + sPrice + "," + sTotalPrice + "," 
                                + sDisPercentage + ", " 
                                + sDisPrice + ", "+ sTotalDisPrice +");";
                            
                        stmt.execute(sqlQuery);
                        Main.infoPane.append(Main.tp, new Color(10,100,10),
                                "new item added to the Database (" 
                                + sItem + ")\n");
                            
                        dTableModel.addRow(tempRow);
                        tfItem.requestFocus();
                    }
                    
                    rset.close();
                    conn.close();
                    stmt.close();
                    
                    SInvoice_NewInvoice.loadItemData();
                    PInvoice_NewInvoice.loadItemData();
                    
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    Main.infoPane.append
                            (Main.tp, Color.RED,
                            "can't connect to the Database\n");
                    
                } catch(ClassNotFoundException ex) {
                    ex.printStackTrace();
                    Main.infoPane.append
                            (Main.tp, Color.RED,
                            "can't connect to the Database\n");
                } 
            }
        } catch(NumberFormatException ex) {
            
            Main.infoPane.append(Main.tp, Color.RED, "Invalid data\n");
            ex.printStackTrace();
            
        } catch (NullPointerException ex) {
        }
    }
        
    public static void removeAction() {
    
        try{
            Object temp = itemTable.getValueAt(itemTable.getSelectedRow(), 1);
            
            int result = 
                    JOptionPane.showConfirmDialog
                    (null, "Are you sure you want to "
                    + "permanently delete this item?",
                    "Delete Item",JOptionPane.YES_OPTION);
            
            switch (result) {
                case JOptionPane.YES_OPTION :
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        
                        String username = LogIn.username;
                        String password = LogIn.password;
                        
                        String url = 
                                "jdbc:mysql://" + LogIn.ip + "/inventory";
                        
                        conn = DriverManager.getConnection
                                (url, username, password);
                        
                        Statement stmt = conn.createStatement();
                        
                        String newPres = 
                                "DELETE FROM items WHERE itemName = '" 
                                + temp + "';";
                        
                        stmt.executeUpdate(newPres);
                        
                        dTableModel.removeRow(itemTable.getSelectedRow());
                        int count = 0;
                        
                        for(int i = 0; i < dTableModel.getRowCount(); i++)
                            itemTable.setValueAt(++count, i, 0);
                        
                        Main.infoPane.append
                                (Main.tp, new Color(10,100,10),
                                "item \"" + temp + "\" removed\n");
                        
                        stmt.close();
                        conn.close();
                                                
                        SInvoice_NewInvoice.loadItemData();
                        PInvoice_NewInvoice.loadItemData();
                    
                    } catch (SQLException e1) {
                        
                        Main.infoPane.append
                                (Main.tp, Color.RED,
                                "can't connect to the Database\n");
                        
                        e1.printStackTrace();
                        
                    } catch (ClassNotFoundException e1) {
                        
                        Main.infoPane.append
                                (Main.tp, Color.RED,
                                "can't connect to the Database\n");
                        
                        e1.printStackTrace();
                    }
                    break;
                    
                case JOptionPane.NO_OPTION :
                    break;
                }
        } catch(IndexOutOfBoundsException ee) {
            Main.infoPane.append(Main.tp, Color.RED, "No item selected\n");
        }
    }
    
    public static void updateSumTotalDisPrice() {
        double total = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String username = LogIn.username;
            String password = LogIn.password;
            String url = 
                    "jdbc:mysql://" + LogIn.ip + "/inventory";
            
            conn = DriverManager.getConnection(url, username, password);
            
            String selectStuff =
                    "SELECT SUM(totalDisPrice) "
                    + "AS sumTotalDisPrice FROM items;";
            
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(selectStuff);
            
            while(rset.next())
                total = rset.getDouble(1);
            
        }catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        total = Double.parseDouble( new DecimalFormat(".##").format(total));
        Main.statusBar.setText("Database", "items", "" + total);
    }
}

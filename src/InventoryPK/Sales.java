
package InventoryPK;

import java.awt.BorderLayout;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Sales {
    
    static Connection conn;
    static JButton btnRefresh;
    
    static Object[][] dataResult;
	static Object[] columns = {"Debit", "Credit"};
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
        
        static JTable salesTable;
    
    public static JScrollPane sales() {
        JPanel btnPanel = new JPanel (new BorderLayout());
        btnRefresh = new JButton("Refresh");
        btnPanel .add(btnRefresh, BorderLayout.EAST);
        
        salesTable = new JTable(dTableModel);
        salesTable.setRowHeight(salesTable.getRowHeight() + 16);
        JScrollPane salesTableSP = new JScrollPane(salesTable);
           
        //loadData();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(salesTableSP, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JScrollPane mainPanelSP = new JScrollPane(mainPanel);
        
        return mainPanelSP;
        
    }
    
    public static void loadData() {
         try {
             Class.forName("com.mysql.jdbc.Driver");
             
             String username = LogIn.username;
             String password = LogIn.password;
             String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
             conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             String selectStuff ="SELECT * FROM sales;";
             ResultSet rset = stmt.executeQuery(selectStuff);
             
             Object[] tempRow;
             while(rset.next()) {
                 tempRow = new Object[]{rset.getDouble(1), rset.getDouble(2)};
                 dTableModel.addRow(tempRow);
             }
             
             rset.close();
             stmt.close();
             conn.close();
               
         } catch (SQLException ex) {
             ex.printStackTrace();
         } catch (ClassNotFoundException ex) {
             ex.printStackTrace();
         }
    }
}

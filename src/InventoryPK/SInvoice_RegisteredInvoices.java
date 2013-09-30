package InventoryPK;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;



public class SInvoice_RegisteredInvoices {
	
	
    static Connection conn = null;
    static JFrame viewFrame;
    static JButton viewbtm;
    public static JRadioButton dateRB, customerNameRB;
    public static JLabel lFilter;
    public static JTextField tfFilter;
    static ButtonGroup filterBy;
    static JButton printbtm;
    static JSpinner dateSpinner;
    static String selectedDate;
    public static JPanel filterPanel1;
    static String requiredDate;
    
    static Object[][] dataResult;
    static Object[] columns = 
    {"invoiceID","Customer Name", "Invoice Type", "Date"};
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
    
    private static TableRowSorter<DefaultTableModel> sorter;
    public static JTable rSInvoiceTable;
    public static JTable viewTable;
    
    public static JScrollPane RegisteredInvoice() {
        
        rSInvoiceTable = new JTable(dTableModel);
        rSInvoiceTable.setAutoCreateRowSorter(true);
        rSInvoiceTable.setRowHeight(rSInvoiceTable.getRowHeight() + 16);
        sorter = new TableRowSorter<DefaultTableModel>(dTableModel);
        rSInvoiceTable.setRowSorter(sorter);
        
        TableColumn column = null;
        for(int i = 0; i < rSInvoiceTable.getColumnCount(); i++) {
            column = rSInvoiceTable.getColumnModel().getColumn(i);
            if(i == 0)
                column.setPreferredWidth(100);
            else if(i == 2)
                column.setPreferredWidth(90);
            else
                column.setPreferredWidth(200);
        }
        
        customerNameRB = new JRadioButton("Customer Name");
        dateRB = new JRadioButton("Date");
        customerNameRB.setSelected(true);	
        filterBy = new ButtonGroup();
        filterBy.add(customerNameRB);
        filterBy.add(dateRB);
        Box box = Box.createHorizontalBox();
        box.add(customerNameRB);
        box.add(dateRB);
        
        //creating customer filter component 
        lFilter = new JLabel("  Filter Text:  ");
        tfFilter = new JTextField(20);
        
        //creating date spinner
        Date now = new Date();
        SpinnerModel model = 
                new SpinnerDateModel(now, null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(model);
        DateEditor dateEditor = new DateEditor(dateSpinner, "E yy.MM.dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setSize(500, 30);
        
        selectedDate = new SimpleDateFormat("E yy.MM.dd").format(new Date());
        model.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                
                Date date = ((SpinnerDateModel)e.getSource()).getDate();
                SimpleDateFormat fotmatDate = 
                        new SimpleDateFormat("E yy.MM.dd");
                selectedDate = fotmatDate.format(date);
            }
        });
        
        tfFilter.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        filter();
                    }                    
                    public void insertUpdate(DocumentEvent e) {
                        filter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        filter();
                    }
                });
        
        JPanel radioBtmPanel = new JPanel(new BorderLayout());
        radioBtmPanel.add(box, BorderLayout.WEST);
	
        filterPanel1 = new JPanel();
        filterPanel1.add(lFilter);
        filterPanel1.add(tfFilter);
        
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(filterPanel1, BorderLayout.WEST);
        
        JPanel inputPanel = new JPanel(new GridLayout(0,1,2,2));
        inputPanel.add(radioBtmPanel);
       
        inputPanel.setBorder(BorderFactory.createTitledBorder("Filter By"));
        
        viewbtm = new JButton("View");
        viewbtm.setToolTipText("View selected invoice");
        JPanel btmPanel = new JPanel(new BorderLayout());
        btmPanel.add(viewbtm, BorderLayout.EAST);
        JScrollPane rPInvoiceTableSP = new JScrollPane(rSInvoiceTable);
        JPanel mainPanel = new JPanel(new BorderLayout());
	
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(rPInvoiceTableSP, BorderLayout.CENTER);
        mainPanel.add(btmPanel, BorderLayout.SOUTH);
	
        loadDate();
        
        viewbtm.addActionListener(new ActionListener() {
	
            @Override
            public void actionPerformed(ActionEvent arg0) {
                viewAction();
            }
        });
        
        rSInvoiceTable.addKeyListener(new KeyListener() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 10) 
                    viewAction();
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
	
        rSInvoiceTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2)
                    viewAction();
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }
            
            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }
            
            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
	
        model.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                Date spinnerdate = ((SpinnerDateModel)e.getSource()).getDate();
                requiredDate = 
                        new SimpleDateFormat("E yy.MM.dd").format(spinnerdate);
                filter();
            }
        });
        
        ListenForRadiobutton listenForRadiobutton = new ListenForRadiobutton();
	
        customerNameRB.addItemListener (listenForRadiobutton);
        dateRB.addItemListener (listenForRadiobutton);        
	
        JScrollPane mainPanelSP = new JScrollPane(mainPanel);
        return mainPanelSP;
    }
    
    private static void filter() {
        RowFilter<DefaultTableModel, Object> rf = null;
        
        //If current expression doesn't parse, don't update.
        try {
            if(customerNameRB.isSelected())
                rf = RowFilter.regexFilter("(?i)" + tfFilter.getText(), 1);
            else 
                rf = RowFilter.regexFilter("(?i)" + requiredDate, 3);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }
    
    public static void loadDate() {
        
        dTableModel.setRowCount(1);
        dTableModel.removeRow(0);
        
        try {
	
            Class.forName("com.mysql.jdbc.Driver");
            String username = LogIn.username;
            String password = LogIn.password;
            String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
            conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            String selectStuff = "SELECT * FROM registered_sInvoice;";
            ResultSet rset = stmt.executeQuery(selectStuff);
            Object[] tempRow;
            while(rset.next()) {
                tempRow = 
                        new Object[]{rset.getInt(1),
                            rset.getString(2),
                            rset.getString(3),
                            rset.getString(4)};
                
                dTableModel.addRow(tempRow);
            }
            rset.close();
            stmt.close();
            conn.close();
            
            rSInvoiceTable.getColumnModel().getColumn(0).setPreferredWidth(1);            
	
            //Center the data in the table
            DefaultTableCellRenderer centerRenderer = 
                    new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            for(int i = 0; i < rSInvoiceTable.getColumnCount(); i++)
                rSInvoiceTable.getColumnModel().
                        getColumn(i).setCellRenderer( centerRenderer );
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void viewAction() {
        
        try {
            viewFrame = new JFrame();
            String customerName = 
                    (String) rSInvoiceTable.
                    getValueAt(rSInvoiceTable.getSelectedRow(), 1);
            
            viewFrame.setTitle(customerName);
            viewFrame.setSize(800, 600);
            viewFrame.setLocationRelativeTo(null);
	
            Object[][] dataResult_view = null;
            Object[] columns_view =
            {"","Item", "Quantity", "Price/Item", "Total Price"};
            DefaultTableModel dTableModel_view = 
                    new DefaultTableModel(dataResult_view, columns_view) {
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
            
            viewTable = new JTable(dTableModel_view);
            viewTable.setRowHeight(viewTable.getRowHeight() + 16);
	
            TableColumn column = null;
            for(int i = 0; i < viewTable.getColumnCount(); i++) {
                column = viewTable.getColumnModel().getColumn(i);
                if(i == 0)
                    column.setPreferredWidth(1);
                else
                    column.setPreferredWidth(150);
		}
            
            int invoiceID = 
                    (int) rSInvoiceTable.
                    getValueAt(rSInvoiceTable.getSelectedRow(), 0);
            String invoiceDate = 
                    (String) rSInvoiceTable.
                    getValueAt(rSInvoiceTable.getSelectedRow(), 3);            
	
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String username = LogIn.username;
                String password = LogIn.password;
                String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                
                String selectStuff = 
                        "SELECT * FROM sInvoice WHERE customerName = '" 
                        + customerName + "' AND invoiceDate = '" 
                        + invoiceDate + "';";
                
                System.out.println(selectStuff);
                ResultSet rset = stmt.executeQuery(selectStuff);
		
                Object[] tempRow;
		
                int count = 0;
                
                while(rset.next()) {
                    tempRow = 
                            new Object[]{ ++count ,
                                rset.getString(2),
                                rset.getString(3),
                                rset.getDouble(4),
                                rset.getDouble(5)};
                    
                    dTableModel_view.addRow(tempRow);
                }
                
                String sumLine = 
                        "SELECT SUM(totalPrice) AS newTotalPrice "
                        + "FROM sInvoice WHERE customerName = '" 
                        + customerName + "' AND invoiceDate = '" 
                        + invoiceDate + "';";
                
                rset = stmt.executeQuery(sumLine);
                double totalprice = 0;
                while(rset.next()) 
                    totalprice = rset.getDouble(1);
                
                totalprice = 
                        Double.parseDouble
                        (new DecimalFormat(".##").
                        format(totalprice));
                
                tempRow = new Object[]{"","","","", totalprice};
                dTableModel_view.addRow(tempRow);
		
                stmt.close();
                rset.close();
                conn.close();
                
                rSInvoiceTable.getColumnModel().
                        getColumn(0).setPreferredWidth(1);
		
                //Center the data in the table
                DefaultTableCellRenderer centerRenderer =
                        new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment( JLabel.CENTER );
                for(int i = 0; i < viewTable.getColumnCount(); i++)
                    
                    viewTable.getColumnModel().
                            getColumn(i).setCellRenderer( centerRenderer );

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
	
            
            JScrollPane viewTableSP = new JScrollPane(viewTable);
            printbtm = new JButton("Print");
            JPanel btmPanel = new JPanel(new BorderLayout());
            btmPanel.add(printbtm, BorderLayout.EAST);
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(viewTableSP, BorderLayout.CENTER);
            mainPanel.add(btmPanel, BorderLayout.SOUTH);
            
            printbtm.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    
                    try {
                        viewTable.print();
                    } catch (PrinterException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            KeyboardFocusManager manager = 
                    KeyboardFocusManager.getCurrentKeyboardFocusManager();
            manager.addKeyEventDispatcher(new KeyEventDispatcher() {

                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
   		
                    if (e.getID() == KeyEvent.KEY_PRESSED){ 
                        if(e.getKeyCode() == 27)
                            viewFrame.dispose();
                    } else if (e.getID() == KeyEvent.KEY_RELEASED) {  
                    } else if (e.getID() == KeyEvent.KEY_TYPED) {
                    }  			 
                    return false;
   		}   	
            });
            
            viewFrame.add(mainPanel);
            viewFrame.setVisible(true);
            
        }catch (IndexOutOfBoundsException e) {
            if(rSInvoiceTable.getRowCount() == 0)
                Main.infoPane.append
                        (Main.tp, Color.RED,
                        "there is no registered invoices!\n");
            else
                Main.infoPane.append
                        (Main.tp, Color.RED,
                        "no invoice selected\n");
        }
    }
}

class ListenForRadiobutton implements ItemListener  {

    @Override
    public void itemStateChanged (ItemEvent e) {
        if(e.getSource() == SInvoice_RegisteredInvoices.customerNameRB) {
            SInvoice_RegisteredInvoices.
                    filterPanel1.removeAll();
            SInvoice_RegisteredInvoices.
                    filterPanel1.add(SInvoice_RegisteredInvoices.lFilter);
            SInvoice_RegisteredInvoices.
                    filterPanel1.add(SInvoice_RegisteredInvoices.tfFilter);
            SInvoice_RegisteredInvoices.
                    filterPanel1.revalidate();
        }
	
        if(e.getSource() == SInvoice_RegisteredInvoices.dateRB) {
            SInvoice_RegisteredInvoices.
                    filterPanel1.removeAll();
            SInvoice_RegisteredInvoices.
                    filterPanel1.add(SInvoice_RegisteredInvoices.dateSpinner);
            SInvoice_RegisteredInvoices.
                    filterPanel1.revalidate();
        }
	
        if(e.getSource() == PInvoice_RegisteredInvoices.vendorNameRB) {	
            PInvoice_RegisteredInvoices.
                    filterPanel1.removeAll();
            PInvoice_RegisteredInvoices.
                    filterPanel1.add(PInvoice_RegisteredInvoices.lFilter);
            PInvoice_RegisteredInvoices.
                    filterPanel1.add(PInvoice_RegisteredInvoices.tfFilter);
            PInvoice_RegisteredInvoices.
                    filterPanel1.revalidate();
        }
	
        if(e.getSource() == PInvoice_RegisteredInvoices.dateRB) {
            PInvoice_RegisteredInvoices.
                    filterPanel1.removeAll();
            PInvoice_RegisteredInvoices.
                    filterPanel1.add(PInvoice_RegisteredInvoices.dateSpinner);
            PInvoice_RegisteredInvoices.
                    filterPanel1.revalidate();
        }
    }
}

package InventoryPK;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main { 
    
    static Connection conn;
    static JTabbedPane tPane;
    public static JTextPane tp;
    public static StatusBar statusBar;
    public static AppendToTextPane infoPane;
    public static JFrame mainFrame;
    static JCheckBoxMenuItem checkInfoPanel, checkStatusBar;
    static JPanel mainPanel;
    static JMenuBar menuBar;
    static JSplitPane splitPane;
    static JScrollPane infoPanelSP, tPaneSP;
    static JMenuItem refresh, find;
    
    public static void main(String[] args) {
        new LogIn();
    }
        
	
    Main() {
        
        JFrame mainFrame = new JFrame();
        mainFrame.setSize(850, 800);
        mainFrame.setTitle("Inventory");
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        infoPane = new AppendToTextPane();
        tPane = new JTabbedPane();
        tPane.addTab("Database", Database.Database());
        tPane.addTab("Purchasing invoice",
                PurchasingInvoice.purchasingInvoicePanel());
        tPane.addTab("Selling invoice",
                SellingInvoice.sellingInvoicePanel());
        tPane.addTab("Cash", Cash.cash());
        tPane.addTab("Reports", Reports.reportsPane());
        
        tPaneSP = new JScrollPane(tPane); 
        tPane.setPreferredSize(tPane.getPreferredSize());
        tp = new JTextPane();
        
        infoPanelSP = 
                new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        infoPanelSP.setBorder(null);
        Dimension dim = mainFrame.getSize();
        Dimension infoPanelDim = new Dimension();
        infoPanelDim.setSize(dim.getWidth(), 100);
        
        infoPanelSP.setMaximumSize(infoPanelDim);
        
        tp.setBackground(new Color(238,238,238));
        tp.setPreferredSize(new Dimension(20,20)); 
        
       
 
        //Provide minimum sizes for the two components in the split pane
        tPane.setMinimumSize(new Dimension(850, 700));
        infoPanelSP.setMinimumSize(new Dimension(850, 100));
        infoPanelSP.setMaximumSize(new Dimension(850, 100));
 
        
        splitPane = 
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tPane, infoPanelSP);
        
        splitPane.setDividerLocation(618);
        tPane.setMinimumSize(new Dimension(100, 50));
        infoPanelSP.setMinimumSize(new Dimension(100, 30));
        
        Font font = new Font("Red", Font.PLAIN, 14);
        
        tp.setFont(font);
        tp.setEditable(false);
        
        
        statusBar = new StatusBar();
        
        Items.updateSumTotalDisPrice();
        
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_X);
        exit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        find = new JMenuItem("Find");
        find.setMnemonic(KeyEvent.VK_F);
        find.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
        find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 0) {
                    new Find("Item", Items.itemTable);
                }
                if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 1) {
                    new Find("Customer", Customers.customerTable);
                }
                if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 2) {
                    new Find("Vendor", Vendors.vendorTable);
                }
            }
        });
        
        refresh = new JMenuItem("Refresh");
        refresh.setMnemonic(KeyEvent.VK_R);
        refresh.setAccelerator(
                KeyStroke.getKeyStroke("F5"));
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                //case 1: items tab is selected
                if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 0) {
                           
                    Main.infoPane.append(Main.tp, Color.BLACK,
                            "Loading data...\n");
                    double ts1 = System.currentTimeMillis();
                    Items.dTableModel.setRowCount(1);
                    Items.dTableModel.removeRow(0);
                    Items.loadData();
                    double ts2 = System.currentTimeMillis();
                    double T = 
                            Double.parseDouble(
                            new DecimalFormat(".###").
                            format((ts2 - ts1) / 1000));
                    Main.infoPane.append(Main.tp, new Color(10, 100, 10),
                            "Data has been successfully loaded (" 
                            + T + " sec)\n");
                }
                
                //case 2: customers tab is selected
                if(tPane.getSelectedIndex() == 0 
                        && Database.tPane.getSelectedIndex() == 1) { 
                    
                    Main.infoPane.append(Main.tp, Color.BLACK,
                            "Loading data...\n");
                    double ts1 = System.currentTimeMillis();
                    Customers.dTableModel.setRowCount(1);
                    Customers.dTableModel.removeRow(0);
                    Customers.loadData();
                    double ts2 = System.currentTimeMillis();
                    double T = 
                            Double.parseDouble(
                            new DecimalFormat(".###").
                            format((ts2 - ts1) / 1000));
                    Main.infoPane.append(Main.tp, new Color(10, 100, 10),
                            "Data has been successfully loaded ("
                            + T + " sec)\n");
                }
                
                //case 3: vendors tab is selected
                if(tPane.getSelectedIndex() == 0 
                        && Database.tPane.getSelectedIndex() == 2) {
                          
                    Main.infoPane.append(Main.tp, Color.BLACK,
                            "Loading data...\n");
                    double ts1 = System.currentTimeMillis();
                    Vendors.dTableModel.setRowCount(1);
                    Vendors.dTableModel.removeRow(0);
                    Vendors.loadData();
                    double ts2 = System.currentTimeMillis();
                    double T = Double.parseDouble(new DecimalFormat(".###").
                            format((ts2 - ts1) / 1000));
                    Main.infoPane.append(Main.tp, new Color(10, 100, 10),
                            "Data has been successfully loaded (" 
                            + T + " sec)\n");            
                }
                
                //case 4: cash tab is selected
                if(tPane.getSelectedIndex() == 3) {
                          
                    Main.infoPane.append(Main.tp, Color.BLACK,
                            "Loading data...\n");
                    double ts1 = System.currentTimeMillis();
                    Cash.dTableModel.setRowCount(1);
                    Cash.dTableModel.removeRow(0);
                    Cash.loadData();
                    double ts2 = System.currentTimeMillis();
                    double T = Double.parseDouble(new DecimalFormat(".###").
                            format((ts2 - ts1) / 1000));
                    Main.infoPane.append(Main.tp, new Color(10, 100, 10),
                            "Data has been successfully loaded (" 
                            + T + " sec)\n");            
                }
            }
        });

        
        file.add(find);
        file.add(refresh);
        file.addSeparator();
        file.add(exit);
        
        JMenu navigate = new JMenu("Navigate");
        navigate.setMnemonic(KeyEvent.VK_N);
        
        JMenuItem items = new JMenuItem("Go to Items");
        items.setMnemonic(KeyEvent.VK_I);
        items.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK));
        items.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(0);
                Database.tPane.setSelectedIndex(0);
            }
        });
        
        JMenuItem customers = new JMenuItem("Go to Customers");
        customers.setMnemonic(KeyEvent.VK_C);
        customers.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        customers.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(0);
                Database.tPane.setSelectedIndex(1);
            }
        });       
        
        JMenuItem vendors = new JMenuItem("Go to Vendors");
        vendors.setMnemonic(KeyEvent.VK_V);
        vendors.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
        vendors.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(0);
                Database.tPane.setSelectedIndex(2);
            }
        });  
        
        JMenuItem rpi = 
                new JMenuItem("Go to Registered Purchasing Invoices");
        rpi.setMnemonic(KeyEvent.VK_P);
        rpi.setAccelerator(
                KeyStroke.getKeyStroke("control alt P"));
        rpi.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(1);
                PurchasingInvoice.tPane.setSelectedIndex(0);
            }
        });     
        
        JMenuItem spi = 
                new JMenuItem("Go to New Purchasing Invoices");
        spi.setMnemonic(KeyEvent.VK_E);
        spi.setAccelerator(
                KeyStroke.getKeyStroke("control alt E"));
        spi.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(1);
                PurchasingInvoice.tPane.setSelectedIndex(1);
            }
        });     
        
        JMenuItem rp = 
                new JMenuItem("Go to Return on  Purchase");
        rp.setMnemonic(KeyEvent.VK_R);
        rp.setAccelerator(
                KeyStroke.getKeyStroke("control alt R"));
        rp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(1);
                PurchasingInvoice.tPane.setSelectedIndex(2);
            }
        }); 
        
        JMenuItem rsi = 
                new JMenuItem("Go to Registered Selling Invoices");
        rsi.setMnemonic(KeyEvent.VK_P);
        rsi.setAccelerator(
                KeyStroke.getKeyStroke("control P"));
        rsi.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(2);
                SellingInvoice.tPane.setSelectedIndex(0);
            }
        });     
        
        JMenuItem ssi = 
                new JMenuItem("Go to New Selling Invoices");
        ssi.setMnemonic(KeyEvent.VK_S);
        ssi.setAccelerator(
                KeyStroke.getKeyStroke("control S"));
        ssi.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(2);
                SellingInvoice.tPane.setSelectedIndex(1);
            }
        });     
        
        JMenuItem rs = 
                new JMenuItem("Go to Return on  Sales");
        rs.setMnemonic(KeyEvent.VK_R);
        rs.setAccelerator(
                KeyStroke.getKeyStroke("control R"));
        rs.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(2);
                SellingInvoice.tPane.setSelectedIndex(2);
            }
        }); 
        
        JMenuItem cash = 
                new JMenuItem("Go to Cash");
        cash.setMnemonic(KeyEvent.VK_C);
        cash.setAccelerator(
                KeyStroke.getKeyStroke("alt C"));
        cash.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(3);
            }
        }); 
        
        JMenuItem sales = 
                new JMenuItem("Go to Sales");
        sales.setMnemonic(KeyEvent.VK_S);
        sales.setAccelerator(
                KeyStroke.getKeyStroke("alt S"));
        sales.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(4);
                Reports.tPane.setSelectedIndex(0);
                IncomeStatement.tPane.setSelectedIndex(0);
            }
        }); 
        
        JMenuItem COGS = 
                new JMenuItem("Go to COGS");
        COGS.setMnemonic(KeyEvent.VK_O);
        COGS.setAccelerator(
                KeyStroke.getKeyStroke("control O"));
        COGS.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(4);
                Reports.tPane.setSelectedIndex(0);
                IncomeStatement.tPane.setSelectedIndex(1);
            }
        }); 
        
        JMenuItem balanceSheet = 
                new JMenuItem("Go to Balance Sheet");
        balanceSheet.setMnemonic(KeyEvent.VK_B);
        balanceSheet.setAccelerator(
                KeyStroke.getKeyStroke("control B"));
        balanceSheet.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                tPane.setSelectedIndex(4);
                Reports.tPane.setSelectedIndex(1);
            }
        }); 
        navigate.add(items);
        navigate.add(customers);
        navigate.add(vendors);
        navigate.addSeparator();
        navigate.add(rpi);
        navigate.add(spi);
        navigate.add(rp);
        navigate.addSeparator();
        navigate.add(rsi);
        navigate.add(ssi);
        navigate.add(rs);
        navigate.addSeparator();
        navigate.add(cash);
        navigate.addSeparator();
        navigate.add(sales);
        navigate.add(COGS);
        navigate.add(balanceSheet);
        
        JMenu view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        
        checkInfoPanel = new JCheckBoxMenuItem("Info Panel");
        checkInfoPanel.setSelected(true);
        checkInfoPanel.setMnemonic(KeyEvent.VK_I);
        checkInfoPanel.setAccelerator(
                KeyStroke.getKeyStroke("F11"));
        
        checkInfoPanel.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(checkStatusBar.isSelected()) { 
                    if(checkInfoPanel.isSelected()) {
                        mainPanel.removeAll();

                        mainPanel.add(menuBar, BorderLayout.NORTH);
                        splitPane = 
                        new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        tPane, infoPanelSP);

                        splitPane.setDividerLocation(618);
                        tPane.setMinimumSize(new Dimension(100, 50));
                        infoPanelSP.setMinimumSize(new Dimension(100, 30));

                        tPane.setPreferredSize(tPane.getPreferredSize());
                        mainPanel.add(splitPane, BorderLayout.CENTER);
                        mainPanel.add(statusBar, BorderLayout.SOUTH);
                        mainPanel.revalidate();
                        mainPanel.repaint();


                    } else {
                        mainPanel.removeAll();
                        mainPanel.add(menuBar, BorderLayout.NORTH);

                        tPane.setPreferredSize(tPane.getPreferredSize());

                        splitPane.setDividerLocation(618);
                        tPane.setMinimumSize(new Dimension(100, 50));
                        infoPanelSP.setMinimumSize(new Dimension(100, 30));

                        mainPanel.add(tPane, BorderLayout.CENTER);
                        mainPanel.add(statusBar, BorderLayout.SOUTH);
                        mainPanel.revalidate();
                        mainPanel.repaint();
                        splitPane.remove(splitPane);
                    }
                }else {
                    if(checkInfoPanel.isSelected()) {
                    mainPanel.removeAll();
                    
                    mainPanel.add(menuBar, BorderLayout.NORTH);
                    splitPane = 
                    new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    tPane, infoPanelSP);
                    
                    splitPane.setDividerLocation(618);
                    tPane.setMinimumSize(new Dimension(100, 50));
                    infoPanelSP.setMinimumSize(new Dimension(100, 30));
                    
                    tPane.setPreferredSize(tPane.getPreferredSize());
                    mainPanel.add(splitPane, BorderLayout.CENTER);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                   
                    
                } else {
                    mainPanel.removeAll();
                    mainPanel.add(menuBar, BorderLayout.NORTH);
                    
                    tPane.setPreferredSize(tPane.getPreferredSize());
                    
                    splitPane.setDividerLocation(618);
                    tPane.setMinimumSize(new Dimension(100, 50));
                    infoPanelSP.setMinimumSize(new Dimension(100, 30));
                    
                    mainPanel.add(tPane, BorderLayout.CENTER);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    splitPane.remove(splitPane);
                }
                }
            }
        });
        
        checkStatusBar = new JCheckBoxMenuItem("Status Bar");
        checkStatusBar.setSelected(true);
        checkStatusBar.setMnemonic(KeyEvent.VK_S);
        checkStatusBar.setAccelerator(
                KeyStroke.getKeyStroke("F12"));
        
        checkStatusBar.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                
                if(checkInfoPanel.isSelected()) {
                    if(checkStatusBar.isSelected()) {
                        mainPanel.removeAll();

                        mainPanel.add(menuBar, BorderLayout.NORTH);

                        splitPane = 
                        new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        tPane, infoPanelSP);
                        
                        splitPane.setDividerLocation(618);
                        tPane.setMinimumSize(new Dimension(100, 50));
                        infoPanelSP.setMinimumSize(new Dimension(100, 30));
                        
                        tPane.setPreferredSize(tPane.getPreferredSize());

                        mainPanel.add(splitPane, BorderLayout.CENTER);
                        mainPanel.add(statusBar, BorderLayout.SOUTH);
                        mainPanel.revalidate();
                        mainPanel.repaint();

                        mainPanel.add(statusBar, BorderLayout.SOUTH);
                    } else {
                      mainPanel.removeAll();
                        mainPanel.add(menuBar, BorderLayout.NORTH);

                        tPane.setPreferredSize(tPane.getPreferredSize());
                        splitPane = 
                        new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        tPane, infoPanelSP);
                        
                        splitPane.setDividerLocation(618);
                        tPane.setMinimumSize(new Dimension(100, 50));
                        infoPanelSP.setMinimumSize(new Dimension(100, 30));
                        
                        mainPanel.add(splitPane, BorderLayout.CENTER);
                        mainPanel.revalidate();
                        mainPanel.repaint();

                        mainPanel.remove(statusBar);
                    }
                }else {
                    if(checkStatusBar.isSelected()) {
                        mainPanel.removeAll();

                        mainPanel.add(menuBar, BorderLayout.NORTH);
                        
                        tPane.setPreferredSize(tPane.getPreferredSize());

                        mainPanel.add(tPane, BorderLayout.CENTER);
                        mainPanel.add(statusBar, BorderLayout.SOUTH);
                        mainPanel.revalidate();
                        mainPanel.repaint();

                        mainPanel.add(statusBar, BorderLayout.SOUTH);
                    } else {
                      mainPanel.removeAll();
                        mainPanel.add(menuBar, BorderLayout.NORTH);

                        tPane.setPreferredSize(tPane.getPreferredSize());
                        mainPanel.add(tPane, BorderLayout.CENTER);
                        mainPanel.revalidate();
                        mainPanel.repaint();

                        mainPanel.remove(statusBar);
                    }
                }
            }
        });
        
        view.add(checkInfoPanel);
        view.add(checkStatusBar);
        
        menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(navigate);
        menuBar.add(view);
                
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(menuBar, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);
       
        
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        
        tPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 0) {
                    refresh.setEnabled(true);
                    find.setEnabled(true);
                    Items.updateSumTotalDisPrice();
                } else if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 1) {
                    statusBar.setText("Database", "Customers", "");
                    refresh.setEnabled(true);
                    find.setEnabled(true);
                }else if(tPane.getSelectedIndex() == 0 &&
                        Database.tPane.getSelectedIndex() == 2) {
                    statusBar.setText("Database", "Vendors", "");
                    refresh.setEnabled(true);
                    find.setEnabled(true);
                
                }else {
                    refresh.setEnabled(false);
                    find.setEnabled(false);
                }
                if(tPane.getSelectedIndex() == 1 &&
                        PurchasingInvoice.tPane.getSelectedIndex() == 0) 
                   statusBar.setText("Purchaing Invoice",
                           "Registered Invoices", "");
                if(tPane.getSelectedIndex() == 1 &&
                        PurchasingInvoice.tPane.getSelectedIndex() == 1) 
                   statusBar.setText("Purchaing Invoice","New Invoice", "");
                 if(tPane.getSelectedIndex() == 1 &&
                        PurchasingInvoice.tPane.getSelectedIndex() == 2)
                   Main.statusBar.setText("Purchaing Invoice","Returns", "");
                if(tPane.getSelectedIndex() == 2 &&
                        SellingInvoice.tPane.getSelectedIndex() == 0) 
                   statusBar.setText("Selling Invoice",
                           "Registered Invoices", "");
                if(tPane.getSelectedIndex() == 2 &&
                        SellingInvoice.tPane.getSelectedIndex() == 1) 
                   statusBar.setText("Selling Invoice", "New Invoice", "");
                if(tPane.getSelectedIndex() == 2 &&
                        SellingInvoice.tPane.getSelectedIndex() == 2)
                   Main.statusBar.setText("Selling Invoice","Returns", "");
                if(tPane.getSelectedIndex() == 3) {
                   Cash.updateBalance();
                   refresh.setEnabled(true);
                }else if (tPane.getSelectedIndex() != 0) {
                    refresh.setEnabled(false);
                }
                if(tPane.getSelectedIndex() == 4 &&
                        Reports.tPane.getSelectedIndex() == 0
                        && IncomeStatement.tPane.getSelectedIndex() == 0)
                    statusBar.setText("Reports", "Sales", "");
                 if(tPane.getSelectedIndex() == 4 &&
                        Reports.tPane.getSelectedIndex() == 0
                        && IncomeStatement.tPane.getSelectedIndex() == 1)
                    statusBar.setText("Reports", "COGS", "");
                  if(tPane.getSelectedIndex() == 4 &&
                        Reports.tPane.getSelectedIndex() == 1)
                    statusBar.setText("Reports", "Balance sheet", "");
            }
        });
    }
}




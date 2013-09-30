package InventoryPK;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;


public class Find {

    static JTextField tfFind;
    static JLabel lFind;
    static JButton btmFind;
    public static JFrame findFrame;
    
    Find(final String type, final JTable table) {

        findFrame = new JFrame();	
        KeyboardFocusManager manager = 
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
   	
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
   		
                if (e.getID() == KeyEvent.KEY_PRESSED){ 
                    if(e.getKeyCode() == 27)
                        findFrame.dispose();
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {  
   		   
                } else if (e.getID() == KeyEvent.KEY_TYPED) {
   	        
                }  			 

                return false;
            }
        });
	
        findFrame.setTitle("Find");
        findFrame.setSize(390,120);
        findFrame.setLocationRelativeTo(null);
        tfFind = new JTextField(16);
        lFind = new JLabel(type + " Name: ");
        btmFind = new JButton("Find");
        btmFind.setEnabled(false);
        tfFind.addActionListener(new ActionListener() {            
	
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(tfFind.getText().length() == 0){}
                else {
                    int col = 1;
                    boolean found = false;
                    for(int row = 0;
                            row < table.getRowCount();
                            row ++) {
                        if(tfFind.getText().
                                equals(table.getValueAt(row, col))) {
                            table.setRowSelectionInterval(row, row);
                            table.scrollRectToVisible
                                    (table.getCellRect(row, 0, true));
                            found = true;
                        }
                    }
                    if(!found)
                        JOptionPane.showMessageDialog(findFrame,
                                "Can't find " + type + " \"" 
                                + tfFind.getText() 
                                + "\"" , type + "s",
                                JOptionPane.INFORMATION_MESSAGE);
                }
            }		
        });
	
        tfFind.addKeyListener(new KeyListener() {
	
            @Override
            public void keyPressed(KeyEvent arg0) {
                
            }
            @Override
            public void keyReleased(KeyEvent arg0) {
                // TODO Auto-generated method stub
                if(tfFind.getText().length() == 0)
                    btmFind.setEnabled(false);
                else 
                    btmFind.setEnabled(true);
            }
            @Override
            public void keyTyped(KeyEvent arg0) {
              
            }
        });
	
        btmFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                int col = 1;
                boolean found = false;
                for(int row = 0;
                        row < table.getRowCount();
                        row ++) {
                    if(tfFind.getText().
                            equals(table.getValueAt(row, col))) {
                        table.setRowSelectionInterval(row, row);
                        table.scrollRectToVisible
                                (table.getCellRect(row, 0, true));
                        found = true;
                        break;
                    }
                }
                if(!found)
                    JOptionPane.showMessageDialog(findFrame,
                            "Can't find " + type 
                            + " \"" + tfFind.getText() + "\"" ,
                            type + "s", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JPanel panel = new JPanel();
        panel.add(lFind);
        panel.add(tfFind);
        panel.add(btmFind);
        findFrame.add(panel);
        findFrame.setResizable(false); 
        findFrame.setVisible(true);
    }
}


package InventoryPK;

import java.awt.*;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel{
    
    public static String text1, text2, text3 ;
    
    public StatusBar(String txt1 , String txt2, String txt3) {
        
        text1 = txt1;
        text2 = txt2;
        text3 = txt3;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(10, 23));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel(new CornerIcon()), BorderLayout.SOUTH);
        rightPanel.setOpaque(false);
         
        add(rightPanel, BorderLayout.EAST);
        setBackground(SystemColor.control);
                
    }
    
    public StatusBar() {
        
        text1 = "";
        text2 = "";
        text3 = "";
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(10, 23));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel(new CornerIcon()), BorderLayout.SOUTH);
        rightPanel.setOpaque(false);
         
        add(rightPanel, BorderLayout.EAST);
        setBackground(SystemColor.control);
                
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int y = 0;
        g.setColor(new Color(156, 154, 140));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(196, 194, 183));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(218, 215, 201));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 217));
        g.drawLine(0, y, getWidth(), y);

        y = getHeight() - 3;
        g.setColor(new Color(233, 232, 218));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 216));
        g.drawLine(0, y, getWidth(), y);
        y = getHeight() - 1;
        g.setColor(new Color(221, 221, 220));
        g.drawLine(0, y, getWidth(), y);
        
        Font font = new Font("",Font.ROMAN_BASELINE, 12);
        g.setFont(font);
        
        
        //1st line
        g.setColor(new Color(170, 170, 170));
        g.drawLine(160, 5, 160, getHeight()-4);
        g.setColor(new Color(233, 233, 233));
        g.drawLine(161, 5, 161, getHeight()-4);
        
        //2nd line
        g.setColor(new Color(170, 170, 170));
        g.drawLine(320, 5, 320, getHeight()-4);
        g.setColor(new Color(233, 233, 233));
        g.drawLine(321, 5, 321, getHeight()-4);
        
        
        g.setColor(Color.black);
        g.drawString(text1, 20, getHeight()-6);
        g.drawString(text2, 170, getHeight()-6);
        g.drawString(text3, 330, getHeight()-6);

        
        repaint();
    }
    
    public void setText(String txt1, String txt2, String txt3) {
        new StatusBar(txt1, txt2, txt3);
    }
   
}

class CornerIcon implements Icon{
    
    private static final int WIDTH = 13;
    private static final int HEIGHT = 13;
    

    public void paintIcon(Component c, Graphics g, int x, int y) {
        
       
        g.setColor(new Color(255,255,255));
        g.drawLine(0, 12, 12, 0);
        g.drawLine(5, 12, 12, 5);
        g.drawLine(10, 12, 12, 10);

        g.setColor(new Color(172, 168, 153));
        g.drawLine(1, 12, 12, 1);
        g.drawLine(2, 12, 12, 2);
        g.drawLine(3, 12, 12, 3);

        g.drawLine(6, 12, 12, 6);
        g.drawLine(7, 12, 12, 7);
        g.drawLine(8, 12, 12, 8);

        g.drawLine(11, 12, 12, 11);
        g.drawLine(12, 12, 12, 12);
        

    }

    @Override
    public int getIconWidth() {
        return WIDTH;
    }

    @Override
    public int getIconHeight() {
        return HEIGHT;
    }
}

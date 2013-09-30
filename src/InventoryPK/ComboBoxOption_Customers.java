package InventoryPK;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

// **the work done in this class comes from the help of this site:
// http://www.orbital-computer.de/JComboBox/
public class ComboBoxOption_Customers extends PlainDocument{
	
	public static JComboBox comboBox;
	static JTextComponent editor;
	ComboBoxModel model;
	boolean selecting = false;
	static boolean hitBackspace = false;
	static boolean hitBackspaceOnSelection, hidePopupOnFocusLoss;
	
	
	 
    public ComboBoxOption_Customers(final JComboBox comboBox) {
        this.comboBox = comboBox;
        model = comboBox.getModel();
        editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
        editor.setDocument(this);
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed1(ActionEvent e) {
                if (!selecting) highlightCompletedText(0);
            }

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
        });
        editor.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (comboBox.isDisplayable()) comboBox.setPopupVisible(true);
                hitBackspace=false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_BACK_SPACE : hitBackspace=true;
                    hitBackspaceOnSelection=editor.getSelectionStart()!=editor.getSelectionEnd();
                    break;
                    case KeyEvent.VK_DELETE : e.consume();
                    comboBox.getToolkit().beep();
                    break;
                }
            }
        });
        hidePopupOnFocusLoss=System.getProperty("java.version").startsWith("1.5");
        editor.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                highlightCompletedText(0);
            }
            public void focusLost(FocusEvent e) {
                if (hidePopupOnFocusLoss) comboBox.setPopupVisible(false);
            }
        });
        Object selected = comboBox.getSelectedItem();
        if (selected!=null) setText(selected.toString());
        highlightCompletedText(0);
    }
	public void keyPressed(KeyEvent e) {
		if(comboBox.isDisplayable())
			comboBox.setPopupVisible(true);
		 hitBackspace = false;
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			 hitBackspace = true;
	}
	
	private void setText(String text) {
        try {
            super.remove(0, getLength());
            super.insertString(0, text, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e.toString());
        }
    }
	
	public void remove(int offs, int len) throws BadLocationException {
        if (selecting) return;
        if(hitBackspace) 
        	highlightCompletedText(offs-1);
        else 
        	super.remove(offs, len);		
        
    }
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		System.out.println("insert " + str + " at " + offs);
		
		if(selecting) return;
		super.insertString(offs, str, a);
		String content = getText(0, getLength());
		Object item = lookUpItem(content);
		if(item != null)
			setSelectedItem(item);
		else {
			item = comboBox.getSelectedItem();
			offs = offs-str.length();
			UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
		}
		super.remove(0, getLength());
		super.insertString(0, item.toString(), a);
		JTextComponent editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
		editor.setSelectionStart(offs+str.length());
		editor.setSelectionEnd(getLength());
		highlightCompletedText(offs + str.length());
	}
	
	private void setSelectedItem(Object item) {
		selecting = true;
		model.setSelectedItem(item);
		selecting = false;
	}
	
	private Object lookUpItem(String pattern) {
		
		for(int i = 0; i < model.getSize(); i++) {
			Object currentItem = model.getElementAt(i);
			if(startsWithIgnoreCase(currentItem.toString(), pattern)) {
				return currentItem;
			}
		}
		return null;
	}
	
    private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }
    
    private void highlightCompletedText(int start) {
    	editor.setCaretPosition(getLength());
    	editor.moveCaretPosition(start);
    }
}

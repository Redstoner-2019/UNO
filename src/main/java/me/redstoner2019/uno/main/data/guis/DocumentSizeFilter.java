package me.redstoner2019.uno.main.data.guis;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class DocumentSizeFilter extends DocumentFilter {
    int maxCharacters;
    boolean DEBUG = false;
    public DocumentSizeFilter(int maxChars){
        maxCharacters = maxChars;
    }
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
        if(DEBUG){
            System.out.println("in DocumentSizeFilter's insertString method");
        }
        String newString = "";
        for(char c : str.toCharArray()) if("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".contains(c+"")) newString+=c;
        if((fb.getDocument().getLength() + newString.length()) <= maxCharacters)
            super.insertString(fb, offs, newString, a);
        else
            Toolkit.getDefaultToolkit().beep();
    }
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException{
        if(DEBUG){
            System.out.println("in DocumentSizeFilter's replace method");
        }
        try{
            if((fb.getDocument().getLength() + str.length() - length) <= maxCharacters)
            super.replace(fb, offs, length, str, a);
        else
            Toolkit.getDefaultToolkit().beep();
        }catch (Exception ignored){}

    }

}

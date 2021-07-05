/**
 * 
 */

package ijt.table.gui.frame;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import ijt.table.gui.BaseFrame;

/**
 * A frame that displays a block of text.
 * 
 * @author dlegland
 *
 */
public class TextFrame extends BaseFrame
{
    JTextArea textArea;
    
    ArrayList<String> textLines;
    
    public TextFrame(JFrame parent, String title, String[] textLines)
    {
        super(title);
        
        this.textLines = new ArrayList<String>(textLines.length);
        for (String line : textLines)
            this.textLines.add(line);
        
        setupLayout();
        locateFromParent(parent);
    }
    
    public TextFrame(JFrame parent, String title, Collection<String> textLines)
    {
        super(title);
        
        this.textLines = new ArrayList<String>(textLines.size());
        this.textLines.addAll(textLines);
        
        setupLayout();
        locateFromParent(parent);
    }
    
    private void setupLayout()
    {
        // creates text area
        textArea = new JTextArea(15, 80);
        textArea.setEditable(false); // set textArea non-editable
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        // populates text area with text content
        for (String line : textLines)
        {
            textArea.append(line + "\n");
        }
        
        // add Text area in the middle panel
        this.jFrame.add(scroll);
        
        this.jFrame.setPreferredSize(new Dimension(300, 200));
        this.jFrame.pack();
    }
    
    private void locateFromParent(JFrame parentFrame)
    {
        Point pos = parentFrame.getLocation();
        this.jFrame.setLocation(pos.x + 30, pos.y + 30);
    }
    
    public void addText(String text)
    {
        this.textLines.add(text);
        this.textArea.append(text + "\n");
    }

}

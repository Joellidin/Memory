// Joel Lidin and Filip Ahlman, Group 39

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;


public class Card extends JButton {
    private Status status;
    private Icon icon;

    public Card(Icon icon) {
        this(icon, Status.MISSING);
    }
    
    public Card(Icon icon, Status status) {
        this.icon = icon;
        setStatus(status);
    }
    
    public void setStatus(Status status) {
        this.status = status;
        setIcon(status != Status.VISIBLE ? null : this.icon);
        setOpaque(true);
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        } catch (Exception e) {
                    e.printStackTrace();
        }
        if (status == Status.MISSING) {
            setBackground(Color.WHITE);
        }
        else if (status == Status.HIDDEN) {
            setBackground(Color.BLUE);
        }
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public Card copy() {
        return new Card(this.icon, this.status);
    }
    
    public boolean equalIcon(Card card) {
        if (this.icon == null || card == null)
            return false;
        return this.icon.equals(card.icon);
    }
    
    public enum Status {HIDDEN, VISIBLE, MISSING}
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static javax.swing.JOptionPane.*;

 class OvalIcon implements Icon {
   private int w, h;        // bredd och h�jd
   private Color color;
   private boolean filled;  // ifylld eller inte
   public OvalIcon(int width, int height, Color col, boolean fi){
     w = width; h = height; color = col; filled = fi;
   }
   public int getIconWidth() { 
     return w; 
   }
   public int getIconHeight() { 
     return h; 
   }
   public void paintIcon(Component c, Graphics g, int x, int y) {
     g.setColor(color);
     if (filled)
       g.fillOval(x, y, w, h);
     else
       g.drawOval(x, y, w, h);
   }
 }
 
public class Cardtest extends JFrame implements ActionListener {
  OvalIcon i1 = new OvalIcon(50, 50, Color.red, true);
  OvalIcon i2 = new OvalIcon(50, 50, Color.yellow, true);
  OvalIcon i4 = new OvalIcon(50, 50, Color.black, true);
  Card c1 = new Card(i1);
  Card c2 = new Card(i2, Card.Status.HIDDEN);
  Card c3;
  Card c4 = new Card(i4, Card.Status.VISIBLE);
  JPanel p = new JPanel();
  int test = 1;
  JLabel lab = new JLabel("Test 1: Nu skall det visas en vit knapp. Klicka p� knappen!", JLabel.CENTER);
  
  public Cardtest() {
    try { // F�r att f� det att fungera i Mac
       UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
     } 
     catch (Exception e) {
       e.printStackTrace();
    }
    setTitle("Cardtest");
    if (!(c1 instanceof JButton)) {
      showMessageDialog(null, "Klassen Card �r ingen JButton");
      System.exit(1);
    }
    add(lab, BorderLayout.NORTH);
    add(p, BorderLayout.CENTER);
    p.add(c1);
    c1.addActionListener(this);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    c1.setPreferredSize(new Dimension(100, 70));
    c1.setFocusPainted(false); c2.setFocusPainted(false); 
    setSize(450,150);
    setLocationRelativeTo(null);  // centrera
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {     
    if (test == 1) { 
      if (c1.getStatus() != Card.Status.MISSING) {
        showMessageDialog(this, "Fel status i konstruktorn eller i getStatus");
        System.exit(1);
      }
      c1.setStatus(Card.Status.HIDDEN);
      lab.setText("Test 2: Nu skall det visas en bl� knapp. Klicka p� knappen!");
    }
    else if (test == 2) {
      if (c1.getStatus() != Card.Status.HIDDEN) {
        showMessageDialog(this, "Fel i getStatus eller setStatus");
        System.exit(1);
      }
      c1.setStatus(Card.Status.VISIBLE);
      lab.setText("Test 3: Nu skall det visas en bl� knapp med en r�d cirkel. Klicka p� knappen!");
    }  
    else if (test == 3) {
      if (c1.getStatus() != Card.Status.VISIBLE) {
        showMessageDialog(this, "Fel i getStatus eller setStatus");
        System.exit(1);
      }
      c1.setStatus(Card.Status.HIDDEN);
      lab.setText("Test 4: Nu skall det visas en bl� knapp. Klicka p� knappen!");
    } 
    else if (test == 4) {
      if (c1.getStatus() != Card.Status.HIDDEN) {
        showMessageDialog(this, "Fel i getStatus eller setStatus");
        System.exit(1);
      }
      c1.setStatus(Card.Status.VISIBLE);
      lab.setText("Test 5: Nu skall det visas en bl� knapp med en r�d cirkel. Klicka p� knappen!");
    } 
    else if (test == 5) {
      if (c1.getStatus() != Card.Status.VISIBLE) {
        showMessageDialog(this, "Fel i getStatus eller setStatus");
        System.exit(1);
      }
      c1.setStatus(Card.Status.MISSING);
      lab.setText("Test 6: Nu skall det visas en vit knapp. Klicka p� knappen!");
    }   
    else if (test == 6) {
      if (c1.getStatus() != Card.Status.MISSING) {
        showMessageDialog(this, "Fel i getStatus eller setStatus");
        System.exit(1);
      }
      p.remove(c1);
      p.add(c2, BorderLayout.CENTER);
      c2.setPreferredSize(new Dimension(100, 70));
      c2.addActionListener(this);
      lab.setText("Test 7: Nu skall det visas en bl� knapp. Klicka p� knappen!");
    }
    else if (test == 7) {
      if (c2.getStatus() != Card.Status.HIDDEN) {
        showMessageDialog(this, "Fel status i konstruktorn eller i getStatus");
        System.exit(1);
      }
      p.remove(c2);
      c3 = c2.copy();
      p.add(c3, BorderLayout.CENTER);
      c3.setPreferredSize(new Dimension(100, 70));
      c3.addActionListener(this);
      if (!c3.equalIcon(c2) || c3 == c2) {
        showMessageDialog(this, "Fel i copy eller sammaBild");
        System.exit(1);
      }
      c3.setStatus(Card.Status.VISIBLE);
      lab.setText("Test 8: Nu skall det visas en bl� knapp med en gul cirkel. Klicka p� knappen!");
    }
    else if (test == 8) {
      if (c3.getStatus() != Card.Status.VISIBLE) {
        showMessageDialog(this, "Fel i getStatus eller setStatus");
        System.exit(1);
      }
      p.remove(c3);
      p.add(c4, BorderLayout.CENTER);
      c4.setPreferredSize(new Dimension(100, 70));
      c4.setBackground(Color.yellow); 
      c4.addActionListener(this);
      lab.setText("Test 9: Nu skall det visas en gul knapp med en svart cirkel. Klicka p� knappen!");
    }
    else if (test == 9) {
      if (c4.getStatus() != Card.Status.VISIBLE) {
        showMessageDialog(this, "Fel status i konstruktorn eller i getStatus");
        System.exit(1);
      }
      showMessageDialog(this, "Grattis! Inga fel funna!");
      System.exit(0);
    }
    test++;
  }

  public static void main(String[] arg) {
    new Cardtest();
  }
}

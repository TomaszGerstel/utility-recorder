import javax.swing.*;
import java.awt.*;

public class Panel {

    public Panel() {

        JFrame jfrm = new JFrame("Utility Recorder");
        jfrm.setLayout(new FlowLayout());
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setSize(400, 200);

        // Tworzy panele
        JTabbedPane jtp = new JTabbedPane();
        jtp.addTab("Miasta", new CitiesPanel());
        jtp.addTab("Kolory", new ColorsPanel());
        jtp.addTab("Smaki", new FlavorsPanel());
        jfrm.add(jtp);

        jfrm.setVisible(true);
    }

    public static void main(String[] args) {

         SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        new Panel();
                    }
                }
        );
    }
}

class CitiesPanel extends JPanel {

    public CitiesPanel() {
        JButton b1 = new JButton("Nowy Jork");
        add(b1);
        JButton b2 = new JButton("Londyn");
        add(b2);
        JButton b3 = new JButton("Hongkong");
        add(b3);
        JButton b4 = new JButton("Tokio");
        add(b4);
    }
}

class ColorsPanel extends JPanel {

    public ColorsPanel() {
        JCheckBox cb1 = new JCheckBox("Czerwony");
        add(cb1);
        JCheckBox cb2 = new JCheckBox("Zielony");
        add(cb2);
        JCheckBox cb3 = new JCheckBox("Niebieski");
        add(cb3);
    }
}

class FlavorsPanel extends JPanel {

    public FlavorsPanel() {
        JComboBox<String> jcb = new JComboBox<String>();
        jcb.addItem("Waniliowy");
        jcb.addItem("Czekoladowy");
        jcb.addItem("Truskawkowy");
        add(jcb);
    }
}
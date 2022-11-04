import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;

class AboutDialog extends JDialog {

    JTextArea textA;
    JPanel panel;

    AboutDialog(String title)  {

        setTitle(title);
        setVisible(true);
        setSize(400, 500);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setAutoRequestFocus(true);
        setResizable(false);
        panel = new JPanel();
        textA = new JTextArea();
        add(panel);
        panel.setSize(360, 450);
        panel.add(textA);

        generateTextArea();

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });
    }

    private void generateTextArea() {

        textA.setMargin(new Insets(5,5,5,5));
        textA.setEditable(false);
        textA.append("This tool allows you to monitor the consumption of utilities, " +
                "like for e.g. water or electricity consumption.\n\n" +
                "It operates on the data contained in file 'data_utility.txt'.\n" +
                "If the file does not exist, it will be generated.\n" +
                "After creating a new utility, you can enter data in the form: date and value. " +
                "New data can be entered from within the tool, but can also be modified in a file.\n\n" +
                "New utility in file starts with '*' and name of utility. after that there can be no empty line, "+
                "just records starting with '>'. A record consists of '>', date in the format dd-mm-yyyy ':' "+
                "and a number (can be a float). E.g: >31-01-2022: 123.5. Before next utility have to be empty line.\n\n"+
                "The tool calculates consumption after each record, total consumption, average consumption "+
                "and generates diagram. The tool sorts the dates, checks if they are in the correct format "+
                "and if the record has a greater value than the previous one. "+
                "In case of inconsistencies, the record is not saved.\n\n"+
                "The author of the tool:\ngerstel.tomasz@gmail.com\nhttps://github.com/TomaszGerstel");

        textA.setSize(360, 450);
        textA.setLineWrap(true);
        textA.setWrapStyleWord(true);
    }
}

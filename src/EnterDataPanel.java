import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class EnterDataPanel extends JPanel implements ActionListener {

    JTextField addData;
    JButton button;
    JLabel label;
    JToolBar toolBar;
    JPanel panel;

    public EnterDataPanel() {
        setLayout(new BorderLayout());
        addData = new JTextField(25);
        button = new JButton("Save");
        toolBar = new JToolBar("Info");
        label = new JLabel("Enter name of new utility");
        panel = new JPanel(new FlowLayout(1, 10, 50));
        button.addActionListener(this);
        addData.addActionListener(this);
        toolBar.add(label);
        panel.add(addData);
        panel.add(button);
        add(panel, BorderLayout.NORTH);
        add(toolBar, BorderLayout.SOUTH);
        button.setToolTipText("save name of new utility");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();
        if (comStr.equals("Save")) {
            if (addData.getText().length() > 1) {
                File data = new File("data_utility.txt");
                try {
                    writeNewUtility(data);
                    label.setText("New utility saved. Reload data and go to loaded utilities");
                    addData.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else label.setText("name is too short");
        }
    }

    private void writeNewUtility(File data) throws IOException {
        try(FileWriter fw = new FileWriter(data, true)) {
            fw.write("\n\n");
            fw.write("*");
            fw.write(addData.getText());
        }
    }
}

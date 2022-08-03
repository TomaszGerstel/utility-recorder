import javax.swing.*;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.*;
import javax.swing.text.TableView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Panel implements ActionListener {

    JMenuBar jmb;
//    JToolBar jtb;
//    JPopupMenu jpu;

    public Panel() throws IOException, ParseException {

        JFrame jfrm = new JFrame("Utility Recorder");
//        jfrm.setLayout(new FlowLayout());
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setSize(600, 400);

        jmb = new JMenuBar();

        makeFileMenu();
        makeOptionsMenu();
        makeHelpMenu();

        // Tworzy panele
        JTabbedPane jtp = new JTabbedPane();
        jtp.addTab("Data", new DataPanel());
        jtp.addTab("Diagram", new DiagramPanel());
        jtp.addTab("Add entry", new EnterDataPanel());
        jfrm.add(jtp);

        jfrm.setJMenuBar(jmb);

        jfrm.setVisible(true);
    }

    public static void main(String[] args) {

         SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        try {
                            new Panel();
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if(comStr.equals("Exit")) System.exit(0);
        // W pozostałych przypadkach wyświetlana jest nazwa opcji
//        jlab.setText("Wybrano " + comStr);
    }

    void makeFileMenu() {
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem jmiOpen = new JMenuItem("Open",
                KeyEvent.VK_O);
        jmiOpen.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiClose = new JMenuItem("Close",
                KeyEvent.VK_C);
        jmiClose.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_C,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiSave = new JMenuItem("Save",
                KeyEvent.VK_S);
        jmiSave.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiExit = new JMenuItem("Exit",
                KeyEvent.VK_X);
        jmiExit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_X,
                        InputEvent.CTRL_DOWN_MASK));

        jmFile.add(jmiOpen);
        jmFile.add(jmiClose);
        jmFile.add(jmiSave);
        jmFile.addSeparator();
        jmFile.add(jmiExit);
        jmb.add(jmFile);

        jmiOpen.addActionListener(this);
        jmiClose.addActionListener(this);
        jmiSave.addActionListener(this);
        jmiExit.addActionListener(this);
    }

    // Utworzenie menu Opcje
    void makeOptionsMenu() {
        JMenu jmOptions = new JMenu("Options");

        // Utworzenie podmenu Kolory
        JMenu jmColors = new JMenu("Kolory ");

        // Tworzy elementy menu z polami wyboru i dodaje je do podmenu Kolory, dzięki
        // czemu użytkownik będzie mógł wybrać więcej niż jeden kolor
        JCheckBoxMenuItem jmiRed = new JCheckBoxMenuItem("Czerwony");
        JCheckBoxMenuItem jmiGreen = new JCheckBoxMenuItem("Zielony");
        JCheckBoxMenuItem jmiBlue = new JCheckBoxMenuItem("Niebieski");

        // Dodaje elementy do podmenu Kolory
        jmColors.add(jmiRed);
        jmColors.add(jmiGreen);
        jmColors.add(jmiBlue);
        jmOptions.add(jmColors);

        // Utworzenie podmenu Priorytet
        JMenu jmPriority = new JMenu("Priorytet");

        // Tworzy elementy menu z przyciskami opcji i dodaje je do menu Priorytet;
        // w ten sposób możemy pokazać aktualnie wybrany priorytet,
        // a jednocześnie mamy pewność, że w dowolnej chwili będzie wybrany
        // tylko jeden priorytet; warto zwrócić uwagę, że początkowo zostanie
        // zaznaczona opcja „Wysoki”
        JRadioButtonMenuItem jmiHigh =
                new JRadioButtonMenuItem("Wysoki", true);
        JRadioButtonMenuItem jmiLow =
                new JRadioButtonMenuItem("Niski");

        // Dodaje elementy do podmenu Priorytet
        jmPriority.add(jmiHigh);
        jmPriority.add(jmiLow);
        jmOptions.add(jmPriority);

        // Tworzy grupę przycisków dla elementów menu zawierających
        // przyciski opcji
        ButtonGroup bg = new ButtonGroup();
        bg.add(jmiHigh);
        bg.add(jmiLow);

        // Tworzy opcję Resetuj
        JMenuItem jmiReset = new JMenuItem("Resetuj");
        jmOptions.addSeparator();
        jmOptions.add(jmiReset);

        // Wreszcie całe menu Opcje jest dodawane do paska menu
        jmb.add(jmOptions);

        // Określa obiekty nasłuchujące dla wszystkich elementów
        // menu Opcje, z wyjątkiem elementów w podmenu
        // Uruchamianie
        jmiRed.addActionListener(this);
        jmiGreen.addActionListener(this);
        jmiBlue.addActionListener(this);
        jmiHigh.addActionListener(this);
        jmiLow.addActionListener(this);
        jmiReset.addActionListener(this);
    }

    // Tworzy menu Pomoc
    void makeHelpMenu() {
        JMenu jmHelp = new JMenu("Help");

        // Dodaje ikonę do elementu menu
//        ImageIcon icon = new ImageIcon("AboutIcon.gif");

        JMenuItem jmiAbout = new JMenuItem("About...");
//        JMenuItem jmiAbout = new JMenuItem("O programie", icon);
        jmiAbout.setToolTipText("About this tool...");
        jmHelp.add(jmiAbout);
        jmb.add(jmHelp);

        // Określa obiekt nasłuchujący
        jmiAbout.addActionListener(this);
    }

}

class DataPanel extends JPanel  {

    ArrayList<RecordModel> records;
    String line = "";
    File data;
    String[] headers = {"Date", "Value", "Consumption"};
    ArrayList<String> headers2 = new ArrayList<String>();


    public DataPanel() throws IOException, ParseException {
//        text = new StringBuilder();
        this.setLayout(new BorderLayout());
        records = new ArrayList<>();
        data = new File("data_utility.txt");
        loadData();
    }

    public void loadData() throws IOException, ParseException {
//        enters = new String[][];
        System.out.print("load data");
        if(data.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(data));
            while ((line = br.readLine()) != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//                Pattern datePattern = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
//                Matcher dateMatcher = datePattern.matcher(line);
//                String dateFromLine = "";
////                String dateFromLine = datePattern.matcher(line).group(1);
//                if(dateMatcher.find()) dateFromLine = dateMatcher.group(0);
//                System.out.print(dateFromLine);
                Date date = formatter.parse(line);
                RecordModel rec = new RecordModel(date, 28.0f);
                records.add(rec);
//                text.append(line);
            }
            br.close();
        }
//        else text = new StringBuilder("File with data doesn't exist." +
//                "Set up a new utility recorder or load data from a file from an external location.");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Date");
        tableModel.addColumn("State");
        tableModel.addColumn("Consumption");
        tableModel.addRow(headers);
        JTable table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(600, 100));
        table.setFillsViewportHeight(true);
        TableColumn col1 = new TableColumn(0, 21);


        table.setEnabled(false);
        table.setRowSelectionAllowed(true);


//        table.setBounds(30, 40, 200, 600);
//        table.setSize(600,300);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        float consumption = 0;
        float lastValue = 0;
        for(RecordModel rm : records) {
            consumption = rm.getValue() - lastValue;
            lastValue = rm.getValue();
            tableModel.addRow(new Object[]{formatter.format(rm.getDate()), rm.getValue(), consumption});
        }

//        JTextArea textArea = new JTextArea(text.toString(), 25, 35);
//        add(textArea);

        add(new ScrollPane().add(table));

    }

    public class SimpleHeaderRenderer extends JLabel implements TableCellRenderer {

        public SimpleHeaderRenderer() {
            setFont(new Font("Consolas", Font.BOLD, 14));
            setForeground(Color.BLUE);
            setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

}



class DiagramPanel extends JPanel {

    public DiagramPanel() {
        JCheckBox cb1 = new JCheckBox("Czerwony");
        add(cb1);
        JCheckBox cb2 = new JCheckBox("Zielony");
        add(cb2);
        JCheckBox cb3 = new JCheckBox("Niebieski");
        add(cb3);
    }
}

class EnterDataPanel extends JPanel implements ActionListener {

    JTextField addData;

    public EnterDataPanel() {
        addData = new JTextField(25);
        JButton button = new JButton("Save");
        button.addActionListener(this);
        addData.addActionListener(this);

        add(addData);
        add(button);


    }

    @Override
    public void actionPerformed(ActionEvent ae) {
//        file.createNewFile()
        String comStr = ae.getActionCommand();
        if(comStr.equals("Save")) {
            File data = new File("data_utility.txt");
            try {
                FileWriter fr = new FileWriter(data, true);
                fr.write("\r");

                fr.write(addData.getText());
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.table.*;
import javax.swing.text.TableView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panel implements ActionListener {

    JMenuBar jmb;
    JFrame jfrm;
    JTabbedPane jtp;
    JTabbedPane subPane01;
    JToolBar jtb;
    JButton button;
//    JPopupMenu jpu;

    public Panel() throws IOException, ParseException {

        jfrm = new JFrame("Utility Recorder");
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setSize(600, 400);
        jmb = new JMenuBar();
        jtp = new JTabbedPane();
        subPane01 = new JTabbedPane();
        subPane01.setName("Loaded utilities");
        jtb = new JToolBar("Actions");
        button = new JButton("Reload");
        button.addActionListener(this);

        makeFileMenu();
        makeHelpMenu();
        addPanelsWithUtilities();

        jtp.add(subPane01,0);
        jtp.addTab("Add new utility", new EnterDataPanel());
        jtb.setFloatable(false);
        jfrm.add(jtp);
        jfrm.setJMenuBar(jmb);
        jfrm.setVisible(true);
        jtb.add(button);
        jfrm.add(jtb, BorderLayout.PAGE_START);
    }

    private void addPanelsWithUtilities() throws IOException {
        File data = new File("data_utility.txt");
        ArrayList<RecordsOfUtilityModel> records = loadData(data);
        for (RecordsOfUtilityModel rec : records) {
            subPane01.addTab(rec.getName(), new DataPanel(rec, rec.getName()));
        }
    }

    public ArrayList<RecordsOfUtilityModel> loadData(File data) throws IOException {
        ArrayList<RecordsOfUtilityModel> utilityRecords = new ArrayList<>();
        if (data.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(data));
            String line;
            while ((line = br.readLine()) != null) {
                String encLine = new String(line.getBytes(StandardCharsets.UTF_8));
                if (line.startsWith("*")) {
                    utilityRecords.add(makeUtilityRecords(encLine, br));
                }
            }
            br.close();
        }
        return utilityRecords;
    }

    public RecordsOfUtilityModel makeUtilityRecords(String encLine, BufferedReader br) throws IOException {
        String line;
        RecordModel record;
        RecordsOfUtilityModel utilityRecord = new RecordsOfUtilityModel(encLine);
        while ((line = br.readLine()) != null && line.startsWith(">")) {
            record = parseToRecordModel(line);
            utilityRecord.addRecord(record);
        }
        return utilityRecord;
    }

    public RecordModel parseToRecordModel(String line) {
        Pattern utilityValuePatternFromFile = Pattern.compile(":\\s*\\d+\\S*\\s*$");
        Matcher utilityValueMatcher = utilityValuePatternFromFile.matcher(line);
        utilityValueMatcher.find();
        return new RecordModel(parseDate(line), getCleanValue(utilityValueMatcher));
    }

    public Date parseDate (String line) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        ParsePosition pp = new ParsePosition(1);
        return formatter.parse(line, pp);
    }

    public Float getCleanValue (Matcher valueMatcherFromFile) {
        Pattern cleanValue = Pattern.compile("\\d+\\.*\\d*");
        Matcher cleanValueMatch = cleanValue.matcher(valueMatcherFromFile.group());
        cleanValueMatch.find();
        String valueFromMatch = cleanValueMatch.group();
        return Float.valueOf(valueFromMatch);
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
        if (comStr.equals("Exit")) System.exit(0);
        if (comStr.equals("Reload")) reloadTabsAndData();
        if (comStr.equals("Open")) {
            Runtime rt = Runtime.getRuntime();
            String file;
            file = "data_utility.txt";
            try {
                Process p = rt.exec("notepad " + file);
            } catch (IOException ex) {
//                Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void reloadTabsAndData () {
            jtp.removeAll();
            subPane01.removeAll();
            jtp.add(subPane01, 0);
            jtp.addTab("Add new utility", new EnterDataPanel());
            try {
                addPanelsWithUtilities();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    void makeFileMenu() {
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem jmiOpen = new JMenuItem("Open",
                KeyEvent.VK_O);
        jmiOpen.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiReload = new JMenuItem("Reload",
                KeyEvent.VK_R);
        jmiReload.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiExit = new JMenuItem("Exit",
                KeyEvent.VK_X);
        jmiExit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_X,
                        InputEvent.CTRL_DOWN_MASK));

        jmFile.add(jmiOpen);
        jmFile.add(jmiReload);
        jmFile.addSeparator();
        jmFile.add(jmiExit);
        jmb.add(jmFile);

        jmiOpen.addActionListener(this);
        jmiReload.addActionListener(this);
        jmiExit.addActionListener(this);
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

class DataPanel extends JPanel implements ActionListener, FocusListener {

    DefaultTableModel tableModel;
    JTable table;
    RecordsOfUtilityModel records;
    ScrollPane pane;
    JToolBar jtb;
    String name;
    JLabel label;
    JButton button;

    public DataPanel(RecordsOfUtilityModel records, String name) {
        this.setLayout(new BorderLayout());
        this.records = records;
        this.name = name;
        buildTable();
        loadDataToTable();
    }

    private void buildTable() {

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        pane = new ScrollPane();
        jtb = new JToolBar("Actions");
        label = new JLabel();

        tableModel.addColumn("No.");
        tableModel.addColumn("Date");
        tableModel.addColumn("Value");
        tableModel.addColumn("Consumption");

        table.setPreferredScrollableViewportSize(new Dimension(600, 100));
        table.setFillsViewportHeight(true);

        button = new JButton("Save Changes");
        button.addActionListener(this);
        button.addFocusListener(this);

        TableColumn column = null;
        for (int i = 0; i < 4; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(30);
                column.setMaxWidth(30);
            } else {
                column.setPreferredWidth(100);
            }
        }
        table.setRowSelectionAllowed(true);

        add(table.getTableHeader(), BorderLayout.PAGE_START);
        pane.add(table, BorderLayout.PAGE_START);
        add(pane);
        jtb.setLayout(new BorderLayout());
        jtb.add(button, BorderLayout.WEST);
        jtb.add(label, BorderLayout.EAST);
        add(jtb, BorderLayout.SOUTH);
    }

    private void loadDataToTable() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        float consumption;
        float lastValue = 0;
        boolean isFirstRecord = true;
        int count = 1;
        for (RecordModel rm : records.getRecords()) {
            if (isFirstRecord) consumption = 0;
            else consumption = rm.getValue() - lastValue;
            lastValue = rm.getValue();
            tableModel.addRow(new Object[]{count + ".", formatter.format(rm.getDate()), rm.getValue(),
                    String.format("%.01f", consumption)});
            count += 1;
            isFirstRecord = false;
        }
    }

    public void saveData(String name) throws IOException {
        ArrayList<String> numdata = new ArrayList<>();
        String line;
        String dateFromTable;
        String dataLineFromTable;
        for (int count = 0; count < tableModel.getRowCount(); count++) {
            dateFromTable = tableModel.getValueAt(count, 1).toString();
            dataLineFromTable = ">" + dateFromTable + ": " + tableModel.getValueAt(count, 2).toString();
            numdata.add(dataLineFromTable);
        }

        File destination = new File("data_utility.txt");
        File newFile = new File("temp.txt");

        BufferedReader br = new BufferedReader(new FileReader(destination));
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));

        while ((line = br.readLine()) != null) {
            String encLine = new String(line.getBytes(StandardCharsets.UTF_8));
            if (encLine.startsWith(name)) {
                bw.write(name);
                bw.write('\n');
                for (String s : numdata) {
                    bw.write(s);
                    bw.write('\n');
                }
                while (br.readLine().startsWith(">")) ;
            } else {
                bw.write(encLine);
            }
            bw.write('\n');
        }
        br.close();
        bw.close();
        destination.delete();
        newFile.renameTo(destination);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();
        if (comStr.equals("Save Changes")) {
            try {
                saveData(name);
                label.setText("Data saved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        label.setText("");
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
        if (comStr.equals("Save")) {
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

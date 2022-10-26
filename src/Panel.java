import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panel implements ActionListener {

    JMenuBar jmb;
    JFrame jfrm;
    JTabbedPane jtp;
    JTabbedPane subPane01;
    JToolBar jtb;
    JButton button;
    JButton button2;

    public Panel() throws IOException, ParseException {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jfrm = new JFrame("Utility Recorder");
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setSize(600, 400);
        jfrm.setLocation((screenSize.width / 2) - (600 / 2), (screenSize.height / 2) - (400 / 2));
        jmb = new JMenuBar();
        jtp = new JTabbedPane();
        subPane01 = new JTabbedPane();
        subPane01.setName("Loaded utilities");
        jtb = new JToolBar("Actions");
        button = new JButton("Reload data");
        button2 = new JButton("Show file with data");
        button.addActionListener(this);
        button2.addActionListener(this);

        makeFileMenu();
        makeHelpMenu();
        addPanelsWithUtilities();

        jtp.add(subPane01, 0);
        jtp.addTab("Add new utility", new EnterDataPanel());
        jtb.setFloatable(false);
        jfrm.add(jtp);
        jfrm.setJMenuBar(jmb);
        jfrm.setVisible(true);
        jtb.add(button);
        jtb.add(button2);
        jfrm.add(jtb, BorderLayout.PAGE_START);
    }

    private void addPanelsWithUtilities() throws IOException {
        File data = new File("data_utility.txt");
        if (!data.exists()) data.createNewFile(); // metoda z info w pliku
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
            if (record != null) utilityRecord.addRecord(record);
        }
        filterWrongRecords(utilityRecord);
        return utilityRecord;
    }

    public RecordsOfUtilityModel filterWrongRecords(RecordsOfUtilityModel records) {
        Iterator<RecordModel> iterator = records.getRecords().iterator();
        Float val = 0.0F;
        RecordModel rec;
        while (iterator.hasNext()) {
            rec = iterator.next();
            if (rec.getValue() < val) iterator.remove();
            else val = rec.getValue();
        }
        return records;
    }

    public RecordModel parseToRecordModel(String line) {
        Pattern utilityValuePatternFromFile = Pattern.compile(":\\s*\\d+\\S*\\s*$");
        Matcher utilityValueMatcher = utilityValuePatternFromFile.matcher(line);
        Date date = parseDate(line);
        if (!utilityValueMatcher.find() || date == null) return null;
        return new RecordModel(date, getCleanValue(utilityValueMatcher));
    }

    public Date parseDate(String line) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        ParsePosition pp = new ParsePosition(1);
        return formatter.parse(line, pp);
    }

    public Float getCleanValue(Matcher valueMatcherFromFile) {
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
        if (comStr.equals("Reload data")) reloadTabsAndData();
        if (comStr.equals("Show file with data")) {
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

    public void reloadTabsAndData() {
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

        JMenuItem jmiOpen = new JMenuItem("Show file with data",
                KeyEvent.VK_O);
        jmiOpen.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiReload = new JMenuItem("Reload data",
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

    void makeHelpMenu() {
        JMenu jmHelp = new JMenu("Help");
        JMenuItem jmiAbout = new JMenuItem("About...");

        jmiAbout.setToolTipText("About this tool...");
        jmHelp.add(jmiAbout);
        jmb.add(jmHelp);

        jmiAbout.addActionListener(this);
    }
}

class DataPanel extends JPanel implements ActionListener, FocusListener, TableModelListener {

    DefaultTableModel tableModel;
    JTable table;
    RecordsOfUtilityModel records;
    ScrollPane pane;
    JToolBar jtb;
    String name;
    JLabel label;
    JButton saveButton;
    JButton deleteButton;
    String inputDefaultInfo = "You can adding, deleting and changing the data in table";
    JDialog dialog;
    File currentFile;
    File newFile;
    BufferedReader br;
    BufferedWriter bw;

    public DataPanel(RecordsOfUtilityModel records, String name) {
        this.setLayout(new BorderLayout());
        this.records = records;
        this.name = name;
        buildPanelWithTable();
        loadDataToTable();
    }

    private void buildPanelWithTable() {

        dialog = new JDialog();
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

        saveButton = new JButton("Save Changes");
        saveButton.setName("Save");
        deleteButton = new JButton("Delete Utility");
        deleteButton.setName("Delete");
        saveButton.addActionListener(this);
        deleteButton.addActionListener(this);
        saveButton.addFocusListener(this);
        deleteButton.addFocusListener(this);

        setTableSizes();
        table.setCellSelectionEnabled(true);

        add(table.getTableHeader(), BorderLayout.PAGE_START);
        pane.add(table, BorderLayout.PAGE_START);
        add(pane);
        jtb.setLayout(new BorderLayout());
        JPanel buttonpanel = new JPanel();
        jtb.add(buttonpanel, BorderLayout.WEST);
        buttonpanel.add(saveButton);
        buttonpanel.add(deleteButton);
        deleteButton.setSize(saveButton.getWidth(), saveButton.getHeight());
        jtb.add(label, BorderLayout.EAST);
        label.setText(inputDefaultInfo);
        add(jtb, BorderLayout.SOUTH);
    }

    private void setTableSizes() {

        TableColumn column;
        for (int i = 0; i < 4; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(30);
                column.setMaxWidth(30);
            } else {
                column.setPreferredWidth(100);
            }
        }
    }

    private void loadDataToTable() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        float consumption;
        float lastValue = 0;
        int count = 1;
        for (RecordModel rm : records.getRecords()) {
            if (count == 1) consumption = 0;
            else consumption = rm.getValue() - lastValue;
            lastValue = rm.getValue();
            tableModel.addRow(new Object[]{count + ".", formatter.format(rm.getDate()), String.format("%.01f", rm.getValue()),
                    String.format("%.01f", consumption)});
            count += 1;
        }
        addEmptyRow();
        tableModel.addTableModelListener(this);
    }

    private void addEmptyRow() {
        tableModel.addRow(new Object[]{"", "", "", ""});
    }

    public void saveData() throws IOException {

        ArrayList<String> tableData = getDataFromTable();
        getReaderAndWriter();
        copyAndRewriteData(br, bw, tableData);
        currentFile.delete();
        newFile.renameTo(currentFile);
    }

    private void getReaderAndWriter() throws IOException {

        currentFile = new File("data_utility.txt");
        newFile = new File("temp_data.txt");
        br = new BufferedReader(new FileReader(currentFile));
        bw = new BufferedWriter(new FileWriter(newFile));
    }

    private void deleteData() throws IOException {

        getReaderAndWriter();
        copyAndRewriteData(br, bw, null);
        currentFile.delete();
        newFile.renameTo(currentFile);
    }

    private void copyAndRewriteData(BufferedReader br, BufferedWriter bw,
                                    ArrayList<String> tableData) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            String encLine = new String(line.getBytes(StandardCharsets.UTF_8));
            if (encLine.startsWith(name)) {
                if (tableData != null) rewriteCurrentUtility(bw, tableData);
//                else br.readLine();
                // skip old data in current utility
                while ((line = br.readLine()) != null && line.startsWith(">")) ;
            } else {
                bw.write(encLine);
            }
            bw.write('\n');
        }
        br.close();
        bw.close();
    }

    private void rewriteCurrentUtility(BufferedWriter bw, ArrayList<String> tableData) throws IOException {
        bw.write(name);
        bw.write("\n");
        for (String s : tableData) {
            bw.write(s);
            bw.write('\n');
        }
    }

    private ArrayList<String> getDataFromTable() {

        ArrayList<String> numdata = new ArrayList<>();


        String dateFromTable;
        String dataLineFromTable;
        String valueFromTable;
        for (int count = 0; count < tableModel.getRowCount(); count++) {
            dateFromTable = tableModel.getValueAt(count, 1).toString();
            valueFromTable = tableModel.getValueAt(count, 2).toString();
            dataLineFromTable = ">" + dateFromTable + ": " + valueFromTable;
            if (dateHasCorrectFormat(dateFromTable) && isCorrectValue(valueFromTable)) numdata.add(dataLineFromTable);
        }
        return numdata;
    }

    private boolean dateHasCorrectFormat(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            formatter.parse(date);
        } catch (ParseException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isCorrectValue (String val) {
        Pattern cleanValue = Pattern.compile("\\d+\\.*\\d*");
        Matcher cleanValueMatch = cleanValue.matcher(val);
        return cleanValueMatch.find();
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();
        if (comStr.equals("Save Changes")) {
            try {
                saveData();
                label.setText("Data saved. Reload data.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (comStr.equals("Delete Utility")) {
            showDialogToDelete();
        }
        if (comStr.equals("No")) dialog.dispose();
        if (comStr.equals("Yes")) {
            try {
                deleteData();
                dialog.dispose();
                label.setText("Data deleted. Reload data.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ;
    }

    private void showDialogToDelete() {
        JPanel panel = new JPanel();
        JButton noButton = new JButton("No");
        JButton yesButton = new JButton("Yes");
        noButton.addActionListener(this);
        yesButton.addActionListener(this);
        dialog.setTitle("Delete Utility");
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.setSize(250, 110);
        dialog.setLocationRelativeTo(this);
        dialog.setAutoRequestFocus(true);
        dialog.add(panel);
        panel.add(new Label(
                "Do you really want to delete this utility?"));
        panel.add(yesButton);
        panel.add(noButton);
        dialog.setVisible(true);
        dialog.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dialog.dispose();
            }
        });
    }

    @Override
    public void focusGained(FocusEvent e) {
        String com = e.getComponent().getName();
        if (com.equals("Save")) label.setText("saving...");
    }

    @Override
    public void focusLost(FocusEvent e) {
        String com = e.getComponent().getName();
        if (com.equals("Save")) label.setText(inputDefaultInfo);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int lastRow = tableModel.getRowCount() - 1;
        if (!tableModel.getValueAt(lastRow, 2).equals("")) {
            addEmptyRow();
        }

    }
}

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
                    FileWriter fw = new FileWriter(data, true);
                    fw.write("\n\n");
                    fw.write("*");
                    fw.write(addData.getText());
                    fw.close();
                    label.setText("New utility saved. Reload data and go to loaded utilities");
                    addData.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else label.setText("name is too short");
        }
    }

}

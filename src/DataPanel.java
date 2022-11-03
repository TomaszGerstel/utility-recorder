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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DataPanel extends JPanel implements ActionListener, FocusListener, TableModelListener {

    DefaultTableModel tableModel;
    JTable table;
    RecordsOfUtilityModel records;
    JPanel pane;
    JPanel buttonPanel;
    JToolBar jtb;
    String name;
    JLabel label;
    JLabel calcLabel;
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
        pane = new JPanel();
        buttonPanel = new JPanel();
        jtb = new JToolBar("Actions");
        label = new JLabel();
        calcLabel = new JLabel();

        tableModel.addColumn("No.");
        tableModel.addColumn("Date (dd-mm-yyyy)");
        tableModel.addColumn("Value");
        tableModel.addColumn("Consumption");

//        table.setPreferredScrollableViewportSize(new Dimension(600, 300));
        table.setFillsViewportHeight(true);
        table.setCellSelectionEnabled(true);

        saveButton = new JButton("Save Changes");
        saveButton.setName("Save");
        deleteButton = new JButton("Delete Utility");
        deleteButton.setName("Delete");
        saveButton.addActionListener(this);
        deleteButton.addActionListener(this);
        saveButton.addFocusListener(this);
        deleteButton.addFocusListener(this);

        setLayout(new BorderLayout());

        pane.setLayout(new BorderLayout());
        pane.add(table, BorderLayout.NORTH);

        pane.add(calcLabel, BorderLayout.SOUTH);
        jtb.setLayout(new BorderLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        jtb.add(buttonPanel, BorderLayout.WEST);
        jtb.add(label, BorderLayout.EAST);
        label.setText(inputDefaultInfo);

        setTableSizes();

        add(table.getTableHeader(), BorderLayout.PAGE_START);
        add(new JScrollPane(pane), BorderLayout.CENTER);
        add(jtb, BorderLayout.PAGE_END);
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
        LinkedHashMap<Date, Float> dateConsumption = new LinkedHashMap<>();

        float consumption;
        float lastValue = 0;
        int count = 0;
        float total = 0;
        for (RecordModel rm : records.getRecords()) {
            if (count == 0) consumption = 0;
            else {
                consumption = rm.getValue() - lastValue;
                dateConsumption.put(rm.getDate(),  Float.parseFloat(String.format("%.01f", consumption).replace(",",".")));
                total += consumption;
            }
            lastValue = rm.getValue();
            tableModel.addRow(new Object[]{count + ".", formatter.format(rm.getDate()), String.format("%.01f", rm.getValue()),
                    String.format("%.01f", consumption)});
            count += 1;
        }
        addEmptyRow();
        tableModel.addTableModelListener(this);
        calcLabel.setText(" total consumption: " + String.format("%.01f", total) + "   average: "
                +  String.format("%.01f", (total/(count-1))));
        if(dateConsumption.size() > 1) pane.add(new DiagramPanel(dateConsumption), BorderLayout.CENTER);
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
        int emptyLinesCounter = 0;
        while ((line = br.readLine()) != null) {
            if(line.isEmpty()) emptyLinesCounter++;
            else emptyLinesCounter = 0;
            if(emptyLinesCounter > 1) continue;
            String encLine = new String(line.getBytes(StandardCharsets.UTF_8));
            if (encLine.startsWith(name)) {
                if (tableData != null) rewriteCurrentUtility(bw, tableData);
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
            dataLineFromTable = ">" + dateFromTable + ": " + valueFromTable.replace(",", ".");
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

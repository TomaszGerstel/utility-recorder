import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPanel implements ActionListener {

    JMenuBar jmb;
    JFrame jfrm;
    JTabbedPane jtp;
    JTabbedPane subPane01;
    JToolBar jtb;
    JButton button;
    JButton button2;
    File data;

    public MainPanel() throws IOException {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jfrm = new JFrame("Utility Recorder");
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setSize(800, 650);
        jfrm.setMinimumSize(new Dimension(800, 650));
        jfrm.setLocation((screenSize.width / 2) - (800 / 2), (screenSize.height / 2) - (650 / 2));
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
        data = new File("data_utility.txt");
        if (!data.exists()) {
            data.createNewFile();
            loadDefaultDataToFile();
        }
        ArrayList<RecordsOfUtilityModel> records = loadData();
        for (RecordsOfUtilityModel rec : records) {
            subPane01.addTab(rec.getName().replace("*", ""), new DataPanel(rec, rec.getName()));
        }
    }

    private void loadDefaultDataToFile() throws IOException {
//        BufferedWriter bw = new BufferedWriter(new Fi)
        FileWriter fw = new FileWriter(data);
        fw.write("// this is the data file of tool 'Utility Recorder'\n");
        fw.write("// this tool allows you to monitor the consumption of utilities\n");
        fw.write("// it operates on the data contained in this file\n");
        fw.write("// data can be entered from within the tool, but can also be modified in a file\n");
        fw.write("// the recording format is:\n");
        fw.write("// new utility have to begin with '*' and name of utility\n");
        fw.write("// after that there can be no empty line, just records starting with '>'\n");
        fw.write("// the entry consists of '>' date in the format dd-mm-yyyy ':' and a number (can be a float)\n");
        fw.write("// e.g: >31-01-2022: 123.5,\n");
        fw.write("// before next utility have to be empty line\n");
        fw.close();
    }

    private ArrayList<RecordsOfUtilityModel> loadData() throws IOException {
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

    public void filterWrongRecords(RecordsOfUtilityModel records) {
        Iterator<RecordModel> iterator = records.getRecords().iterator();
        Float val = 0.0F;
        RecordModel rec;
        while (iterator.hasNext()) {
            rec = iterator.next();
            if (rec.getValue() < val) iterator.remove();
            else val = rec.getValue();
        }
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
                            new MainPanel();
                        } catch (IOException e) {
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




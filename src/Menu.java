// Końcowa wersja programu MenuDemo

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Menu implements ActionListener {

    JLabel jlab;

    JMenuBar jmb;

    JToolBar jtb;

    JPopupMenu jpu;

    DebugAction setAct;
    DebugAction clearAct;
    DebugAction resumeAct;

    Menu() {
        // Tworzy kontener JFrame
        JFrame jfrm = new JFrame("Prezentacja menu - wersja końcowa");

        // Program używa domyślnego menedżera układu - BorderLayout

        // Określenie początkowych wymiarów ramki
        jfrm.setSize(360, 200);

        // Zamknięcie okna aplikacji ma kończyć działanie programu
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tworzy etykietę, w której będą wyświetlane nazwy
        // wybieranych opcji menu
        jlab = new JLabel();

        // Tworzy pasek menu
        jmb = new JMenuBar();

        // Tworzy menu Plik
        makeFileMenu();

        // Tworzy akcje paska narzędzi i menu Uruchamianie
        makeActions();
        makeToolBar();
        makeOptionsMenu();
        makeHelpMenu();

        // Tworzy menu podręczne Edycja
        makeEditPUMenu();

        // Dodaje obiekt nasłuchujący, który sprawdza wystąpienie warunku wyświetlenia menu
        jfrm.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if(me.isPopupTrigger())
                    jpu.show(me.getComponent(), me.getX(), me.getY());
            }
            public void mouseReleased(MouseEvent me) {
                if(me.isPopupTrigger())
                    jpu.show(me.getComponent(), me.getX(), me.getY());
            }
        });

        // Dodaje etykietę do centralnego obszaru panelu zawartości
        jfrm.add(jlab, SwingConstants.CENTER);

        // Dodaje pasek narzędzi u góry obszaru zawartości
        // (w pozycji określonej jako BorderLayout.NORTH)
        jfrm.add(jtb, BorderLayout.NORTH);

        // Dodaje pasek menu do ramki
        jfrm.setJMenuBar(jmb);

        // Wyświetla ramkę
        jfrm.setVisible(true);
    }

    // Obsługa zdarzeń generowanych przez wybierane opcje menu
    // Ta metoda NIE obsługuje zdarzeń generowanych przez opcje
    // paska narzędzi i menu Uruchamianie
    public void actionPerformed(ActionEvent ae) {
        // Pobranie łańcucha polecenia wybranej opcji menu
        String comStr = ae.getActionCommand();

        // W przypadku wybrania opcji Zakończ należy zakończyć
        // działanie programu
        if(comStr.equals("Zakończ")) System.exit(0);

        // W pozostałych przypadkach wyświetlana jest nazwa opcji
        jlab.setText("Wybrano " + comStr);
    }

    // Klasa akcji używana przez przyciski paska narzędzi
    // i elementy podmenu Uruchamianie
    class DebugAction extends AbstractAction {
        public DebugAction(String name, Icon image, int mnem,
                           int accel, String tTip) {
            super(name, image);
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(accel,
                            InputEvent.CTRL_DOWN_MASK));
            putValue(MNEMONIC_KEY, mnem);
            putValue(SHORT_DESCRIPTION, tTip);
        }

        // Obsługuje zdarzenia generowane przez przyciski paska
        // narzędzi i elementy menu Uruchamianie
        public void actionPerformed(ActionEvent ae) {
            String comStr = ae.getActionCommand();

            jlab.setText("Wybrano " + comStr);

            // Przełącza status opcji Ustaw punkt wstrzymania
            // i Usuń punkt wstrzymania
            if(comStr.equals("Ustaw punkt wstrzymania")) {
                clearAct.setEnabled(true);
                setAct.setEnabled(false);
            } else if(comStr.equals("Usuń punkt wstrzymania")) {
                clearAct.setEnabled(false);
                setAct.setEnabled(true);
            }
        }
    }

    // Tworzy menu Plik wraz z mnemonikami i akceleratorami
    void makeFileMenu() {
        JMenu jmFile = new JMenu("Plik");
        jmFile.setMnemonic(KeyEvent.VK_P);

        JMenuItem jmiOpen = new JMenuItem("Otwórz",
                KeyEvent.VK_O);
        jmiOpen.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiClose = new JMenuItem("Zamknij",
                KeyEvent.VK_M);
        jmiClose.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_M,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiSave = new JMenuItem("Zapisz",
                KeyEvent.VK_Z);
        jmiSave.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                        InputEvent.CTRL_DOWN_MASK));

        JMenuItem jmiExit = new JMenuItem("Zakończ",
                KeyEvent.VK_K);
        jmiExit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_K,
                        InputEvent.CTRL_DOWN_MASK));

        jmFile.add(jmiOpen);
        jmFile.add(jmiClose);
        jmFile.add(jmiSave);
        jmFile.addSeparator();
        jmFile.add(jmiExit);
        jmb.add(jmFile);

        // Dodaje obiekty nasłuchujące do elementów menu Plik
        jmiOpen.addActionListener(this);
        jmiClose.addActionListener(this);
        jmiSave.addActionListener(this);
        jmiExit.addActionListener(this);
    }

    // Utworzenie menu Opcje
    void makeOptionsMenu() {
        JMenu jmOptions = new JMenu("Opcje");

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

        // Tworzy podmenu Uruchamianie, umieszczone w menu
        // Opcje; elementy tego menu są tworzone na podstawie
        // akcji
        JMenu jmDebug = new JMenu("Uruchamianie");
        JMenuItem jmiSetBP = new JMenuItem(setAct);
        JMenuItem jmiClearBP = new JMenuItem(clearAct);
        JMenuItem jmiResume = new JMenuItem(resumeAct);

        // Dodaje elementy do podmenu Uruchamianie
        jmDebug.add(jmiSetBP);
        jmDebug.add(jmiClearBP);
        jmDebug.add(jmiResume);
        jmOptions.add(jmDebug);

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
        JMenu jmHelp = new JMenu("Pomoc");

        // Dodaje ikonę do elementu menu
        ImageIcon icon = new ImageIcon("AboutIcon.gif");

        JMenuItem jmiAbout = new JMenuItem("O programie", icon);
        jmiAbout.setToolTipText("Informacje o programie MenuDemo.");
        jmHelp.add(jmiAbout);
        jmb.add(jmHelp);

        // Określa obiekt nasłuchujący
        jmiAbout.addActionListener(this);
    }

    // Tworzy akcje używane do utworzenia i obsługi
    // elementów paska narzędzi i podmenu Uruchamianie
    void makeActions() {
        // Wczytuje obrazki używane przez akcje
        ImageIcon setIcon = new ImageIcon("setBP.gif");
        ImageIcon clearIcon = new ImageIcon("clearBP.gif");
        ImageIcon resumeIcon = new ImageIcon("resume.gif");

        // Tworzy akcje
        setAct =
                new DebugAction("Ustaw punkt wstrzymania",
                        setIcon,
                        KeyEvent.VK_U,
                        KeyEvent.VK_U,
                        "Ustawia punkt wstrzymania.");

        clearAct =
                new DebugAction("Usuń punkt wstrzymania",
                        clearIcon,
                        KeyEvent.VK_S,
                        KeyEvent.VK_S,
                        "Usuwa punkt wstrzymania.");

        resumeAct =
                new DebugAction("Wznów",
                        resumeIcon,
                        KeyEvent.VK_W,
                        KeyEvent.VK_W,
                        "Wznawia wykonywanie programu.");

        // Początkowo wyłącza opcję Usuń punkt wstrzymania
        clearAct.setEnabled(false);
    }

    // Tworzy pasek narzędzi Uruchamianie
    void makeToolBar() {
        // Tworzy przyciski paska narzędzi, używając przy tym akcji
        JButton jbtnSet = new JButton(setAct);
        JButton jbtnClear = new JButton(clearAct);
        JButton jbtnResume = new JButton(resumeAct);

        // Tworzy pasek narzędzi Uruchamianie
        jtb = new JToolBar("Uruchamianie");

        // Dodaje przyciski do paska narzędzi
        jtb.add(jbtnSet);
        jtb.add(jbtnClear);
        jtb.add(jbtnResume);
    }

    // Tworzenie menu podręcznego Edycja
    void makeEditPUMenu() {
        jpu = new JPopupMenu();

        // Tworzy elementy menu
        JMenuItem jmiCut = new JMenuItem("Wytnij");
        JMenuItem jmiCopy = new JMenuItem("Kopiuj");
        JMenuItem jmiPaste = new JMenuItem("Wklej");

        // Dodaje elementy do menu
        jpu.add(jmiCut);
        jpu.add(jmiCopy);
        jpu.add(jmiPaste);

        // Dodaje obiekty nasłuchujące do elementów menu podręcznego
        jmiCut.addActionListener(this);
        jmiCopy.addActionListener(this);
        jmiPaste.addActionListener(this);
    }

    public static void main(String args[]) {
        // Tworzy ramkę w wątku obsługi zdarzeń
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Menu();
            }
        });
    }
}


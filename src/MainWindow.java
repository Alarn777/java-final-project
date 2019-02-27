/**
 * This is a simple Currency Converter application made with swing GUI.
 * @Authors:
 *          Michael Rokitko (334065893)
 *          Evgeny Alterman (317747814)
 *
 * @version 1.0
 * @created 28-02-2019
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

import static javax.swing.JOptionPane.*;


public class MainWindow extends javax.swing.JFrame {
    static Logger log = LogManager.getLogger(MainWindow.class.getName());
    // Top Panel and its components
    private final JPanel currenciesTablePanel = new JPanel();

    // Splitter
    private final JSplitPane splitPane = new JSplitPane();  // split the window in top and bottom

    // Bottom Panel and its components

    private final JPanel conversionsPanel = new JPanel();
    private final JPanel mainControlsPanel = new JPanel();
    // and a exitButton at the right, to send the text
    private String[][] currenciesTableValues = new String[14][5];
    private JTextField targetAmountTextField;
    private JTable currenciesTable;
    private JComboBox sourceCurrencyCombo;

    private final JButton exitButton = new JButton("Exit");
    private JComboBox targetCurrencyCombo;
    private JTextField sourceAmountTextField;
    private String[] currenciesTypes;
    private ICurrencyServiceRepository currencyServiceRepository = new CurrencyServiceRepository();


    public MainWindow() {
        loadCurrenciesValues();
        setCurrenciesTypes();

        setupMainWindow();

        populateCurrenciesTablePanel();
        populateConversionsPanel();
        populateMainControlsPanel();

        pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }

    private void populateMainControlsPanel() {
        createConvertButton();
        createExitButton();

        mainControlsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        mainControlsPanel.setLayout(new BoxLayout(mainControlsPanel, BoxLayout.X_AXIS));
        conversionsPanel.add(mainControlsPanel);
    }

    private void createExitButton() {
        exitButton.addActionListener(e -> System.exit(0));
        mainControlsPanel.add(exitButton);           // and right the "send" exitButton
    }

    private void createConvertButton() {
        var convertButton = new JButton("Convert");
        convertButton.addActionListener(e -> {

            float amount = 0;
            try {
                amount = Float.parseFloat(sourceAmountTextField.getText());
            } catch (Exception ex) {
                showMessageDialog(null, "Bad Source Amount");
            }

            var calculator = new CurrencyCalculator(currenciesTableValues);

            float result = calculator.convert(
                    (String) sourceCurrencyCombo.getSelectedItem(),
                    (String) targetCurrencyCombo.getSelectedItem(),
                    amount);
            log.info("Converted " +
                    amount +  (String) sourceCurrencyCombo.getSelectedItem() +
                    " to " +result+  (String) targetCurrencyCombo.getSelectedItem());

            targetAmountTextField.setText(String.valueOf(result));
        });
        mainControlsPanel.add(convertButton);
    }

    private void setupMainWindow() {
        setPreferredSize(new Dimension(600, 450));
        getContentPane().setLayout(new GridLayout());
        getContentPane().add(splitPane);
    }

    private void populateConversionsPanel() {
        conversionsPanel.setLayout(new BoxLayout(conversionsPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically
        splitPane.setBottomComponent(conversionsPanel);

        sourceCurrencyCombo = new JComboBox(currenciesTypes);
        sourceAmountTextField = new JTextField();
        sourceAmountTextField.setColumns(5);

        var sourceSelectPanel = new JPanel();
        sourceSelectPanel.add(new JLabel("From:"));
        sourceSelectPanel.add(sourceCurrencyCombo);
        sourceSelectPanel.add(sourceAmountTextField);


        targetCurrencyCombo = new JComboBox(currenciesTypes);
        targetAmountTextField = new JTextField();
        targetAmountTextField.setColumns(10);
        targetAmountTextField.setEditable(false);

        var targetSelectPanel = new JPanel();
        targetSelectPanel.add(new JLabel("To:"));
        targetSelectPanel.add(targetCurrencyCombo);
        targetSelectPanel.add(targetAmountTextField);

        conversionsPanel.add(sourceSelectPanel);
        conversionsPanel.add(targetSelectPanel);

    }

    private void setCurrenciesTypes() {
        currenciesTypes = new String[currenciesTableValues.length + 1];

        for (int i = 0; i < currenciesTableValues.length; i++)
            currenciesTypes[i+1] = currenciesTableValues[i][1];

        currenciesTypes[0] = "NIS";
    }

    private void populateCurrenciesTablePanel() {
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(300);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(currenciesTablePanel);      // at the top we want our "currenciesTablePanel"
        InitializeCurrenciesTable();

    }

    private void InitializeCurrenciesTable() {
        String[] columnNames = {"NAME", "CURRENCY CODE", "COUNTRY", "RATE", "UNITS"};

        currenciesTable = new JTable(currenciesTableValues, columnNames);
        currenciesTable.setBounds(30, 40, 300, 300);

        var currenciesTableScrollPane = new JScrollPane(currenciesTable);

        var updateCurrenciesButton  =new JButton("Update Currencies");
        updateCurrenciesButton.addActionListener(e-> loadCurrenciesValues());

        currenciesTablePanel.setLayout(new BoxLayout(currenciesTablePanel,BoxLayout.Y_AXIS));
        currenciesTablePanel.add(currenciesTableScrollPane);
        currenciesTablePanel.add(updateCurrenciesButton);
    }

    private void loadCurrenciesValues() {
        var data = currencyServiceRepository.getCurrData();
        for (int i = 0; i < data.size(); i++) {
            Vector n = (Vector) data.elementAt(i);

            for (int j = 0; j < 5; j++) {
                currenciesTableValues[i][j] = (String) n.elementAt(j);
            }
        }
    }

    public static void main(String args[]) {

        EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
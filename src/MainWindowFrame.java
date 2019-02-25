import javax.swing.*;
import java.awt.*;
import java.util.Vector;


public class MainWindowFrame extends javax.swing.JFrame{

    // first, lets create the containers:


    // the splitPane divides the window in two components (here: top and bottom)


    // users can then move the divider and decide how much of the top component


    // and how much of the bottom component they want to see.
    // these are the components we need.
    private final JSplitPane splitPane = new JSplitPane();  // split the window in top and bottom
    // our top component
    private final JPanel currenciesTablePanel = new JPanel();       // container panel for the top
    // our bottom component
    private final JPanel bottomPanel = new JPanel();    // container panel for the bottom

    // in our bottom panel we want the text area and the input components
// this scrollPane is used to make the text area scrollable
    private final JScrollPane scrollPane = new JScrollPane(); // makes the text scrollable
    // this text area will be put inside the scrollPane
    private final JTextArea textArea = new JTextArea();     // the text

    // the input components will be put in a separate panel
    private final JPanel inputPanel = new JPanel();      // under the text a container for all the input elements
    // and a exitButton at the right, to send the text
    private final JButton exitButton = new JButton("Exit");         // and a "send" exitButton
    private String [][] currenciesTableValues = new String [14][5];
    private JTextArea area;
    private CurrencyCalculator calculator = new CurrencyCalculator();

    JTable currenciesTable;
    public MainWindowFrame(){

        UpdateCurrencyValue(currenciesTableValues);

        InitializeCurrenciesTable();

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(270);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(currenciesTablePanel);      // at the top we want our "currenciesTablePanel"
        splitPane.setBottomComponent(bottomPanel);

        scrollPane.setViewportView(textArea);// and at the bottom we want our "bottomPanel"
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically
        bottomPanel.add(scrollPane);


        // now lets define the default size of our window and its layout:
        setPreferredSize(new Dimension(500, 600));     // let's open the window with a default
        // the contentPane is the container that holds all our components
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,
        // we only add one element to the window itself
        getContentPane().add(splitPane);               // due to the GridLayout, our splitPane will now fill the whole window

        JSplitPane splitPanel2 = new JSplitPane();

        String[] currencies = new String[currenciesTableValues.length+1];

        for (int i = 1; i < currenciesTableValues.length; i++)
            currencies[i] = currenciesTableValues[i][1];

        currencies[0] = "NIS";

        final JPanel comboPanel = new JPanel();
        comboPanel.add(new JLabel("Currency:"));
        comboPanel.add(new JComboBox(currencies));

        JTextField amountChooser = new JTextField();
        amountChooser.setColumns(5);
        comboPanel.add(new JLabel("Pick an amount in NIS:"));
        comboPanel.add(amountChooser);

        final JPanel comboPanel1 = new JPanel();
        JLabel comboLbl1 = new JLabel("Pick a currency to convert to:");
        JComboBox currencyDest = new JComboBox(currencies);
        comboPanel1.add(comboLbl1);
        comboPanel1.add(currencyDest);
        splitPanel2.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window verticaly
        splitPanel2.setDividerLocation(100);                    // the initial position of the divider is 200
        splitPanel2.setTopComponent(comboPanel);                  // at the top we want our "currenciesTablePanel"
        splitPanel2.setBottomComponent(comboPanel1);            // and at the bottom we want our "bottomPanel"


        scrollPane.setViewportView(splitPanel2);

        bottomPanel.add(inputPanel);                // then we add the inputPanel to the bottomPanel, so it under the scrollPane / textArea

        // let's set the maximum size of the inputPanel, so it doesn't get too big when the user resizes the window
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));     // we set the max height to 75 and the max width to (almost) unlimited
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));   // X_Axis will arrange the content horizontally
        JButton Convert = new JButton("Convert");
        Convert.addActionListener(e -> {
            UpdateCurrencyValue(currenciesTableValues);
            String currCurrency = (String) new JComboBox(currencies).getSelectedItem();
            String currencyDestination = (String) currencyDest.getSelectedItem();
            String currencyAmount = amountChooser.getText();
            float amount = 0;
            try{
                amount = Float.parseFloat(currencyAmount);
            }catch (Exception e1){
                System.err.println("Not an integer!");
            }

            float result = calculator.calculate(currCurrency,currencyDestination,amount, currenciesTableValues);

            area.setText(String.valueOf(result));
            amountChooser.setText("");
        });

        inputPanel.add(Convert);
        area = new JTextArea();
        inputPanel.add(area);        // left will be the textField
        inputPanel.add(exitButton);           // and right the "send" exitButton

        exitButton.addActionListener(e -> System.exit(0));

        pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }

    private void InitializeCurrenciesTable() {
        String[] columnNames = {"NAME","CURRENCY CODE", "COUNTRY" , "RATE", "UNITS"};

        currenciesTable = new JTable(currenciesTableValues, columnNames);
        currenciesTable.setBounds(30, 40, 200, 300);

        var currenciesTableScrollPane = new JScrollPane(currenciesTable);
        currenciesTablePanel.add(currenciesTableScrollPane);

    }

    private void UpdateCurrencyValue(String[][] temp) {
        CurrencyServiceRepository parserDemo = new CurrencyServiceRepository();
        Vector bla = parserDemo.getCurrData();
        for (int i = 0; i< bla.size(); i++) {
            Vector n = (Vector) bla.elementAt(i);

            for (int j = 0; j < 5; j++) {
                temp[i][j] = (String) n.elementAt(j);
            }
        }
    }

    public static void main(String args[]){

        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                new MainWindowFrame().setVisible(true);
            }
        });
    }
}
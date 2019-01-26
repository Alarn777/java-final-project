import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


public class MyFrame extends javax.swing.JFrame{

    // these are the components we need.
    private final JSplitPane splitPane;  // split the window in top and bottom
    private final JPanel topPanel;       // container panel for the top
    private final JPanel bottomPanel;    // container panel for the bottom
    private final JScrollPane scrollPane; // makes the text scrollable
    private final JTextArea textArea;     // the text
    private final JPanel inputPanel;      // under the text a container for all the input elements
    private final JTextField textField;   // a textField for the text the user inputs
    private final JButton button;         // and a "send" button
    private String [][] temp;
    private JTextArea area;
    private CurrencyCalculator calculator;
    JTable j;
    public MyFrame(){
        temp = new String [14][5];
        calculator = new CurrencyCalculator();
        // first, lets create the containers:
        // the splitPane devides the window in two components (here: top and bottom)
        // users can then move the devider and decide how much of the top component
        // and how much of the bottom component they want to see.
        splitPane = new JSplitPane();

        topPanel = new JPanel();         // our top component
        UpdateCurrencyValue(temp);
//        String[][] data = {
//                { "Dollar","US", "USA", "3.683" },
//                { "Pound","GBP", "Great Britain", "4.7971" }
//        };

        // Column Names

        String[] columnNames = {"NAME","CURRENCY CODE", "COUNTRY" , "RATE", "UNITS"};

        // Initializing the JTable
        j = new JTable(temp, columnNames);
        j.setBounds(30, 40, 200, 300);

        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(j);
        topPanel.add(sp);

        bottomPanel = new JPanel();      // our bottom component

        // in our bottom panel we want the text area and the input components
        scrollPane = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        textArea = new JTextArea();      // this text area will be put inside the scrollPane

        // the input components will be put in a separate panel
        inputPanel = new JPanel();
        textField = new JTextField();    // first the input field where the user can type his text
        button = new JButton("Exit");    // and a button at the right, to send the text
        // now lets define the default size of our window and its layout:
        setPreferredSize(new Dimension(500, 600));     // let's open the window with a default size of 400x400 pixels
        // the contentPane is the container that holds all our components
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,
        // we only add one element to the window itself
        getContentPane().add(splitPane);               // due to the GridLayout, our splitPane will now fill the whole window

        // let's configure our splitPane:
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window verticaly
        splitPane.setDividerLocation(270);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(topPanel);                  // at the top we want our "topPanel"
        splitPane.setBottomComponent(bottomPanel);            // and at the bottom we want our "bottomPanel"

        // our topPanel doesn't need anymore for this example. Whatever you want it to contain, you can add it here
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically

        bottomPanel.add(scrollPane);                // first we add the scrollPane to the bottomPanel, so it is at the top
        scrollPane.setViewportView(textArea);       // the scrollPane should make the textArea scrollable, so we define the viewport



        JSplitPane splitPanel2 = new JSplitPane();

        String[] fruitOptions = new String[temp.length+1];
        for (int i = 1; i < temp.length; i++){
            fruitOptions[i] = temp[i][1];
        }
        fruitOptions[0] = "NIS";
//        String[] fruitOptions = {"Apple", "Apricot", "Banana"
//                ,"Cherry", "Date", "Kiwi", "Orange", "Pear", "Strawberry"};
        final JPanel comboPanel = new JPanel();
        JLabel comboLbl = new JLabel("Currency:");
        JComboBox CurrencyBox = new JComboBox(fruitOptions);
        comboPanel.add(comboLbl);
        comboPanel.add(CurrencyBox);

        JLabel amountLabel = new JLabel("Pick an amount in NIS:");
        JTextField amountChooser = new JTextField();
        amountChooser.setColumns(5);
        comboPanel.add(amountLabel);
        comboPanel.add(amountChooser);

//        comboPanel.setSize(new Dimension(Integer.MAX_VALUE, 100));



        final JPanel comboPanel1 = new JPanel();
        JLabel comboLbl1 = new JLabel("Pick a currency to convert to:");
        JComboBox currencyDest = new JComboBox(fruitOptions);
        comboPanel1.add(comboLbl1);
        comboPanel1.add(currencyDest);


//        JLabel chooseCurrency2Label = new JLabel("Pick a second value:");
//        JTextField CurrencyChooser = new JTextField();
//        CurrencyChooser.setColumns(5);
//        comboPanel1.add(chooseCurrency2Label);
//        comboPanel1.add(CurrencyChooser);




        splitPanel2.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window verticaly
        splitPanel2.setDividerLocation(100);                    // the initial position of the divider is 200
        splitPanel2.setTopComponent(comboPanel);                  // at the top we want our "topPanel"
        splitPanel2.setBottomComponent(comboPanel1);            // and at the bottom we want our "bottomPanel"


        scrollPane.setViewportView(splitPanel2);

        bottomPanel.add(inputPanel);                // then we add the inputPanel to the bottomPanel, so it under the scrollPane / textArea

        // let's set the maximum size of the inputPanel, so it doesn't get too big when the user resizes the window
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));     // we set the max height to 75 and the max width to (almost) unlimited
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));   // X_Axis will arrange the content horizontally
        JButton Convert = new JButton("Convert");
        Convert.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                UpdateCurrencyValue(temp);
                String currCurrency = (String) CurrencyBox.getSelectedItem();
                String currencyDestination = (String) currencyDest.getSelectedItem();
                String currencyAmount = amountChooser.getText();
                float amount = 0;
                try{
                    amount = Float.parseFloat(currencyAmount);
                }catch (Exception e1){
                    System.err.println("Not an integer!");
                }
                
                float result = calculator.calculate(currCurrency,currencyDestination,amount,temp);

                area.setText(String.valueOf(result));
                amountChooser.setText("");
            }
        });
        inputPanel.add(Convert);
        area = new JTextArea();
        inputPanel.add(area);        // left will be the textField
        inputPanel.add(button);           // and right the "send" button

        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });



        pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }

    private void UpdateCurrencyValue(String[][] temp) {
        DomParserDemo parserDemo = new DomParserDemo();
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
                new MyFrame().setVisible(true);
            }
        });
    }
}
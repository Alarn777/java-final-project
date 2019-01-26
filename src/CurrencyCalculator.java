
public class CurrencyCalculator {
    public float calculate(String currencyOrigin,String currencyDest, float amount,String[][] data) {
        float rate = 0.0f;
        int units = 0;
        for (int i = 0;i < data.length; i++)
        {
            if(data[i][1].equals(currencyOrigin)){
                rate = Float.parseFloat(data[i][3]);
                units = Integer.parseInt(data[i][4]);
            }
        }
        if (currencyOrigin.equals("NIS"))
            return amount * 1;

        return amount/units * rate;
    }
}

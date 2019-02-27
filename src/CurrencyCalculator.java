/**
 * This is Simple Currency converter Class
 */

import java.util.HashMap;

public class CurrencyCalculator {

    private final HashMap<String, Float> conversionsMap;
    private final String primary = "NIS";


    public CurrencyCalculator(String[][] conversionsTable) {
        conversionsMap = new HashMap<>();
        for (int i = 0; i < conversionsTable.length; i++) {
            conversionsMap.put(
                    conversionsTable[i][1],
                    Float.parseFloat(conversionsTable[i][3]) / Float.parseFloat(conversionsTable[i][4])
            );
        }
    }

    /**
     * This method converts some amount form source currency to target currency
     * example:
     *      convert("NIS","USD,100);
     *      // result --> 370  if rate is 3.7
     * @param source
     * @param target
     * @param amount
     * @return converted amount
     */
    public float convert(String source, String target, float amount) {

        if (target.equals(source))
            return amount;

        if (source.equals(primary))
            return amount / conversionsMap.get(target);

        if (target.equals(primary))
            return amount * conversionsMap.get(source);

        float amountInPrim = amount * conversionsMap.get(source);

        return amountInPrim / conversionsMap.get(target);
    }
}

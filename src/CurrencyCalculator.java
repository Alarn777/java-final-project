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

    public float convert(String source, String target, float amount) {

        if (target.equals(source))
            return amount;

        if (source.equals(primary))
            return amount / conversionsMap.get(target);

        if (target.equals(primary))
            return amount * conversionsMap.get(source);

        //$-->#
        //10$ * ShR = 340Sh
        //340Sh / #R = 7#
        //10$=7#
        float amountInPrim = amount * conversionsMap.get(source);

        return amountInPrim / conversionsMap.get(target);
    }
}

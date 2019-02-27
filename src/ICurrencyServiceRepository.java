import java.io.IOException;
import java.util.Vector;

public interface ICurrencyServiceRepository {
    Vector getCurrData();
    void downloadLatestRates() throws IOException;

}

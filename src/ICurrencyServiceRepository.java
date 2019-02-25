import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public interface ICurrencyServiceRepository {
    Vector getCurrData();
    void DownloadLatestRates() throws IOException;
}

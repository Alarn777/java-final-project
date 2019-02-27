/**
 * This is Simple repository service for getting and managing Currencies Feed From bank of Israel
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class CurrencyServiceRepository implements ICurrencyServiceRepository {
    static Logger log = LogManager.getLogger(CurrencyServiceRepository.class.getName());

    private final String lastDownloadedRatesFilePath = "lastCurrencyCheck.xml";
    private final String serviceUrl = "https://www.boi.org.il/currency.xml";

    private FileTime lastSyncDate;

    public CurrencyServiceRepository() {
        getLastSyncDate();
    }

    private void getLastSyncDate() {
        try {
            if (new File(lastDownloadedRatesFilePath).exists())
                lastSyncDate = Files.getLastModifiedTime(Paths.get(lastDownloadedRatesFilePath));
            else
                lastSyncDate = FileTime.fromMillis(0);

        } catch (IOException e) {
            log.error("Can't read currencies file");
        }
    }

    /**
     * This Method Downloads current Currencies feed from the website of bank of Israel (https://www.boi.org.il/currency.xml)
     *
     * @throws IOException
     */
    @Override
    public void downloadLatestRates() throws IOException {
        try {
            CurrencyServiceRepository.log.info("Starting rates download.");

            URL url = new URL(serviceUrl);
            var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            var inputStream = connection.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            var targetFile = new File(lastDownloadedRatesFilePath);
            new FileOutputStream(targetFile).write(buffer);

            getLastSyncDate();
            CurrencyServiceRepository.log.info("Downloading rates from " + serviceUrl + " completed.");

        } catch (Exception e) {
            CurrencyServiceRepository.log.error("Failed to download latest currency rates.");
        }
    }


    /**
     * This method attempt to download latest rates feed and if fails uses previously saved locally rates and returns its values.
     *
     * @return Collection of available currencies rates
     */
    @Override
    public Vector getCurrData() {
        try {
            var lastUpdateTimestamp = lastSyncDate.toMillis();

            downloadLatestRates();

            if (lastUpdateTimestamp == lastSyncDate.toMillis())
                log.info("Using previously downloaded rates from "
                        + new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss").format(lastSyncDate.toMillis())
                );

            return parseRatesXmlFile();

        } catch (Exception e) {
            log.error("Error on loading rates.");
            log.error(e.getStackTrace());
            return null;
        }
    }

    private Vector parseRatesXmlFile() throws ParserConfigurationException, SAXException, IOException {
        var savedFileStream = new FileInputStream(new File(lastDownloadedRatesFilePath));

        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(savedFileStream);

        document.getDocumentElement().normalize();

        var currencyTree = document.getElementsByTagName("CURRENCY");
        var rates = new Vector();

        for (int i = 0; i < currencyTree.getLength(); i++) {
            var currencyNode = currencyTree.item(i);
            var currencyInfo = new Vector();

            if (currencyNode.getNodeType() == Node.ELEMENT_NODE) {

                currencyInfo.add(getNodeValue((Element) currencyNode, "NAME"));
                currencyInfo.add(getNodeValue((Element) currencyNode, "CURRENCYCODE"));
                currencyInfo.add(getNodeValue((Element) currencyNode, "COUNTRY"));
                currencyInfo.add(getNodeValue((Element) currencyNode, "RATE"));
                currencyInfo.add(getNodeValue((Element) currencyNode, "UNIT"));

                rates.add(currencyInfo);
            }
        }

        return rates;
    }

    private String getNodeValue(Element element, String name) {
        return element
                .getElementsByTagName(name)
                .item(0)
                .getTextContent();
    }

//    class CurrenciesFeedUpdater extends Thread {
//        private Timer timer;
//
//        Logger log = LogManager.getLogger(CurrenciesFeedUpdater.class.getName());
//
//        public void run() {
//
//        }
//    }
}
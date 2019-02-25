
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
@Override
    public void DownloadLatestRates() throws IOException {
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

    @Override
    public Vector getCurrData() {
        try {
            var lastUpdateTimestamp = lastSyncDate.toMillis();

            DownloadLatestRates();

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

        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.parse(savedFileStream);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("CURRENCY");

        var rates = new Vector();
        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            Vector oneLine = new Vector();

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elm = (Element) nNode;
                String value = elm
                        .getElementsByTagName("NAME")
                        .item(0)
                        .getTextContent();


                oneLine.add(value);

                value = elm
                        .getElementsByTagName("CURRENCYCODE")
                        .item(0)
                        .getTextContent();


                oneLine.add(value);

                value = elm
                        .getElementsByTagName("COUNTRY")
                        .item(0)
                        .getTextContent();


                oneLine.add(value);

                value = elm
                        .getElementsByTagName("RATE")
                        .item(0)
                        .getTextContent();

                oneLine.add(value);

                value = elm
                        .getElementsByTagName("UNIT")
                        .item(0)
                        .getTextContent();

                oneLine.add(value);

                rates.add(oneLine);
            }
        }
        return rates;
    }

}
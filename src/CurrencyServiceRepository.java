
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class CurrencyServiceRepository {
    static Logger log = LogManager.getLogger(CurrencyServiceRepository.class.getName());

    private final String lastDownloadedRatesFilePath = "lastCurrencyCheck.xml";
    private Vector returnArr;
    private FileTime lastSyncDate;

    public CurrencyServiceRepository() {
        getLastSyncDate();
    }

    private void getLastSyncDate(){

        try {
            this.lastSyncDate = Files.getLastModifiedTime(Paths.get(lastDownloadedRatesFilePath));
        } catch (IOException e) {
            log.error("Can't read currencies file");
        }
    }
    public Vector getCurrData() {
        try {
            DownloadLatestRates();

            InputStream savedFileStream = new FileInputStream(
                    new File(lastDownloadedRatesFilePath));


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(savedFileStream);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("CURRENCY");
            returnArr = new Vector();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Vector oneLine = new Vector();
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String value = eElement
                            .getElementsByTagName("NAME")
                            .item(0)
                            .getTextContent();


                    oneLine.add(value);

                    value = eElement
                            .getElementsByTagName("CURRENCYCODE")
                            .item(0)
                            .getTextContent();


                    oneLine.add(value);

                    value = eElement
                            .getElementsByTagName("COUNTRY")
                            .item(0)
                            .getTextContent();


                    oneLine.add(value);

                    value = eElement
                            .getElementsByTagName("RATE")
                            .item(0)
                            .getTextContent();

                    oneLine.add(value);

                    value = eElement
                            .getElementsByTagName("UNIT")
                            .item(0)
                            .getTextContent();

                    oneLine.add(value);


                    returnArr.add(oneLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnArr;
    }

    private void DownloadLatestRates() throws IOException {
        try {
            log.info("Starting rates download.");

            URL url = new URL("https://www.boi.org.il/currency.xml");
            var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            var inputStream = connection.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            var targetFile = new File(lastDownloadedRatesFilePath);
            new FileOutputStream(targetFile).write(buffer);

            getLastSyncDate();

        } catch (Exception e) {
            log.error("Failed to download latest currency rates.");
        }
    }

}

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class DomParserDemo {
    private static HttpURLConnection con;
    private static InputStream is;
    private Vector returnArr;
    public Vector getCurrData() {

        try {
            URL url = new URL("https://www.boi.org.il/currency.xml");
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            is = con.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
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

}
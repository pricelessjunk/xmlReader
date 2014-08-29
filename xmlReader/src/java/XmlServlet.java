
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class XmlServlet
 */
@WebServlet(urlPatterns = {"/XmlServlet"})
public class XmlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public XmlServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String actionType = request.getParameter("actionType");
        String movieName = request.getParameter("movieName");
        String filePath = request.getParameter("filePath");

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                createFile(filePath);
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            Node root = doc.getDocumentElement();

            if ("remove".equals(actionType)) {
                NodeList nameList = doc.getElementsByTagName("name");
                if (nameList != null && nameList.getLength() > 0) {
                    for (int i = 0; i < nameList.getLength(); i++) {
                        Node node = nameList.item(i);

                        if (node.hasAttributes()) {
                            NamedNodeMap attrs = node.getAttributes();
                            Attr attr = (Attr) attrs.item(0);

                            if (movieName.equals(attr.getValue())) {
                                root.removeChild(node);
                                rebuildXml(doc, filePath);
                                break;
                            }
                        }
                    }
                }

                System.out.println("Removing Done");
            } else if ("add".equals(actionType)) {
                int size = Integer.parseInt(request.getParameter("size"));

                Element childElement = doc.createElement("name");
                childElement.setAttribute("movieName", movieName);
                root.appendChild(childElement);

                NodeList childNode = root.getChildNodes();
                Node parentNode = null;

                for (int i = 0; i < childNode.getLength(); i++) {
                    Node node = childNode.item(i);
                    if (node.hasAttributes()) {
                        NamedNodeMap attrs = node.getAttributes();
                        Attr attr = (Attr) attrs.item(0);

                        if (movieName.equals(attr.getValue())) {
                            parentNode = node;
                            break;
                        }
                    }
                }

                for (int i = 0; i < size; i++) {
                    String myInputs = request.getParameter("myInputs" + i);

                    if (myInputs != null || !"".equals(myInputs)) {
                        Element linkElement = doc.createElement("link");
                        parentNode.appendChild(linkElement);
                        Text text = doc.createTextNode(myInputs);
                        linkElement.appendChild(text);
                    }
                }
                rebuildXml(doc, filePath);
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NumberFormatException e) {
            e.printStackTrace();
        }
        response.sendRedirect("LinkShow.jsp");
    }

    private void rebuildXml(Document doc, String filePath) {

        try {
            //setting up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();

            //generating string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();

            //Saving the XML content to File
            OutputStream f0;
            byte buf[] = xmlString.getBytes();
            f0 = new FileOutputStream(filePath);
            for (int i = 0; i < buf.length; i++) {
                f0.write(buf[i]);
            }
            f0.close();
            buf = null;

            System.out.println("Done");

        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(String filePath) throws IOException {
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><movie></movie>";

        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "utf-8"));
        writer.write(text);
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger("XmlServlet").log(Level.SEVERE, ex.getMessage());
        }
    }

}

package lk.dialog.crm.rbm;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author mohan narangoda
 */
public class RBMXMLFileProcessor {
    //private static String sourcePath = "D:\\Dialog\\Dev\\Bitbucket\\rbm-xml-process\\input";
    private static String sourcePath = ".\\input";
    private static String outputPath = ".\\output";
    public static void main(String[] args) throws IOException {
        RBMXMLFileProcessor fileProcessor = new RBMXMLFileProcessor();
        System.out.println("### Starting XML Processor ###");
        ClassPathResource resource = new ClassPathResource("prcustref-mapping.txt");
        System.out.println("-> loading mapping file :"+  resource.getURL().getPath());
        System.out.println("----------------------------------------");
        try (Stream<String> stream = Files.lines(Paths.get(resource.getURI()))) {
            List<String> list = stream.collect(Collectors.toList());
            for (String line : list) {
                String fileName = line.split(",")[0];
                String prCustRef = line.split(",")[1];
                System.out.println("-> Going to process File :"+ fileName + ", PRCustRef :"+prCustRef);
                Document xmlDom = fileProcessor.loadXMLDOM(sourcePath + File.separator +fileName);
                fileProcessor.constuctFinalXML(xmlDom,prCustRef,outputPath,fileName);
                System.out.println("-> Successfully processed File :"+ fileName);
                System.out.println("----------------------------------------");
            }
            // stream.forEach(System.out::println);

            System.out.println("### End XML Processor ###");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param resourcePath
     * @return XML Document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private Document loadXMLDOM(String resourcePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document input = factory
            .newDocumentBuilder()
            .parse(resourcePath);
        return input;
    }

    private void constuctFinalXML(Document doc, String prCustRef, String outputPath, String fileName) throws XPathExpressionException, TransformerException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        //Select the current value
        NodeList nodes = (NodeList) xpath.evaluate("//CONTRACT_BILLDATA/CONTRACTATTRIBUTES|@PRCUSTOMERREF", doc,
                                                   XPathConstants.NODESET);
        //replace all nodes matching with new value
        for (int idx = 0; idx < nodes.getLength(); idx++) {
            ((Element)nodes.item(idx)).setAttribute("PRCUSTOMERREF",prCustRef);
        }
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(new DOMSource(doc), new StreamResult(new File(outputPath+File.separator+fileName)));
    }
}

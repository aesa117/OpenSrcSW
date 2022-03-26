package scripts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MakeCollection {
	private String data_path;
	private String output_file = "./collection.xml";
	
	public MakeCollection(String path) {
		this.data_path = path;
	}
	
	public void makeXml() throws ParserConfigurationException, IOException, TransformerException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document document = docBuilder.newDocument();
		
		Element docs = document.createElement("docs");
		document.appendChild(docs);
		
		Element[] doc = new Element[5];
		Element[] title = new Element[5];
		Element[] body = new Element[5];
		
		File[] files = new File[5];
		files = makeFileList(data_path);
		for(int i=0; i<5; i++) {
			doc[i] = document.createElement("doc");
			docs.appendChild(doc[i]);
			
			doc[i].setAttribute("id", String.valueOf(i));
			title[i] = document.createElement("title");
			body[i] = document.createElement("body");
			doc[i].appendChild(title[i]);
			doc[i].appendChild(body[i]);
		}
		
		for(int i=0; i<5; i++) {
			org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
			String titleData = html.title();
			String bodyData = html.body().text();
			title[i].appendChild(document.createTextNode(titleData));
			body[i].appendChild(document.createTextNode(bodyData));
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));
		
		transformer.transform(source, result);
		
		System.out.println("2주차 실행완료");
	}
	
	public static File[] makeFileList(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}
	
	
}


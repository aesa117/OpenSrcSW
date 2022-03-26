package scripts;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Kuir {

	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {
		String command = args[0];   
		String path = args[1];
		String path2 = args[2];
		String path3 = args[3];

		if(command.equals("-c")) {
			MakeCollection collection = new MakeCollection(path);
			collection.makeXml();
		}
		else if(command.equals("-k")) {
			MakeKeyword keyword = new MakeKeyword(path2);
			keyword.convertXml();
		}
		else if(command.equals("-i")) {
			Indexer indexer = new Indexer(path3);
			indexer.makeIndexer();
		}

	}

}
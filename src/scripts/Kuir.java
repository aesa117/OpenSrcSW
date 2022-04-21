package scripts;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Kuir {

	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {
		String command = args[0];   
		String path = args[1];
		String command2 = args[2];
		String query = args[3];

		if(command.equals("-c")) {
			MakeCollection collection = new MakeCollection(path);
			collection.makeXml();
		}
		else if(command.equals("-k")) {
			MakeKeyword keyword = new MakeKeyword(path);
			keyword.convertXml();
		}
		else if(command.equals("-i")) {
			Indexer indexer = new Indexer(path);
			indexer.makeIndexer();
		}
		else if(command.equals("-s")) {
			if(command2.equals("-q")) {
				Searcher searcher = new Searcher(path, query);
				searcher.CalcSim();
			}
		}
		else if(command.equals("-m")) {
			if(command2.equals("-q")) {
				Midterm mt = new Midterm(path, query);
				mt.showSnippset();
			}
		}
	}

}

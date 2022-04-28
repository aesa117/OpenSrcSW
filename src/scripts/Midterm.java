package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.index.Keyword;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Midterm {
	private String input_file;
	private String query;
	
	public Midterm(String filename, String query) {
		input_file = filename;
		this.query = query;
	}
	
	public void showSnippset() throws ParserConfigurationException, SAXException, IOException {
		InputStream inputStream = new FileInputStream(new File(input_file));
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document document = docBuilder.parse(is);
		
		Element docs = document.getDocumentElement(); //루트 노드 얻기 - docs
		NodeList docsList = docs.getChildNodes();
		
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		HashMap<Integer, String> bm = new HashMap<Integer, String>();
		
		LinkedList<String> list = new LinkedList<String>();
	
		for(int i=0; i< kl.size(); i++) {
			Keyword kwrd = kl.get(i);
			hm.put(kwrd.getString(), 0);
		}
		
		
		
		for(int i=0; i < docsList.getLength(); i++) {
			Node docNode = docsList.item(i);
			if(docNode.getNodeType() == Node.ELEMENT_NODE) {
				Element docEl = (Element)docNode;
				NodeList docChildList = docEl.getChildNodes(); //현재 노드의 자식들 정보 리스트
				for(int j=0; j < docChildList.getLength(); j++) {
					Node item = docChildList.item(j); 
					if(item.getNodeType() == Node.ELEMENT_NODE) { //현재 노드가 Element이면 수행
						Element itemEl = (Element)item;
						if(itemEl.getNodeName().equals("title")) { //노드 이름이 title와 동일하면 수행
							map.put(j, itemEl.getTextContent());
						}
						else if(itemEl.getNodeName().equals("body")) {
							bm.put(j, itemEl.getTextContent()); //j번째
						}
					}
				}
				
			}
		}
		
		
		
		for(int i=0; i<docsList.getLength(); i++) {
			int snippet = 0;
			int score[] = new int[bm.size()];
			for(int j=0; j<bm.size(); j++) {
				score[j] = 0;
				String text = bm.get(j);
				String[] s = text.split("");
				String c = "";
				
				for(int k=0; k <= s.length - 30; k++) {
					c = text.substring(k, k+30);
					KeywordList kl2 = ke.extractKeyword(c, true);
					String words[] = new String[kl2.size()];
					for(int l=0; l<kl2.size(); l++) {
						Keyword kwrd = kl2.get(k);
						words[l] = kwrd.getString();
					}
					for(int l=0; l<words.length; l++) {
						if(hm.containsKey(words[l])) {
							score[j] += 1;
						}
					}
					if(score[j] >= score[snippet]) {
						snippet = k; 
					}
				}
				System.out.println(map.get(j) + bm.get(j).substring(snippet, snippet+30) + score[j]);
			}
			
		}
		
		
		
	}
		
	
	
}


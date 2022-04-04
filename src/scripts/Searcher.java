package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Searcher {
	private String input_file;
	private String query;
	
	public Searcher(String fileName, String query) {
		input_file = fileName;
		this.query = query;
	}
	
	public void CalcSim() throws FileNotFoundException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		LinkedList<Integer> qList = new LinkedList<Integer>(); //문자의 빈도수 저장 리스트
		double[] qInnerProd = new double[5];
		HashMap<Integer, Double> qInnerProdMap = new HashMap<Integer, Double>();
		HashMap<String, Double> qMap = new HashMap<String, Double>();
		
		for(int i=0; i<5; i++) {
			qInnerProd[i] = 0.0;
			qInnerProdMap.put(i, 0.0);
		}
		
		
		String queryText[] = new String[kl.size()];
		
		for(int i=0; i< kl.size(); i++) {
			Keyword kwrd = kl.get(i);
			queryText[i] = kwrd.getString(); //문자 추출
			qList.add(i, kwrd.getCnt()); //빈도수 추출
		}
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(input_file));
		Object obj = ois.readObject();
		ois.close();
		
		//Object로부터 HashMap을 받고 그에 맞는 반복자를 생성
		HashMap<String, LinkedList> hm = (HashMap)obj;
		Iterator<String> iter = hm.keySet().iterator();
		
		for(int i=0; i<queryText.length; i++) {
			if(hm.containsKey(queryText[i])) {
				LinkedList<String> list = (LinkedList<String>)hm.get(queryText[i]);
				for(int j=0; j<list.size(); j++) {
					String[] temp = list.get(j).split(" ");
					double weight = Double.parseDouble(temp[1]);
					
					double value = weight + qList.get(i);
					qInnerProd[j] += value;
					double origin = qInnerProdMap.get(j);
					qInnerProdMap.put(j, origin + value);
				}
			}
		}
		
		for(int i=0; i<qInnerProd.length; i++) {
			qInnerProd[i] = Math.round(qInnerProd[i] * 100) / 100.0;
			double origin = qInnerProdMap.get(i);
			qInnerProdMap.put(i, Math.round(origin * 100) / 100.0);
		}
		
		
		
		//
		InputStream inputStream = new FileInputStream(new File("./index.xml"));
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document document = docBuilder.parse(is);
		
		Element docs = document.getDocumentElement(); //루트 노드 얻기 - docs
		NodeList docsList = docs.getChildNodes();
		
		for(int i=0; i < docsList.getLength(); i++) {
			Node docNode = docsList.item(i);
			if(docNode.getNodeType() == Node.ELEMENT_NODE) {
				Element docEl = (Element)docNode;
				NodeList docChildList = docEl.getChildNodes(); //현재 노드의 자식들 정보 리스트
				Node item = docChildList.item(0); //title 부분 노드만 추출
				if(item.getNodeType() == Node.ELEMENT_NODE) { //현재 노드가 Element이면 수행
					Element itemEl = (Element)item;
					if(itemEl.getNodeName().equals("title")) { //노드 이름이 title와 동일하면 수행
						//키 값이 i인 qInnerProdMap으로부터 값을 읽어와 해당 값에 맞는 타이틀을 넣음
						qMap.put(itemEl.getTextContent(), qInnerProdMap.get(i));
						
					}
				}
			}
		}
		
		List<Entry<String, Double>> qIPList = new ArrayList<Entry<String, Double>>(qMap.entrySet());
		Collections.sort(qIPList, new Comparator<Entry<String, Double>>() {
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		int count = 0;
		for(Entry<String, Double> entry : qIPList) {
			if(entry == null || count == 3) break;
			System.out.println("Q와 " + entry.getKey() + " 문서와의 내적 : " + entry.getValue());
			count++;
		}
		if(count == 0) {
			System.out.println("검색된 문서가 없습니다.");
		}
	}
	
}

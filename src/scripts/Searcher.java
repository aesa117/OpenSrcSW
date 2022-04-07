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
		InputStream inputStream = new FileInputStream(new File("./index.xml"));
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
		LinkedList<Integer> qList = new LinkedList<Integer>(); //문자의 빈도수 저장 리스트
		
		double[] qInnerProd = new double[docsList.getLength()]; //id번째 문서에 대한 내적 합
		double[] q_absolute = new double[docsList.getLength()];
		double[] id_absolute = new double[docsList.getLength()];
		String queryText[] = new String[kl.size()];
		
		HashMap<Integer, LinkedList<String>> idMap = new HashMap<Integer, LinkedList<String>>(); //각 문서의 단어에 대한 가중치 정보
<<<<<<< HEAD
		HashMap<String, Double> cosMap = new HashMap<String, Double>();
		
		for(int i=0; i<5; i++) {
			qInnerProd[i] = 0.0;
			q_absolute[i] = 0.0;
			id_absolute[i] = 0.0;
=======
		HashMap<String, Double> qMap = new HashMap<String, Double>();
		
		for(int i=0; i<5; i++) {
			qInnerProd[i] = 0.0;
>>>>>>> feature
		}
		
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
		
		for(int i=0; i<docsList.getLength(); i++) {
			for(int j=0; j<queryText.length; j++) {
				if(hm.containsKey(queryText[j])) {
					LinkedList<String> list = (LinkedList<String>)hm.get(queryText[j]);
					for(int k=0; k<list.size(); k++) {
						if(i == k) {
							String[] temp = list.get(k).split(" ");
							double weight = Double.parseDouble(temp[1]);
							
							double value = weight * qList.get(j);
							qInnerProd[i] += value;
<<<<<<< HEAD
							q_absolute[i] += Math.pow(qList.get(j), 2);
							id_absolute[i] += Math.pow(weight, 2);
=======
>>>>>>> feature
						}
					}
					
				}
			}
<<<<<<< HEAD
			q_absolute[i] = Math.sqrt(q_absolute[i]);
			id_absolute[i] = Math.sqrt(id_absolute[i]);
=======
>>>>>>> feature
		}
		
		for(int i=0; i < docsList.getLength(); i++) {
			Node docNode = docsList.item(i);
			if(docNode.getNodeType() == Node.ELEMENT_NODE) {
				Element docEl = (Element)docNode;
				NodeList docChildList = docEl.getChildNodes(); //현재 노드의 자식들 정보 리스트
				Node item = docChildList.item(0); //title 부분 노드만 추출
				if(item.getNodeType() == Node.ELEMENT_NODE) { //현재 노드가 Element이면 수행
					Element itemEl = (Element)item;
					if(itemEl.getNodeName().equals("title")) { //노드 이름이 title와 동일하면 수행
<<<<<<< HEAD
						double value = qInnerProd[i] / (q_absolute[i] * id_absolute[i]);
						value = Math.round(value * 100) / 100.0;
						cosMap.put(itemEl.getTextContent(), value);
=======
						double value = qInnerProd[i];
						value = Math.round(value * 100) / 100.0;
						qMap.put(itemEl.getTextContent(), value);
>>>>>>> feature
					}
				}
			}
		}
	
<<<<<<< HEAD
		List<Entry<String, Double>> qIPList = new ArrayList<Entry<String, Double>>(cosMap.entrySet());
=======
		List<Entry<String, Double>> qIPList = new ArrayList<Entry<String, Double>>(qMap.entrySet());
>>>>>>> feature
		Collections.sort(qIPList, new Comparator<Entry<String, Double>>() {
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		int count = 0;
		for(Entry<String, Double> entry : qIPList) {
			if(entry.getValue() == 0 || count == 3) break;
<<<<<<< HEAD
			System.out.println("Sim(Q, " + entry.getKey() + ") : " + entry.getValue());
=======
			System.out.println("(Q, " + entry.getKey() + ") : " + entry.getValue());
>>>>>>> feature
			count++;
		}
		if(count == 0) {
			System.out.println("검색된 문서가 없습니다.");
		}
	}
	
<<<<<<< HEAD
}
=======
}
>>>>>>> feature

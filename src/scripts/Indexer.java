package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Indexer {
	private String input_file;
	private String output_file = "./index.post";
	
	public Indexer(String file) {
		input_file = file;
	}
	
	public void makeIndexer() throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {
		//input_file 읽어오기
		InputStream inputStream = new FileInputStream(new File(input_file));
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		
		//Document 읽어오기
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document document = docBuilder.parse(is);
		
		Element docs = document.getDocumentElement(); //루트 노드 얻기 - docs
		NodeList docsList = docs.getChildNodes();
		
		//HashMap을 두개 생성
		HashMap<Keyword, Integer> tempMap = new HashMap<Keyword, Integer>();
		HashMap<String, LinkedList> kwMap = new HashMap<String, LinkedList>();
		
		//body 부분의 textContent를 String 배열에 저장
		String[] textContent = new String[docsList.getLength()];
		
		//반복문을 통해 Node의 자식들을 찾아가기
		for(int i=0; i < docsList.getLength(); i++) {
			Node docNode = docsList.item(i);
			if(docNode.getNodeType() == Node.ELEMENT_NODE) {
				Element docEl = (Element)docNode;
				NodeList docChildList = docEl.getChildNodes(); //현재 노드의 자식들 정보 리스트
				Node item = docChildList.item(1); //body 부분 노드만 추출
				if(item.getNodeType() == Node.ELEMENT_NODE) { //현재 노드가 Element이면 수행
					Element itemEl = (Element)item;
					if(itemEl.getNodeName().equals("body")) { //노드 이름이 body와 동일하면 수행
						textContent[i] = itemEl.getTextContent(); //i번째 id의 doc의 body 정보를 저장해두기
						String temp[] = textContent[i].split("#"); //단어 개수만큼 생성
						for(int j=0; j<temp.length; j++) { 
							String[] w = temp[j].split(":"); 
							boolean check = false;
							
							//반복자를 통해 tempMap의 Keyword 중에 하나인지 확인
							Iterator<Keyword> it = tempMap.keySet().iterator(); 
							while(it.hasNext()) {
								Keyword kwKey = it.next();
								if(kwKey.keyword.equals(w[0].trim())) {
									kwKey.setCnt(kwKey.getCnt() + 1);
									tempMap.put(kwKey, kwKey.cnt);
									check = true;
								}
							}
							
							//아직 동일한 문자열의 Keyword 객체가 없다면 생성 후 tempMap에 객체 추가
							if(check == false) {
								Keyword kw = new Keyword(w[0].trim());
								tempMap.put(kw, 0);
							}
							
							//kwMap에 넣을 list를 미리 0 0 0 0 0 0 0 0 0 0 상태로 초기화해둠
							LinkedList<String> list = new LinkedList<String>();
							for(int k=0; k<docsList.getLength(); k++) {
								list.add(String.valueOf(k)+ " " + "0.0");
							}
							kwMap.put(w[0].trim(), list);
						}
					}   
				}
			}
		}
		
		
		//반복문을 다시 수행하여 kwMap의 내용 채우기 시작
		for(int i=0; i<docsList.getLength(); i++) {
			String temp[] = textContent[i].split("#");
			for(int j=0; j<temp.length; j++) {
				String[] w = temp[j].split(":");
				int tf = Integer.parseInt(w[1].trim());
				Keyword kw;
				
				//반복자를 통해 tempMap에서 추출된 단어와 동일한 단어를 가진 Keyword 객체를 찾음
				Iterator<Keyword> it = tempMap.keySet().iterator(); 
				while(it.hasNext()) {
					Keyword kwKey = it.next();
					//동일한 단어를 가진 객체를 찾았다면
					if(kwKey.keyword.equals(w[0].trim())) {
						kw = kwKey;
						
						//kwMap에 있는 리스트를 찾고 가중치를 갱신함
						LinkedList<String> kl = (LinkedList<String>)kwMap.get(kw.keyword);
						double val = tf * (Math.log(docsList.getLength()) - Math.log(kw.cnt));
						val = Math.round(val * 100)/100.0;
						String id_weight = String.valueOf(i) + " " + String.valueOf(val);
						kl.set(i, id_weight);
						
						//kwMap을 갱신
						kwMap.put(kw.keyword, kl);
					}
				}
				
				
			}
		}
		
		//post 파일이 제대로 생성되었는지 확인하기 위해 파일을 읽어봄
		FileOutputStream fs = new FileOutputStream(output_file);
		ObjectOutputStream oos = new ObjectOutputStream(fs);
		oos.writeObject(kwMap);
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(output_file));
		Object obj = ois.readObject();
		ois.close();
		
		//Object로부터 HashMap을 받고 그에 맞는 반복자를 생성
		HashMap<String, LinkedList> hm = (HashMap)obj;
		Iterator<String> iter = hm.keySet().iterator();
		
		//반복자를 통해 키 값인 단어와 value 값인 list를 받아 출력함
		while(iter.hasNext()) {
			String key = iter.next();
			LinkedList<String> list = (LinkedList<String>)hm.get(key);
			System.out.print(key + " -> ");
			for(int i=0; i<list.size(); i++) {
				System.out.print(list.get(i) + " ");
			}
			System.out.println();
		}
		
		
	}
	
}

//단어에 대한 정보를 저장하기 위한 Keyword 객체
class Keyword {
	String keyword;
	int cnt = 0;
	LinkedList<String> kwList = new LinkedList<String>();
	
	public Keyword(String keyword) {
		this.keyword = keyword;
		this.cnt++;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	
	
}


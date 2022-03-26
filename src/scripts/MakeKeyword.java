package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

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

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MakeKeyword {
	private String input_file;
	private String output_file = "./index.xml";
	
	public MakeKeyword(String file) {
		this.input_file = file;
	}

	public void convertXml() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		//collection.xml 파일을 읽어서 UTF-8 형식으로 셋팅
				InputStream inputStream = new FileInputStream(new File(input_file));
				Reader reader = new InputStreamReader(inputStream, "UTF-8");
				InputSource is = new InputSource(reader);
				is.setEncoding("UTF-8");
				
				//Document 객체 생성을 위한 DocumentBuilderFactory, DocumentBuilder 객체 생성
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				
				//collection.xml의 Document와 index.xml의 Document 객체 생성
				Document document = docBuilder.parse(is);
				Document iDocument = docBuilder.newDocument();
				
				//collection.xml의 루트 노드와 루트 노드의 자식 노드 리스트
				Element docs = document.getDocumentElement(); //루트 노드 얻기 - docs
				NodeList docsList = docs.getChildNodes();
				
				//index.xml의 docs 요소를 생성하고 iDocument의 자식으로 추가
				Element iDocs = iDocument.createElement("docs");
				iDocument.appendChild(iDocs);
				
				//iDocument의 각 자식들이 될 요소들을 생성
				Element[] iDoc = new Element[docsList.getLength()];
				Element[] iTitle = new Element[docsList.getLength()];
				Element[] iBody = new Element[docsList.getLength()];
				
				//collection.xml의 자식 노드 리스트의 개수만큼 반복
				for(int i=0; i < docsList.getLength(); i++) {
					Node docNode = docsList.item(i);
					
					if(docNode.getNodeType() == Node.ELEMENT_NODE) { //얻은 노드의 정보가 Element라면
						//새로 만들어질 index.xml의 document 구성
						iDoc[i] = iDocument.createElement("doc");
						iDocs.appendChild(iDoc[i]);
						iDoc[i].setAttribute("id", String.valueOf(i));
							
						iTitle[i] = iDocument.createElement("title");
						iDoc[i].appendChild(iTitle[i]);
						iBody[i] = iDocument.createElement("body");
						iDoc[i].appendChild(iBody[i]);
						
						Element docEl = (Element)docNode;
						NodeList docChildList = docEl.getChildNodes(); //현재 노드의 자식들 정보 리스트
						
						for(int j=0; j < docChildList.getLength(); j++) {//title과 body 정보를 반복하여 읽음
							Node item = docChildList.item(j);
							if(item.getNodeType() == Node.ELEMENT_NODE) { //현재 노드가 Element이면 수행
								Element itemEl = (Element)item;
								if(itemEl.getNodeName().equals("title")) { //현재 노드의 이름이 "title"이라면
									iTitle[i].appendChild(iDocument.createTextNode(itemEl.getTextContent())); //title의 텍스트 정보 읽어와 index.xml에 추가
								} else { //현재 노드의 이름이 "title"이 아니면 "body"임
									//형태소 추출 객체 생성
									KeywordExtractor ke = new KeywordExtractor();
									KeywordList kl = ke.extractKeyword(itemEl.getTextContent(), true);
									//문자열을 받을 객체 생성
									String iBodyString = "";
									StringBuffer sb = new StringBuffer();
									
									//추출된 문자 갯수만큼 반복하여 키워드 정보를 얻고 문자열로 변환하여 추가하는 것을 반복
									for(int k=0; k < kl.size(); k++) {
										Keyword kwrd = kl.get(k);
										String s = kwrd.getString()+":"+ String.valueOf(kwrd.getCnt())+"#";
										sb.append(s); 
									}
									
									//추가된 StringBuffer의 정보를 문자열 형태로 바꾸어 index.xml의 body 부분에 추가
									iBodyString = sb.toString();
									iBody[i].appendChild(iDocument.createTextNode(iBodyString));
								}
							}
						}
						
						
					}
					
					
				}
				
				//TransformerFactory 객체를 이용하여 index.xml을 UTF-8 형식으로 생성
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				
				DOMSource source = new DOMSource(iDocument);
				StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));
				
				transformer.transform(source, result);
		
		System.out.println("3주차 실행완료");
	}

}

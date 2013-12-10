package detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.Constants;
import utils.FileUtil;
import categorize.CategoryName;

public class ResultProducer {

	private int ncCount;
	
	public ResultProducer() {
		ncCount = 0;
	}
	
	public int getNCTotal(){
		return this.ncCount;
	}

	public Set<TestError> listErrors(String path, int compiler){
		File results = new File(path);
		List<TestError> result;
		if(compiler == Constants.JMLC_COMPILER){
			result = getErrorsFromXML(results);
		} else {
			result = getErrorsFromFile(results);
		}
		Set<TestError> result2 = new HashSet<TestError>(result);
		return result2;
		
	}
	
	private List<TestError> getErrorsFromXML(File file) {
		List<TestError> result = new ArrayList<TestError>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder;

		try {
			docBuilder = dbf.newDocumentBuilder();
			Document xml = docBuilder.parse(file);

			NodeList list = xml.getDocumentElement().getElementsByTagName(
					"testcase");

			for (int i = 0; i < list.getLength(); i++) {
				Element testcase = (Element) list.item(i);
				if (testcase.hasChildNodes()) {
					NodeList subNodes = testcase.getChildNodes();
					for (int j = 0; j < subNodes.getLength(); j++) {
						if (subNodes.item(j) instanceof Element) {
							Element problem = (Element) subNodes.item(j);
							if (problem.getTagName().equals("error")) {
								String name = testcase.getAttribute("name");
								String errorType = problem.getAttribute("type");
								String message = problem
										.getAttribute("message");
								TestError te = new TestError(name, message,errorType);
								if(te.isNonconformance()){
									result.add(te);
									this.ncCount++;
								}
							} else if (problem.getTagName().equals("failure")) {
							}
						}
					}
				}

			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private List<TestError> getErrorsFromFile(File file) {
		List<TestError> result = new ArrayList<TestError>();
		try {
			FileReader f = new FileReader(file);
			BufferedReader in = new BufferedReader(f);
			String line = "";
			while ((line=in.readLine()) != null) {
				StringBuilder text = new StringBuilder();
				if (line.contains("JML ")) {
					text.append(in.readLine());
					if(!line.contains(CategoryName.PRECONDITION)){
						in.readLine();
						text.append(in.readLine());
						text.append(in.readLine());
						text.append("\n");
					}
					TestError te = new TestError(text.toString(), line);
					if(te.isNonconformance()){
						result.add(te);
						this.ncCount++;
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private Element createsElement(Document doc, String type, String message){
		Element e = doc.createElement("error");
		e.setAttribute("type", type);
		e.setAttribute("message", message);
		return e;
	}
	
	public Set<TestError> generateResult(int compiler){
		Set<TestError> errors = listErrors(Constants.TEST_RESULTS, compiler);
		Document doc = FileUtil.createXMLFile(Constants.RESULTS);
		Element raiz = doc.getDocumentElement();
		for (TestError testError : errors) {
			Element e = createsElement(doc, testError.getType(), testError.getMessage());
			raiz.appendChild(e);
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result;
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			result = new StreamResult(new FileOutputStream(Constants.RESULTS));
			transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
		return errors;
	}

}

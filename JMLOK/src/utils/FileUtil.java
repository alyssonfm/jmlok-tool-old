package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class that contains some methods to manipulate files.
 * @author Alysson Milanez
 * @version 1.0
 *
 */
public class FileUtil {

	/**
	 * Method to list all names into the project.
	 * @param path - base directory of the project.
	 * @param base - used to indicate the base name of directory, utilized for purposes of recursion.
	 * @return - The names of all files presents into the current project.
	 */
	public static List<String> listNames(String path, String base, String fileExtension) {
		List<String> result = new ArrayList<String>();
		try {
			File dir = new File(path);

			if (!dir.exists()) {
				throw new RuntimeException("Directory " + dir.getAbsolutePath()
						+ " does not exist.");
			}
            File[] arquivos = dir.listFiles();
			int tam = arquivos.length;
			for (int i = 0; i < tam; i++) {
				if (arquivos[i].isDirectory()) {
					String baseTemp = base + arquivos[i].getName() + ".";
					result.addAll(listNames(arquivos[i].getAbsolutePath(), baseTemp, fileExtension));
				} else {
					if (arquivos[i].getName().endsWith(fileExtension)) {
						String temp = base + arquivos[i].getName();
						temp = removeExtension(temp,fileExtension);
						if (!result.contains(temp))
							result.add(temp);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error in FileUtil.listNames()");
			e.printStackTrace();
		}
		return result; 
	}
	
	/**
	 * Method to remove the extension of the files.
	 * @param arquivo - the file that extension will be removed.
	 * @param extension - the extension to be removed.
	 * @return - the filename without the extension.
	 */
	private static String removeExtension(String arquivo, String extension) {
		arquivo = arquivo.replaceAll(extension+"\\b", "");
		return arquivo;
	}
	
	/**
	 * Method to creates a new file with the name and content received as parameter.
	 * @param name - the name of the new file.
	 * @param texto - the content of the file.
	 * @return - the new file created.
	 */
	public static File makeFile(String name, String texto) {
		File result = new File(name);
		try {
			result.createNewFile();
			FileWriter fw;
			while (!result.canWrite()) {
				result.createNewFile();
			}
			fw = new FileWriter(result);
			fw.write(texto);
			fw.close();
		} catch (IOException e) {
			System.err.println("Error in method FileUtil.makeFile()");
		}
		return result;
	}
	
	/**
	 * Method to read a file received as parameter.
	 * @param name - the name of the file to be read.
	 * @return - the content of the file.
	 */
	public static String readFile(String name) {
		String result = "";
		try {
			FileReader fr = new FileReader(new File(name));
			BufferedReader buf = new BufferedReader(fr);
			while (buf.ready()) {
				result += buf.readLine();
				result += "\n";
			}
			buf.close();
		} catch (Exception e) {
			System.err.println("Error in method FileUtil.readFile()");
		}
		return result;
	}
	
	/**
	 * Method that creates a xml file used to store the nonconformances detected.
	 * @param path - the name of the xml file to be produced.
	 * @return - the xml document.
	 */
	public static Document createXMLFile(String path){
		File f = new File(path);
		DocumentBuilderFactory docFactory;
		DocumentBuilder docBuilder;
		Document doc = null;
		
		try {
			f.createNewFile();
			while (!f.canWrite()) {
				f.createNewFile();
			}
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			
			Element root = doc.createElement("Nonconformances");
			doc.appendChild(root);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
		  } catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		
		return doc;
	} 
}


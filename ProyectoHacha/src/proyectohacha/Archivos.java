package proyectohacha;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Scanner;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Archivos {
	Scanner sc = new Scanner(System.in); //Scanner para guardar lo que introduzca el usuario
	private File archivo; //File que guarda el archivo que se divide
	private File direccionParticiones; //File que guarda la ubicacion de las particiones
	private File direccionXML; //File que guarda la ubicacion donde se creara el archivo XML
	
	//Metodo que asigna un archivo en el File, dado el enlace que proporciona el usuario, y llama al metodo existencia()
	public void selectorArchivo() {
		System.out.print("Escribe el enlace del archivo que quieras utilizar: ");
		String enlace = sc.nextLine();
		archivo = new File(enlace);
		existencia();
	}
	
	//Metodo que realiza un exists() con el archivo y llama al metodo confirmacion con el valor del exists como entrada
	private void existencia() {
		boolean existencia = archivo.exists();
		confirmacion(existencia);
	}
	
	//Metodo que, segun si el valor booleano de entrada es verdadero o falso, hara que el programa vuelva al menu principal o que vuelva al metodo selectorArchivo() respectivamente
	private void confirmacion(boolean existencia) {
		if (existencia == true)
			System.out.println("OK");
		else {
			System.out.println("El archivo que la ruta especifica no existe. Por favor, asegúrese de asignar la ruta de un archivo que exista");
			selectorArchivo();
		}
	}
	
	//Metodo que pregunta al usuario sobre en que directorio se guardara el archivo XML que se creara cuando se divida el archivo y guardara la direccion que introduzca el usuario en el File direccionXML
	public void directorioXML() {
		System.out.print("Escribe la direccion del XML: ");
		direccionXML = new File (sc.nextLine());
	}
	
	//Metodo que llamara al metodo dividir con el numero de partes como su entrada. En caso de que el archivo asignado no exista o no haya un archivo asignado volvera al metodo mostrarMenu()
	public void partes() {	
		try {
			if (archivo.exists()) {
				int partes;
				System.out.println("En cuantas partes quiere dividir el archivo?");
				partes = sc.nextInt();
				dividir(partes);
			} else {
				System.out.println("No se encuentra el archivo, por favor, elige un archivo que exista");
			}
		} catch (NullPointerException e) {
			System.out.println("No hay un archivo asignado. Por favor, asigna un archivo");
		}
		
	}
	
	//Realiza la division del archivo en multiples partes
	private void dividir(int partes) {
		
		int partCounter = 1; //Contador que determina en que particion del archivo se esta escribiendo

		long sizeOfFiles = archivo.length()/partes; //Tamanyo por defecto de cada particion del archivo
		byte[] buffer = new byte[(int) sizeOfFiles]; //Buffer de bytes en el que se introduce el tamanyo por defecto de cada particion del archivo

		String fileName = archivo.getName(); //Nombre del archivo

		
		try (FileInputStream fis = new FileInputStream(archivo);
				BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			while ((bytesAmount = bis.read(buffer)) > 0) {
				//Escritura de cada trozo del archivo en cada particion
				File newFile = new File(archivo.getAbsolutePath() + "." + partCounter++);
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					out.write(buffer, 0, bytesAmount);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		crearXML(fileName, archivo.length(), partCounter);
		
		/*try {
			int idparte = 1;
			int restante;
			int char1;
			int particiondefecto;
			
			FileInputStream leer = new FileInputStream(archivo);
			BufferedInputStream bl = new BufferedInputStream(leer);
			FileOutputStream escribir = new FileOutputStream(archivo+"part"+idparte);
			BufferedOutputStream bo = new BufferedOutputStream(escribir);
			
			char1 = bl.read();
			//tamanyototal = bl.available();
			restante=(int) archivo.length();
			//System.out.println(restante);
			particiondefecto = restante / partes;
			
			while(char1 != -1) {
				while(restante >= particiondefecto) {
					for (int i = 0; i != particiondefecto ; i++) {
						bo.write(char1);
						char1 = bl.read();
						restante--;
					}
					
					escribir = new FileOutputStream(archivo+"part"+idparte);
					bo = new BufferedOutputStream(escribir);
					idparte++;
				}	
				bo.write(char1);
				char1 = bl.read();
				restante--;
			}*/
			
			
			/*restante = tamanyototal - particiondefecto;
			while(char1 != -1) {
				while( != restante) {
					bo.write(char1);
					char1 = bl.read();
					archivoparte = archivoparte++;
				}
			}
			
		
			bl.close();
			leer.close();
			bo.close();
			escribir.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	//Crea el archivo XML con los datos del archivo a dividir
	private void crearXML(String nombreArchivo, long tamanyoArchivo, int numPartes) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			//Elemento raíz
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("ARCHIVO");
			doc.appendChild(rootElement);
			
			//Elemento Nombre
			String nombreSinExtension = "";
			
			int pos = nombreArchivo.lastIndexOf(".");
			if (pos > 0) {
				nombreSinExtension = nombreArchivo.substring(0, pos);
			}
			
			Element nombre = doc.createElement("NOMBRE");
			nombre.setTextContent(nombreSinExtension);
			rootElement.appendChild(nombre);
			
			//Elemento Extension
			String ext = "";
			
			int i = nombreArchivo.lastIndexOf('.');
			if (i > 0) {
				ext = nombreArchivo.substring(i+1);
			}
			
			Element extension = doc.createElement("EXTENSION");
			extension.setTextContent(ext);
			rootElement.appendChild(extension);
			
			
			//Elemento Tamaño
			Element tamanyo = doc.createElement("TAMANYO");
			tamanyo.setTextContent(Long.toString(tamanyoArchivo));
			rootElement.appendChild(tamanyo);
			
			//Elemento Partes
			Element partes = doc.createElement("PARTES");
			partes.setTextContent(Integer.toString(numPartes-1));
			rootElement.appendChild(partes);
			
			//Se escribe el contenido del XML en un archivo
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(direccionXML +"\\xml.xml"));
			
			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	public void direccionParticiones() {
		sc.nextLine();
		System.out.println("Escribe la ubicación de las particiones del archivo");
		direccionParticiones = new File(sc.nextLine());
	}
	
	//Lee y guarda los datos del archivo original desde los datos guardados en el XML creado para llamar al metodo unir()
	public void datosXML() {
		String XMLNombreArchivo = ""; //Nombre del archivo
		String XMLExtensionArchivo = "" ; //Extension del archivo
		long XMLTamanyoArchivo = 0; //Tamanyo del archivo
		int XMLPartesArchivo = 0; //En cuantas partes se ha dividido el archivo
		
		try {
            File XML = new File(direccionXML+ "\\xml.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(XML);
            document.getDocumentElement().normalize();
            NodeList nList = document.getElementsByTagName("ARCHIVO"); //La lista de nodos es el contenido de la etiqueta ARCHIVO
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    XMLNombreArchivo = eElement.getElementsByTagName("NOMBRE").item(0).getTextContent(); //Guardar nombre del archivo
                    XMLExtensionArchivo = eElement.getElementsByTagName("EXTENSION").item(0).getTextContent(); //Guardar extension del archivo
                    XMLTamanyoArchivo = Long.parseLong(eElement.getElementsByTagName("TAMANYO").item(0).getTextContent()); //Guardar tamanyo del archivo
                    XMLPartesArchivo = Integer.parseInt(eElement.getElementsByTagName("PARTES").item(0).getTextContent()); //Guardar partes en las que se ha dividido archivo
                    
                }
            }
            unir(XMLNombreArchivo, XMLExtensionArchivo, XMLTamanyoArchivo, XMLPartesArchivo);
        }
        catch(IOException e) {
            System.out.println(e);
        } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	//une las particiones en un solo archivo a partir de los datos de su XML
	private void unir(String XMLNombreArchivo, String XMLExtensionArchivo, long XMLTamanyoArchivo, int XMLPartesArchivo) {
		//Archivos XML = new Archivos(XMLNombreArchivo, XMLExtensionArchivo, XMLTamanyoArchivo, XMLPartesArchivo);
		try {
			//Creacion del archivo
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(direccionParticiones+ "\\"+ "Restaurado." + XMLExtensionArchivo));

		
			for(int i=1; i<=XMLPartesArchivo; i++) {
				//Acceso a las particiones
				File f = new File(direccionParticiones+ "\\" + XMLNombreArchivo + "." + XMLExtensionArchivo + "." + i);
				RandomAccessFile raf = new RandomAccessFile(f, "r");
				
				//Tamanyo de cada particion
				long tamanyoCopia = raf.length();

				//Escritura usando cada particion
				if(f.exists()) {
					byte[] buf = new byte[(int) tamanyoCopia];
					int val = raf.read(buf);
					if(val != -1) {
						bos.write(buf);
						raf.close();
					}	
				}
			}
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}	
}
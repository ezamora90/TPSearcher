package Searcher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Indexer {

	//Atributos
	private IndexWriter writer = null;

	//Metodos

	//Constructor
	public Indexer(){		
	}	

	//Si existe el indice lo devuelve y sino lo crea
	public IndexWriter getIndexWriter(boolean create, String indexPath) throws IOException {
		if (writer == null) {
			Path path = Paths.get(indexPath);
			Directory indexDir = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			writer = new IndexWriter(indexDir, config);
		}
		return writer;
	}


	//Indexa un documento
	private void atomIndex(String xmlPath, String indexPath){
		
		XMLdocsParser dataParser = new XMLdocsParser();
		Document thread = dataParser.parse(xmlPath);
		try {
			writer.addDocument(thread);
		} catch (IOException e) {
			System.out.println("Error al agregar el documento"+thread.get("Path"));
			e.printStackTrace();
		}	
		System.out.println("Documento "+thread.get("ThreadID")+" agregado al indice");

	}

	//Recorre el directorio dado e indexa los archivos xml encontrados
	public void index (String xmlPath, String indexPath){

		//Verifica que exista el directorio raiz de los archivos
		File f = new File(xmlPath);
		if (!f.exists()){ 
			System.out.println("La ruta "+xmlPath+" no existe");	
		}
		File[] directories = f.listFiles();

		//Inicializo el writer
		try {
			writer = getIndexWriter(false, indexPath);
		} catch (IOException e) {
			System.out.println("Error al crear el indice");
			e.printStackTrace();
		}

		//Por cada archivo en el directorio
		for (int x=0; x<directories.length; x++)	{
			// Path contiene la ruta raiz mas las carpetas y archivos
			String path = xmlPath+"\\"+directories[x].getName(); 
			System.out.println(path);

			String files;
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles(); 

			for (int i = 0; i < listOfFiles.length; i++) 
			{
				if (listOfFiles[i].isFile()) 
				{
					files = listOfFiles[i].getName();
					if (files.endsWith(".xml") || files.endsWith(".XML"))
					{
						System.out.println(path+"\\"+files);
						atomIndex(path+"\\"+files, indexPath);
					}
				}
			}
		}
		
		//Cierra el Writer
		try {
			closeIndexWriter();
		} catch (IOException e) {
			System.out.println("No se pudo cerrar el indice");
			e.printStackTrace();
		}
		System.out.println("indice creado");


	}

	//Cierra el indice
	public void closeIndexWriter() throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

}

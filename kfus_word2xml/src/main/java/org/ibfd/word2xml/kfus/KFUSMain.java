package org.ibfd.word2xml.kfus;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.ibfd.common.aspose.AsposeLicenseStore;
import org.ibfd.common.commandline.Options;
import org.ibfd.common.commandline.Options.Multiplicity;
import org.ibfd.common.commandline.Options.Separator;
import org.ibfd.common.lang.FileUtil;
import org.ibfd.word2xml.common.DocUtils;
import org.ibfd.word2xml.common.FileLogger;

import java.io.File;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSMain {
	
	/**
	 * Log file is created in the project's root directory
	 */
	public static FileLogger logger = new FileLogger("KfusWord2XML.log");
	
	/**
	 * For log purpose. Stores the file path that's being processed.
	 */
	public static String processingFilePath = "";

	/**
	 * Word Parser
	 */
	private static KFUSWordParser kFUSWordParser = new KFUSWordParser();
	
	/**
	 * Xml Builder
	 */
	private static KFUSXmlBuilder kFUSXmlBuilder = new KFUSXmlBuilder();
	
	/**
	 * Read Aspose License
	 */
	static {
		try{
			AsposeLicenseStore.getAsposeWords17License();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options(args);
		options.getSet().addOption("src", Separator.BLANK, Multiplicity.ONCE);
		options.getSet().addOption("des", Separator.BLANK, Multiplicity.ONCE);

		if(!options.check(false, false)){
			printUsage();
			exit(0);
		}
		processingFilePath = "";
		String src = options.getSet().getOption("src").getResultValue(0);
		String des = options.getSet().getOption("des").getResultValue(0);

		verifyExistence(src);
		verifyValidDirectory(des);

		final String absSrcDir = getAbsDir(src);
		final String absDesDir = getAbsDir(des);
			
		try{
			File srcFile = new File(src); 
			if ( srcFile.isDirectory() ){
				List<File> files = FileUtil.getAllFilesRecursively(new File(absSrcDir), ".*\\.docx?");
				
				if ( files == null || files.size() == 0 ){
					logger.error("Please check the src directory name. No \".doc\" or \".docx\" file is found there.");
				}
				
				for (Iterator<File> fIterator=files.iterator(); fIterator.hasNext(); ){
					File f = fIterator.next();
					processingFilePath = f.getAbsolutePath();
					convertWordToXml(processingFilePath, absDesDir);
				}
			}else{
				if ( srcFile.getName().toLowerCase().matches("^.*\\.docx?$")){
					processingFilePath = srcFile.getAbsolutePath();
					convertWordToXml(processingFilePath, absDesDir);
				}else {
					logger.error("File : " + srcFile.getName() + " is not a valid document file.");
				}
			}
		}catch (Exception e){
			handleException(e);
		}

		exit(0);
	}
	
		
   	/**
	 * 
	 * @param e
	 */
	public static void handleException(Exception e) {
		logger.error("Error while processing file: " + processingFilePath + " Error: " + e );
		e.printStackTrace();
	}
	
	 /**
     * 
     * @param wordFileLocation
     * @param destinationDir
     */
	private static void convertWordToXml(String wordFileLocation, String destinationDir) {
		try {
			System.out.println("\n\n## Converting " + wordFileLocation + " starts " );
			KFUSWordData kFUSWordData = kFUSWordParser.parse(wordFileLocation);
			Document xmlDoc = kFUSXmlBuilder.buildXml(kFUSWordData);
			String xmlFileName = kFUSXmlBuilder.getDestPath();
			if (StringUtils.isNotEmpty(xmlFileName)) {
				DocUtils.writeDocument(xmlDoc, new File(destinationDir,xmlFileName));
			}else {
				logger.error("Converting " + processingFilePath + " terminates : Output xml file name can not be produced." );
			}
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	/**
	 * 
	 * @param path
	 */
	private static void verifyValidDirectory(String path) {
		if (!new File(path).exists() && !new File(path).mkdirs()) {
			String err = path + " directory does not exist.";
			System.err.println(err);
			logger.error(err);
			exit(1);
		}
	}

	/**
	 * 
	 * @param code
	 */
	private static void exit(int code) {
		logger.close();
		if (code == 0 && ! logger.hasErrors()) { 
			System.out.print("--Program ended successfully.--"); 
		}
		System.exit(code);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private static String getAbsDir(String path) {
		File f = new File(path).getAbsoluteFile();
		return f.isFile() ? f.getParent() : f.getAbsolutePath();
	}

	/**
	 * 
	 */
	private static void printUsage() {
		
		System.out.println("Usage: java -jar kfusw2x.jar [-options]");
		System.out.println("where options include:");
		System.out.println();
		System.out.println("-src [value] \t File or Directory name. All DOC files in this directory or single file given will be considered as input file(s).");
		System.out.println("-des [value] \t Directory. All output XML files will be saved in this directory.");
		
	}

	/**
	 * 
	 * @param path
	 */
	private static void verifyExistence(String path) {
		if (!new File(path).exists()) {
			String err = path + " file/directory does not exist.";
			System.err.println(err);
			logger.error(err);
			exit(1);
		}
	}
}

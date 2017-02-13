package com.sedat.log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class LogToFile {

	
	public static Hashtable<String, StringBuffer> logStringRepository = new Hashtable<>() ;
	
	/**
	 * Buffer logs and if needed writes to log file
	 * Returns status type of int.
	 * 0 = start
	 * 1 = log is buffered
	 * 2 = log buffer is written to log file
	 * -1 = log repository error
	 * @param fileNameString
	 * @param logString
	 * @return
	 */
	public static synchronized int logToFile(String fileNameString,String logString){
		int status = 0;
		if(logStringRepository != null){// depoyu al
			StringBuffer logBuffer = logStringRepository.get(fileNameString);
			if(logBuffer == null){
				//create StringBuffer for this file's logs and add to repository
				logBuffer = new StringBuffer();
				logStringRepository.put(fileNameString, logBuffer);
			}
			//append log to logBuffer
			logBuffer.append(logString);
			status = 1;
			int sizeOfLog = logBuffer.toString().getBytes().length;
			if(sizeOfLog > 2*1024 ){//char length = 2 byte, greater than 1024 char
				if(logBufferToFile(fileNameString, logBuffer)){
					logBuffer = null;
					status=2;
				}
			}
		}else{//repository not initiated
			status=-1;
		}
		return status;
	}

	public static boolean logBufferToFile(String fileNameString, StringBuffer logBuffer){
		boolean result = false;
		BufferedWriter bWriter = null;
		try{
			String directory = "CmsGpsService"+File.separator+"Log";
			File directoryFile = new File(directory);
			if (!directoryFile.exists()) {
				directoryFile.mkdirs();
			}

			String filename = "CmsGpsService"+File.separator+"Log"+File.separator+fileNameString+".txt";
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fileWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fileWriter);
			bWriter.write(logBuffer.toString());
			result = true;
		}catch(Exception ex){
			ex.printStackTrace();
			result = false;
		}finally{
			if(bWriter != null){
				try {
					bWriter.flush();
					bWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static void flushLogBuffer(){
		Enumeration<String> fileNameList = logStringRepository.keys();
		while(fileNameList.hasMoreElements()){
			String fileName = fileNameList.nextElement();
			logBufferToFile(fileName, logStringRepository.get(fileName));
		}
	}
}

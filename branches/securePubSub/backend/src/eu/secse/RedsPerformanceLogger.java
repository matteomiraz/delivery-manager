package eu.secse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.secse.deliveryManager.utils.LogFileCreator;

public class RedsPerformanceLogger {
	
	public static final boolean LOG_MATCH_TIME = true;
	private static final String PREFIX_MATCH_TIME = "MATCH";
	
	public static final boolean LOG_MESSAGES = false;
	public static final Object LOG_MESSAGES_OBJ = new Object();
	private static final String PREFIX_MESSAGES = "MSG";
	
	private static final RedsPerformanceLogger singleton = new RedsPerformanceLogger();
	
	public static RedsPerformanceLogger getSingleton() {
		return singleton;
	}
	
	//--------------------------------------------------------
	
	private static final int SIZE = 1024 * 1024; 
	private static final int FLUSH_SIZE = SIZE - 1024; 
	private static final String SEPARATOR = "#";
	private File file;
	
	private StringBuffer buffer; 
	
	private RedsPerformanceLogger() { 
		buffer = new StringBuffer(SIZE);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						Thread.sleep(5 * 60 * 1000);
						flush();
					}
				} catch (InterruptedException e) {
				}
				
				flush();
			}
		});
		
		t.setDaemon(true);
		t.start();
		
		try {
			file = LogFileCreator.createFile("performanceReds", ".log");
			System.out.println("logging reds performance in " + file.getAbsolutePath());
		} catch (IOException e) {
		}
	}
	
	public String getFileName() {
		return file.getAbsolutePath();
	}
	
	public void setFileName(String fileName) {
		this.file = new File(fileName);
	}

	/* timestamp in nanoseconds */
	public void logMatchTime(long timestamp, String messageId, String messageType, String filterType, boolean matches, long duration) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX_MATCH_TIME).append(SEPARATOR).append(timestamp).append(SEPARATOR).append(messageId).append(SEPARATOR).append(messageType).append(SEPARATOR).append(filterType).append(SEPARATOR).append(matches).append(SEPARATOR).append(duration).append("\n");
		
		buffer.append(sb.toString());
		
		if(buffer.length() > FLUSH_SIZE)
			flush();
	}

	/* timestamp in milliseconds */
	public void logMessages(long timestamp, String messageId, String type, String elementId) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX_MESSAGES).append(SEPARATOR).append(timestamp).append(SEPARATOR).append(messageId).append(SEPARATOR).append(type).append(SEPARATOR).append(elementId).append("\n");
		
		buffer.append(sb.toString());
		
		if(buffer.length() > FLUSH_SIZE)
			flush();
	}


	public void flush() {
		StringBuffer oldBuffer = buffer;
		buffer = new StringBuffer(SIZE);
		
		if(oldBuffer.length() == 0) return;

		System.out.println("Saving reds performance in " + file.getAbsolutePath());
		
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(oldBuffer.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getSize() {
		return SIZE;
	}
	
	public int getUsed() {
		return buffer.length();
	}
	
	@Override
	protected void finalize() throws Throwable {
		flush();
		
		super.finalize();
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse;

import eu.secse.deliveryManager.utils.LogFileCreator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Mario
 */
public class CryptoPerformanceLogger {

    public static final boolean LOG_ENCRYPT_TIME = true;
    public static final boolean LOG_DECRYPT_TIME = true;
    public static final boolean LOG_SIGN_TIME = true;
    public static final String PREFIX_ENCRYPT = "Encryption";
    public static final String PREFIX_DECRYPT = "Decryption";
    public static final String PREFIX_SIGN = "Signing";
    private static final CryptoPerformanceLogger singleton = new CryptoPerformanceLogger();
    private static final int SIZE = 1024 * 1024;
    private static final int FLUSH_SIZE = SIZE - 1024;
    private static final String SEPARATOR = "#";
    private File file;
    private StringBuffer buffer;

    
    private CryptoPerformanceLogger() {
        buffer = new StringBuffer(SIZE);

        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5 * 60 *1000);
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
            file = LogFileCreator.createFile("performanceCryptographic", ".log");
            System.out.println("logging cryptographic performance in " + file.getAbsolutePath());
        } catch (IOException e) {
        }
    }

    public String getFileName() {
        return file.getAbsolutePath();
    }
    
    public static CryptoPerformanceLogger getSingleton() {
		return singleton;
	}

    public void setFileName(String fileName) {
        this.file = new File(fileName);
    }

    /* timestamp in nanoseconds */
    public void logEncryptionTime(String messageId,long duration) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_ENCRYPT).append(SEPARATOR).append(messageId).append(SEPARATOR).append(duration).append("\n");

        buffer.append(sb.toString());

        if (buffer.length() > FLUSH_SIZE) {
            flush();
        }
    }

    /* timestamp in milliseconds */
    public void logDecryptionTime(String messageId, long duration) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_DECRYPT).append(SEPARATOR).append(messageId).append(SEPARATOR).append(duration).append("\n");

        buffer.append(sb.toString());

        if (buffer.length() > FLUSH_SIZE) {
            flush();
        }
    }
    
    public void logSignTime(String messageId, long duration) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_SIGN).append(SEPARATOR).append(messageId).append(SEPARATOR).append(duration).append("\n");

        buffer.append(sb.toString());

        if (buffer.length() > FLUSH_SIZE) {
            flush();
        }
    }

    public void flush() {
        StringBuffer oldBuffer = buffer;
        buffer = new StringBuffer(SIZE);

        if (oldBuffer.length() == 0) {
            return;
        }
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

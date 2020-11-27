package org.ibfd.word2xml.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.ibfd.word2xml.kfus.KFUSMain;

/**
 * 
 * Legacy code
 *
 */
public class FileLogger {
	private String filePath;
	private BufferedWriter bw;
	private boolean log_infos = false;
	private boolean log_warnings = true;
	private boolean log_errors = true;
	ArrayList<String> errors = new ArrayList<String>();
	ArrayList<String> warnnings = new ArrayList<String>();
	ArrayList<String> infos = new ArrayList<String>();

	public FileLogger(String filePath) {
		try {
			this.filePath = filePath;
			bw = new BufferedWriter(new FileWriter(filePath));
		} catch (Exception e) {
			KFUSMain.handleException(e);
		}
	}

	public void info(String msg) {
		infos.add("Info: " + msg);
	}

	public void warning(String msg) {
		warnnings.add("Warning: " + msg);
	}

	public void error(String msg) {
		errors.add("Error: " + msg);
	}
	
	public boolean hasErrors() {
		return (errors.size() > 0);
	}

	private void log(ArrayList<String> msgs) {
		for (int i = 0; i < msgs.size(); i++) {
			log(msgs.get(i));
		}
		log("");
	}

	private void log(String info) {
		try {
			bw.write(info + "\r\n");
			System.err.print(info + "\r\n");
		} catch (IOException e) {
			KFUSMain.handleException(e);
		}
	}

	public void close() {
		try {
			String header = "\nLog generated at " + new File(filePath).getAbsolutePath() + "\n" + "with "
					+ (log_errors ? errors.size() + " error(s) " : "") 
					+ (log_warnings ? ", " + warnnings.size() + " warning(s) " : "") 
					+ (log_infos ? infos.size() + " information(s)" : "")
					+ ".\n";

			log(header);

			if (log_errors) {
				log(errors);
			}
			if (log_warnings) {
				log(warnnings);
			}
			if (log_infos) {
				log(infos);
			}

			bw.close();

			System.out.println(header);

		} catch (IOException e) {
			KFUSMain.handleException(e);
		}
	}
}
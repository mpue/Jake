/**

	Jake
	Written and maintained by Matthias Pueski 
	
	Copyright (c) 2009 Matthias Pueski
	
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 */
package org.pmedv.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FileUtils {

	protected static final Log log = LogFactory.getLog(FileUtils.class);
	private static final byte[] buffer = new byte[0xFFFF];

	public static String readFile(File file) {

		StringBuffer fileBuffer;
		String fileString = null;
		String line;

		try {
			FileReader in = new FileReader(file);
			BufferedReader dis = new BufferedReader(in);
			fileBuffer = new StringBuffer();

			while ((line = dis.readLine()) != null) {
				fileBuffer.append(line + "\n");
			}

			in.close();
			dis.close();
			fileString = fileBuffer.toString();
		}
		catch (IOException e) {
			return null;
		}
		return fileString;
	}

	/**
	 * Writes a string to a file;
	 * 
	 * @param file
	 * @param dataString
	 * @return
	 */
	public static boolean writeFile(File file, String dataString) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			out.print(dataString);
			out.flush();
			out.close();
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Copies a file
	 * 
	 * @param in Source file
	 * @param out Destination file
	 * @throws IOException
	 */
	public static void copyFile(File in, File out) throws IOException {

		int bufsize = 8192;
		int transferred = 0;

		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();

		try {
			while (transferred < inChannel.size()) {
				inChannel.transferTo(transferred, bufsize, outChannel);
				transferred += bufsize;
			}

		}
		catch (IOException e) {
			throw e;
		}
		finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

	/**
	 * Copies a file from in to out using a progress monitor
	 * 
	 * @param in the file to read from
	 * @param out the file to write to
	 * @param monitor the monitor interface to use
	 * @throws IOException
	 */
	public static void copyFile(File in, File out, IProgressMonitor monitor) throws IOException {

		int bufsize = 8192;
		int transferred = 0;

		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();

		try {
			while (transferred < inChannel.size()) {
				inChannel.transferTo(transferred, bufsize, outChannel);
				transferred += bufsize;
				int progress = (int) (transferred / (inChannel.size() / 100));
				monitor.setProgress(progress);
			}

		}
		catch (IOException e) {
			throw e;
		}
		finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

	public static void deleteFile(String FileLocation) throws Exception {

		File fileDelete = new File(FileLocation);

		if (fileDelete.delete() == true) {
			log.debug("deleted file : " + FileLocation);
		}
		else {
			log.debug("couldn't delete file : " + FileLocation);
			throw new Exception("could not delete file.");

		}
	}

	public static void renameFile(String oldName, String newName) throws Exception {

		File fileToRename = new File(oldName);
		File newFile = new File(newName);

		if (fileToRename.renameTo(newFile) == true) {
			log.debug("renamed file : " + oldName + " to " + newName);
		}
		else {
			log.debug("couldn't rename file : " + oldName);
			throw new IllegalArgumentException("could not rename file.");
		}

	}

	public static boolean makeDirectory(String destination) {

		File dir = new File(destination);

		if (dir.mkdir() == true) {
			log.debug("Created dirctory: " + destination);
			return true;
		}
		else {
			log.debug("couldn't create dir : " + destination);
			return false;
		}

	}

	public static void extractZipFile(String source, String destination) {

		try {
			ZipFile zipFile = new ZipFile(source);
			Enumeration<? extends ZipEntry> zipEntryEnum = zipFile.entries();

			while (zipEntryEnum.hasMoreElements()) {
				ZipEntry zipEntry = zipEntryEnum.nextElement();
				System.out.print(zipEntry.getName() + ".");
				extractEntry(zipFile, zipEntry, destination);
			}
		}

		catch (FileNotFoundException e) {
			System.err.println("Fehler: ZipFile nicht gefunden!");
		}
		catch (IOException e) {
			System.err.println("Fehler: Allgemeiner Ein-/Ausgabefehler!");
		}

	}

	private static void extractEntry(ZipFile zf, ZipEntry entry, String destDir) throws IOException {
		
		File file = new File(destDir, entry.getName());

		if (entry.isDirectory())
			file.mkdirs();
		else {
			new File(file.getParent()).mkdirs();

			InputStream is = null;
			OutputStream os = null;

			try {
				is = zf.getInputStream(entry);
				os = new FileOutputStream(file);

				for (int len; (len = is.read(buffer)) != -1;)
					os.write(buffer, 0, len);
			}
			finally {
				if (os != null)
					os.close();
				if (is != null)
					is.close();
			}
		}
	}

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 * 
	 * @param file is a file which already exists and can be read.
	 */
	public static String getContent(File file) {
		
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		return contents.toString();
	}

	/**
	 * Deletes a directory recursively
	 * 
	 * @param dir the directory to delete recursively
	 * 
	 * @return true if successful, false if not
	 */
	public static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * Creates a list of files of a directory recursively
	 * 
	 * @param fileList The list to fill with files
	 * @param rootdir The directory to start from
	 */
	public static void getDirectoryContents(List<File> fileList, File rootdir) {

		File[] list = rootdir.listFiles();

		if (list == null)
			throw new IllegalArgumentException("Directory does not exist.");

		for (int i = 0; i < list.length; i++) {
			File eachFile = (java.io.File) list[i];
			if (eachFile.isDirectory()) {
				getDirectoryContents(fileList, eachFile);
			}
			else if (eachFile.isFile()) {
				fileList.add(eachFile);
			}
		}

	}
	
	/**
	 * Downloads a file from the specified location. The file will be stored at
	 * the outputDir. The download is done using a progress mointor in order to
	 * track the download.
	 * 
	 * @param url The location to download the file from
	 * @param outputDir The directory to store the file to
	 * @param monitor The progres monitor to use for the download
	 * 
	 * @return true if the download was successful, false if not
	 * 
	 */
	public static boolean downloadFile(String url, String outputDir, IProgressMonitor monitor) throws Exception {
		
		String filename = url.substring(url.lastIndexOf("/") + 1);

		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(url);

		int numBytesRead = 0;

		try {

			int statusCode = httpClient.executeMethod(getMethod);

			// HTTP ok

			if (statusCode == 200) {

				// get the response as an InputStream

				InputStream in = getMethod.getResponseBodyAsStream();
				long totalLength = getMethod.getResponseContentLength();

				int progress = 0;

				FileOutputStream fos = new FileOutputStream(new File(outputDir + filename));

				byte[] b = new byte[1024];
				int len;

				while ((len = in.read(b)) != -1) {

					numBytesRead += len;
					// write byte to file
					fos.write(b, 0, len);

					double onePercent = (double) totalLength / 100;
					progress = (int) Math.round((double) numBytesRead / onePercent);
					monitor.setProgress(progress);
				}

				in.close();
				fos.close();

			}
			else
				throw new Exception("HTTP Error " + statusCode);

		}
		catch (HttpException e) {
			log.info("HttpException occured : " + e.getMessage());
			return false;
		}
		catch (IOException e) {
			log.info("IOException occured : " + e.getMessage());
			return false;
		}
		finally {
			// release the connection
			getMethod.releaseConnection();
		}

		return true;
	}

	/**
	 * recursively creates a list of urls for a given directory on the server in
	 * order to be able to download the files from the client.
	 * 
	 * @param directory The directory to search in
	 * @param hostUrl The host url to add to the filename (msut end with a
	 *            slash)
	 * 
	 * @return An array list of urls for the files contained in the directory
	 */
	public static ArrayList<String> getFileURLsForDirectory(String directory, String hostUrl) {

		ArrayList<File> files = new ArrayList<File>();
		FileUtils.getDirectoryContents(files, new File(directory));

		ArrayList<String> urlList = new ArrayList<String>();

		for (File f : files) {
			urlList.add(hostUrl + f.getAbsolutePath().substring(directory.length() + 1));
		}

		return urlList;
	}

	/**
	 * Recursively searches for a given filename and puts all found occurences
	 * of a given filename into an ArrayList.
	 * 
	 * @param fileList The list to put the found files in
	 * @param rootdir The directory to start the search from
	 * @param name The filename to search for
	 * @param ignoreCase if set to true, the case of the filename is ignored
	 */
	public static void findFile(List<File> fileList, File rootdir, String name, boolean ignoreCase, boolean sequence) {

		File[] list = rootdir.listFiles();

		if (list == null)
			return;
		
		for (int i = 0; i < list.length; i++) {

			File eachFile = (java.io.File) list[i];

			if (eachFile.isDirectory()) {
				findFile(fileList, eachFile, name, ignoreCase, sequence);
			}
			else if (eachFile.isFile()) {

				if (sequence) {

					if (ignoreCase)
						name = name.toLowerCase();

					if (eachFile.getName().toLowerCase().contains(name))
						fileList.add(eachFile);

				}
				else {

					if ((ignoreCase && eachFile.getName().equalsIgnoreCase(name))
							|| (!ignoreCase && eachFile.getName().equals(name))) {

						fileList.add(eachFile);
					}

				}

			}

		}

	}

}

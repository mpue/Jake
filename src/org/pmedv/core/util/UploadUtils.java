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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadUtils {

	private static final Log log = LogFactory.getLog(UploadUtils.class);
	
	private static String hostname;
	private static int port;
	private static String username;
	private static String password;
	
	static {

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("remoting.properties");		
		Properties uploadProps = new Properties();
		
		try {
			
			uploadProps.load(is);
			
			username = uploadProps.getProperty("username");
			password = uploadProps.getProperty("password");
			hostname = uploadProps.getProperty("host.name");
			port = Integer.valueOf(uploadProps.getProperty("host.port"));
			
		}
		catch (IOException e) {
			log.info("Could not load remoting.properties, is it in classpath?");
			throw new RuntimeException("Could not load upload.properties, is it in classpath?");
		}
		
	}
	


	public static boolean uploadFile(File sourceFile, String targetURL, UploadMonitor monitor) {
		
		log.info("uploading "+sourceFile+" to "+targetURL);
		
		PostMethod filePost = new PostMethod(targetURL);
		filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
		filePost.getParams().setContentCharset("ISO-8859-15");
		
		try {

			Part[] parts = { new CustomizableFilePart(sourceFile.getName(), sourceFile, monitor) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		    Credentials defaultcreds = new UsernamePasswordCredentials(username,password);
		    client.getState().setCredentials(new AuthScope(hostname,port, AuthScope.ANY_REALM), defaultcreds);
			
			int status = client.executeMethod(filePost);

			if (status == HttpStatus.SC_OK) {
				log.info("Upload complete, response=" + filePost.getResponseBodyAsString());
			} 
			else {
				log.info("Upload failed, response=" + HttpStatus.getStatusText(status));
				return false;
			}
			
		} 
		catch (Exception ex) {
			log.error("An exception occured :");
			log.error(ResourceUtils.getStackTrace(ex));
			return false;
		} 
		finally {
			filePost.releaseConnection();
		}

		return true;

	}

	public static boolean uploadFile(File sourceFile, String targetURL, UploadMonitor monitor, String requestParams) {
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("remoting.properties");		
		Properties uploadProps = new Properties();
		
		try {
			uploadProps.load(is);
		}
		catch (IOException e) {
			log.info("Could not load upload.properties, is it in classpath?");
			return false;
		}
		
		log.info("uploading "+sourceFile+" to "+targetURL);
		
		PostMethod filePost = new PostMethod(targetURL+requestParams);

		filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
		
		try {

			Part[] parts = { new CustomizableFilePart(sourceFile.getName(), sourceFile, monitor) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		    Credentials defaultcreds = new UsernamePasswordCredentials(username,password);
		    client.getState().setCredentials(new AuthScope(hostname,port, AuthScope.ANY_REALM), defaultcreds);

			int status = client.executeMethod(filePost);

			if (status == HttpStatus.SC_OK) {
				log.info("Upload complete, response=" + filePost.getResponseBodyAsString());
			} 
			else {
				log.info("Upload failed, response=" + HttpStatus.getStatusText(status));
				return false;
			}
			
		} 
		catch (Exception ex) {
			log.error("An exception occured :");
			log.error(ResourceUtils.getStackTrace(ex));
			return false;
		} 
		finally {
			filePost.releaseConnection();
		}

		return true;

	}
	
	
}

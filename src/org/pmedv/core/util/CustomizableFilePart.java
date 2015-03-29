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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomizableFilePart extends FilePart {

	private static final Log log = LogFactory.getLog(FilePart.class);

	private UploadMonitor uploadMonitor;

	private long fileSize = 0;

	public CustomizableFilePart(String name, File file, UploadMonitor uploadMonitor) throws FileNotFoundException {
		
		super(name, file);
		this.fileSize = file.length();
		this.uploadMonitor = uploadMonitor;
		
	}

	/**
	 * Write the data in "source" to the specified stream.
	 * 
	 * @param out The output stream.
	 * @throws IOException if an IO problem occurs.
	 * @see org.apache.commons.httpclient.methods.multipart.Part#sendData(OutputStream)
	 */
	
	protected void sendData(OutputStream out) throws IOException {

		if (lengthOfData() == 0) {

			// this file contains no data, so there is nothing to send.
			// we don't want to create a zero length buffer as this will
			// cause an infinite loop when reading.

			log.debug("No data to send.");
			
			return;
		}

		long bytesSent = 0;

		byte[] tmp = new byte[4096];
		
		InputStream instream = super.getSource().createInputStream();
		
		try {
		
			int len;
			
			while ((len = instream.read(tmp)) >= 0) {
				
				out.write(tmp, 0, len);
				bytesSent += len;

				uploadMonitor.completionStatus((int) (bytesSent / (this.fileSize / 100)));
				
			}
			
		}
		finally {
			instream.close();
		}
		
	}
	
}

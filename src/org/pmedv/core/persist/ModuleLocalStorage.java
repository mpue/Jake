package org.pmedv.core.persist;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

/**
 * A LocalStorage for modules. It respects the direcory property in JNLP mode.
 * 
 * 
 * 
 * Needed due to issue <a> HREF="https://appframework.dev.java.net/issues/show_bug.cgi?id=112">
 * https://appframework.dev.java.net/issues/show_bug.cgi?id=112</a>
 * 
 * @author puce
 */

public class ModuleLocalStorage extends LocalStorage {

	public ModuleLocalStorage(ApplicationContext context) {
		super(context);
	}

	@Override
	public boolean deleteFile(String fileName) throws IOException {
		File path = new File(getDirectory(), fileName);
		return path.delete();
	}

	@Override
	public InputStream openInputFile(String fileName) throws IOException {
		File path = new File(getDirectory(), fileName);
		return new BufferedInputStream(new FileInputStream(path));
	}

	@Override
	public OutputStream openOutputFile(String fileName) throws IOException {
		File path = new File(getDirectory(), fileName);
		return new BufferedOutputStream(new FileOutputStream(path));
	}
}
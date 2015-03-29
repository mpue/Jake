package org.pmedv.core.persist;

import java.io.File;

import org.jdesktop.appframework.swingx.XProperties;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.swingx.JXTable;

public class ModuleApplicationContext extends ApplicationContext {

	private String storageDirectoryPath = "";

	static {

		// download from
		// https://jdnc-incubator.dev.java.net/source/browse/jdnc-incubator/trunk/src/kleopatra/java/org/jdesktop/appframework/swingx/XProperties.java?rev=3198&view=markup
		new XProperties().registerPersistenceDelegates();
	}

	public ModuleApplicationContext(String path) {
		// Needed due to issue
		// https://appframework.dev.java.net/issues/show_bug.cgi?id=112
		setLocalStorage(new ModuleLocalStorage(this));
		// getLocalStorage().setDirectory(getModuleSessionStorageDir(moduleInfo));
		storageDirectoryPath = path;
		getLocalStorage().setDirectory(new File(storageDirectoryPath));
		getSessionStorage().putProperty(JXTable.class, new XProperties.XTableProperty());
	}

}
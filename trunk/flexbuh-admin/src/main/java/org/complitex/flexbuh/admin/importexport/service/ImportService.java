package org.complitex.flexbuh.admin.importexport.service;

import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.admin.importexport.entity.DictionaryConfig;
import org.complitex.flexbuh.service.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 12.08.11 16:37
 */
public abstract class ImportService {
	private final static Logger log = LoggerFactory.getLogger(ImportService.class);

	@EJB
    private ConfigBean configBean;

	@Asynchronous
	public void processFiles(ImportListener listener, Date beginDate, Date endDate) {
		try {

			for (File importFile : listImportFiles()) {
				ImportListener childListener = listener.getChildImportListener(importFile);
				try {
					processFile(listener, importFile, beginDate, endDate);
				} catch (Throwable th) {
					childListener.cancel();
				}
			}

		} catch (Throwable th) {
			log.debug("Import interrupted", th);
			listener.cancel();
		}
	}

	@Asynchronous
	public void processFiles(ImportListener listener, List<String> importFileNames, Date beginDate, Date endDate) {
		log.debug("Start import files: {}", importFileNames);
		listener.begin();
		try {
			File rootDir = getRootDir();
			for (String importFileName : importFileNames) {

				File importFile = new File(getRootDir(), importFileName);

				ImportListener childListener = listener.getChildImportListener(importFile);

				if (!importFile.exists()) {
					log.info("Import file '{}' does not exist in root dir {}", importFile, getRootDir());
					listener.cancel();
				} else {
					try {
						processFile(childListener, importFile, beginDate, endDate);
					} catch (Throwable th) {
						log.error("Cancel processing file " + importFile, th);
						childListener.cancel();
					}
				}
			}
			listener.completed();
		} catch (Throwable th) {
			log.debug("Import interrupted", th);
			listener.cancel();
		}
		log.debug("Import complete");
	}

	public void processFile(ImportListener listener, File importFile, Date beginDate, Date endDate) {
		ImportFileService fileService = getImportFileService(importFile.getName());
		if (fileService == null) {
			throw new RuntimeException("Can not find service for processing file " + importFile.getName());
		}
		fileService.process(listener, importFile, beginDate, endDate);
	}

	public void processFile(ImportListener listener, String importFileName, Date beginDate, Date endDate) {
		File importFile = new File(getRootDir(), importFileName);
		if (!importFile.exists()) {
			log.debug("Import file '{}' does not exist in root dir {}", importFile, getRootDir());
			listener.cancel();
			return;
		}
		processFile(listener, importFile, beginDate, endDate);
	}

	public File[] listImportFiles() {
		File importDir = getImportDir();

		Validate.isTrue(importDir.exists(), "Import dir do not exist: " + importDir);
		Validate.isTrue(importDir.isDirectory(), "Import dir " + importDir + " is not directory");

		return importDir.listFiles();
	}

	protected abstract ImportFileService getImportFileService(@NotNull String fileName);

	protected File getRootDir() {
		String dir = configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true);
		log.debug("root dir: {}", dir);

		File rootDir = new File(dir);

		Validate.isTrue(rootDir.exists(), "Import root dir do not exist: " + dir);
		Validate.isTrue(rootDir.isDirectory(), "Import root dir " + dir + " is not directory");

		return rootDir;
	}

	protected abstract File getImportDir();
}

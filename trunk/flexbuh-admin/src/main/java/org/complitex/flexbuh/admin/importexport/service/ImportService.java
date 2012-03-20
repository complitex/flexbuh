package org.complitex.flexbuh.admin.importexport.service;

import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.admin.importexport.entity.DictionaryConfig;
import org.complitex.flexbuh.common.service.AbstractImportListener;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ImportFileService;
import org.complitex.flexbuh.common.service.ImportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 12.08.11 16:37
 */
public abstract class ImportService<T> {
    private final static Logger log = LoggerFactory.getLogger(ImportService.class);

    @EJB
    private ConfigBean configBean;

    @Asynchronous
    public void processFiles(Long sessionId, @NotNull ImportListener<File> listener, @Null Date beginDate, @Null Date endDate) {
        listener.begin();

        File[] importFiles = listImportFiles();;

        MDC.put("module", "");
        log.debug("Import files: {}", importFiles);

        for (File importFile : importFiles) {
            try {
                //todo add to parent listener
                processFile(sessionId, new AbstractImportListener<T>() {}, importFile, beginDate, endDate);

                listener.processed(importFile);
            } catch (Throwable th) {
                listener.skip(importFile);

                log.error("Cancel import file " + importFile.getName(), th);
            }
        }

        listener.completed();
    }

    @Asynchronous
    public void processFiles(Long sessionId, @NotNull ImportListener<File> listener, @NotNull List<String> importFileNames,
                             @Null Date beginDate, @Null Date endDate) {
        log.debug("Start import files: {}", importFileNames);
        listener.begin();

        for (String importFileName : importFileNames) {

            File importFile = new File(getImportDir(), importFileName);

            if (importFile.exists()) {
                try {
                    //todo add to parent listener
                    processFile(sessionId, new AbstractImportListener<T>() {}, importFile, beginDate, endDate);

                    listener.processed(importFile);
                } catch (Throwable th) {
                    listener.skip(importFile);

                    log.error("Cancel processing file " + importFile, th);
                }
            } else {
                log.info("Import file '{}' does not exist in root dir {}", importFile, getRootDir());
                listener.skip(importFile);
            }
        }

        listener.completed();

        log.debug("Import complete");
    }

    public void processFile(Long sessionId, ImportListener<T> listener, File importFile, Date beginDate, Date endDate) {
        listener.begin();

        ImportFileService<T> fileService = getImportFileService(importFile.getName());

        if (fileService == null) {
            throw new RuntimeException("Can not find service for processing file " + importFile.getName());
        }

        try {
            fileService.process(sessionId, listener, importFile.getName(), new FileInputStream(importFile), null, beginDate, endDate);
        } catch (FileNotFoundException e) {
            listener.error();

            log.warn("Can not find file: " + importFile, e);
        }
    }

    public File[] listImportFiles() {
        File importDir = getImportDir();

        Validate.isTrue(importDir.exists(), "Import dir do not exist: " + importDir);
        Validate.isTrue(importDir.isDirectory(), "Import dir " + importDir + " is not directory");

        return importDir.listFiles(getFilenameFilter());
    }

    protected FilenameFilter getFilenameFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        };
    }

    protected abstract <T> ImportFileService<T> getImportFileService(@NotNull String fileName);

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

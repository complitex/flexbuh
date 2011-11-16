package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.common.service.ImportFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 19:35
 */
@Singleton
public class ImportTemplateControlService extends ImportService {
	private final static Logger log = LoggerFactory.getLogger(ImportTemplateControlService.class);

	private static final String SUB_DIR = "controls";

	private static final Pattern PATTERN = Pattern.compile("\\w{6}\\d{2}.[Xx][Mm][Ll]");

	@EJB
	private ImportTemplateControlFileService importTemplateFileService;

	@Override
	protected ImportFileService getImportFileService(@NotNull String fileName) {
		return importTemplateFileService;
	}

	@Override
	protected File getImportDir() {
		return new File(getRootDir(), SUB_DIR);
	}

	@Override
	protected FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return PATTERN.matcher(name).matches();
			}
		};
	}
}


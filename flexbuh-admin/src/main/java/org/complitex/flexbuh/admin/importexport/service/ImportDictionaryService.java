package org.complitex.flexbuh.admin.importexport.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.File;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 14:32
 */
@Singleton
public class ImportDictionaryService extends ImportService {
	private final static Logger log = LoggerFactory.getLogger(ImportDictionaryService.class);

	private static final String SUB_DIR = "spr";

	@EJB
	private ImportCurrencyXMLService importCurrencyXMLService;

	@EJB
	private ImportDocumentXMLService importDocumentXMLService;

	@EJB
	private ImportDocumentTermXMLService importDocumentTermXMLService;

	@EJB
	private ImportDocumentVersionXMLService importDocumentVersionXMLService;

	@EJB
	private ImportRegionXMLService importRegionXMLService;

	@EJB
	private ImportTaxInspectionXMLService importTaxInspectionXMLService;

	@Null
	@Override
	protected ImportFileService getImportFileService(@NotNull String fileName) {
		if (StringUtils.equalsIgnoreCase("SPR_CURRENCY.XML", fileName)) {
			return importCurrencyXMLService;
		} else if (StringUtils.equalsIgnoreCase("SPR_DOC.XML", fileName)) {
			return importDocumentXMLService;
		} else if (StringUtils.equalsIgnoreCase("SPR_TERM.XML", fileName)) {
			return importDocumentTermXMLService;
		} else if (StringUtils.equalsIgnoreCase("SPR_VER.XML", fileName)) {
			return importDocumentVersionXMLService;
		} else if (StringUtils.equalsIgnoreCase("SPR_REGION.XML", fileName)) {
			return importRegionXMLService;
		}else if (StringUtils.equalsIgnoreCase("SPR_STI.XML", fileName)) {
			return importTaxInspectionXMLService;
		}
		return null;
	}

	@Override
	protected File getImportDir() {
		return new File(getRootDir(), SUB_DIR);
	}
}

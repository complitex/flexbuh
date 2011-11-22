package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.common.service.ImportFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
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

    @EJB
    private ImportFieldCodeService importFieldCodeService;

	@Null
	@Override
	protected ImportFileService getImportFileService(@NotNull String fileName) {
        switch (fileName.toUpperCase()){
            case "SPR_CURRENCY.XML":
                return importCurrencyXMLService;
            case "SPR_DOC.XML":
                return importDocumentXMLService;
            case "SPR_TERM.XML":
                return importDocumentTermXMLService;
            case "SPR_VER.XML":
                return importDocumentVersionXMLService;
            case "SPR_REGION.XML":
                return importRegionXMLService;
            case "SPR_STI.XML":
                return importTaxInspectionXMLService;
            case "SPRFORFIELDS.XML":
                return importFieldCodeService;

            default:
                throw new RuntimeException("Import File Service is not found");
        }
	}

	@Override
	protected File getImportDir() {
		return new File(getRootDir(), SUB_DIR);
	}
}

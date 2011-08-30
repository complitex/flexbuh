package org.complitex.flexbuh.admin.importexport.service;

import javax.ejb.Stateless;
import java.io.File;
import java.io.IOException;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 15:34
 */
@Stateless
public class ImportTemplateXSLFileFileService extends ImportTemplateFileService {

	@Override
	protected String getData(File file) throws IOException {
		return changeWhitespaceUTF8(super.getData(file));
	}

	private String changeWhitespaceUTF8(String s){
        return s.replaceAll("&amp;nbsp;", "&amp;#160;");
    }
}

package org.complitex.flexbuh.admin.importexport.service;

import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 15:34
 */
@Stateless
public class ImportTemplateXSLFileFileService extends ImportTemplateFileService {

	@Override
	protected String getData(InputStream inputStream) throws IOException {
		return changeWhitespaceUTF8(super.getData(inputStream));
	}

	private String changeWhitespaceUTF8(String s){
        return s.replaceAll("&amp;nbsp;", "&amp;#160;");
    }
}

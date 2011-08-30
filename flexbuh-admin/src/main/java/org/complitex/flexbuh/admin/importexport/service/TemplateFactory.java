package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.entity.*;
import org.complitex.flexbuh.service.TemplateBean;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 18:45
 */
@Singleton
public class TemplateFactory {

	@EJB
	private TemplateBean templateBean;

	public AbstractTemplate instanceTemplate(@NotNull String fileName, String data) {
		String extention = getFileExtention(fileName);
		String name = getName(fileName);
		Date uploadDate = new Date();

		AbstractTemplate template;

		if ("xml".equalsIgnoreCase(extention)) {
			template = new TemplateControl(name, data, uploadDate);
		} else if ("xsd".equalsIgnoreCase(extention)) {
			template = new TemplateXSD(name, data, uploadDate);
		} else if ("xsl".equalsIgnoreCase(extention)) {
			template = new TemplateXSL(name, data, uploadDate);
		} else if ("fo".equalsIgnoreCase(extention)) {
			template = new TemplateFO(name, data, uploadDate);
		} else {
			return null;
		}

		if (templateBean.isExist(template)) {
			throw new RuntimeException("Template with name exist '" + fileName + "'");
		}

		templateBean.save(template);

		return template;
	}

	private String getFileExtention(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	private String getName(@NotNull String fileName){
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

}

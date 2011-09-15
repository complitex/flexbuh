package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.entity.template.*;
import org.complitex.flexbuh.service.TemplateBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 18:45
 */
@Stateless
public class TemplateService {
	@EJB
	private TemplateBean templateBean;

	public AbstractTemplate save(@NotNull String fileName, String data) {
		String extension = getFileExtension(fileName).toLowerCase();
		String name = getName(fileName);
		Date uploadDate = new Date();

		AbstractTemplate template;

        switch (extension){
            case "xml":
                template = new TemplateControl(name, data, uploadDate);
                templateBean.save((TemplateControl) template);
                break;
            case "xsd":
                template = new TemplateXSD(name, data, uploadDate);
                templateBean.save((TemplateXSD) template);
                break;
            case "xsl":
                template = new TemplateXSL(name, data, uploadDate);
                templateBean.save((TemplateXSL) template);
                break;
            case "fo":
                template = new TemplateFO(name, data, uploadDate);
                templateBean.save((TemplateFO) template);
                break;
            default:
                return null;
        }

		return template;
	}

	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	private String getName(@NotNull String fileName){
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}

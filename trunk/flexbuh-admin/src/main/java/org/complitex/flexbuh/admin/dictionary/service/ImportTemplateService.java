package org.complitex.flexbuh.admin.dictionary.service;

import org.complitex.flexbuh.admin.dictionary.entity.DictionaryConfig;
import org.complitex.flexbuh.admin.dictionary.web.DictionaryImportListener;
import org.complitex.flexbuh.common.entity.template.TemplateXML;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.TemplateXMLBean;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.io.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:30
 */
@Stateless
public class ImportTemplateService {
    private final static Logger log = LoggerFactory.getLogger(ImportTemplateService.class);

    private static final int BUFFER = 2048;
    private static final String DEFAULT_FILE_ENCODING = "CP1251";

    @EJB
    private TemplateXMLBean templateXMLBean;

    @EJB
    private ConfigBean configBean;

    @Asynchronous
    public void process(TemplateXMLType type, ImportListener<String> listener){
        String root = configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true);

        String[] fileNames = FileUtil.getFileNames(root, type.getSubDir(), type.getPattern());

        //count
        if (listener instanceof DictionaryImportListener){
            //todo ((DictionaryImportListener) listener).addCountTotal(fileNames.length);
        }

        listener.begin();

        for (String fileName : fileNames){
            InputStream inputStream = FileUtil.getFileInputStream(root, type.getSubDir(), fileName);

            processFile(type, listener, fileName, getData(inputStream));
        }

        listener.completed();
    }

    protected String getData(InputStream inputStream){
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            bufferedInputStream.mark(BUFFER);

            byte[] line = new byte[BUFFER];

            int n = bufferedInputStream.read(line);

            if (n != -1 && new String(line).toUpperCase().contains("UTF-8")) {
                bufferedInputStream.reset();

                return getData(bufferedInputStream, "UTF-8");
            }

            bufferedInputStream.reset();

            return changeWhitespaceUTF8(getData(bufferedInputStream, DEFAULT_FILE_ENCODING));
        } catch (IOException e) {
            log.error("Ошибка импорта файла");
        }

        return null;
    }

    protected String getData(InputStream inputStream, String encoding) throws IOException {
        StringBuilder data = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding));

        String line;

        while ((line = br.readLine()) != null){
            data.append(line).append('\n');
        }

        br.close();

        return data.toString();
    }

    private String changeWhitespaceUTF8(String s){
        return s.replaceAll("&amp;nbsp;", "&amp;#160;");
    }

    public void processFile(TemplateXMLType type, ImportListener<String> listener, String fileName, String data) {
        if (data == null){
            listener.skip(fileName);

            return;
        }

        try {
            TemplateXML templateXML = new TemplateXML(type, getName(fileName), data, DateUtil.getCurrentDate());
            templateXML.setId(templateXMLBean.getTemplateId(templateXML));

            templateXMLBean.save(templateXML);

            listener.processed(fileName);
        } catch (Exception e) {
            log.error("Ошибка импорта файла", e);

            listener.skip(fileName);
        }
    }

    private String getName(@NotNull String fileName){
        return fileName.substring(0, fileName.lastIndexOf('.')).toUpperCase();
    }
}

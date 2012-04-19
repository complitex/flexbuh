package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.admin.importexport.entity.DictionaryConfig;
import org.complitex.flexbuh.admin.importexport.web.DictionaryImportListener;
import org.complitex.flexbuh.common.entity.template.*;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.TemplateBean;
import org.complitex.flexbuh.common.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:30
 */
@Stateless
public class ImportTemplateService {
    private final static Logger log = LoggerFactory.getLogger(ImportTemplateService.class);

    private static final int BUFFER = 2048;
    private static final String DEFAULT_FILE_ENCODING = "CP1251";

    public static enum TYPE {
        FO("fo", "\\w{6}\\d{2}.[Ff][Oo]"),
        XSD("xsd", "((\\w{6}\\d{2})|(common_types)).[Xx][Ss][Dd]"),
        XSL("xsl", "\\w{6}\\d{2}.[Xx][Ss][Ll]"),
        CONTROL("controls", "\\w{6}\\d{2}.[Xx][Mm][Ll]");

        private String subDir;
        private Pattern pattern;

        private TYPE(String subDir, String pattern) {
            this.subDir = subDir;
            this.pattern = Pattern.compile(pattern);
        }

        public String getSubDir() {
            return subDir;
        }

        public Pattern getPattern() {
            return pattern;
        }
    }

    @EJB
    private TemplateBean templateBean;

    @EJB
    private ConfigBean configBean;

    @Asynchronous
    public void process(TYPE type, ImportListener<String> listener){
        String root = configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true);

        String[] fileNames = FileUtil.getFileNames(root, type.getSubDir(), type.getPattern());

        //count
        if (listener instanceof DictionaryImportListener){
            ((DictionaryImportListener) listener).addCountTotal(fileNames.length);
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

    public void processFile(TYPE type, ImportListener<String> listener, String fileName, String data) {
        if (data == null){
            listener.skip(fileName);

            return;
        }

        try {
            String name = getName(fileName);
            Date uploadDate = new Date();

            AbstractTemplate template;

            switch (type){
                case CONTROL:
                    template = new TemplateControl(name, data, uploadDate);
                    templateBean.save((TemplateControl) template);
                    break;
                case XSD:
                    template = new TemplateXSD(name, data, uploadDate);
                    templateBean.save((TemplateXSD) template);
                    break;
                case XSL:
                    template = new TemplateXSL(name, data, uploadDate);
                    templateBean.save((TemplateXSL) template);
                    break;
                case FO:
                    template = new TemplateFO(name, data, uploadDate);
                    templateBean.save((TemplateFO) template);
                    break;
            }

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

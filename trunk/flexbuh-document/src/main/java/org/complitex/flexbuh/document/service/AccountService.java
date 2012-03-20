package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.ApplicationConfig;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.io.NoCloseInputStream;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.util.ZipUtil;
import org.complitex.flexbuh.document.entity.CounterpartRowSet;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;
import org.complitex.flexbuh.document.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.03.12 17:41
 */
@Stateless
public class AccountService {
    private final static Logger log = LoggerFactory.getLogger(AccountService.class);

    private final static String XML_DIR = "xml";
    private final static String SPR_DIR = "spr";

    @EJB
    private DeclarationService declarationService;

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private ImportUserProfileXMLService importUserProfileXMLService;

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private CounterpartBean counterpartBean;

    @EJB
    private EmployeeBean employeeBean;

    @EJB
    private ConfigBean configBean;

    public void writeAccountZip(Long sessionId, OutputStream outputStream){
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream))){
            //Declaration
            declarationService.writeXmlZip(sessionId, zipOutputStream, XML_DIR);

            //Profile
            List<PersonProfile> personProfiles = personProfileBean.getAllPersonProfiles(sessionId);

            for(PersonProfile pp : personProfiles){
                if (PersonType.PHYSICAL_PERSON.equals(pp.getPersonType())){
                    pp.mergePhysicalNames();
                }
            }

            ZipUtil.writeZip(Settings.class, new Settings(personProfiles), zipOutputStream, SPR_DIR, Settings.FILE_NAME);

            //Counterpart
            ZipUtil.writeZip(CounterpartRowSet.class, new CounterpartRowSet(counterpartBean.getAllCounterparts(sessionId)),
                    zipOutputStream, SPR_DIR, CounterpartRowSet.FILE_NAME);

            //Employee
            ZipUtil.writeZip(EmployeeRowSet.class, new EmployeeRowSet(employeeBean.getAllEmployees(sessionId)),
                    zipOutputStream, SPR_DIR, EmployeeRowSet.FILE_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания архива учетной записи", e);
        }
    }

    public void readAccountZip(Long sessionId, InputStream inputStream){
        List<PersonProfile> profiles = personProfileBean.getAllPersonProfiles(sessionId);

        Locale locale = new Locale(configBean.getString(ApplicationConfig.SYSTEM_LOCALE, true));

        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream))){
            ZipEntry entry;

            //todo empty zip

            while((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();

                if (name.contains(".xml")){
                    if (name.startsWith("xml/")){
                        declarationService.save(sessionId, profiles, name, zipInputStream);
                    }else if (name.startsWith("spr/")){
                        if (name.contains(Settings.FILE_NAME.toLowerCase())){
                            importUserProfileXMLService.process(sessionId, null, name,
                                    new NoCloseInputStream(zipInputStream), locale, null, null);
                        }else if (name.contains(CounterpartRowSet.FILE_NAME.toLowerCase())){
                            counterpartBean.save(sessionId, new NoCloseInputStream(zipInputStream), locale);
                        }else if (name.contains(EmployeeRowSet.FILE_NAME.toLowerCase())){
                            employeeBean.save(sessionId, new NoCloseInputStream(zipInputStream), locale);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки архива учетной записи", e);
        }
    }
}

package org.complitex.flexbuh.document.service;

import com.google.common.io.ByteStreams;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.io.NoClosableInputStream;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.util.ZipUtil;
import org.complitex.flexbuh.document.entity.*;
import org.complitex.flexbuh.document.exception.ImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private PersonProfileBean personProfileBean;

    @EJB
    private CounterpartBean counterpartBean;

    @EJB
    private EmployeeBean employeeBean;

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

            ZipUtil.writeZip(PersonProfile.RS.class, new PersonProfile.RS (personProfiles), zipOutputStream, SPR_DIR,
                    PersonProfile.RS.FILE_NAME);

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

    public void readAccountZip(Long sessionId, InputStream inputStream) throws ImportException {
        try (BufferedInputStream buf = new BufferedInputStream(inputStream)) {
            byte[] data = ByteStreams.toByteArray(buf);

            Map<Long, Long> personProfileIdMap = new HashMap<>();

            //Person Profile
            readPersonProfile(sessionId, data, personProfileIdMap);

            //Counterpart, Employee, Declaration
            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(data))){
                ZipEntry entry;

                while((entry = zipInputStream.getNextEntry()) != null) {
                    String name = entry.getName();

                    if (name.startsWith(XML_DIR)){
                        readDeclaration(sessionId, zipInputStream, personProfileIdMap);
                    } else if (name.contains(CounterpartRowSet.FILE_NAME)){
                        readCounterpart(sessionId, zipInputStream, personProfileIdMap);
                    } else if (name.contains(EmployeeRowSet.FILE_NAME)){
                        readEmployee(sessionId, zipInputStream, personProfileIdMap);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка чтения архива учетной записи", e);
            throw new ImportException(e);
        }
    }

    private void readDeclaration(Long sessionId, InputStream inputStream, Map<Long, Long> personProfileIdMap) throws JAXBException {
        Declaration declaration = (Declaration) JAXBContext
                .newInstance(Declaration.class, DeclarationValue.class)
                .createUnmarshaller()
                .unmarshal(new NoClosableInputStream(inputStream));

        if (declaration.getPersonProfileId() == null || personProfileIdMap.containsKey(declaration.getPersonProfileId())){
            declaration.setId(null);
            declaration.setSessionId(sessionId);
            declaration.setPersonProfileId(personProfileIdMap.get(declaration.getPersonProfileId()));

            declaration.fillValuesFromXml();

            declarationBean.save(declaration);
        }
    }

    private void readEmployee(Long sessionId, InputStream inputStream, Map<Long, Long> personProfileIdMap) throws JAXBException {
        EmployeeRowSet employeeRowSet = (EmployeeRowSet) JAXBContext
                .newInstance(EmployeeRowSet.class)
                .createUnmarshaller()
                .unmarshal(new NoClosableInputStream(inputStream));

        List<Employee> employees = employeeRowSet.getEmployees();

        for (Employee employee : employees){
            if (personProfileIdMap.containsKey(employee.getPersonProfileId())){
                employee.setId(null);
                employee.setSessionId(sessionId);
                employee.setPersonProfileId(personProfileIdMap.get(employee.getPersonProfileId()));

                employeeBean.save(employee);
            }
        }
    }

    private void readCounterpart(Long sessionId, InputStream inputStream, Map<Long, Long> personProfileIdMap) throws JAXBException {
        CounterpartRowSet counterpartRowSet = (CounterpartRowSet) JAXBContext
                .newInstance(CounterpartRowSet.class)
                .createUnmarshaller()
                .unmarshal(new NoClosableInputStream(inputStream));

        List<Counterpart> counterparts = counterpartRowSet.getCounterparts();

        for (Counterpart counterpart : counterparts){
            if (personProfileIdMap.containsKey(counterpart.getPersonProfileId())){
                counterpart.setId(null);
                counterpart.setSessionId(sessionId);
                counterpart.setPersonProfileId(personProfileIdMap.get(counterpart.getPersonProfileId()));

                counterpartBean.save(counterpart);
            }
        }
    }

    private void readPersonProfile(Long sessionId, byte[] data, Map<Long, Long> personProfileIdMap) throws IOException, JAXBException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(data))){
            ZipEntry entry;

            while((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();

                if (fileName.endsWith(PersonProfile.RS.FILE_NAME)){
                    PersonProfile.RS rs = (PersonProfile.RS) JAXBContext.newInstance(PersonProfile.RS.class)
                            .createUnmarshaller()
                            .unmarshal(zipInputStream);

                    List<PersonProfile> personProfiles = rs.getRows();

                    for (PersonProfile personProfile : personProfiles){
                        Long oldId = personProfile.getId();

                        personProfile.setId(null);
                        personProfile.setSessionId(sessionId);

                        personProfileBean.save(personProfile);

                        personProfileIdMap.put(oldId, personProfile.getId());
                    }

                    break;
                }
            }
        }
    }
}

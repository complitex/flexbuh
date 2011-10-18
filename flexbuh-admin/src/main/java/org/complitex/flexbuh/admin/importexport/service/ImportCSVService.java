package org.complitex.flexbuh.admin.importexport.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.flexbuh.service.ImportFileService;

import javax.ejb.*;
import java.io.*;

/**
 * @author Pavel Sknar
 *         Date: 12.08.11 16:02
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public abstract class ImportCSVService implements ImportFileService {

	protected CSVReader getCsvReader(File file) throws FileNotFoundException, UnsupportedEncodingException {
        return new CSVReader(new InputStreamReader(new FileInputStream(
                    file), "cp1251"), ',', '"', 1);
    }

}

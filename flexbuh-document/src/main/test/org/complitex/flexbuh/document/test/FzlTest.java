package org.complitex.flexbuh.document.test;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.08.12 17:21
 */
public class FzlTest {
    public static void main(String... args) throws IOException {
        // Базовая дата - 31/12/1899
        Calendar base = Calendar.getInstance();
        base.set(1899, Calendar.DECEMBER, 31);

        byte[] array = Files.toByteArray(new File("C:\\Anatoly\\Java\\org.complitex.flexbuh\\dev\\zvit2opz\\worker.fzl"));
        ByteBuffer buffer = ByteBuffer.wrap(array);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int rows = buffer.getInt();

        int ptr = 4;

        for (int i = 0; i < rows; i++){
            byte[] inn = new byte[11];
            buffer.get(inn);

            System.out.println(new String(inn, Charset.forName("cp866")).trim());

            byte[] name = new byte[61];
            buffer.get(name);

            System.out.println(new String(name, Charset.forName("cp866")).trim());

            int dateIn = unsignedToBytes(buffer.getShort(ptr + 225));
            int dateOut = unsignedToBytes(buffer.getShort(ptr + 227));
            int birthday = unsignedToBytes(buffer.getShort(ptr + 335));

            ptr += 342;
            buffer.position(ptr);

            Calendar birthdayCalendar = Calendar.getInstance();
            birthdayCalendar.set(1899, Calendar.DECEMBER, 31);
            birthdayCalendar.add(Calendar.DAY_OF_MONTH, birthday - 8708);
            System.out.println("BD: " + birthdayCalendar.getTime());

            Calendar dateInCalendar = (Calendar) base.clone();
            dateInCalendar.add(Calendar.DATE, dateIn - 1);
            System.out.println("DI: " + dateInCalendar.getTime());

            Calendar dateOutCalendar = Calendar.getInstance();
            dateOutCalendar.set(1899, Calendar.DECEMBER, 31);
            dateOutCalendar.add(Calendar.DATE, dateOut - 1);
            System.out.println("DO: " + dateOutCalendar.getTime());
        }
    }

    public static int unsignedToBytes(short shortVal) {
        return shortVal >= 0 ? shortVal : 0x10000 + shortVal;
    }
}

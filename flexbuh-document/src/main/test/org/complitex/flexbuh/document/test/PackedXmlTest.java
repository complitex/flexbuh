package org.complitex.flexbuh.document.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.11 16:19
 */
public class PackedXmlTest {
    private final static int[] buf = {0x0E8, 0x0D5, 1, 3, 0x0C3, 0x0C1, 0x83, 0x3D, 0x0B7, 0x0F0, 0x41, 5, 7, 0x72, 0x10, 0x0E8};

    public static void main(String... args) throws Exception {
        FileInputStream fis = new FileInputStream("c:\\1.xml");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        byte[] header = new byte[15];
        fis.read(header);

        String s = new String(header);

        if (s.contains("PACKED_XML")){
            System.out.println("HELLO WORLD!");
        }

        System.out.println(s);
        
        int b;
        int index = -1;

        while ((b = fis.read()) != -1){
            out.write(++index < 160 ? b ^ buf[index%16] : b);
        }

        ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());

        inStream.skip(1);

        FileOutputStream outStream = new FileOutputStream("c:\\2.xml");
//
//        while ((b = inStream.read()) != -1) {
//            outStream.write(b);
//        }

        int propertiesSize = 5;
        byte[] properties = new byte[propertiesSize];
        if (inStream.read(properties, 0, propertiesSize) != propertiesSize)
            throw new Exception("input .lzma file is too short");
        SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
        if (!decoder.SetDecoderProperties(properties))
            throw new Exception("Incorrect stream properties");
        long outSize = 0;
        for (int i = 0; i < 8; i++)
        {
            int v = inStream.read();
            if (v < 0)
                throw new Exception("Can't read stream size");
            outSize |= ((long)v) << (8 * i);
        }
        if (!decoder.Code(inStream, outStream, outSize))
            throw new Exception("Error in data stream");

    }

}

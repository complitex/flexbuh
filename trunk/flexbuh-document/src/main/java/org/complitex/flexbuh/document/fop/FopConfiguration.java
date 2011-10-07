package org.complitex.flexbuh.document.fop;

import org.apache.fop.apps.FOPException;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.FontInfo;
import org.apache.fop.configuration.FontTriplet;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.10.11 18:12
 */
public class FopConfiguration {
    private final static List fonts = new ArrayList();

    private static class EmbeddedFontInfo extends FontInfo {
        private String metricsFile;
        private String embedFile;

        public EmbeddedFontInfo(String name, String metricsFile, boolean kerning, List<FontTriplet> fontTriplets, String embedFile) {
            super(name, metricsFile, kerning, fontTriplets, embedFile);

            this.metricsFile = metricsFile;
            this.embedFile = embedFile;
        }

        public static EmbeddedFontInfo create(String metricsFile, String embedFile,
                                              String tripletName, String tripletStyle, String tripletWeight,
                                              String tripletMTName, String tripletMTStyle, String tripletMTWeight){
            List<FontTriplet> fontTriplets = new ArrayList<>();

            fontTriplets.add(new FontTriplet(tripletName, tripletWeight, tripletStyle));
            fontTriplets.add(new FontTriplet(tripletMTName, tripletMTWeight, tripletMTStyle));

            return new EmbeddedFontInfo(null, metricsFile, true, fontTriplets, embedFile);
        }

        @Override
        public URL getEmbedFile() throws FOPException {
            return WebCommonResourceInitializer.class.getResource("font/" + embedFile);
        }

        @Override
        public URL getMetricsFile() throws FOPException {
            return WebCommonResourceInitializer.class.getResource("font/" + metricsFile);
        }
    }

    public static void init(){
        Configuration.put("fonts", fonts);

        addFont("arial.xml", "arial.ttf", "Arial", "normal", "normal", "ArialMT", "normal", "normal");
        addFont("arialbd.xml", "arialbd.ttf", "Arial", "normal", "bold", "ArialMT", "normal", "bold");
        addFont("ariali.xml", "ariali.ttf", "Arial", "italic", "normal", "ArialMT", "italic", "normal");
        addFont("arialbi.xml", "arialbi.ttf", "Arial", "italic", "bold", "ArialMT", "italic", "bold");

        addFont("times.xml", "times.ttf", "TimesNewRoman", "normal", "normal", "TimesNewRomanMT", "normal", "normal");
        addFont("timesbd.xml", "timesbd.ttf", "TimesNewRoman", "normal", "bold", "TimesNewRomanMT", "normal", "bold");
        addFont("timesi.xml", "timesi.ttf", "TimesNewRoman", "italic", "normal", "TimesNewRomanMT", "italic", "normal");
        addFont("timesbi.xml", "timesbi.ttf", "TimesNewRoman", "italic", "bold", "TimesNewRomanMT", "italic", "bold");
    }

    @SuppressWarnings("unchecked")
    public static void addFont(String metricsFile, String embedFile,
                               String tripletName, String tripletStyle, String tripletWeight,
                               String tripletMTName, String tripletMTStyle, String tripletMTWeight){
        fonts.add(EmbeddedFontInfo.create(metricsFile, embedFile,
                tripletName, tripletStyle, tripletWeight,
                tripletMTName, tripletMTStyle, tripletMTWeight));
    }

}

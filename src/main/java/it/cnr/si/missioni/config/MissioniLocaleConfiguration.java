package it.cnr.si.missioni.config;

import it.cnr.si.config.LocaleConfiguration;
import net.sf.jasperreports.engine.fonts.SimpleFontFamily;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MissioniLocaleConfiguration extends LocaleConfiguration {

    @Bean
    public SimpleFontFamily simpleFontFamily() {
        final SimpleFontFamily fontFamily = new SimpleFontFamily();

		fontFamily.setName("Times New Roman");
        fontFamily.setNormal("times.ttf");
        fontFamily.setBold("timesbd.ttf");
        fontFamily.setItalic("timesi.ttf");
        fontFamily.setBoldItalic("timesbi.ttf");
        fontFamily.setPdfEncoding("Identity-H");
        fontFamily.setPdfEmbedded(true);
        return fontFamily;
    }

}


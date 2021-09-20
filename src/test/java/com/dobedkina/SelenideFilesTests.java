package com.dobedkina;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.LocalFileHeader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import net.lingala.zip4j.io.inputstream.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTests {

    @Test
    public void txtFileTest() throws Exception {
        String result = null;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("example.txt")) {
            if (stream != null) {
                result = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        assertThat(result).contains("рыбатекст является альтернативой традиционному lorem ipsum");
        assertThat(result).hasLineCount(2);
    }

    @Test
    public void pdfFileTest() throws Exception {
        PDF parsed = new PDF(getClass().getClassLoader().getResource("Locators.pdf"));
        assertThat(parsed.author).contains("Michael Sorens");
        assertThat(parsed.text).contains("Sprinkled with Selenium usage tips, this is both a general-purpose set of recipes " +
                "for each technology as well as a cross-reference to map from one to another");
    }

    @Test
    public void excelFileTest() throws Exception {
        XLS parsed = new XLS(getClass().getClassLoader().getResource("example.xlsx"));
        assertThat(parsed.excel.getNumberOfSheets()).isEqualTo(1);
        assertThat(parsed.excel.getSheetAt(0).getRow(2).getCell(3).getStringCellValue()).isEqualTo("ООО \"Вкусные фрукты\"");
    }

    @Test
    public void zipArchiveTest() throws Exception {
        ZipFile zipFile  = new ZipFile("src/test/resources/example.zip");
        String password = "Qwerty123";
        LocalFileHeader localFileHeader;

        //Проверяем количество файлов в архиве
        assertThat(zipFile.getFileHeaders().size()).isEqualTo(3);

        try (InputStream inputStream = new FileInputStream(String.valueOf(zipFile))) {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream,password.toCharArray());
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                File extractedFile = new File(localFileHeader.getFileName());

            }

        }





    }
}

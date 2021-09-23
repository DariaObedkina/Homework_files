package com.dobedkina;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTests {

    @Test
    void txtFileTest() throws Exception {
        String result = null;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("example.txt")) {
                result = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }

        //Проверка текста в файле
        assertThat(result).contains("рыбатекст является альтернативой традиционному lorem ipsum");

        //Проверка количества строк
        assertThat(result).hasLineCount(2);
    }

    @Test
    void pdfFileTest() throws Exception {
        PDF parsed = new PDF(getClass().getClassLoader().getResource("Locators.pdf"));

        //Проверка автора
        assertThat(parsed.author).contains("Michael Sorens");

        //Проверка содержимого
        assertThat(parsed.text).contains("Sprinkled with Selenium usage tips, this is both a general-purpose set of recipes " +
                "for each technology as well as a cross-reference to map from one to another");
    }

    @Test
    void excelFileTest() throws Exception {
        XLS parsed = new XLS(getClass().getClassLoader().getResource("example.xlsx"));

        //Проверка количества листов в файле
        assertThat(parsed.excel.getNumberOfSheets()).isEqualTo(1);

        //Проверка содержимого определенной ячейки
        assertThat(parsed.excel.getSheetAt(0).getRow(2).getCell(3).getStringCellValue()).isEqualTo("ООО \"Вкусные фрукты\"");
    }

    @Test
    void zipArchiveTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/Archive.zip");
        String textInFiles = "Тестовый файл 123";
        String password = "Qwerty123";
        LocalFileHeader localFileHeader;

        //Проверка количества файлов в архиве
        assertThat(zipFile.getFileHeaders().size()).isEqualTo(3);


        try (InputStream inputStream = new FileInputStream(String.valueOf(zipFile))) {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream, password.toCharArray());
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                File extractedFile = new File(localFileHeader.getFileName());
                //Проверка, что все файлы в архиве содержат заданный текст
                assertThat(extractedFile.toString()).contains(textInFiles);
            }

        }
    }

    @Test
    void docxFileTest() throws Exception {
        File docxFile = new File("src/test/resources/example.docx");
        try (FileInputStream fis = new FileInputStream(docxFile)) {
            XWPFDocument doc = new XWPFDocument(fis);
            XWPFWordExtractor extract = new XWPFWordExtractor(doc);
            //Проверка содержимого файла
            assertThat(extract.getText()).contains("Lorem Ipsum является стандартной \"рыбой\" для текстов");
        }
    }


}

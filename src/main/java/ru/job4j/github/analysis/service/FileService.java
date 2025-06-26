package ru.job4j.github.analysis.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Этот сервис выполняет с текстовыми файлами на диске.
 * (используется для подгрузки тестовых JSON кейсов)
 */

@Service
public class FileService {

    /**
     * Данный метод выгружает содержимое текстовых файлов в String
     */
    public String readFileContent(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);

        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IOException(String.format("Файл адресу %s не найден", filePath));
        }
    }
}

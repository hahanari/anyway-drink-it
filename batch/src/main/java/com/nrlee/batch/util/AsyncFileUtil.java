package com.nrlee.batch.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncFileUtil {

    public void getFileContent(String filePath, Consumer<String> callback) throws Exception {
        String str = "";
        StringBuilder stringBuilder = new StringBuilder(4000);
        log.info("file path : " + filePath);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((new ClassPathResource(filePath)).getInputStream()));

        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }

        callback.accept(stringBuilder.toString());
    }

}

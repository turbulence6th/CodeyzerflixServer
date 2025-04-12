package com.codeyzerflix.admin.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Component
public class Flv2Mp4Converter {

    public InputStream convertFlvToMp4(InputStream flvInputStream) throws IOException, InterruptedException {
        File tempFlvFile = File.createTempFile("input-" + UUID.randomUUID(), ".flv");
        File tempMp4File = File.createTempFile("output-" + UUID.randomUUID(), ".mp4");

        boolean success = false;

        try (FileOutputStream fos = new FileOutputStream(tempFlvFile)) {
            // FLV input stream'ini temp dosyaya yaz
            IOUtils.copy(flvInputStream, fos);

            // FFmpeg komutunu çalıştır
            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", tempFlvFile.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-y",
                    tempMp4File.getAbsolutePath()
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            // FFmpeg loglarını oku (opsiyonel)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFmpeg] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
            }

            // Başarılı oldu
            success = true;

            // MP4 dosyasını stream olarak döndür (kapanınca silinecek şekilde)
            FileInputStream fileInputStream = new FileInputStream(tempMp4File);
            return new FilterInputStream(fileInputStream) {
                @Override
                public void close() throws IOException {
                    super.close();
                    tempMp4File.delete(); // MP4 dosyasını sil
                }
            };

        } finally {
            // FLV dosyasını her durumda sil
            if (tempFlvFile.exists()) {
                tempFlvFile.delete();
            }

            // Eğer dönüş başarısızsa (exception olduysa), MP4 dosyasını da sil
            if (!success && tempMp4File.exists()) {
               tempFlvFile.delete();
            }
        }
    }
}

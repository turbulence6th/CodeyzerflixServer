package com.codeyzerflix.admin.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.UUID;

@Component
public class Divx2Mp4Converter {
    public InputStream convertDivxToMp4(InputStream aviInputStream) throws IOException, InterruptedException {
        File tempAviFile = File.createTempFile("input-" + UUID.randomUUID(), ".avi");
        File tempMp4File = File.createTempFile("output-" + UUID.randomUUID(), ".mp4");

        boolean success = false;

        try (FileOutputStream fos = new FileOutputStream(tempAviFile)) {
            // AVI (DivX) input stream'ini geçici dosyaya yaz
            IOUtils.copy(aviInputStream, fos);

            // FFmpeg komutunu hazırla
            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", tempAviFile.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-y", // üzerine yaz
                    "-movflags",
                    "+faststart",
                    tempMp4File.getAbsolutePath()
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            // FFmpeg loglarını oku (isteğe bağlı)
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

            success = true;

            // MP4 dosyasını stream olarak döndür (kapanınca silinsin)
            FileInputStream fileInputStream = new FileInputStream(tempMp4File);
            return new FilterInputStream(fileInputStream) {
                @Override
                public void close() throws IOException {
                    super.close();
                    tempMp4File.delete();
                }
            };

        } finally {
            // AVI dosyasını sil
            if (tempAviFile.exists()) {
                tempAviFile.delete();
            }

            // Eğer dönüş başarısızsa MP4 dosyasını da sil
            if (!success && tempMp4File.exists()) {
                tempMp4File.delete();
            }
        }
    }
}

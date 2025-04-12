package com.codeyzerflix.common.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class FileTypeDetector {
    
    private static final byte[] MP4_MAGIC = {(byte)0x66, (byte)0x74, (byte)0x79, (byte)0x70}; // ftyp
    private static final byte[] WEBM_MAGIC = {(byte)0x1A, (byte)0x45, (byte)0xDF, (byte)0xA3}; // WEBM
    private static final byte[] QUICKTIME_MAGIC = {(byte)0x66, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x71, (byte)0x74}; // ftypqt
    private static final byte[] MOV_MAGIC = {(byte)0x6D, (byte)0x6F, (byte)0x6F, (byte)0x76}; // moov
    private static final byte[] MOV_MAGIC2 = {(byte)0x6D, (byte)0x64, (byte)0x61, (byte)0x74}; // mdat
    private static final byte[] FLV_MAGIC = {(byte)0x46, (byte)0x4C, (byte)0x56, (byte)0x01}; // FLV
    
    private static final byte[] JPEG_MAGIC = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] PNG_MAGIC = {(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47};
    private static final byte[] GIF_MAGIC = {(byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38}; // GIF8
    
    public static String detectVideoType(MultipartFile file) throws IOException {
        byte[] buffer = readFileHeader(file, 1024); // İlk 1KB'ı oku
        
        if (findMagicBytes(buffer, MP4_MAGIC) != -1) {
            return "video/mp4";
        } else if (findMagicBytes(buffer, WEBM_MAGIC) != -1) {
            return "video/webm";
        } else if (findMagicBytes(buffer, QUICKTIME_MAGIC) != -1) {
            return "video/quicktime";
        } else if (findMagicBytes(buffer, MOV_MAGIC) != -1 || findMagicBytes(buffer, MOV_MAGIC2) != -1) {
            return "video/quicktime"; // MOV formatı quicktime olarak döner
        } else if (findMagicBytes(buffer, FLV_MAGIC) != -1) {
            return "video/x-flv";
        }
        
        throw new IllegalArgumentException("Desteklenmeyen video formatı");
    }
    
    public static String detectImageType(MultipartFile file) throws IOException {
        byte[] buffer = readFileHeader(file, 16); // Resimler için ilk 16 byte yeterli
        
        if (findMagicBytes(buffer, JPEG_MAGIC) != -1) {
            return "image/jpeg";
        } else if (findMagicBytes(buffer, PNG_MAGIC) != -1) {
            return "image/png";
        } else if (findMagicBytes(buffer, GIF_MAGIC) != -1) {
            return "image/gif";
        }
        
        throw new IllegalArgumentException("Desteklenmeyen resim formatı");
    }
    
    /**
     * MIME type'a göre dosya uzantısını döndürür
     * @param contentType MIME type
     * @return Dosya uzantısı (örn: .mp4, .jpg)
     */
    public static String getFileExtension(String contentType) {
        switch (contentType) {
            case "video/mp4":
                return ".mp4";
            case "video/webm":
                return ".webm";
            case "video/quicktime":
                return ".mov";
            case "video/x-flv":
                return ".flv";
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            default:
                throw new IllegalArgumentException("Desteklenmeyen dosya tipi: " + contentType);
        }
    }
    
    private static byte[] readFileHeader(MultipartFile file, int length) throws IOException {
        byte[] buffer = new byte[length];
        try (InputStream is = file.getInputStream()) {
            int read = is.read(buffer);
            if (read < 0) {
                throw new IOException("Dosya okunamadı");
            }
        }
        return buffer;
    }
    
    private static int findMagicBytes(byte[] buffer, byte[] magic) {
        for (int i = 0; i <= buffer.length - magic.length; i++) {
            boolean found = true;
            for (int j = 0; j < magic.length; j++) {
                if (buffer[i + j] != magic[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }
} 
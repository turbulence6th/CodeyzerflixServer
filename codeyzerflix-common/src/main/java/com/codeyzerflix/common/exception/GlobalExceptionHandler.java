package com.codeyzerflix.common.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.codeyzerflix.common.dto.ErrorResponse;
import com.mongodb.MongoException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, WebRequest request) {
        log.error("Dosya işlemi hatası: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Dosya İşlemi Hatası")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Geçersiz istek: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Geçersiz İstek")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(MongoException.class)
    public ResponseEntity<ErrorResponse> handleMongoException(MongoException ex, WebRequest request) {
        log.error("MongoDB hatası: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Veritabanı Hatası")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("Dosya boyutu hatası: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
            .error("Dosya Boyutu Hatası")
            .message("Yüklenen dosya izin verilen maksimum boyutu aşıyor")
            .path("/admin/videos/upload")
            .build();
        
        return ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Çalışma zamanı hatası: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Çalışma Zamanı Hatası")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Beklenmeyen hata: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Sunucu Hatası")
            .message("Beklenmeyen bir hata oluştu")
            .build();
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
    
    /**
     * Video streaming sırasında oluşan hataları işler.
     * Bu metot, video streaming endpoint'lerinde oluşan hataları yakalar ve
     * uygun bir hata yanıtı döndürür.
     */
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotWritableException(
            org.springframework.http.converter.HttpMessageNotWritableException ex, 
            HttpServletRequest request) {
        
        log.error("Video streaming hatası: {}", ex.getMessage(), ex);
        
        // İstek URL'sini kontrol et
        String requestURI = request.getRequestURI();
        boolean isVideoStreamingRequest = requestURI != null && 
                (requestURI.contains("/videos/") && requestURI.contains("/stream"));
        
        if (isVideoStreamingRequest) {
            // Video streaming isteği için özel hata yanıtı
            ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Video Streaming Hatası")
                .message("Video akışı sırasında bir hata oluştu")
                .path(requestURI)
                .build();
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
        
        // Diğer istekler için genel hata yanıtı
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Sunucu Hatası")
            .message("İstek işlenirken bir hata oluştu")
            .path(requestURI)
            .build();
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error);
    }
    
    /**
     * İstemci tarafından bağlantının iptal edilmesi durumunda oluşan hataları işler.
     * Bu durum genellikle video streaming sırasında kullanıcı videoyu durdurduğunda
     * veya başka bir konuma atladığında meydana gelir.
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        boolean isVideoStreamingRequest = requestURI != null && 
                (requestURI.contains("/videos/") && requestURI.contains("/stream"));
        
        if (isVideoStreamingRequest) {
            // Video streaming isteği için sadece log kaydı tut, hata yanıtı dönme
            log.info("Video streaming bağlantısı istemci tarafından iptal edildi: {}", requestURI);
        } else {
            // Diğer istekler için normal hata loglaması
            log.warn("İstemci bağlantısı iptal edildi: {}", requestURI, ex);
        }
        
        // ClientAbortException durumunda yanıt dönmeye gerek yok
        // çünkü istemci zaten bağlantıyı kapatmış durumda
    }
} 
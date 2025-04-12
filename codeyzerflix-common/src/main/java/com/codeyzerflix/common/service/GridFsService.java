package com.codeyzerflix.common.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;

    public String saveFile(InputStream inputStream, String filename, String contentType) throws IOException {
        return gridFsTemplate.store(
            inputStream,
            filename,
            contentType
        ).toString();
    }

    public InputStream getFile(String fileId) throws IOException {
        GridFSFile file = getGridFSFile(fileId);
        return gridFsTemplate.getResource(file).getInputStream();
    }

    public InputStream getFileRange(String fileId, long start, long end) throws IOException {
        GridFSFile file = getGridFSFile(fileId);
        InputStream inputStream = gridFsTemplate.getResource(file).getInputStream();
        
        // Başlangıç pozisyonuna atla
        long skipped = inputStream.skip(start);
        if (skipped < start) {
            log.warn("İstenen başlangıç pozisyonuna ulaşılamadı. İstenen: {}, Atlanan: {}", start, skipped);
            throw new IOException("İstenen başlangıç pozisyonuna ulaşılamadı");
        }
        
        // Eğer end parametresi belirtilmişse, dosya boyutunu kontrol et
        if (end > 0) {
            long fileLength = file.getLength();
            if (end > fileLength) {
                log.warn("İstenen bitiş pozisyonu dosya boyutundan büyük. İstenen: {}, Dosya boyutu: {}", end, fileLength);
                end = fileLength;
            }
        }
        
        return inputStream;
    }

    public long getFileLength(String fileId) throws IOException {
        GridFSFile file = getGridFSFile(fileId);
        return file.getLength();
    }

    public void deleteFile(String fileId) {
        gridFsTemplate.delete(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(fileId)
            )
        );
    }
    
    public GridFSFile getGridFSFile(String fileId) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(
            Query.query(
                Criteria.where("_id").is(fileId)
            )
        );
        
        if (file == null) {
            throw new IOException("GridFSFile bulunamadı: " + fileId);
        }

        return file;
    }
} 
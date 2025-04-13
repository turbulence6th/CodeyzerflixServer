package com.codeyzerflix.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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
    private final MongoTemplate mongoTemplate;

    public String saveFile(InputStream inputStream, String filename, String contentType) throws IOException {
        return gridFsTemplate.store(
            inputStream,
            filename,
            contentType
        ).toString();
    }

    public InputStream getFile(ObjectId fileId) throws IOException {
        GridFSFile file = getGridFSFile(fileId);
        return gridFsTemplate.getResource(file).getInputStream();
    }

    public InputStream getFileRange(ObjectId fileId, long start, long end) throws IOException {
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

    public long getFileLength(ObjectId fileId) throws IOException {
        GridFSFile file = getGridFSFile(fileId);
        return file.getLength();
    }

    public void deleteFile(ObjectId fileId) {
        gridFsTemplate.delete(
            Query.query(
                Criteria.where("_id").is(fileId)
            )
        );
    }
    
    public GridFSFile getGridFSFile(ObjectId fileId) throws IOException {
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

    public List<ObjectId> findUnusedGridFSFileIds() {
        Aggregation aggregation = Aggregation.newAggregation(
                // matchedVideos array'i oluştur
                Aggregation.lookup("videos", "_id", "file_id", "matchedVideos"),

                // Şu anki zamandan 30 dakika çıkaran bir alan oluştur
                Aggregation.addFields()
                        .addFieldWithValue("thirtyMinutesAgo",
                                Document.parse("{ $dateSubtract: { startDate: '$$NOW', unit: 'minute', amount: 30 } }"))
                        .build(),

                // matchedVideos boş ve uploadDate < thirtyMinutesAgo olanları filtrele
                Aggregation.match(Criteria.where("matchedVideos").size(0)
                        .andOperator(Criteria.where("uploadDate").lt("$thirtyMinutesAgo"))),

                // Sadece _id'leri projekte et
                Aggregation.project("_id")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "fs.files", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> doc.getObjectId("_id"))
                .collect(Collectors.toList());
    }

    public void deleteUnusedGridFsFiles() {
        List<ObjectId> unusedFileIds = findUnusedGridFSFileIds();

        for (ObjectId fileId : unusedFileIds) {
            deleteFile(fileId);
            System.out.println("Deleted unused file: " + fileId);
        }
    }
} 
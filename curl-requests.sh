#!/bin/bash

# Renk tanımlamaları
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}CodeyzerFlix API Test İstekleri${NC}\n"

# Admin API İstekleri
echo -e "${GREEN}Admin API İstekleri:${NC}\n"

# Video Yükleme
echo -e "${BLUE}Video Yükleme:${NC}"
echo "curl -X POST http://localhost:8081/admin/videos/upload \\"
echo "  -F \"title=Test Video\" \\"
echo "  -F \"description=Bu bir test videosudur\" \\"
echo "  -F \"file=@/path/to/your/video.mp4\" \\"
echo "  -F \"thumbnail=@/path/to/your/thumbnail.jpg\""
echo ""

# Tüm Videoları Listele
echo -e "${BLUE}Tüm Videoları Listele:${NC}"
echo "curl -X GET http://localhost:8081/admin/videos"
echo ""

# Video Detayı
echo -e "${BLUE}Video Detayı:${NC}"
echo "curl -X GET http://localhost:8081/admin/videos/YOUR_VIDEO_ID"
echo ""

# Video Silme
echo -e "${BLUE}Video Silme:${NC}"
echo "curl -X DELETE http://localhost:8081/admin/videos/YOUR_VIDEO_ID"
echo ""

# Thumbnail Yükleme
echo -e "${BLUE}Thumbnail Yükleme:${NC}"
echo "curl -X POST http://localhost:8081/admin/videos/YOUR_VIDEO_ID/thumbnail \\"
echo "  -F \"thumbnail=@/path/to/your/thumbnail.jpg\""
echo ""

# Public API İstekleri
echo -e "${GREEN}Public API İstekleri:${NC}\n"

# Tüm Videoları Listele
echo -e "${BLUE}Tüm Videoları Listele:${NC}"
echo "curl -X GET http://localhost:8080/api/videos"
echo ""

# Video Detayı
echo -e "${BLUE}Video Detayı:${NC}"
echo "curl -X GET http://localhost:8080/api/videos/YOUR_VIDEO_ID"
echo ""

# Video Arama
echo -e "${BLUE}Video Arama:${NC}"
echo "curl -X GET \"http://localhost:8080/api/videos/search?keyword=test\""
echo ""

# Video Akışı
echo -e "${BLUE}Video Akışı:${NC}"
echo "curl -X GET http://localhost:8080/api/videos/YOUR_VIDEO_ID/stream"
echo ""

# Thumbnail Görüntüleme
echo -e "${BLUE}Thumbnail Görüntüleme:${NC}"
echo "curl -X GET http://localhost:8080/api/videos/YOUR_VIDEO_ID/thumbnail"
echo ""

echo -e "${GREEN}Not:${NC} YOUR_VIDEO_ID yerine gerçek video ID'sini kullanın."
echo -e "${GREEN}Not:${NC} Dosya yollarını kendi sisteminize göre güncelleyin." 
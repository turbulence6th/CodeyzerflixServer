# CodeyzerFlix Server

Bu proje, CodeyzerFlix video platformu için sunucu tarafı uygulamasıdır.

## Proje Yapısı

Proje üç Maven modülünden oluşmaktadır:

- **codeyzerflix-common**: Entity ve Repository sınıfları
- **codeyzerflix-admin**: Admin spesifik işlemler
- **codeyzerflix-api**: Herkese açık API işlemleri

## Gereksinimler

- Java 17 veya üzeri
- Maven 3.8 veya üzeri
- Docker ve Docker Compose

## Kurulum

### MongoDB Kurulumu

Projeyi çalıştırmadan önce MongoDB'yi başlatmanız gerekmektedir:

```bash
docker-compose up -d
```

Bu komut, MongoDB ve MongoDB Express'i başlatacaktır:
- MongoDB: localhost:27017
- MongoDB Express: http://localhost:8082 (kullanıcı adı: admin, şifre: password)

### Uygulamayı Çalıştırma

1. Admin modülünü başlatın:
```bash
cd codeyzerflix-admin
mvn spring-boot:run
```

2. API modülünü başlatın:
```bash
cd codeyzerflix-api
mvn spring-boot:run
```

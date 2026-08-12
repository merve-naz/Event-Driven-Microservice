## RUN

Uygulamayı çalıştırırken aşağıdaki sırayı takip edin.

### 1. Elasticsearch için Linux Ayarı

Elasticsearch çok sayıda memory-mapped alan kullandığı için `vm.max_map_count` limitinin yeterince yüksek olması gerekir.

Linux / WSL üzerinde:

```bash
sudo sysctl -w vm.max_map_count=262144
```

### 2. Encryption Key'i Ayarlama

Config dosyalarında bulunan `{cipher}` değerlerin çözülebilmesi için `ENCRYPT_KEY` tanımlanmalıdır.

IntelliJ IDEA:

`Run → Edit Configurations → Environment Variables`

```text
ENCRYPT_KEY=<your-encryption-key>
```

`ENCRYPT_KEY` şu uygulamalara eklenmelidir:

- **Config Server**
- Kendi `application.yml` dosyasında `{cipher}` değer bulunan **microservice'ler**

Örneğin bir microservice'in `application.yml` dosyasında:

```yaml
spring:
  cloud:
    config:
      password: "{cipher}<encrypted-value>"
```

bulunuyorsa, bu microservice Config Server'a bağlanmadan önce değeri çözmek zorunda olduğu için kendi `ENCRYPT_KEY` değerine ihtiyaç duyar.

> Aynı `{cipher}` değerini çözebilmek için encrypt sırasında kullanılan aynı `ENCRYPT_KEY` kullanılmalıdır.

Yeni bir key kullanılıyorsa Config Server'ı bu key ile başlatın ve gerekli değerleri yeniden encrypt edin:

```bash
curl -X POST http://localhost:8888/encrypt -d "your-password"
```

Dönen değeri ilgili config'e ekleyin:

```yaml
password: "{cipher}<encrypted-value>"
```

### 3. Config Server'ı Başlatma

Microservice'ler merkezi configuration bilgilerini Config Server'dan aldığı için **önce Config Server başlatılmalıdır.**

Config Server:

```text
http://localhost:8888
```

Microservice'ler Config Server'a şu şekilde bağlanır:

```yaml
spring:
  config:
    import: "configserver:http://localhost:8888"
```

### 4. Docker Servislerini Başlatma

Config Server çalıştıktan sonra Docker servislerini başlatın:

```bash
docker compose -f common.yml -f kafka-cluster.yml -f elastic_cluster.yml -f services.yml up -d
```

- `common.yml` → Ortak Docker/network ayarları
- `kafka-cluster.yml` → Kafka ve Schema Registry
- `elastic_cluster.yml` → Elasticsearch
- `services.yml` → Microservice'ler
- `-d` → Container'ları arka planda çalıştırır
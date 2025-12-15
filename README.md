# NMSP (Messaging Service Platform)

**Version:** 2.0.0-SNAPSHOT

## 專案概述

NMSP 是一個企業級 SMS/MMS 訊息平台，支援多種訊息處理方式，包含 HTTP API、SMPP 協議、IBM MQ 整合及檔案批次處理。

## 目錄結構

```
NMSP/
├── Common/              # 共用工具類別與基礎元件
├── DAL/                 # 資料存取層 (Data Access Layer)
├── SAC/                 # 系統管理控制台 (Web UI)
├── CacheServer/         # 快取服務
├── MultiSMS/            # SMPP 協議處理模組
├── HttpApi/             # HTTP API 端點
├── FileHandler/         # 檔案式訊息處理
├── Tester/              # 測試工具
├── ActionReport/        # 報表產生模組
├── IBMMQSAC/            # IBM MQ 管理控制台
├── IBMMQClient/         # IBM MQ 客戶端
└── pom.xml              # Maven 父專案設定
```

## 模組說明

### Common
共用函式庫，提供跨模組使用的工具與基礎類別：
- SMPP 常數與協議處理
- API 常數與工具
- 基礎訊息物件 (MessageObject, MessageRequest)
- 解析工具 (XML, SMS)

### DAL (Data Access Layer)
資料庫與訊息佇列抽象層：
- Hibernate ORM 映射
- Apache Artemis JMS 設定
- Oracle 資料庫連線
- Hazelcast 快取整合

### MultiSMS
透過 SMPP 協議處理 SMS 訊息：
- Producer/Consumer 模式處理訊息
- SMPP 協議處理器 (RxHandler, TxHandler)
- 訊息佇列與 Delivery Receipt 處理
- 支援多個 Content Provider 區域 (INTRA, INTER, IBM QM1-QM6)

### HttpApi
RESTful API 端點，用於 SMS 提交與狀態查詢：
- XML 格式 API 協議
- SMS 提交 (SmsSubmit)
- Delivery Receipt 處理 (SmsRetrieveDR, SmsQueryDR, SmsBatchRetrieveDR)
- 流量管控與配額管理
- 垃圾訊息關鍵字過濾
- MNP (號碼可攜) 檢查
- 黑名單驗證

### FileHandler
檔案式訊息處理：
- 檔案監控與佇列處理
- SMS 記錄檔案處理
- 處理紀錄日誌

### CacheServer
分散式快取服務：
- SSH 與 HTTP 端點進行快取管理
- WiseLCS 整合
- 快取統計與監控

### SAC (System Admin Console)
系統管理 Web 介面：
- 使用者管理與認證
- Content Provider (CP) 管理
- SMS 記錄查詢
- 報表產生 (JasperReports)
- 排程設定管理
- 垃圾訊息關鍵字管理
- 黑名單管理

### IBMMQClient
IBM MQ 客戶端整合：
- IBM MQ 訊息生產與消費
- 訊息格式處理 (Format One / Format Two)
- ZIP 壓縮支援
- MQ 連線池管理

### IBMMQSAC
IBM MQ 管理控制台：
- IBM MQ 系統監控
- 認證管理
- 系統設定

### ActionReport
報表產生模組：
- JasperReports 整合
- CLI 報表產生
- 報表資料彙整

### Tester
測試工具：
- 訊息產生測試
- 效能/負載測試
- SMPP 協議測試

## 系統架構

```
輸入管道:
  ├── HttpApi (XML REST API)
  ├── FileHandler (檔案式)
  └── SMPP Protocol (MultiSMS)
          │
          ▼
訊息處理流程:
  ├── 驗證 (流量控管、配額、黑名單、垃圾訊息)
  ├── 資料庫持久化 (DAL/Hibernate)
  ├── 佇列路由 (Artemis JMS)
  └── 發送與狀態追蹤
          │
          ▼
輸出管道:
  ├── SMPP (MultiSMS)
  ├── IBM MQ (IBMMQClient)
  └── File Output (FileHandler)

管理介面:
  ├── SAC (系統管理控制台)
  └── IBMMQSAC (MQ 管理控制台)

支援服務:
  ├── CacheServer (效能快取)
  └── ActionReport (報表產生)
```

## 技術堆疊

| 技術 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.x | 應用程式框架 |
| Apache Artemis | 2.28.0 | 訊息佇列 |
| Hibernate | 5.x+ | ORM 層 |
| Oracle JDBC | 21.5.0.0 | 資料庫連線 |
| JasperReports | 6.20.1 | 報表產生 |
| Hazelcast | 3.12.13 | 分散式快取 |
| Log4j 2 | Latest | 日誌記錄 |
| Lombok | Latest | 程式碼產生 |
| Jackson | 2.14.2+ | JSON 處理 |
| AngularJS | 1.x | 前端框架 (SAC) |
| Bootstrap | 3.x | 響應式設計 |

## 模組相依關係

```
MultiSMS, HttpApi, FileHandler ──► DAL + Common
SAC, IBMMQSAC ──────────────────► DAL + Common
IBMMQClient ────────────────────► DAL + Common
CacheServer ────────────────────► DAL + Common
ActionReport ───────────────────► Common
Tester ─────────────────────────► Common
```

## 建置與部署

- **建置工具:** Maven 3.x
- **Java 版本:** Java 11
- **打包格式:**
  - JAR: MultiSMS, HttpApi, FileHandler, CacheServer, IBMMQClient, Tester, ActionReport
  - WAR: SAC, IBMMQSAC

### 建置指令

```bash
# 建置所有模組
mvn clean install

# 建置特定模組
mvn clean install -pl <module-name>

# 跳過測試
mvn clean install -DskipTests
```

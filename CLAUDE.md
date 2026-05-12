# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

办公用品进销存管理系统 (Office Supplies Inventory Management System) — a two-module monolith with a Spring Boot backend and Vue 3 frontend.

## Module Structure

```
demo/         — Spring Boot 2.7 backend (Java 17, Maven)
demo-web/     — Vue 3 + Vite frontend (port 5173, proxies /api → :8080)
```

## Backend (`demo/`)

### Build & Run

```bash
cd demo
mvn clean package -DskipTests    # build
mvn spring-boot:run              # run (starts on port 8080)
```

### Architecture

Standard layered pattern: `controller → service → mapper`. MyBatis-Plus provides ORM (extending `BaseMapper<T>`) with MySQL via Druid connection pool.

| Layer | Package |
|-------|---------|
| Controllers | `com.example.demo.controller` |
| Services | `com.example.demo.service` |
| Mappers | `com.example.demo.mapper` |
| Entities | `com.example.demo.entity` |
| Config | `com.example.demo.config` |
| Common | `com.example.demo.common` |

**API response format**: All endpoints return `Result<T>` wrapper — `{ code: 200, message: "success", data: T }`. The frontend Axios interceptor unwraps `.data` on code 200.

### Key Design Decisions

- **Startup bootstrap**: `DataInitializer` (`@PostConstruct` in `config/DataInitializer.java`) runs `db/schema.sql` (CREATE TABLE IF NOT EXISTS for all 6 tables), then imports seed data from `data.xlsx` using Apache POI. It skips import if `office_supply` table already contains data.
- **Concurrency**: MyBatis-Plus `OptimisticLockerInnerInterceptor` is configured (via `MybatisPlusConfig`) and the `inventory` table has a `version` column with `@Version` — this protects inventory row updates via `updateById()`. However, the `PurchaseService` and `UsageService` do NOT use Redisson distributed locks (the `RedissonClient` bean is configured but not injected anywhere). Purchase and usage inserts go directly to `purchase_record` / `usage_record` tables without lock protection; only cache invalidation follows the write.
- **Cache strategy**: Redis caching via `RedisTemplate` (configured in `RedisConfig` with `GenericJackson2JsonRedisSerializer`). Randomized TTL jitter prevents thundering herd. Cache keys use the `cache:<domain>:<entity>` convention. Cache is invalidated on write (Cache-Aside pattern). `RedisTemplate` is injected with `required = false` so the app functions without Redis.
  - `cache:supply:list` — 30 min
  - `cache:inventory:list` — 10 min
  - `cache:dashboard:stats` — 5 min
- **Database**: `office_supply` database with tables: `office_supply`, `department`, `personnel`, `purchase_record`, `usage_record`, `inventory`
- **Redisson**: Configured for `redis://localhost:6379` (single-server mode). Falls back to `null` if Redis is unavailable. Currently unused by any service — available for future distributed lock needs.
- **No authentication/authorization**: No security framework is configured. CORS allows all origins (`CorsConfig`).
- **No tests**: The `src/test/` directory is empty.

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/supply` | List supplies (optional `?name=`) |
| POST/PUT/DELETE | `/api/supply[/{id}]` | CRUD |
| GET | `/api/purchase?pageNum=&pageSize=&year=&month=&purchaser=&supplyName=` | Paginated purchase records |
| POST | `/api/purchase` | Create purchase (invalidates cache) |
| GET | `/api/usage?pageNum=&pageSize=&year=&month=&deptName=&personName=&supplyName=` | Paginated usage records |
| POST | `/api/usage` | Create usage (validates stock sufficiency, personnel existence; invalidates cache) |
| GET | `/api/inventory` | Current stock list |
| GET | `/api/inventory/lowStock` | Low-stock count |
| GET | `/api/dashboard/stats` | Dashboard aggregates |
| GET | `/api/personnel?name=&deptName=` | List personnel (optional filters) |
| POST/PUT/DELETE | `/api/personnel[/{id}]` | CRUD |

The `ExpenseReport` frontend page fetches ALL purchase and usage records (pageSize=9999) and does client-side aggregation by supply name.

### Service Layer Details

- **PurchaseService.save()**: Auto-fills year/month from date, resolves supply info from `supplyId`, validates quantity/price/amount bounds, auto-generates `purchaseNo`, inserts record, then deletes inventory + dashboard caches.
- **UsageService.save()**: Same auto-fill and validation as purchase, plus validates that `personName` exists in the personnel table and that current stock >= requested quantity. Auto-generates `usageNo`, inserts, then deletes caches.
- **InventoryService.list()**: Delegates to `InventoryMapper.selectAllWithStock()` which runs a multi-table LEFT JOIN to compute real-time current stock from purchase/usage records.
- **PersonnelService**: Validates name + deptName required on save/update. Auto-assigns `personNo` if not set.

## Frontend (`demo-web/`)

### Commands

```bash
cd demo-web
npm install          # install dependencies
npm run dev          # dev server on port 5173
npm run build        # production build to dist/
npm run preview      # preview production build
```

### Architecture

- **Framework**: Vue 3 (Options API with `setup()` function — not `<script setup>`)
- **UI library**: Element Plus (Chinese locale)
- **Charts**: ECharts 5
- **Routing**: Vue Router 4 with `createWebHistory` mode. Single root route `/` using `Layout.vue` shell — sidebar nav renders child routes, header shows `$route.meta.title`
- **HTTP**: Axios instance at `src/api/request.js` with response interceptor that unwraps `Result.data` on code 200 and shows `ElMessage.error` on failures
- **State management**: None (no Pinia/Vuex). Each view manages its own state locally via `ref()`/`reactive()`.

### Routes

| Path | Component | Description |
|------|-----------|-------------|
| `/dashboard` | `Dashboard.vue` | Charts + stats |
| `/supply` | `SupplyList.vue` | CRUD table for office supplies |
| `/purchase` | `PurchaseList.vue` | Purchase records table + add form |
| `/usage` | `UsageList.vue` | Usage records table + add form |
| `/inventory` | `InventoryList.vue` | Current stock + low-stock alerts |
| `/personnel` | `PersonnelList.vue` | Personnel CRUD |
| `/report` | `ExpenseReport.vue` | Date-range expense summary |

### API module pattern

Each domain has a module under `src/api/` that imports the shared `request.js` instance and exports functions returning promises. Example: `src/api/purchase.js` exports `getPurchaseList(params)` and `addPurchase(data)`.

## Infrastructure Requirements

- **MySQL** on `localhost:3306`, database `office_supply`, credentials `root:123456`
- **Redis** on `localhost:6379` (optional — app functions without it, but caching and future distributed locking need it)

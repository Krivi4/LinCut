# LinCut

**URL‑shortener на Spring Boot + PostgreSQL + pg\_cron**

Сервис сокращает длинные ссылки и хранит их в таблице `url`.  Раз в **2 минуты** фоновый воркер `pg_cron` удаляет строки, у которых `expires_date` меньше текущего времени — база не разрастается бесконечно.

---

## ⚙️ Что нужно для запуска

- **Docker Engine ≥ 20.10** и встроенный плагин Compose v2\
  (в Linux ставится пакетами `docker-ce docker-compose-plugin`, а в Windows / macOS входит в Docker Desktop).

Никаких локальных PostgreSQL, Node JS, Java — кроме JDK 17 для самого Spring‑приложения — устанавливать не придётся.

---

## 🚀 Запуск в три команды

```bash
# 1. Клонируем репозиторий
$ git clone https://github.com/<your‑org>/LinCut.git
$ cd LinCut

# 2. Поднимаем контейнер с Postgres + pg_cron (порт 5432 внутри, 5433 снаружи)
$ docker compose up -d db

# 3. Запускаем Spring Boot‑приложение
$ ./mvnw spring-boot:run              # или ./gradlew bootRun
```

Через пару минут после старта в таблице `cron.job_run_details` уже будут записи со `status = succeeded` — значит авто‑очистка работает.

> **Важно!** Если у вас на машине уже запущен «родной» PostgreSQL и занимает 5432, контейнер сразу стартует, потому что наружу публикуется **5433**.  Нужен другой порт — задайте переменную `HOST_DB_PORT` (см. ниже).

---

## 🔍 Проверка, что всё ок

```bash
# выводим список заданий
$ docker compose exec db \
    psql -U postgres -d lincut_db \
    -c "SELECT jobid, schedule, nodename FROM cron.job;"

# смотрим последние запуски задачи
$ docker compose exec db \
    psql -U postgres -d lincut_db \
    -c "SELECT runid, status, return_message FROM cron.job_run_details ORDER BY runid DESC LIMIT 5;"
```

Каждые две минуты будет добавляться новая строка со `status = succeeded`.

---

## ⚙️ Переменные окружения (.env)

| Переменная          | По‑умолчанию | Что делает                                 |
| ------------------- | ------------ | ------------------------------------------ |
| `HOST_DB_PORT`      | **5433**     | Порт, на котором PostgreSQL виден на хосте |
| `POSTGRES_PASSWORD` | **postgres** | Пароль суперпользователя в контейнере      |

Пример запуска на порту 5555:

```bash
HOST_DB_PORT=5555 docker compose up -d db
```

---

## 🗂️ Структура проекта

```
LinCut/
├── docker-compose.yml          # сервис db (Postgres + pg_cron)
├── postgres/
│   └── initdb/                 # SQL‑скрипты, выполняются 1‑й раз
│       ├── 010_create_extensions.sql   # CREATE EXTENSION pg_cron;
│       └── 020_schedule_jobs.sql       # cron.schedule('delete_expired_urls', …)
├── src/                         # код Spring Boot‑приложения
└── README.md
```

---

## 🛠️ Как это устроено

- **`shared_preload_libraries=pg_cron`** и параметр `cron.database_name=lincut_db` задаются прямо в `command:` docker‑композа.
- При первом запуске контейнера Postgres выполняет все `.sql`‑файлы из папки `postgres/initdb/`:
    - `010_create_extensions.sql` — подключает расширение;
    - `020_schedule_jobs.sql` — вносит задачу `delete_expired_urls` и сразу обнуляет `nodename`, чтобы воркер подключался к базе через Unix‑сокет.

Таким образом никаких ручных действий после `git clone` не требуется — база, расширение и крон‑задача создаются автоматически.

---

##

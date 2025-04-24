CREATE TABLE url (
                     id           BIGSERIAL PRIMARY KEY,
                     long_url     TEXT                     NOT NULL,
                     created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                     expires_date TIMESTAMP WITH TIME ZONE
);

CREATE EXTENSION IF NOT EXISTS pg_cron;
 -- каждые 2 минуты проверяет не истекло ли время жизни ссылки в бд, если истекло - удаляет её из бд
SELECT cron.schedule(
               'delete_expired_urls',
               '*/2 * * * *',
               $$
                   DELETE FROM url
        WHERE expires_date IS NOT NULL
        AND expires_date < now()
    $$);
SELECT cron.schedule(
               'delete_expired_urls',
               '*/2 * * * *',
               $$
                   DELETE FROM url
    WHERE expires_date IS NOT NULL
      AND expires_date < now();
$$,
  ''      -- пустой nodename ⇒ подключение через Unix-socket
);

# liferay-hmac-user-authenticator

## Database configuration

Set the database connection using environment variables or system properties:

* `DB_URL` / `db.url`
* `DB_USERNAME` / `db.username`
* `DB_PASSWORD` / `db.password`
* `DB_DRIVER` / `db.driver` (optional, auto-detected for SQL Server URLs)

For Microsoft SQL Server, use a JDBC URL like:

```
jdbc:sqlserver://<host>:<port>;databaseName=<db>
```

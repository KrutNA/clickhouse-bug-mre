# clickhouse-bug-mre

It's a repository to reproduce bug with ClickHouse JDBC prepared statement with Date type value

## Description

1. Created any table with field of type `Date`
2. Inserted any data
3. Trying to fetch with prepared statement ```SELECT ... WHERE ... IN (?)```

## Preliminary requirements

1. ClickHouse DB. Example:
```bash
docker run -d --name click-local \
    -e CLICKHOUSE_DB=test \
    -e CLICKHOUSE_USER=test_user_click \
    -e CLICKHOUSE_DEFAULT_ACCESS_MANAGEMENT=1 \
    -e CLICKHOUSE_PASSWORD=test_password_click \
    -p 8123:8123 \
    clickhouse/clickhouse-server:24.12-alpine
```
2. Java

## Steps to reproduce

1. Configure user, password and url for ClickHouse
2. Execute ```./gradlew run``` or ```./gradlew.bat run```

## Expected

1. There are 10 numbers printed on the screen

## Actual behavior

1. Got an exception `Type mismatch in IN or VALUES section. Expected: Date. Got: Decimal64.`


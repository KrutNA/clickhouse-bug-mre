package com.krutna.mre.clickhousejdbc;

import com.clickhouse.client.api.ClientConfigProperties;
import com.clickhouse.jdbc.ConnectionImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;

public class Mre {

    //language=ClickHouse
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS `test_date_prepare`
            (
                `id` Int64,
                `date` Date
            ) ENGINE = ReplacingMergeTree
            ORDER BY id""";

    //language=ClickHouse
    private static final String INSERT = """
            INSERT INTO `test_date_prepare` (`id`, `date`)
            VALUES (? , ?)
            """;

    //language=ClickHouse
    private static final String SELECT = """
            SELECT `id`
            FROM `test_date_prepare`
            WHERE `date` IN (?)
            """;

    private static String getEndpoint() {
        return "jdbc:clickhouse:http://localhost:8123";
    }

    private static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(ClientConfigProperties.USER.getKey(), "test_user_click");
        properties.setProperty(ClientConfigProperties.PASSWORD.getKey(), "test_password_click");
        properties.setProperty(ClientConfigProperties.DATABASE.getKey(), "test");
        return new ConnectionImpl(getEndpoint(), properties);
    }

    public static void main(String[] args) throws SQLException {
        try (var connection = getConnection()) {
            create(connection);
            insert(connection);
            select(connection);
        }
    }

    private static Date getDate() {
        return Date.valueOf(LocalDate.now());
    }

    private static void create(Connection connection) throws SQLException {
        connection.prepareStatement(CREATE_TABLE).execute();
    }

    private static void insert(Connection connection) throws SQLException {
        var date = getDate();
        var prepare = connection.prepareStatement(INSERT);
        for (var i = 0L; i < 10L; ++i) {
            prepare.setLong(1, i);
            prepare.setDate(2, date);
            prepare.addBatch();
        }
        prepare.executeBatch();
    }

    private static void select(Connection connection) throws SQLException {
        var prepare = connection.prepareStatement(SELECT);
        prepare.setDate(1, getDate());
        var result = prepare.executeQuery();
        while (result.next()) {
            System.out.println(result.getLong(1));
        }
    }
}

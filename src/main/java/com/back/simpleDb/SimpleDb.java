package com.back.simpleDb;

import java.sql.*;

public class SimpleDb {
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private final String DB_NAME;
    private boolean isDev;

    public SimpleDb(String URL, String USERNAME, String PASSWORD, String DB_NAME){
        this.URL = URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.DB_NAME = DB_NAME;
        this.isDev = false;
    }

    public Connection connectDB(){
        String connectionString = "jdbc:mysql://" + URL + "/" + DB_NAME;
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connectionString, USERNAME, PASSWORD);
        } catch (SQLException e){
            System.err.println("DB 연결 중 에러가 발생했습니다.");
        }

        return connection;
    }

    public Sql genSql(){
        return new Sql(this);
    }

    public void setDevMode(boolean bool){
        this.isDev = bool;
    }

    public void run(String query){
        try (Statement statement = connectDB().createStatement()){
            statement.execute(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void run(String query, String title, String body, boolean isBlind){
        try (PreparedStatement preparedStatement = connectDB().prepareStatement(query)){
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, body);
            preparedStatement.setBoolean(3, isBlind);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}

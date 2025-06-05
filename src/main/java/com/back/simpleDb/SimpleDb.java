package com.back.simpleDb;

import java.sql.*;

public class SimpleDb {
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private final String DB_NAME;
    private boolean isDev;
    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public SimpleDb(String URL, String USERNAME, String PASSWORD, String DB_NAME){
        this.URL = URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.DB_NAME = DB_NAME;
        this.isDev = false;
    }

    public Connection connectDB() { //쓰레드 로컬 사용하는 로직으로 변경.
        String connectionURL = "jdbc:mysql://" + URL + "/" + DB_NAME;
        Connection connection = connectionThreadLocal.get(); //남아 있는 연결이 있다면 사용
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(connectionURL, USERNAME, PASSWORD);
                connectionThreadLocal.set(connection);
            }
        } catch (SQLException e){
            System.out.println("Connection을 생성하는 데 문제가 발생했습니다.");
            e.printStackTrace();
        }
        return connection;
    }

    public Sql genSql() {
        return new Sql(connectDB());
    }

    public void setDevMode(boolean bool){
        this.isDev = bool;
    }

    public void run(String query){
        try (Statement statement = connectDB().createStatement()){
            statement.execute(query);
        } catch (SQLException e){
            System.out.println("쿼리 실행 중 오류가 발생했습니다.");
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
            System.out.println("쿼리 실행 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            connectDB().close();
        } catch (SQLException e){
            System.out.println("이미 닫힌 연결입니다.");
            e.printStackTrace();
        } finally {
            connectionThreadLocal.remove();
        }
    }

    public void startTransaction(){
        try {
            connectDB().setAutoCommit(false);
        } catch (SQLException e){
            System.out.println("트랜잭션을 시작하는데 문제가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public void rollback(){
        try {
            connectDB().rollback();
            connectDB().setAutoCommit(true);
        } catch (SQLException e){
            System.out.println("이전 세이브포인트로 돌아가는데 문제가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public void commit(){
        try {
            connectDB().commit();
            connectDB().setAutoCommit(true);
        } catch (SQLException e){
            System.out.println("Commit 과정에서 문제가 발생했습니다.");
            e.printStackTrace();
        }
    }
}

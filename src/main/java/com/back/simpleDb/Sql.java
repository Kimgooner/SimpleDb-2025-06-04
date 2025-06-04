package com.back.simpleDb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Sql {
    private StringBuilder query;
    private List<Object> params = new ArrayList<>();
    private Connection connection;

    public Sql(SimpleDb simpleDb){
        this.connection = simpleDb.connectDB();
        this.query = new StringBuilder();
    }

    public Sql append(String s){
        this.query.append(s).append(" ");
        return this;
    }

    public Sql append(String s, Object... params){
        this.query.append(s).append(" ");
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public PreparedStatement makeQuery(){
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for(Object obj : params){
                preparedStatement.setObject(index++, obj);
            }
            System.out.println(preparedStatement.toString());
            return preparedStatement;
        } catch (SQLException e){
            System.out.println("쿼리문을 생성하는 데 오류가 발생했습니다.");
        }
        return null;
    }

    public long insert(){
        try (PreparedStatement preparedStatement = makeQuery()){
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e){
            System.out.println("INSERT 에러");
        }
        return -1;
    }

    public int update(){
        try (PreparedStatement preparedStatement = makeQuery()){
            int affected = preparedStatement.executeUpdate();
            return affected;
        } catch (SQLException e){
            System.out.println("UPDATE 에러");
        }
        return -1;
    }

    public int delete(){
        try (PreparedStatement preparedStatement = makeQuery()){
            int affected = preparedStatement.executeUpdate();
            return affected;
        } catch (SQLException e){
            System.out.println("DELETE 에러");
        }
        return -1;
    }

    public List<Map<String, Object>> selectRows() {
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int column_index = resultSetMetaData.getColumnCount();

            List<Map<String, Object>> articleRows = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> articleRowMap = new HashMap<>();
                for(int i = 1; i <= column_index; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    //System.out.println(columnName + ", " + columnValue); 테스트
                    articleRowMap.put(columnName, columnValue);
                }
                articleRows.add(articleRowMap);
            }
            return articleRows;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SELECT * 에러");
        }
        return null;
    }

    public Map<String, Object> selectRow(){
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int column_index = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> articleRowMap = new HashMap<>();
                for(int i = 1; i <= column_index; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    //System.out.println(columnName + ", " + columnValue); 테스트
                    articleRowMap.put(columnName, columnValue);
                }
                return articleRowMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SELECT ROW 에러");
        }
        return null;
    }

    public LocalDateTime selectDatetime(){
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSet.getTimestamp(1).toLocalDateTime();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SELECT NOW() 에러");
        }
        return null;
    }

    public Long selectLong(){
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("SELECT Long 에러");
        }
        return null;
    }

    public String selectString(){
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("SELECT String 에러");
        }
        return null;
    }

    public Boolean selectBoolean(){
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            System.out.println("SELECT Boolean 에러");
        }
        return null;
    }

    public Sql appendIn(String s, Object... params){
        // makeQuery()를 이미 구현했으므로, query문에 ?를 파라미터 개수만큼 추가하는 형태로 변경
        StringBuilder questionMark = new StringBuilder();
        questionMark.append("\\?");
        for(int i = 1; i < params.length; i++){
            questionMark.append(", ?");
        }
        String altered = s;
        altered = altered.replaceAll("\\?", questionMark.toString());

        this.query.append(altered).append(" ");
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public List<Long> selectLongs(){
        try (PreparedStatement preparedStatement = makeQuery()) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Long> longList = new ArrayList<>();
            while (resultSet.next()){
                longList.add(resultSet.getLong(1));
            }
            return longList;
        } catch (SQLException e) {
            System.out.println("SELECT Longs, ORDER BY FILED 에러");
        }
        return null;
    }
}

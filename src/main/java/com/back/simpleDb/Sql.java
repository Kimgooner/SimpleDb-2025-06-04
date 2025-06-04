package com.back.simpleDb;

import java.sql.*;
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
}

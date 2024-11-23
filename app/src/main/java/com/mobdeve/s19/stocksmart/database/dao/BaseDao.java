package com.mobdeve.s19.stocksmart.database.dao;

import java.util.List;

public interface BaseDao<T> {
    long insert(T obj);
    boolean update(T obj);
    boolean delete(long id);
    T get(long id);
    List<T> getAll();
}
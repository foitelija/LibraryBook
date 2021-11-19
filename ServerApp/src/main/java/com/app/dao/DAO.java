package com.app.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * инерфейс Doa
 * @param <T> класс-таблица
 * @param <K> ключ класса-таблицы
 */
public interface DAO<T,K> {
    /**
     * получение T по ключу K
     * @param id ключ
     * @return T
     * @throws SQLException
     */
    Optional<T> get(K id) throws SQLException;

    /**
     * получение всей коллекции T
     * @return коллекция T
     * @throws SQLException
     */
    List<T> getAll() throws SQLException;

    /**
     * сохраняет в базу новую сущность T
     * @param entity T
     * @return ключ который был сгенерирован
     * @throws SQLException
     */
    K save(T entity) throws SQLException;

    /**
     * обвновляет сущность T
     * @param entity
     * @throws SQLException
     */
    void update(T entity) throws SQLException;

    /**
     * удаляет сущность по ключу
     * @param id ключ
     * @throws SQLException
     */
    void delete(K id) throws SQLException;
}

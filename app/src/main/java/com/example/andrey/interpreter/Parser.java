package com.example.andrey.interpreter;

/**
 * Функциональный интерфейс, предназначенный парсить файл, подданный в параметр file
 */

public interface Parser<T> {

    T doParse(String file);

}
package com.github.fleivinho.databaseexample.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DatabaseType {

    MYSQL("MySQL"), SQLITE("SQLite");

    private final String name;
}

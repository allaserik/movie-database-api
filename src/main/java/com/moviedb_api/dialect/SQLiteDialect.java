package com.moviedb_api.dialect;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;

public class SQLiteDialect extends Dialect {
    public SQLiteDialect() {
        super(DatabaseVersion.make(3));
    }

}

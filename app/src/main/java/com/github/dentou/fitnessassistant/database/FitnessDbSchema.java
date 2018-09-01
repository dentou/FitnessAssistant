package com.github.dentou.fitnessassistant.database;

public class FitnessDbSchema {

    public static final class UserTable {
        public static final String NAME = "users";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String DATE_OF_BIRTH = "date_of_birth";
            public static final String HEIGHT = "height";
            public static final String WEIGHT = "weight";
        }
    }

    public static final class DataTable {
        public static final String NAME = "data";

        public static final class Cols {
            public static final String UUID = "uuid";
            //todo
        }
    }
}


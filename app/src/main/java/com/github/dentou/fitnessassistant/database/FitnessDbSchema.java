package com.github.dentou.fitnessassistant.database;

public class FitnessDbSchema {

    public static final class UserTable {
        public static final String NAME = "users";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String GENDER = "gender";
            public static final String DATE_OF_BIRTH = "date_of_birth";
        }
    }

    public static final class BodyTable {
        public static final String NAME = "bodies";

        public static final class Cols {
            public static final String USER_UUID = "user_uuid";
            public static final String UUID = "uuid";
            public static final String DATE = "date";
            public static final String BICEPS = "biceps"; // front of upper arm - biceps
            public static final String TRICEPS = "triceps"; // back of upper arm - triceps
            public static final String SUBSCAPULAR = "subscapular"; // back, below shoulder blade - subscapular
            public static final String SUPRAILIAC = "suprailiac"; // waist - suprailiac
            public static final String HEIGHT = "height";
            public static final String WEIGHT = "weight";
        }
    }
}


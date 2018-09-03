package com.github.dentou.fitnessassistant;

public class FitnessAnalyzer {

    public static double computePercentBodyFat(User user, Body body) {
        return computePercentBodyFat(user.getAge(), user.getGender() == User.MALE,
                body.getBiceps(), body.getTriceps(), body.getSubscapular(), body.getSuprailiac());
    }

    public static double computePercentBodyFat(int age, boolean male,
                       int biceps, int triceps, int subscapular, int suprailiac) {
        double L = Math.log10(biceps + triceps + subscapular + suprailiac);
        double a = 0;
        double b = 0;
        if (male) {
            if (age < 17) {
                a = 1.1533;
                b = 0.0643;
            } else if (age < 20) {
                a = 1.1620;
                b = 0.0630;
            } else if (age < 30) {
                a = 1.1631;
                b = 0.0632;
            } else if (age < 40) {
                a = 1.1422;
                b = 0.0544;
            } else if (age < 50) {
                a = 1.1620;
                b = 0.0700;
            } else {
                a = 1.1715;
                b = 0.0779;
            }
        } else {
            if (age < 17) {
                a = 1.1369;
                b = 0.0598;
            } else if (age < 20) {
                a = 1.1549;
                b = 0.0678;
            } else if (age < 30) {
                a = 1.1599;
                b = 0.0717;
            } else if (age < 40) {
                a = 1.1423;
                b = 0.0632;
            } else if (age < 50) {
                a = 1.1333;
                b = 0.0612;
            } else {
                a = 1.1339;
                b = 0.0645;
            }
        }

        double D = a - b * L;
        return (495 / D) - 450;
    }
}

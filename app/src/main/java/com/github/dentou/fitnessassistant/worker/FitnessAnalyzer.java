package com.github.dentou.fitnessassistant.worker;

import com.github.dentou.fitnessassistant.model.Body;
import com.github.dentou.fitnessassistant.model.BodyIndex;
import com.github.dentou.fitnessassistant.model.User;

import java.util.ArrayList;
import java.util.List;

public class FitnessAnalyzer {

    public static List<BodyIndex> analyze(User user, List<Body> bodies) {
        List<BodyIndex> results = new ArrayList<>();
        for (Body body : bodies) {
            results.add(analyze(user, body));
        }
        return results;
    }

    public static BodyIndex analyze(User user, Body body) {
        BodyIndex result = new BodyIndex(user.getId(), body.getId(), body.getDate());
        result.setFatPercentage(computeBodyFatPercentage(user, body));
        result.setFatMass(computeBodyFatMass(result.getFatPercentage(), body.getWeight()));
        result.setLeanMuscleMass(body.getWeight() - result.getFatMass());
        result.setBMI(computeBodyBMI(body.getHeight(), body.getWeight()));
        result.setBMR(computeBodyBMR(body.getHeight(), body.getWeight(), user.getAge(), user.getGender() == User.MALE));
        return result;
    }

    public static BodyIndex computeMean(List<BodyIndex> bodyIndices) {
        if (bodyIndices == null || bodyIndices.isEmpty()) {
            return null;
        }
        if (bodyIndices.size() == 1) {
            return bodyIndices.get(0);
        }
        BodyIndex latestBodyIndex = bodyIndices.get(bodyIndices.size() - 1);


        float meanPercentFat = 0;
        for (BodyIndex bodyIndex : bodyIndices) {
            meanPercentFat += bodyIndex.getFatPercentage();
        }
        meanPercentFat = meanPercentFat / bodyIndices.size();

        BodyIndex result = new BodyIndex(
                latestBodyIndex.getUserId(),
                latestBodyIndex.getBodyId(),
                latestBodyIndex.getDate());

        result.setFatPercentage(meanPercentFat);
        return result;

    }

    private static float computeBodyBMR(float height, float weight, int age, boolean isMale) {
        // Height in m, weight in kg, age in years
        float result = (10 * weight) + (625 * height) - (5 * age);
        if (isMale) {
            result += 5;
        } else {
            result -= 161;
        }
        return result;
    }

    private static float computeBodyBMI(float height, float weight) {
        return weight / (height * height);
    }

    private static float computeBodyFatMass(float bodyFatPercentage, float weight) {
        return weight * (bodyFatPercentage / 100);
    }

    private static float computeBodyFatPercentage(User user, Body body) {
        return computeBodyFatPercentage(user.getAge(), user.getGender() == User.MALE,
                body.getBiceps(), body.getTriceps(), body.getSubscapular(), body.getSuprailiac());
    }

    private static float computeBodyFatPercentage(int age, boolean male,
                       int biceps, int triceps, int subscapular, int suprailiac) {
        double L = Math.log10(biceps + triceps + subscapular + suprailiac);
        double a;
        double b;
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

        float D = (float) (a - b * L);
        return (495 / D) - 450;
    }
}

package DataGenerator;

import com.github.javafaker.Faker;

import java.util.Locale;

public class GeneratorX {
    public static void main(String[] args) {
        Faker faker = new Faker();
        System.out.println(faker.number().digits(9));
    }
}

package ru.matveykenya;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * При значении:
 * MAX_KEY_INTEGER = 100;       // размер тестовой testMap
 * COUNT_ITERATION = 1000000;  // количество циклов записи чтения
 * COUNT_TEST_THREADS = 100;  // количество рабочих потоков
 * KeyValue - все разные для трех операций (см в функции testOperations)
 * ---------------------------------------------------------------------
 * Испытываем ConcurrentHashMap...
 * Время выполнения теста - 25865
 * Испытываем Collections.synchronizedMap...
 * Время выполнения теста - 35789
 * ConcurrentHashMap БЫСТРЕЕ synchronizedMap В 1.3836845157548812 раз
 *
 * По разным данным в целом быстрее от 1.2 - 1.6 раза
 */

public class Main {
    static final Map<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
    static final Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(new HashMap<>());
    static Map<Integer, Integer> testMap;
    static final int MAX_KEY_INTEGER = 100;     // размер тестовой testMap
    static final int COUNT_ITERATION = 100000; // количество циклов записи чтения
    static final int COUNT_TEST_THREADS = 100;  // количество рабочих потоков
    static final Random random = new Random();

    public static void main(String[] args) {

        long time1 = test("ConcurrentHashMap", concurrentHashMap);
        long time2 = test("Collections.synchronizedMap", synchronizedMap);
        System.out.println("ConcurrentHashMap БЫСТРЕЕ synchronizedMap В " + (double) time2 / time1 + " раз");

    }

    static long test(String name, Map<Integer, Integer> map) {
        System.out.println("Испытываем " + name + "...");
        testMap = map;
        Thread[] testThread = new Thread[COUNT_TEST_THREADS];

        long timeStart = System.currentTimeMillis(); // начало эксперимента
        for (int i = 0; i < COUNT_TEST_THREADS; i++) {
            testThread[i] = new Thread(Main::testOperations);
            testThread[i].start();
        }
        for (int i = 0; i < COUNT_TEST_THREADS; i++) {
            try{
                testThread[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long timeEnd = System.currentTimeMillis(); // конец эксперимента

        long timeDelta = timeEnd - timeStart;
        System.out.println("Время выполнения теста - " + timeDelta);
        return timeDelta;
    }

    static void testOperations() {
        int keyValue;
        for (int i = 0; i < COUNT_ITERATION; i++) {
            keyValue = random.nextInt(MAX_KEY_INTEGER);
            testMap.putIfAbsent(keyValue, keyValue);
            keyValue = random.nextInt(MAX_KEY_INTEGER);
            testMap.replace(keyValue, keyValue);
            //keyValue = random.nextInt(MAX_KEY_INTEGER);
            testMap.remove(keyValue, keyValue);
        }
    }
}

import functions.*;
import functions.basic.*;
import threads.Task;
import threads.SimpleGenerator;
import threads.SimpleIntegrator;
import threads.Generator;
import threads.Integrator;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {

        // Задание 1
        // Проверка метода integral
//        Function exp = new Exp();
//        double theoretical = Math.E - 1;
//        double step = 0.0001;
//        double numeric = Functions.integral(exp, 0, 1, step);
//        System.out.println("Теоретическое значение: " + theoretical);
//        System.out.println("Численное значение: " + numeric);
//        System.out.println("Разность: " + Math.abs(theoretical - numeric));
//        System.out.println("Шаг: " + step);

        // Задание 2 - последовательная версия
//        System.out.println("\n=== nonThread(): последовательная версия ===");
//        nonThread();

        // Задание 3 - простая многопоточная версия
        //System.out.println("\n=== simpleThreads(): простая многопоточная версия ===");
        //simpleThreads();

        // Задание 4 - многопоточная версия с семафорами и interrupt()
        System.out.println("\n=== complicatedThreads():  многопоточная версия с семафорами ===");
        complicatedThreads();
    }

//    public static void nonThread() {
//        Task task = new Task();
//
//        // 100 задач
//        int tasksCount = 100;
//        task.setTasksCount(tasksCount);
//
//        Random random = new Random();
//
//        for (int i = 0; i < task.getTasksCount(); ++i) {
//
//            // 1) Основание логарифма a (1, 10), при этом a > 0 и a != 1.
//            double base = 1 + 9 * random.nextDouble();
//            if (Math.abs(base - 1) < 1e-6) {
//                base += 1e-3;
//            }
//            Function logFunction = new Log(base);
//            task.setFunction(logFunction);
//
//            // 2) Левая граница (0, 100)
//            // Для логарифма нужно ОДЗ x > 0, поэтому исключаем 0
//            double left = 100 * random.nextDouble();
//            if (left <= 0) {
//                left = Double.MIN_VALUE; // очень маленькое положительное число
//            }
//
//            // 3) Правая граница (100, 200)
//            double right = 100 + 100 * random.nextDouble();
//            if (right <= left) {
//                right = left + 1;
//            }
//
//            // 4) Шаг дискретизации (0, 1]
//            double step = random.nextDouble(); // [0, 1)
//            if (step <= 0)
//                step = 1;
//
//            task.setLeft(left);
//            task.setRight(right);
//            task.setStep(step);
//            int taskNumber = i + 1;
//
//            // 5) Вывод
//            System.out.printf(
//                    "Задание %3d:%n" + " левая граница = %.5f%n" + " правая граница = %.5f%n" +
//                            " шаг дискретизации = %.5f%n",
//                    taskNumber,
//                    task.getLeft(),
//                    task.getRight(),
//                    task.getStep());
//
//            // 6) Вычисляем интеграл по методу трапеций
//            double value = functions.Functions.integral(
//                    task.getFunction(),
//                    task.getLeft(),
//                    task.getRight(),
//                    task.getStep()
//            );
//
//            // 7) Выводим результат
//            System.out.printf("Результат %3d:%n" + "  значение интеграла = %.10f%n%n", taskNumber, value);
//        }
//    }

    // 3 задание
//    public static void simpleThreads() {
//        // 1) Создаем общий объект Task, с которым будут работать оба потока
//        Task task = new Task();
//
//        // 2) Устанавливаем количество задач, обрабатываемых генератором и интегратором
//        int tasksCount = 100;
//        task.setTasksCount(tasksCount);
//
//        // 3) Создаем объекты SimpleGenerator и SimpleIntegrator на основе Task
//        SimpleGenerator generator = new SimpleGenerator(task);
//        SimpleIntegrator integrator = new SimpleIntegrator(task);
//
//        // 4) Оборачиваем их в объекты Thread с понятными именами потоков
//        Thread genThread = new Thread(generator, "SimpleGenerator");
//        Thread intThread = new Thread(integrator, "SimpleIntegrator");
//
//        // 5) Запускаем оба потока методом start
//        genThread.start();
//        intThread.start();
//
//        try {
//            // 6) Ждем завершения работы потоков с помощью join
//            genThread.join();
//            intThread.join();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.out.println("main: потоки были прерваны");
//        }
//
//        // 7) После завершения выводим в консоль сообщение о завершении simpleThreads
//        System.out.println("simpleThreads(): оба потока завершили работу");
//    }

            // 4 задание
    public static void complicatedThreads() {
        // 1) Создаем объект Task, общий для потоков генератора и интегратора
        Task task = new Task();
        // 2) Устанавливаем количество задач, которые нужно сгенерировать и обработать
        int tasksCount = 100;
        task.setTasksCount(tasksCount);

        // 3) Создаем семафоры для синхронизации доступа к Task
        //    dataReady с нулем разрешений, интегратор будет ждать данных
        //    dataProcessed с одним разрешением, генератор может сразу записывать первую задачу
        Semaphore dataReady = new Semaphore(0);
        Semaphore dataProcessed = new Semaphore(1);

         // 4) Создаем потоки Generator и Integrator, передаем им общий Task и семафоры
        Generator generator = new Generator(task, dataReady, dataProcessed);
        Integrator integrator = new Integrator(task, dataReady, dataProcessed);

        // 5) Запускаем оба потока
        generator.start();
        integrator.start();

        try {
            // 6) Даем потокам поработать 50 миллисекунд
            Thread.sleep(50);  // Даём потокам поработать 50 мс, затем прерываем их
            // 7) Прерываем оба потока методом interrupt
            generator.interrupt();
            integrator.interrupt();
            // 8) Ждем завершения работы обоих потоков методом join
            generator.join();
            integrator.join();

        } catch (InterruptedException e) {
            // 9) Если main прервали, восстанавливаем флаг и выводим сообщение
            Thread.currentThread().interrupt();
            System.out.println("main: поток main был прерван во время complicatedThreads()");
        }
        // 10) Выводим сообщение о завершении работы complicatedThreads
        System.out.println("complicatedThreads(): оба потока завершили работу");
    }
}


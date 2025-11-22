package threads;

import functions.Function;
import functions.Functions;

// Вычислитель интеграла
public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        if (task == null)
            throw new IllegalArgumentException("Task не должен быть null");
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); ++i) { // Выполняем вычисления для указанного количества задач
            int taskNumber = i + 1;

            // Читаем текущие параметры из общей задачи
            Function f = task.getFunction(); // Может быть null если генератор еще не установил функцию
            double left = task.getLeft();
            double right = task.getRight();
            double step = task.getStep();
            double value = Functions.integral(f, left, right, step); // Вычисляем интеграл с полученными параметрами
            // Выводим результат вычисления
            System.out.printf(
                    "[S-INT] Задание %3d:%n" +
                            "      левая граница      = %.5f%n" +
                            "      правая граница     = %.5f%n" +
                            "      шаг дискретизации  = %.5f%n" +
                            "      значение интеграла = %.10f%n%n",
                    taskNumber, left, right, step, value
            );
        }
    }
}
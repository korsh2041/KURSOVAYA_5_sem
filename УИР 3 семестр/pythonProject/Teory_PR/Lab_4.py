import math
import numpy as np

def objective_function(x1, x2):
    return x1 ** 2 + 3 * x2 ** 2 + math.cos(x1 + x2)

def constraint_function(x1, x2):
    return x1 + x2 - 2

def penalty_function(x1, x2, r):
    return objective_function(x1, x2) + r * (constraint_function(x1, x2) ** 2)

def hooke_jeeves_minimize(x_start, step_size=0.1, tolerance=1e-6, func=None):
    if func is None:
        func = objective_function
    def evaluate_point(x):
        return [x[0], x[1], func(x[0], x[1])]
    current_point = evaluate_point(x_start)
    best_point = current_point.copy()
    iteration = 0
    while step_size > tolerance:
        iteration += 1
        improved = False
        for dx in [-step_size, 0, step_size]:
            for dy in [-step_size, 0, step_size]:
                if dx == 0 and dy == 0:
                    continue
                trial_point = [current_point[0] + dx, current_point[1] + dy]
                trial_eval = evaluate_point(trial_point)
                if trial_eval[2] < best_point[2]:
                    best_point = trial_eval
                    improved = True

        if improved:
            while True:
                new_direction = [
                    best_point[0] + (best_point[0] - current_point[0]),
                    best_point[1] + (best_point[1] - current_point[1])
                ]
                new_point_eval = evaluate_point(new_direction)

                if new_point_eval[2] < best_point[2]:
                    current_point = best_point
                    best_point = new_point_eval
                else:
                    break
            current_point = best_point.copy()
        else:
            step_size /= 2

    return best_point, iteration


def penalty_method(x_start, r0=100.0, beta=2.0, eps=1e-6, max_outer=20):

    x = np.array(x_start, dtype=float)
    r = r0

    print("Метод штрафных функций")
    print(f"Минимизация f(x) = x1² + 3*x2² + cos(x1 + x2)")
    print(f"При ограничении: x1 + x2 = 2")
    print(f"Начальная точка: [{x[0]:.1f}, {x[1]:.1f}]")
    print()

    for k in range(max_outer):
        current_func = lambda x1, x2: penalty_function(x1, x2, r)

        result, inner_iters = hooke_jeeves_minimize(
            [x[0], x[1]],
            step_size=0.1,
            tolerance=1e-6,
            func=current_func
        )

        x = np.array([result[0], result[1]])

        fx = objective_function(x[0], x[1])
        gx = constraint_function(x[0], x[1])

        if k < 3 or k > max_outer - 4 or k % 4 == 0 or abs(gx) < 1e-4:
            print(f"Итер {k + 1:2d}: r={r:8.1f} "
                  f"x=[{x[0]:6.3f}, {x[1]:6.3f}] "
                  f"f={fx:7.3f} "
                  f"ошибка={gx:9.1e}")

        if abs(gx) < eps:
            print("Точность достигнута")
            break

        r *= beta

    return x, fx, gx, k + 1

print("БЕЗ ОГРАНИЧЕНИЙ:")
start_point = [1.0, 1.0]
result_no_constraint, iterations_no = hooke_jeeves_minimize(start_point)
print(f"Минимум найден в точке: ({result_no_constraint[0]:.7f}, {result_no_constraint[1]:.7f})")
print(f"Значение функции: {result_no_constraint[2]:.7f}")
print(f"Ограничение: x1 + x2 = {result_no_constraint[0] + result_no_constraint[1]:.7f}")
print(f"Нарушение ограничения: {constraint_function(result_no_constraint[0], result_no_constraint[1]):.7f}")
print(f"Количество итераций: {iterations_no}")
print("С ОГРАНИЧЕНИЕМ (метод штрафных функций):")
x_opt, f_opt, g_val, outer_iters = penalty_method(start_point)
print("РЕЗУЛЬТАТЫ:")
print(f"Найдена точка: [{x_opt[0]:.7f}, {x_opt[1]:.7f}]")
print(f"Значение функции: {f_opt:.7f}")
print(f"Ограничение: {x_opt[0]:.7f} + {x_opt[1]:.7f} = {x_opt[0] + x_opt[1]:.7f}")
print(f"Нарушение ограничения: {g_val:.7e}")
print(f"Внешних итераций: {outer_iters}")

print("\n" + "=" * 50)
print("СРАВНЕНИЕ:")
print("=" * 50)
print(f"Без ограничений: f(x) = {result_no_constraint[2]:.7f}")
print(f"С ограничением:  f(x) = {f_opt:.7f}")
print(f"Разница: {f_opt - result_no_constraint[2]:.7f}")
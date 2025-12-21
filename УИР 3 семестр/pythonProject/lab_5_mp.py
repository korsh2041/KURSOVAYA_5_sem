import math
import numpy as np
import matplotlib.pyplot as plt
import time

start_time = time.time()

# Параметры задачи
s = 1000  # расстояние до цели
g = 9.8   # ускорение свободного падения
mA = 30   # мат.ожидание для угла (mα)
n = 100000  # количество выстрелов
deltaX = 10  # зона попадания

# Расчет мат. ожидания для скорости
mV = math.sqrt(s * g / math.sin(math.radians(2 * mA)))

# Параметры распределений
Gv = 1  # σv
Ga = 1  # σα

# ОПТИМИЗАЦИЯ 1: Векторизованная генерация случайных величин
# Генерируем все случайные величины сразу
np.random.seed(42)  # для воспроизводимости

# Генерация 2*n случайных чисел и преобразование в нормальное распределение
uniform_randoms = np.random.random(2 * n)
normal_randoms = uniform_randoms.reshape(2, n)
sum_12 = np.sum(normal_randoms.reshape(24, n // 12), axis=0)
normal_values = (sum_12 - 6).repeat(12)[:n]

# Разделяем на скорости и углы
half_n = n // 2
speeds = mV + Gv * normal_values[:half_n]
angles = mA + Ga * normal_values[half_n:n]

# ОПТИМИЗАЦИЯ 2: Векторизованный расчет дальностей
angles_rad = np.radians(2 * angles)
sin_2alpha = np.sin(angles_rad)
mass = (speeds ** 2) * sin_2alpha / g

# ОПТИМИЗАЦИЯ 3: Быстрый расчет мат. ожидания и дисперсии
indices = np.arange(100, n, 100)
matWait = np.cumsum(mass)[indices-1] / indices

# Для дисперсии используем кумулятивные суммы
cumsum = np.cumsum(mass)
cumsum_sq = np.cumsum(mass ** 2)
dispersion = (cumsum_sq[indices-1] - (cumsum[indices-1] ** 2) / indices) / (indices - 1)

# ОПТИМИЗАЦИЯ 4: Быстрый расчет вероятности попадания
wire_hits = np.sum(np.abs(s - mass) <= deltaX)
hit_probability = wire_hits / n

print(f"Вероятность попадания: {hit_probability}")

# Статистики
mean = np.mean(mass)
std = np.std(mass)

print(f"Среднее: {mean:.2f}")
print(f"Стандартное отклонение: {std:.2f}")
print(f"3 сигма: {3*std:.2f}")

# ВИЗУАЛИЗАЦИЯ
plt.figure(figsize=(15, 5))

# 1. График математического ожидания
plt.subplot(1, 3, 1)
plt.plot(indices, matWait)
plt.axhline(y=s, color='r', linestyle='--', label=f'Цель: {s}')
plt.xlabel('Количество выстрелов')
plt.ylabel('Математическое ожидание')
plt.title('График математического ожидания дальности')
plt.legend()
plt.grid(True)

# 2. График дисперсии
plt.subplot(1, 3, 2)
plt.plot(indices, dispersion)
plt.xlabel('Количество выстрелов')
plt.ylabel('Дисперсия')
plt.title('График дисперсии дальности')
plt.grid(True)

# 3. Гистограмма
plt.subplot(1, 3, 3)
lower_bound = mean - 3 * std
upper_bound = mean + 3 * std

plt.hist(mass, bins=50, density=True, alpha=0.7, color='skyblue', edgecolor='black')
plt.axvline(x=s, color='red', linewidth=2, label=f'Цель: {s}')
plt.axvline(x=mean, color='green', linewidth=2, label=f'Среднее: {mean:.1f}')
plt.axvline(x=lower_bound, color='orange', linestyle='--', label=f'-3σ: {lower_bound:.1f}')
plt.axvline(x=upper_bound, color='orange', linestyle='--', label=f'+3σ: {upper_bound:.1f}')
plt.axvspan(s - deltaX, s + deltaX, alpha=0.3, color='green', label=f'Зона попадания ±{deltaX}')
plt.xlabel('Расстояние')
plt.ylabel('Плотность вероятности')
plt.title('Распределение дальностей выстрелов')
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.savefig('ballistics_analysis_optimized.png', dpi=300, bbox_inches='tight')
plt.show()

# Проверка правила 3-х сигм
within_1sigma = np.sum((mass >= mean - std) & (mass <= mean + std)) / n
within_2sigma = np.sum((mass >= mean - 2*std) & (mass <= mean + 2*std)) / n
within_3sigma = np.sum((mass >= mean - 3*std) & (mass <= mean + 3*std)) / n

print(f"\nПравило 3-х сигм для распределения дальностей:")
print(f"В пределах 1σ: {within_1sigma*100:.2f}% (ожидается ~68.27%)")
print(f"В пределах 2σ: {within_2sigma*100:.2f}% (ожидается ~95.45%)")
print(f"В пределах 3σ: {within_3sigma*100:.2f}% (ожидается ~99.73%)")

end_time = time.time()
execution_time = end_time - start_time
print(f"\nВремя выполнения: {execution_time:.2f} секунд")
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from datetime import datetime, timedelta
import pandas as pd

# Данные задач
tasks = [
    # Задача, Исполнитель, Начало (часы.минуты), Длительность (мин)
    ("Дезинсекция", "Олег", 9.0, 30),
    ("Паласы", "Олег", 9.5, 120),
    ("Столы", "Олег", 11.5, 130),
    ("Финальная уборка", "Олег", (11.5 + 130 / 60), 200),

    ("Сбор мусора", "Валерий", 9.0, 240),
    ("Сухая уборка", "Валерий", 13.0, 240),

    ("Шторы", "Никита", 9.0, 200),
    ("Столы", "Никита", (9.0 + 200 / 60), 120),
    ("Финальная уборка", "Никита", (9.0 + 200 / 60 + 120 / 60), 160),

    ("Шторы", "Лэнс", 9.0, 200),
    ("Посуда", "Лэнс", (9.0 + 200 / 60), 120),
    ("Финальная уборка", "Лэнс", (9.0 + 200 / 60 + 120 / 60), 160),
]

# Преобразуем в DataFrame
df = pd.DataFrame(tasks, columns=["Задача", "Исполнитель", "Начало (часы)", "Длительность (мин)"])


# Преобразуем время в datetime
def hours_to_datetime(hours):
    base = datetime(2025, 12, 1, 0, 0)  # понедельник, но это суббота — не важно, дата условная
    return base + timedelta(hours=hours)


df["Начало"] = df["Начало (часы)"].apply(hours_to_datetime)
df["Окончание"] = df["Начало"] + pd.to_timedelta(df["Длительность (мин)"], unit='m')

# Цвета для исполнителей
color_map = {
    "Олег": "#4472C4",  # Синий
    "Валерий": "#70AD47",  # Зелёный
    "Никита": "#ED7D31",  # Оранжевый
    "Лэнс": "#C00000"  # Красный
}

# Уникальные задачи для оси Y
df["Label"] = df["Задача"] + " (" + df["Исполнитель"] + ")"
unique_labels = df["Label"].unique()
label_to_y = {label: i for i, label in enumerate(reversed(unique_labels))}  # сверху вниз

# Построение диаграммы
fig, ax = plt.subplots(figsize=(12, 8))

for _, row in df.iterrows():
    y_pos = label_to_y[row["Label"]]
    start = mdates.date2num(row["Начало"])
    end = mdates.date2num(row["Окончание"])
    color = color_map[row["Исполнитель"]]
    ax.barh(y_pos, end - start, left=start, height=0.5, color=color, edgecolor='black')

# Настройка осей
ax.set_yticks(list(label_to_y.values()))
ax.set_yticklabels(list(label_to_y.keys()))
ax.set_ylim(-0.5, len(unique_labels) - 0.5)

# Временная ось: с 9:00 до 17:00
ax.set_xlim(hours_to_datetime(9), hours_to_datetime(17))
ax.xaxis.set_major_locator(mdates.HourLocator(interval=1))
ax.xaxis.set_major_formatter(mdates.DateFormatter("%H:%M"))
plt.xticks(rotation=0)

# Подписи
plt.xlabel("Время", fontsize=12)
plt.title("Диаграмма Ганта: Уборка офиса — суббота, 1 декабря 2025 г.", fontsize=14)
plt.grid(axis='x', linestyle='--', alpha=0.5)

# Легенда по исполнителям
from matplotlib.patches import Patch

legend_elements = [Patch(facecolor=color_map[name], label=name) for name in color_map]
ax.legend(handles=legend_elements, loc='lower right')

plt.tight_layout()
plt.show()
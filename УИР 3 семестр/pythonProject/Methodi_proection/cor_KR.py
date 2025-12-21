from collections import defaultdict, deque
from typing import Dict, List, Iterable, Tuple, Set, Any
from math import log, exp
import sys

# Вариант 28
# (name, start, end, d_norm, d_min, c_norm, c_crit)
MAS = [
    ("1-2", 1, 2, 2, 1, 21, 40),
    ("1-3", 1, 3, 3, 2, 28, 38),
    ("2-3", 2, 3, 3, 3, 33, 33),
    ("2-4", 2, 4, 4, 2, 42, 65),
    ("3-4", 3, 4, 1, 1, 10, 10),
    ("3-5", 3, 5, 4, 3, 38, 48),
    ("3-6", 3, 6, 2, 2, 18, 18),
    ("4-6", 4, 6, 3, 2, 30, 40),
    ("4-7", 4, 7, 7, 5, 55, 75),
    ("5-6", 5, 6, 4, 2, 45, 61),
    ("5-8", 5, 8, 5, 4, 45, 56),
    ("6-7", 6, 7, 2, 2, 17, 17),
    ("6-8", 6, 8, 4, 3, 36, 48),
    ("7-8", 7, 8, 3, 2, 29, 41),
]

NAME_IDX = 0
START_IDX = 1
END_IDX = 2
D_NORM_IDX = 3
D_MIN_IDX = 4
C_NORM_IDX = 5
C_CRIT_IDX = 6

INDIRECT = 11.0
W_T = 2.0
W_C = 5.0

MAS_BY_NAME = {mas[NAME_IDX]: mas for mas in MAS}
all_nodes = set()
for mas in MAS:
    all_nodes.add(mas[START_IDX])
    all_nodes.add(mas[END_IDX])
NODES = sorted(all_nodes)

OUT_MAS = defaultdict(list)
IN_MAS = defaultdict(list)
for mas in MAS:
    OUT_MAS[mas[START_IDX]].append(mas)
    IN_MAS[mas[END_IDX]].append(mas)

SOURCES = [v for v in NODES if not IN_MAS[v]]
SINKS = [v for v in NODES if not OUT_MAS[v]]


def topo_nodes() -> List[int]:
    indeg = {v: len(IN_MAS[v]) for v in NODES}
    q = deque([v for v in NODES if indeg[v] == 0])
    order = []

    while q:
        v = q.popleft()
        order.append(v)
        for mas in OUT_MAS[v]:
            indeg[mas[END_IDX]] -= 1
            if indeg[mas[END_IDX]] == 0:
                q.append(mas[END_IDX])

    if len(order) != len(NODES):
        raise RuntimeError("Граф содержит цикл")

    return order


TOPO = topo_nodes()

PARAMS = {}
for mas in MAS:
    name = mas[NAME_IDX]
    Dn = mas[D_NORM_IDX]
    Dc = mas[D_MIN_IDX]
    Cn = mas[C_NORM_IDX]
    Cc = mas[C_CRIT_IDX]

    if Dn == Dc or abs(Cn - Cc) < 1e-9:
        a_ij = Cn
        b_ij = 0.0
    else:
        b_ij = (log(Cn) - log(Cc)) / (Dn - Dc)
        a_ij = Cn / exp(b_ij * Dn)

    PARAMS[name] = (a_ij, b_ij)


def calc_schedule(dur: Dict[str, int]) -> Dict[str, Any]:
    TE = {v: 0.0 for v in NODES}
    for v in TOPO:
        for mas in OUT_MAS[v]:
            t = dur[mas[NAME_IDX]]
            if TE[mas[END_IDX]] < TE[v] + t:
                TE[mas[END_IDX]] = TE[v] + t

    T_proj = max(TE[v] for v in SINKS)

    TL = {v: T_proj for v in NODES}
    for v in reversed(TOPO):
        if OUT_MAS[v]:
            TL[v] = min(TL[mas[END_IDX]] - dur[mas[NAME_IDX]] for mas in OUT_MAS[v])
        else:
            TL[v] = T_proj

    slack_mas = {}
    crit_mass = set()
    eps = 1e-9

    for mas in MAS:
        name = mas[NAME_IDX]
        t = dur[name]
        es = TE[mas[START_IDX]]
        ls = TL[mas[END_IDX]] - t
        sl = ls - es
        slack_mas[name] = sl
        if abs(sl) < eps:
            crit_mass.add(name)

    crit_paths = []

    def dfs(node: int, path: List[str], current_crit: Set[str]):
        if node in SINKS:
            crit_paths.append(path[:])
            return
        for mas in OUT_MAS[node]:
            name = mas[NAME_IDX]
            if name in current_crit:
                path.append(name)
                dfs(mas[END_IDX], path, current_crit)
                path.pop()

    for s in SOURCES:
        dfs(s, [], crit_mass)

    return {
        "T": T_proj,
        "TE": TE,
        "TL": TL,
        "slack_mas": slack_mas,
        "critical_mass": crit_mass,
        "critical_paths": crit_paths,
    }


def calc_direct_cost(dur: Dict[str, int]) -> float:
    total = 0.0
    for mas in MAS:
        name = mas[NAME_IDX]
        t = dur[name]
        a_ij, b_ij = PARAMS[name]
        total += a_ij * exp(b_ij * t)
    return total


def calc_total_cost(dur: Dict[str, int]) -> float:
    direct = calc_direct_cost(dur)
    sched = calc_schedule(dur)
    T = sched["T"]
    indirect = INDIRECT * T
    return direct + indirect

MAS_NAMES = [mas[NAME_IDX] for mas in MAS]
MIN_DUR = {mas[NAME_IDX]: mas[D_MIN_IDX] for mas in MAS}
MAX_DUR = {mas[NAME_IDX]: mas[D_NORM_IDX] for mas in MAS}


def _plans_rec(i: int, current: Dict[str, int]) -> Iterable[Dict[str, int]]:
    if i == len(MAS_NAMES):
        yield dict(current)
        return

    name = MAS_NAMES[i]
    for tau in range(MIN_DUR[name], MAX_DUR[name] + 1):
        current[name] = tau
        yield from _plans_rec(i + 1, current)

    current.pop(name, None)


def all_plans() -> Iterable[Dict[str, int]]:
    yield from _plans_rec(0, {})


def print_duration_changes(base_dur: Dict[str, int], opt_dur: Dict[str, int]):
    print("\nИзменения длительности работ:")
    print("Работа   | Нормальная | Оптимальная | Сокращение | Затраты при сокращении")
    print("-" * 65)

    for name in sorted(MAS_NAMES):
        base = base_dur[name]
        opt = opt_dur[name]
        reduction = base - opt

        if reduction > 0:
            base_cost = PARAMS[name][0] * exp(PARAMS[name][1] * base)
            opt_cost = PARAMS[name][0] * exp(PARAMS[name][1] * opt)
            additional_cost = opt_cost - base_cost
            print(f"{name:8} | {base:10} | {opt:11} | {reduction:10} | {additional_cost:10.2f}")


def find_min_total_cost_plan() -> Dict[str, Any]:
    best = None
    best_plan = None

    for plan in all_plans():
        total_cost = calc_total_cost(plan)

        if best is None or total_cost < best:
            best = total_cost
            best_plan = plan

    if best_plan is None:
        raise ValueError("Не найден оптимальный план")

    sched = calc_schedule(best_plan)
    direct_cost = calc_direct_cost(best_plan)

    return {
        "plan": best_plan,
        "T": sched["T"],
        "C_dir": direct_cost,
        "C_total": best,
        "sched": sched,
    }


def find_bicriteria_plan(w_time: float, w_cost: float) -> Dict[str, Any]:

    min_time = float('inf')
    max_time = 0
    min_cost = float('inf')
    max_cost = 0
    all_plans_list = []

    for plan in all_plans():
        sched = calc_schedule(plan)
        T = sched["T"]
        C_dir = calc_direct_cost(plan)

        all_plans_list.append((plan, T, C_dir))

        if T < min_time:
            min_time = T
        if T > max_time:
            max_time = T
        if C_dir < min_cost:
            min_cost = C_dir
        if C_dir > max_cost:
            max_cost = C_dir

    best = None
    best_plan_data = None

    for plan, T, C_dir in all_plans_list:
        if max_time - min_time > 1e-9:
            norm_T = (T - min_time) / (max_time - min_time)
        else:
            norm_T = 0

        if max_cost - min_cost > 1e-9:
            norm_C = (C_dir - min_cost) / (max_cost - min_cost)
        else:
            norm_C = 0

        z = w_time * norm_T + w_cost * norm_C

        if best is None or z < best:
            best = z
            sched = calc_schedule(plan)
            best_plan_data = {
                "plan": plan,
                "T": T,
                "C_dir": C_dir,
                "z": z,
                "sched": sched,
                "norm_T": norm_T,
                "norm_C": norm_C,
            }

    return best_plan_data


print("=" * 80)
print("Задание 1")

dur_norm = {mas[NAME_IDX]: mas[D_NORM_IDX] for mas in MAS}
sched_norm = calc_schedule(dur_norm)
T_norm = sched_norm["T"]
C_norm = calc_direct_cost(dur_norm)

dur_min = {mas[NAME_IDX]: mas[D_MIN_IDX] for mas in MAS}
sched_min = calc_schedule(dur_min)
T_min = sched_min["T"]
C_min = calc_direct_cost(dur_min)

print(f"\nT_norm = {T_norm:.1f}")
print(f"T_crit = {T_min:.1f}")
print(f"\nПрямые затраты при нормальном плане: C_norm = {C_norm:.2f}")
print(f"Прямые затраты при критическом плане: C_crit = {C_min:.2f}")

print("\nКритические пути при T_norm:")
for i, path in enumerate(sched_norm["critical_paths"], 1):
    print(f"  Путь {i}: {' → '.join(path)}")

print("\nКритические пути при T_crit:")
for i, path in enumerate(sched_min["critical_paths"], 1):
    print(f"  Путь {i}: {' → '.join(path)}")

print("\n" + "=" * 80)
print("Задание 2")

print(f"\nКосвенные затраты: {INDIRECT} ден. ед. / ед. времени")

result = find_min_total_cost_plan()

print(f"\nОптимальное время проекта: T_opt = {result['T']:.1f}")
print(f"Прямые затраты: C_dir = {result['C_dir']:.2f}")
print(f"Косвенные затраты: C_ind = {INDIRECT * result['T']:.2f}")
print(f"Суммарные затраты: C_total = {result['C_total']:.2f}")
print_duration_changes(dur_norm, result["plan"])
print("\nКритические пути при оптимальном плане:")
for i, path in enumerate(result["sched"]["critical_paths"], 1):
    print(f"  Путь {i}: {' → '.join(path)}")

print(f"\nСравнение с крайними случаями:")
print(f"Нормальный план: T={T_norm:.1f}, C_total={C_norm + INDIRECT * T_norm:.2f}")
print(f"Критический план: T={T_min:.1f}, C_total={C_min + INDIRECT * T_min:.2f}")
print(f"Оптимальный план: T={result['T']:.1f}, C_total={result['C_total']:.2f}")

print("\n"+"=" * 80)
print("Задание 3")

print(f"\nВеса критериев: W_T = {W_T} (время), W_C = {W_C} (затраты)")

all_T = []
all_C = []
for plan in all_plans():
    sched = calc_schedule(plan)
    all_T.append(sched["T"])
    all_C.append(calc_direct_cost(plan))

T_min_val = min(all_T)
T_max_val = max(all_T)
C_min_val = min(all_C)
C_max_val = max(all_C)

print(f"\nДиапазон значений:")
print(f"Время проекта: от {T_min_val:.1f} до {T_max_val:.1f}")
print(f"Прямые затраты: от {C_min_val:.2f} до {C_max_val:.2f}")
print("Проверка част. случ. :")
print(f"Случай 1(min_затрат): W_T = 0, W_C = 1")
result_cost_only = find_bicriteria_plan(0.0, 1.0)
print(f"Время проекта: {result_cost_only['T']:.1f}")
print(f"Прямые затраты: {result_cost_only['C_dir']:.2f}")
min_cost_plan = min(((plan, calc_direct_cost(plan)) for plan in all_plans()),
                    key=lambda x: x[1])[0]
min_cost = calc_direct_cost(min_cost_plan)
min_cost_T = calc_schedule(min_cost_plan)["T"]
print(f"Проверка (min_затрат): T={min_cost_T:.1f}, C={min_cost:.2f}")
print(f"Случай 2(min_времени): W_T = 1, W_C = 0")
result_time_only = find_bicriteria_plan(1.0, 0.0)
print(f"Время проекта: {result_time_only['T']:.1f}")
print(f"Прямые затраты: {result_time_only['C_dir']:.2f}")
print(f"Сравнение с критическим планом: T={T_min:.1f}, C={C_min:.2f}")
print(f"Осн. двухкритериальная оптимизация:")
print(f"W_T = {W_T}, W_C = {W_C}")
print("-" * 80)

result_bi = find_bicriteria_plan(W_T, W_C)

print(f"\nОптимальное время по свёртке: T_bi = {result_bi['T']:.1f}")
print(f"Прямые затраты: C_bi = {result_bi['C_dir']:.2f}")
print(f"Нормированное время: {result_bi['norm_T']:.4f}")
print(f"Нормированные затраты: {result_bi['norm_C']:.4f}")
print(f"Значение линейной свёртки: Z = {result_bi['z']:.4f}")

print_duration_changes(dur_norm, result_bi["plan"])

print("\nКритические пути:")
for i, path in enumerate(result_bi["sched"]["critical_paths"], 1):
    print(f"  Путь {i}: {' → '.join(path)}")
print("\nСравнительная таблица всех планов")
print(f"{'План':<20} {'Время':<10} {'Прямые затраты':<15} {'Суммарные затраты':<18} {'Z-свёртка':<10}")
print("-" * 80)
z_norm = W_T * (T_norm - T_min_val) / (T_max_val - T_min_val) + \
         W_C * (C_norm - C_min_val) / (C_max_val - C_min_val)
print(f"{'Нормальный':<20} {T_norm:<10.1f} {C_norm:<15.2f} {C_norm + INDIRECT * T_norm:<18.2f} {z_norm:<10.4f}")
z_min = W_T * (T_min - T_min_val) / (T_max_val - T_min_val) + \
        W_C * (C_min - C_min_val) / (C_max_val - C_min_val)
print(f"{'Критический':<20} {T_min:<10.1f} {C_min:<15.2f} {C_min + INDIRECT * T_min:<18.2f} {z_min:<10.4f}")
print(f"{'Оптимальный (затраты)':<20} {result_cost_only['T']:<10.1f} "
      f"{result_cost_only['C_dir']:<15.2f} "
      f"{result_cost_only['C_dir'] + INDIRECT * result_cost_only['T']:<18.2f} "
      f"{result_cost_only['z']:<10.4f}")
print(f"{'Двухкритериальный':<20} {result_bi['T']:<10.1f} "
      f"{result_bi['C_dir']:<15.2f} "
      f"{result_bi['C_dir'] + INDIRECT * result_bi['T']:<18.2f} "
      f"{result_bi['z']:<10.4f}")
print("-" * 80)
print("=" * 80)
print("Задание 4")
# Используем оптимальный план из задания 2 (при INDIRECT = 11)
C_dir_opt = result['C_dir']
T_opt = result['T']
# Находим INDIRECT, при котором C_dir = INDIRECT * T
INDIRECT_new = C_dir_opt / T_opt
print(f"В оптимальном плане (при исходных косвенных затратах 11):")
print(f"Прямые затраты C_dir = {C_dir_opt:.2f}")
print(f"Время проекта T = {T_opt:.1f}")
print(f"Чтобы выполнялось условие C_dir = C_ind, необходимо:")
print(f"INDIRECT = C_dir / T = {C_dir_opt:.2f} / {T_opt:.1f} = {INDIRECT_new:.4f} ден. ед. / ед. времени")
print(f"Проверка: C_ind = {INDIRECT_new:.4f} * {T_opt:.1f} = {INDIRECT_new * T_opt:.2f} ≈ C_dir")
print("=" * 80)
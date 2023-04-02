# Graph-Analyzer
Приложение для визуализации и анализа графов.

Загрузка:
```
git clone https://github.com/qsqnk/Graph-Analyzer
```

Возможности:
* Поиск сообществ (Leiden algorithm). Реализация алгоритма взята из библиотеки 
 ```
 https://github.com/CWTSLeiden/networkanalysis
 ```
* Раскладка графа (ForceAtlas2).
* Выделения ключевых вершин (Harmonic Centrality с алгоритмом Дейкстры и реализация Harmonic Centrality от neo4j из библеотеки https://github.com/neo4j/graph-data-science). При использовании Harmonic Centrality от neo4j программа загружает граф в базу данных neo4j.

### Leiden algorithm
Алгоритм Лейдена состоит из трех этапов: локальное перемещение вершин для максимизации CPM - характерисики качества кластеризации, улучшение кластеризации, построение нового графа на основе выделенных кластеров. Алгоритм позволяет производить поиск сообществ в больших социальных графах не более чем за несколько секунд.

##### Параметры
* Resolution - параметр, определяющий мелкость кластеризации (чем выше значение параметра, тем больше сообществ будет обнаружено)

###  ForceAtlas2
Силовой алгоритм раскладки графа. Позволяет отобразить граф в удобном для человека виде.

##### Параметры
* Scaling - параметр, определяющий визуальный размер графа, Чем он больше, тем больше размер раскладки.
* Gravity - параметр, определяющий силу притяжения каждой вершины графа к центру.
* Barnes Hut Optimization - параметр, определяющий включена ли оптимизация для алгортима. Рекомендуется включать на больших графах.
* LinLog Mode - параметр, определяющий включен ли режим, делающий раскладку более кластерезированной.

###  Harmonic Centrality
Алгоритм определяющий важность каждой вершины в графе. Важность показывается размером самой вершины. Чем вершина больше, тем больше её важность.

### Импорт
Приложение поддерживает чтение графа из csv файла в следующем формате: сначала список вершин, далее список взвешенных ребер. Все ребра и вершины должны быть уникальными.
Пример:

    A
    B
    D
    C
    E
    A,B,1
    A,E,1
    B,D,1
    C,E,1
   
### Экспорт
Приложение поддерживает сохранение графа как csv файла в следующем формате: список вершин с указанием координаты x, y, номера кластера и ранга центральности и список взвешенных ребер. Такой файл может быть повторно импортирован в приложение с сохранением раскладки и кластеризации.
Пример:

    A,10.0,20.0,1,0.0
    B,12.0,29.0,2,0.2
    D,13.4,20.1,3,0.2
    C,10.0,112.2,3,0.0
    E,77.5,401.3,4,0.0
    A,B,1
    A,E,1
    B,D,1
    C,E,1
   
   ###  Neo4j database
   Программа предоставляет возможность сохранить импортированный граф из csv в базу данных neo4j


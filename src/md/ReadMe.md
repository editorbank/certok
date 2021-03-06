﻿# CertOk
Консольная утилита проверки сертификатов.

## Описание принципа работы

Для осуществления мониторинга окончания срока действия сертификатов 
предлагается следующий принцип действий:  
На анализируемых серверах устанавливается утилита проверки.
Проверка запускается периодически по расписанию штатным планировщиком ОС.
Утилита проверяет все хранилища сертификатов и сообщения с результатом
работы отправляет в систему логирования (журналироавния). 
Система логирования утилиты может быть гибко настроена спомощю 
конфигурационных файлов. Логирование можно настроить для отравки
сообщений на централизованный сервер сбора системных логов.
Далее с централизованного сервера сообщения поступают 
в централизованную систему мониторинга через которую производится
анализ и принятие дальнейших решений, например, отправка почты 
группе администраторов АС. 

## Реализация

Утилита проверки сертификатов `CertOk` реализована в виде Java-приложения.
При проверке сертификатов утилита выводит результат в систему логирования.
В зависимости от оставшихся дней до срока окончания действия сертификата,
утилита формирует сообщения с разными уровнями важности:  
* менее 10 дней - с уровнем `ERROR` (ошибка);
* менее 45 дней - `WARN` (предупреждение);
* 45 и более - `INFO` (информация).

В качестве системы логирования используется популярная открытая 
библиотека логирования `Log4j` версии `2.x`. 
Её основной принцип что все сообщения могут быть отфильтрованы 
и одновременно направлены в одному или нескольким получателям,
в терминах этой библиотеки они называются `appenders` (добовлятели).
Подробная информация приведена на сайте [https://logging.apache.org/log4j/2.x/] 
Утилита управляется параметрами командной строки, подсказку 
о них можно получить запустив утилиту без параметров:
```
java -jar certok-1.0.jar
``` 
Параметры командной строки могут указываться в любом порядке.
Параметры являются регистро-чувствительными и должны указываться в том же регистре что и в подсказке.
Они подразделяются на команды и опции.
Одним из параметров должна быть команда. 
В случае одновременного указания нескольких команд утилита выполнит только одну из них в соответствии с внутренней логикой.
Команды и опции могут иметь короткую и/или длинную нотацию. 
Например, следующие команды эквивалентны `-h` и `--help`.
Для выполнения некоторых команд могут требоваться дополнительные опции, 
у которых могут быть ещё и аргумент, в этом случае он должен обязательно следовать за опцией.
Определить требуется ли опции аргумент можно посмотреть в подсказке.
В подсказке аргумент указывается в угловых скобках. 

## Команды

### Подсказка

При указании параметров `-h` или `--help` на консоль выводится подсказка.

### Проверка

Команда:
* `-c` или `--check`
Опции:
* `-s <file>` - хранилище сертификатов.
* `-p <password>` - пароль от хранилища.
* `-e` - при его наличии указывает что пароль указан в зашифрованном виде.
* `-a <name>` - имя псевдонима сертификата внутри хранилища. В случае не указания происходит проверка всех сертификатов в хранилище.

### Шифрование пароля

Команда:
* `-c` или `--check`
Опции:
* `-p <password>` - пароль который будет зашифрован и выведен на консоль.

### Логирование
 
Результаты работы утилиты можно сохранить в файл или отправить на агрегирующий эту информацию сервер.
Для этого нужно настроить систему логирования.
Указать какой файл настройки логгера будет использоваться можно опцией командной сроки `-Dlog4j2.configurationFile=./log4j2.xml`.
Эта опция должна быть указана перед опцией `-jar`.
Пример файла настройки логгера [log4j2-sample.xml](<log4j2-sample.xml>).

С помощью него можно отправлять сообщения в зависимости от указанного аппендера:
* FILE1 - в простой файл 
* HTTP1 - по HTTP протоколу 
* HTTPS1 - по HTTPS протоколу (необходимы файлы сертификатов)
* SYSLOG1, SYSLOG2 - два варианта отправки в формате SYSLOG (второй с SSL/TLS)

Аппендер и параметры соединения указаны в секции Properties.
> Неиспользуемые аппендеры желательно закоментировать, иначе они будут инициализироваться.

В этом файле реализована следующая логика обработки сообщений:
Обработка всех сообщений (Logger/Root) заглушена.
Обработкой сообщений от всех классов утилиты заведует (логгер с именем "com.github.editorbank.certok").
Сначала сообщения фильтруются логгером по приоритету (свойство "remote.level").
Далее цепочкой фильтров, сейчас в ней только один фильтрующий сообщения по регулярному выражению "daysleft.*".
Это выбирает сообщения только с остатком дней действия сертификата.
После фильтрации сообщения отправляются на аппендер "ASYNC", он в свою очередь параллельно отправляет
их на аппендеры:  "SENDLOG", "FAILOVER", "CONSOLE".
* "SENDLOG" – пишет сообщения в локальный файл лога "send.log".
* "CONSOLE" – выводит их на экран
* "FAILOVER" – отправляет ссобщеня на указанный аппендер в свойстве "remote.appender".
Если при этой отправке происходит какая либо ошибка, тогда сообщения перенаправляются в
аппендер "FAILLOG", если и там возникает ошибка то в аппендер "CONSOLE".
* "FAILLOG" -  пишет не отправленные сообщения в локальный файл лога "fail.log".

> !Внимание! Чтобы  логика "FAILOVER" апеендера работала правильно, в аппендерах которые он
> анализирует должно быть указано свойство ignoreExceptions="false".

Формат выводимых сообщений для каждого аппендера можно изменить указав его в тэге  
`<PatternLayout pattern="%d %-5p [%t] %c{10} - %m%n"/>`

Где :
* %p – приоритет сообщения (level)
* %t – имя потока (thread)
* %c – имя класса или имя логгера указанного в программе
* %m – сообщение
* %n – символ перевода строки

Между знаком `%` и буквой можно указать число символов ширины колонки. Если выводимое информация меньше ширины колонки, то она будет прижата к её правому краю. Для прижатия к левому, перед числом нужно указать и символ `-` (минус).
 


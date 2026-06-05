# Функция для сбора статистики с сайта https://35photo.pro/nixx

* PhotoStatsFunction - сбор статистики и сохранения в Firestore

## Операции с Function при помощи командной строки
Команды должны запускаться из корня проекта

### Deploy функции с помощью gcloud CLI

#### Деплой функции доступной для всех (без аутентификации)
gcloud functions deploy PhotoStatsFunctionHTTP
--region europe-north1
--gen2
--timeout=600s
--entry-point lv.nixx.photo.statistic.collector.function.PhotoStatsFunction
--runtime java17 --trigger-http --allow-unauthenticated

### Деплой функции c Pub/Sub триггером
gcloud functions deploy PhotoStatsFunction --gen2 --region="europe-north1" --runtime=java17 --entry-point="lv.nixx.photo.statistic.collector.function.PhotoStatsFunctionPubSub" --trigger-topic=photo-stats-topic --timeout=500s

* Запуск функции по расписанию (для тестирования)
gcloud scheduler jobs run photo-stats-job --location=europe-west1

* Публикация сообщения в Pub/Sub для запуска функции (для тестирования)
gcloud pubsub topics publish photo-stats-topic --message="test-run"
  
* Удаление задания в Cloud Scheduler
gcloud scheduler jobs delete photo-stats-job --location=europe-west1

### Удаление функции
gcloud functions delete PhotoStatsFunction --region europe-north1

### Получение информации о функции
gcloud functions describe PhotoStatsFunction --region europe-north1

### Получение списка всех scheduler jobs
gcloud scheduler jobs list --location=europe-west1

## URL
* Endpoint to execute function : https://europe-north1-lofty-root-435205-d1.cloudfunctions.net/PhotoStatsFunction

## Сборка проекта
* maven-shade-plugin -используется для сборки FatJar и включение в него внешних зависимостей



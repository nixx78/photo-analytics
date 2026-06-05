# === Script for full creation  =========================
# CONFIG
# =========================
$PROJECT_ID="lofty-root-435205-d1"
$REGION="europe-north1"
$SCHED_REGION="europe-west1"
$TOPIC="photo-stats-topic"
$JOB="photo-stats-job"
$SA="scheduler-sa"

Write-Host "=== Enable APIs ==="
gcloud services enable cloudscheduler.googleapis.com pubsub.googleapis.com cloudfunctions.googleapis.com run.googleapis.com

Write-Host "=== Create service account (ignore error if exists) ==="
gcloud iam service-accounts create $SA --display-name="Scheduler SA" 2>$null

Write-Host "=== Create Pub/Sub topic (ignore error if exists) ==="
gcloud pubsub topics create $TOPIC 2>$null

Write-Host "=== Grant Pub/Sub publisher role ==="
gcloud pubsub topics add-iam-policy-binding $TOPIC `
  --member="serviceAccount:$SA@$PROJECT_ID.iam.gserviceaccount.com" `
  --role="roles/pubsub.publisher"

Write-Host "=== Get project number ==="
$PROJECT_NUMBER = gcloud projects describe $PROJECT_ID --format="value(projectNumber)"
Write-Host "Project number: $PROJECT_NUMBER"

Write-Host "=== Deploy Cloud Function (Pub/Sub trigger) ==="
gcloud functions deploy PhotoStatsFunction `
  --gen2 `
  --region=$REGION `
  --runtime=java17 `
  --entry-point="lv.nixx.photo.statistic.collector.function.PhotoStatsFunctionPubSub" `
  --trigger-topic=$TOPIC `
  --timeout=500s

Write-Host "=== Grant invoker role ==="
gcloud functions add-iam-policy-binding PhotoStatsFunction `
  --region=$REGION `
  --gen2 `
  --member="serviceAccount:$PROJECT_NUMBER-compute@developer.gserviceaccount.com" `
  --role="roles/run.invoker"

Write-Host "=== Create scheduler job (ignore if exists) ==="
gcloud scheduler jobs create pubsub $JOB `
  --location=$SCHED_REGION `
  --schedule="0 */12 * * *" `
  --topic=$TOPIC `
  --message-body="run"

Write-Host "=== DONE ==="
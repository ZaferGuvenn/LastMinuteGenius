from fastapi import FastAPI, File, UploadFile, Form
from typing import List
import tempfile, os, logging, io
from google.cloud import storage, speech_v1p1beta1 as speech
import google.generativeai as genai
from PIL import Image
from fastapi.responses import JSONResponse
from fastapi.requests import Request
import traceback
import re

# === Config ===
BUCKET_NAME = "lastminutegenius-da04f.firebasestorage.app"
GOOGLE_API_KEY = "AIzaSyA5xCUzbURItEoSOUwZy6SD3lTNu8X-H90"
genai.configure(api_key=GOOGLE_API_KEY)

# === Logging ===
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# === GCS Ses Yükleme ===
def upload_to_gcs(local_path: str, blob_name: str) -> str:
    client = storage.Client()
    bucket = client.bucket(BUCKET_NAME)
    blob = bucket.blob(blob_name)
    blob.upload_from_filename(local_path)
    return f"gs://{BUCKET_NAME}/{blob_name}"

# === Speech-to-Text ===
def transcribe_from_gcs(gcs_uri: str, language_code: str) -> str:
    client = speech.SpeechClient()
    config = speech.RecognitionConfig(
        encoding=speech.RecognitionConfig.AudioEncoding.FLAC,
        sample_rate_hertz=16000,
        language_code=language_code,
        enable_automatic_punctuation=True,
    )
    audio = speech.RecognitionAudio(uri=gcs_uri)
    operation = client.long_running_recognize(config=config, audio=audio)
    response = operation.result(timeout=None)

    full_text = ""
    for result in response.results:
        full_text += result.alternatives[0].transcript + " "
    return full_text.strip()

# === Metin Bazlı Sadeleştirme ===
def clean_transcript_with_gemini(transcript: str) -> str:
    prompt = f"""
    Aşağıdaki transkripti sadeleştir. Argo, espri, saçma ve gereksiz tekrarları çıkar.
    Sadece anlamlı ve bilgilendirici kısımları döndür.
    Lütfen özetin kendisini sadece `[[[ÖZET]]]` ile `[[[SON]]]` arasına yaz.

    Transkript:
    {transcript}
    """
    model = genai.GenerativeModel("gemini-2.5-pro")
    response = model.generate_content(prompt)
    return extract_between_tags(response.text)

# === Görsellerle Destekli Özet (Gemini Vision) ===
def summarize_with_gemini_multimodal(transcript: str, image_paths: List[str]) -> str:
    model = genai.GenerativeModel("gemini-2.5-pro")
    prompt = [
        {"text": "Aşağıdaki video transkripti ve görselleri dikkate alarak bir özet üret."},
        {"text": "Lütfen yalnızca özet metnini `[[[ÖZET]]]` ile `[[[SON]]]` arasına yaz."},
        {"text": f"Transkript:\n{transcript}"}
    ]

    for i, img_path in enumerate(image_paths):
        with Image.open(img_path) as img:
            img_bytes = io.BytesIO()
            img.save(img_bytes, format="JPEG")
            img_bytes.seek(0)
            prompt.append({"mime_type": "image/jpeg", "data": img_bytes.read()})

    response = model.generate_content(prompt)

    try:
        return extract_between_tags(response.text)
    except Exception as e:
        logger.warning(f"⚠️ Gemini yanıtı alınamadı: {e}")
        return "Özet oluşturulamadı."

# === FastAPI Endpoint ===
@app.post("/summarize")
async def summarize(
    audio: UploadFile = File(...),
    language_code: str = Form(...),
    frames: List[UploadFile] = File([])  # opsiyonel görseller
):
    with tempfile.NamedTemporaryFile(delete=False, suffix=".flac") as tmp:
        tmp.write(await audio.read())
        tmp_path = tmp.name

    frame_paths = []

    try:
        blob_name = f"uploads/{audio.filename}"
        gcs_uri = upload_to_gcs(tmp_path, blob_name)
        transcript = transcribe_from_gcs(gcs_uri, language_code)
        logger.info(f"📜 STT sonucu: {transcript[:100]}...")

        if frames:
            for i, frame in enumerate(frames):
                with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as img_tmp:
                    img_tmp.write(await frame.read())
                    frame_paths.append(img_tmp.name)

            final_text = summarize_with_gemini_multimodal(transcript, frame_paths)
        else:
            final_text = clean_transcript_with_gemini(transcript)

        return {"summary": final_text}

    finally:
        os.remove(tmp_path)
        for path in frame_paths:
            os.remove(path)

# === Global Hata Yakalama ===
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    logger.error("🔥 Internal Server Error", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={"error": str(exc), "detail": traceback.format_exc()},
    )

#artık büyük dosyalar için gcs upload edilip özet işlemleri yapılıyor
@app.post("/summarize_by_gcs")
async def summarize_by_gcs(
    gcs_uri: str = Form(...),
    language_code: str = Form(...),
    frames: List[UploadFile] = File([])
):
    frame_paths = []

    try:
        transcript = transcribe_from_gcs(gcs_uri, language_code)

        if frames:
            for frame in frames:
                with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as img_tmp:
                    img_tmp.write(await frame.read())
                    frame_paths.append(img_tmp.name)
            final_text = summarize_with_gemini_multimodal(transcript, frame_paths)
        else:
            final_text = clean_transcript_with_gemini(transcript)

        return {"summary": final_text}

    finally:
        for path in frame_paths:
            os.remove(path)


#metni parçalama fonksiyonu
def extract_between_tags(text: str, start_tag="[[[ÖZET]]]", end_tag="[[[SON]]]") -> str:
    match = re.search(f"{re.escape(start_tag)}(.*?){re.escape(end_tag)}", text, re.DOTALL)
    if match:
        return match.group(1).strip()
    return text.strip()  # fallback: tüm metni döndür


def generate_quiz_from_summary(summary: str) -> str:
    prompt = f"""
Aşağıdaki özet metne göre 5-10 adet çoktan seçmeli sınav sorusu oluştur.

Her sorunun:
- 1 doğru cevabı ve
- 3 yanlış cevabı olmalı.
- Formatı JSON olarak döndür:
[[[QUIZ]]]
[
  {{
    "soru": "Soru metni",
    "secenekler": ["A", "B", "C", "D"],
    "dogru": "A"
  }},
  ...
]
[[[END]]]

Özet:
{summary}
    """

    model = genai.GenerativeModel("gemini-2.5-pro")
    response = model.generate_content(prompt)
    return extract_between_tags(response.text, "[[[QUIZ]]]", "[[[END]]]")


@app.post("/generate_quiz")
async def generate_quiz(summary: str = Form(...)):
    try:
        quiz_json = generate_quiz_from_summary(summary)
        return {"quiz": quiz_json}
    except Exception as e:
        logger.error("❌ Quiz oluşturma hatası", exc_info=True)
        return JSONResponse(
            status_code=500,
            content={"error": str(e)}
        )

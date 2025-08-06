# 🎬 AI-Powered Video Summarizer & Quiz Generator

This Android application uses the power of **Gemini AI**, **Google Cloud Speech-to-Text**, and **Firebase** to help users **summarize educational videos**, **generate questions**, and **prepare quickly for exams**.

---

## 🚀 Key Features

- 🎥 **Video-to-Audio Extraction**: Extracts audio from videos using `FFmpeg` and uploads it to Firebase Storage securely.
- 📄 **AI-Generated Summaries**: Uses Google's Speech-to-Text API + Gemini AI to generate smart, human-like summaries from transcribed audio.
- 🖼️ **Visual Context Enhancement**: Users can select up to 5 frames from the video to send along with audio, improving summary accuracy with visual cues.
- ✅ **Quiz Generation**: Summaries can be sent to a secondary API endpoint to generate multiple-choice questions with answers.
- 📝 **Summary Editing**: Users can edit AI-generated summaries before saving them for future study.
- 📦 **Offline Storage**: All summaries, questions, and metadata are stored locally using Room database.
- 🛠️ Built with **Jetpack Compose**, **Retrofit**, **Room**, **Firebase Auth & Storage**, and **FastAPI (Cloud Run)** on the backend.

---

## 📱 How It Works

1. **Select a Video**  
   Choose a video from your gallery.

2. **Extract & Upload Audio**  
   The app converts the video to a `.flac` audio file using FFmpeg and uploads it to Firebase Storage using anonymous authentication.

3. **Choose Visual Aids (Optional)**  
   Select up to 5 key video frames to help Gemini produce a more accurate and contextual summary.

4. **AI Summary Generation**  
   Firebase link and optional images are sent to a FastAPI app on Cloud Run. Audio is transcribed using Google Speech-to-Text, then sent (with images if available) to Gemini for summarization.

5. **Edit & Save Summary**  
   Edit the output if needed, and store it locally for future use.

6. **Generate Quiz**  
   Send the summary to another FastAPI function to receive a short multiple-choice quiz. Great for quick study sessions!

---

## 👩‍⚖️ Legal & User Responsibility

> ⚠️ **Important Notice**  
> All media content uploaded by the user (including video frames and audio files) is processed via Firebase and Google Cloud infrastructure. These files are not publicly shared but are temporarily stored for processing.
>
> By using this application, the **user confirms** that they:
>
> - Have the legal right to use and process the selected videos.
> - Take full responsibility for the uploaded content.
> - Understand that summaries and questions are generated based on that content and are for personal or educational use only.
>
> The developer assumes **no liability** for any copyright infringements, misuse of generated content, or any data uploaded by the user.

---

## 👥 Ideal For

- 🎓 Students preparing for exams
- 🧑‍🏫 Educators creating summaries and practice questions
- 📊 Content reviewers and researchers
- 🧠 Anyone wanting to **understand complex video content quickly**

---

## 🧪 Technologies Used

- Android with Kotlin & Jetpack Compose
- Firebase Storage & Authentication
- Retrofit + Room for local data handling
- FFmpeg for audio extraction
- FastAPI (Cloud Run) as backend
- Google Cloud Speech-to-Text
- Gemini API for summarization and question generation

---

## 📥 Installation & Usage

> The app is not yet on the Play Store.

To test or build manually:

```bash
git clone https://github.com/your-repo/video-summarizer-app.git

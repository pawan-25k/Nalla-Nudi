# Nalla-Nudi - Android Dictionary App

An offline Android dictionary app that helps Kannada-medium students learn technical English vocabulary with Kannada explanations and voice pronunciation.

## Features
- Real-time search (under 200ms)
- Kannada explanations for 30+ technical terms
- Voice pronunciation via Text-To-Speech
- Subject filters: Science, Math, Commerce
- My List - bookmark difficult words
- Flashcard revision mode
- 100% offline - no internet required

## Tech Stack
Kotlin, Room Database, ViewModel, LiveData, RecyclerView, Android TTS, ViewBinding

## Firebase Authentication Setup (Email/Password)
1. Create a Firebase project and register this Android app package: `com.nudi.nallanudi`.
2. Enable **Authentication > Sign-in method > Email/Password** in Firebase Console.
3. Download `google-services.json` from Firebase Console.
4. Place the file at:
   - `app/google-services.json`
5. Build and run the app. Login and Registration screens will use Firebase Authentication.

> `google-services.json` is intentionally not included in this repository.

## MindMatrix VTU Internship Program - Project 40

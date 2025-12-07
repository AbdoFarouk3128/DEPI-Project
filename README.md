# 🎬 Absolute Cinema App (Jetpack Compose)

## 📌 Project Name
Absolute Cinema  

## 👥 Team Members
- **Abdelrahman Farouk**
- **Abdelrahman Abdellatif**
- **Salaheldin Magdy**
- **Kyrollos Farid**

## 💡 Project Idea
Absolute Cinema is a modern, native Android application built entirely with **Kotlin** and **Jetpack Compose**.  
The app enables users to **browse, discover, and search for movies** with real-time data provided by **The Movie Database (TMDB)** API.  
It follows **modern Android development standards**, including declarative UI with Compose, clean architecture, secure data handling, and comprehensive unit testing.


## 🛠️ Technologies Used
- **Kotlin** (Primary Language)
- **Jetpack Compose** (UI Toolkit)
- **Android MDC** (Material Design principles)
- **Retrofit** (Networking)
- **Git & GitHub** (Version Control)
- **UI/UX Design**
- **Security Best Practices**

## ✨ Features

- **Explore Movies** Browse Daily Selection, Popular, Now Playing, Upcoming, and Top Rated Movies.
- **Search & Filter:** Search by movie title and filter by single or multiple genres.
- **User Accounts:** Create a profile with name, email, birthday, and profile photo.
- **Movie Details:** View full details including rating, overview, cast, recommended movies, trailer (YouTube), IMDb link.
- **Personal Lists:** Add movies to Watchlist, Liked, Watched, and Rated lists.
- **List Management:** Remove or manage movies within any list.
- **Notifications:** Receive a reminder every day to watch a movie and a special notification on your birthday.
- **User Actions:** Add or remove movies from your Watchlist, Liked, and Watched lists; rate movies; and share movies with others.

## 🏛️ Architecture Overview
Absolute Cinema follows a **Clean Architecture** style combined with the **MVVM (Model–View–ViewModel)** pattern.

- **Data Layer:**  
  Contains API calls, models, helpers, and utilities such as notifications, internet checking, and sharing. This layer handles all data fetching from TMDB and prepares it for the rest of the app.

- **ViewModel Layer:**  
  Each screen has its own ViewModel responsible for state management, business logic, and communicating with the data layer. This ensures a clean separation between UI and logic.

- **UI Layer:**  
  Built entirely with **Jetpack Compose**, organized into reusable components, screens, and theme files. The UI layer observes ViewModel state and displays it declaratively.

- **Navigation Layer:**  
  Uses Jetpack Navigation for Compose, containing a centralized navigation graph and route definitions.

This structure keeps the app **modular, scalable, and easy to maintain**, while following modern Android development best practices.


## 🚀 Quick Start

### 🧩 Prerequisites
Before you begin, make sure you have the following installed and ready:

- **Android Studio Otter** (or newer)
- **Android 10+** (API level 26 or higher)
- A **TMDB API Key** — get one from [The Movie Database (TMDB)](https://www.themoviedb.org/settings/api)

### ⚙️ Installation

#### 1. Clone the Repository
Clone the project using **Git**:

```bash
git clone https://github.com/AbdoFarouk3128/DEPI-Project.git
```

#### 2. Open the project in Android Studio.

#### 3. Add Your TMDB API Key

Navigate to:
```bash
data/api/CinemaCallable.kt
```

Locate the API_KEY variable and replace it with your TMDB key:
```bash
const val API_KEY = "your_api_key_here"
```

#### 4. Run the app on your emulator or device.
Choose your emulator or physical device, then click Run ▶️.

## 🎨 Design
The design for this app is highly influenced from Letterboxd app
You can find that app [here](https://play.google.com/store/apps/details?id=com.letterboxd.letterboxd&hl=en&pli=1)

## 📎 Project Drive Link
[Click here to access project files (Presentation and Demo Video)](https://drive.google.com/drive/folders/1oIn39CgFFAwPu63NSKCKSQ2pffnXIWOW?usp=sharing)

## 📄 Documentation
- [Team Members & Responsibilities](Docs/Team_Members_Responsibilities.docx)
- [Project Description & Analysis](Docs/Project_Description_Analysis.docx)
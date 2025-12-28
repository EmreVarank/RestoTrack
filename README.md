# 🍲 RestoTrack: Restaurant & Cafe Management System

A robust and user-friendly Android application designed for small to medium-sized restaurants or soup houses. This app simplifies table tracking, order management, and financial record-keeping (Cash/Bank) using local storage.

## 🌟 Key Features

- **Dynamic Table Management:** Visual tracking of 8 different tables with real-time occupancy status (Green/Red color coding).
- **Order Details:** Specific order logging for each table with itemized lists.
- **Financial Accounting:** Separate tracking for **Cash (Nakit)** and **POS/Credit Card (Bank)** payments.
- **Persistent Storage:** 
  - **SQLite:** Handles complex relational data like table statuses and specific orders.
  - **SharedPreferences:** Manages cumulative financial totals for quick access.
- **Reset System:** A secure administrative feature to wipe all data and start a fresh session (clears both SQL and SharedPrefs).

## 🛠 Tech Stack

- **Language:** Java
- **Database:** SQLite (Local SQL database)
- **UI Design:** XML (ConstraintLayout & LinearLayout)
- **Data Storage:** SharedPreferences (for financial balances)
- **Tools:** Android SDK, AppCompat v1.7.1

## 📸 Screen Structure

- **Main Screen:** Overview of all tables and their current status.
    - <img width="400" height="884" alt="image" src="https://github.com/user-attachments/assets/850245eb-7ee0-4c68-83a4-6dc4aa451d12" />

- **Payment Screen (`OdemeActivity`):** 
    - Detailed order summary per table.
    - Payment processing (Nakit/POS).
    - Real-time display of total Cash and Bank balances.
    - Navigation back to the Main Menu.
    - <img width="400" height="886" alt="image" src="https://github.com/user-attachments/assets/adbcd518-fc49-4534-b6d2-bdd56d4d6a65" />


## 📋 Installation

1. Clone the repository: bash git clone https://github.com/EmreVarank/RestoTrack-Android.git
2. Open the project in **Android Studio**.
3. Let Gradle sync complete.
4. Run on an Emulator or a physical Android device (API 24+ recommended).

## 📂 Project Architecture

- `MainActivity.java`: Handles the primary grid of tables and occupancy logic.
- `OdemeActivity.java`: Manages the payment lifecycle and UI updates for balances.
- `DatabaseHelper.java`: The core SQL engine managing table updates, order deletions, and data resets.
- `activity_odeme.xml`: The layout for the payment and accounting interface.

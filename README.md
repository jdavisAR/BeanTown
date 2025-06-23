# BeanTown

---

## 🚀 Overview

Basic List/Detail's app for efficiently loading and displaying Jelly Beans fetched from the open-source Jelly Belly Wiki API. This app is built with Jetpack Compose and uses modern MVI/MVVM archictecture.

## ✨ Features

* Jelly Beans are efficiently loaded from the Jelly Bean Wiki using **Ktor** in pages using **Paging3** then displayed in a `LazyColumn`.
* Jelly Beans are cached locally using **Room**.
* Jelly Images are asynchronsouly loaded using **Coil**
* The `lightVibrant` colors are extracted from Jelly Bean images using **Palette** to add vibrancy to the UI.
* All Viewmodels, Repository and Networking components are injected using **Koin**.

## 🛠️ Technologies Used

* **Kotlin (2.1.21)**
* **Jetpack Compose (BOM: 2025.06.01):**
* **Coil (3.2.0):**
* **Jetpack Pallete library (1.0.0)**
* **Jetpack Room Persistence library (2.7.2)**
* **Jetpack Paging 3 library (3.3.6)**
* **Ktor (3.1.2)**
* **Koin (4.1.0)**

## ⚡ Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/jdavisAR/BeanTown.git](https://github.com/jdavisAR/BeanTown.git)
    cd BeanTown
    ```
2.  **Install dependencies:**
    Make sure the latest version of Android Studio is installed. Downloads can be found [here](https://developer.android.com/studio).
    
## 🚀 Usage

Perform a gradle sync by clicking the 'sync' button in the Android Studio menu: 
<img width="118" alt="Screenshot 2025-06-23 at 4 55 36 PM" src="https://github.com/user-attachments/assets/ea2227a3-f42a-41b9-bc64-e36f38ff5afe" />
Once the sync completes, make sure the 'App' module is selected and hit run app: 
<img width="163" alt="Screenshot 2025-06-23 at 4 58 56 PM" src="https://github.com/user-attachments/assets/f143e8a4-301c-45a9-92f2-6c2db4f52cc5" />

## Demo

https://github.com/user-attachments/assets/19056d04-48f8-45dd-b889-b9abe1091a1e

### Note

The Jelly Bean Wiki API appears to enter a hibernation/low power mode, potentially to cut back on operating cost, and API hits are delayed while the backend spins back up (_This is my theory based on usage; as this is undocumentated behavior_). This should only be an issue for the first page fetch as the paging library runs them one after another. Aubsequent pages will be fetched under ≈ 200 ms. Since, the app caches Jelly Beans for an hour in a local database this shouldn't be much of an issue in practice.

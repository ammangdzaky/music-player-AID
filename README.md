# 🎵 AID Music Player
AID (Audio Interface Delight) adalah aplikasi pemutar musik berbasis JavaFX yang dirancang untuk memberikan pengalaman mendengarkan musik yang sederhana, elegan, dan personal. Aplikasi ini dibangun dengan pendekatan modular berorientasi objek (OOP) dan cocok sebagai proyek pembelajaran maupun hiburan.

## 📌 Fitur Utama
* 👤 Autentikasi pengguna (Sign Up & Login)
* 🎶 Pemutaran lagu dari file lokal
* 📂 Pembuatan playlist pengguna secara privat
* 🎨 Tampilan visual interaktif dengan JavaFX
* 💾 Penyimpanan data menggunakan file JSON
* 🌙 Tema warna hitam dan kuning yang bersahabat

## 🔎 Implementasi OOP
isiiiiiii

## 📁 Struktur Folder
```bash
music-player-AID/
├── src/
│   ├── main/
│   │   ├── java/aid/
│   │   │   ├── controllers/       # LoginController, RegisterController, dsb
│   │   │   ├── managers/          # DataManager, SceneManager
│   │   │   ├── models/            # User, Song, Playlist, SmartPlaylist, dst.
│   │   │   ├── utils/             # Validator, IDGenerator
│   │   │   ├── views/             # LoginView, HomeView, ProfileView
│   │   │   └── Main.java          # Entry point aplikasi
│   │   └── resources/
│   │       ├── styles/style.css   # CSS styling
│   │       └── images/            # Aset visual (logo, default avatar)
│   │       └── songs/             # Folder untuk lagu-lagu lokal
│   │       └── data/              # File JSON untuk persistensi
├── pom.xml                        # Konfigurasi Maven
└── README.md
```

## 🔧 Cara Menjalankan (via Maven)
1. Pastikan Java & Maven sudah terinstal
2. Clone repositori:
```bash
git clone https://github.com/username/music-player-AID.git
cd music-player-AID
```

3. Jalankan via Maven:
```bash
mvn clean javafx:run
atau
.\mvnw clean
.\mvnw javafx:run
```

## 👥 Anggota Tim dan pembagian tugas
* Azizah Nurul Izzah (Login dan Sign up)
* Indira Ramayani (Profil Pengguna)
* Abdurrahman Dzaky (Menu atau halaman utama)

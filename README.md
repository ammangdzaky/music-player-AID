# 🎵 AID Music Player

**AID (Audio Interface Delight)** adalah aplikasi pemutar musik berbasis **JavaFX** yang dirancang untuk memberikan pengalaman mendengarkan musik yang sederhana, elegan, dan personal. Aplikasi ini dibangun dengan pendekatan **modular berorientasi objek (OOP)**, cocok sebagai proyek pembelajaran maupun hiburan.

---

## 📌 Fitur Utama

- 👤 **Autentikasi Pengguna**  
  Sistem *Sign Up* dan *Login* untuk manajemen akun pengguna.

- 🎶 **Pemutaran Lagu Lokal**  
  Memutar lagu dari file audio yang disimpan secara lokal.

- 📂 **Manajemen Playlist**  
  - **Standard Playlist**: Pengguna dapat membuat playlist dan menambahkan lagu secara manual.  
  - **Smart Playlist**: Mengisi lagu secara otomatis berdasarkan kriteria tertentu (misalnya genre).  
  - Semua playlist disimpan permanen dan terhubung dengan akun pengguna.

- 🎨 **Antarmuka Visual Interaktif**  
  Antarmuka responsif dan menarik dengan JavaFX.

- 💾 **Penyimpanan Data Persisten**  
  Menggunakan file JSON untuk menyimpan data pengguna, profil, dan playlist.

- 🌙 **Tema Estetika Modern**  
  Skema warna hitam dan kuning yang nyaman di mata.

- ⏩ **Kontrol Pemutaran Lengkap**  
  Play, pause, next, previous, acak, ulangi, dan kontrol kecepatan.

- 🔊 **Kontrol Volume & Progres**  
  Slider volume dan progress bar interaktif.

- 🔎 **Pencarian & Filter Lagu**  
  Cari lagu berdasarkan judul atau filter berdasarkan genre.

---

## 🔎 Implementasi OOP

### 1. 🛡️ Encapsulation (Enkapsulasi)

- Data seperti `userName`, `password`, `title`, `artist` bersifat `private`.
- Akses melalui `getter` dan `setter` yang terkontrol.
- Kelas `DataManager` menyembunyikan detail penyimpanan dan pengambilan data JSON.

### 2. 🧬 Inheritance (Pewarisan)

- `Playlist` sebagai kelas abstrak dengan properti dan metode umum.
- `StandardPlaylist` dan `SmartPlaylist` mewarisi dan mengimplementasikan metode spesifik.

### 3. 🎭 Abstraction (Abstraksi)

- `Playlist` bertindak sebagai antarmuka umum yang memungkinkan bagian lain dari aplikasi tidak perlu tahu implementasi detailnya.
- Fokus pada **"apa" yang dilakukan** bukan **"bagaimana" caranya**.

### 4. 🌀 Polymorphism (Polimorfisme)

- Objek `playlists` dapat berisi `StandardPlaylist` dan `SmartPlaylist`.
- Metode yang dipanggil disesuaikan secara otomatis berdasarkan tipe aktual objek saat runtime.
- `PlaylistTypeAdapter` mendukung serialisasi dan deserialisasi objek turunan dari `Playlist` dengan tepat saat menyimpan/memuat JSON.

---

## 📁 Struktur Folder

```bash
music-player-AID/
├── src/
│   ├── main/
│   │   ├── java/aid/
│   │   │   ├── controllers/        # Controller: Login, Register, Home, Profile
│   │   │   ├── managers/           # Manajer: DataManager, SceneManager, TypeAdapter
│   │   │   ├── models/             # Model: User, Song, Playlist (dan turunannya)
│   │   │   ├── utils/              # Utilitas: IDGenerator, Validator, SongUtils
│   │   │   ├── views/              # Tampilan UI: LoginView, HomeView, dll.
│   │   │   └── Main.java           # Titik masuk aplikasi JavaFX
│   │   └── resources/
│   │       ├── styles/             # CSS kustomisasi tampilan
│   │       ├── images/             # Gambar album, avatar, ikon
│   │       ├── songs/              # File audio lokal (.wav, .mp3)
│   │       └── data/               # File JSON untuk penyimpanan data
├── pom.xml                         # Konfigurasi Maven: dependensi dan build
└── README.md                       # Dokumentasi proyek (file ini)
```

---

## 🚀 Cara Menjalankan Proyek (via Maven)

1. Pastikan sudah menginstal **Java JDK 17+** dan **Apache Maven**.
2. Clone repositori:
   ```bash
   git clone https://github.com/username/music-player-AID.git
   cd music-player-AID
   ```
   Ganti `username` dengan akun GitHub Anda.

3. Jalankan aplikasi dengan perintah:
   ```bash
   mvn clean javafx:run
   ```
   > Jika menggunakan Windows dan `mvn` belum ada di PATH, gunakan:
   ```bash
   .\mvnw clean
   .\mvnw javafx:run
   ```

---

## 👥 Anggota Tim

- Azizah Nurul Izzah (Login dan Sign up)
- Indira Ramayani (Profil Pengguna)
- Abdurrahman Dzaky Safrullah (Menu atau halaman utama)
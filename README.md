# 🎵 AID Music Player
AID adalah aplikasi pemutar musik berbasis JavaFX yang dirancang untuk memberikan pengalaman mendengarkan musik yang sederhana, elegan, dan personal. Aplikasi ini dibangun dengan pendekatan modular berorientasi objek (OOP) dan cocok sebagai proyek pembelajaran maupun hiburan.

## 📌 Fitur Utama
* 👤 Autentikasi pengguna (Sign Up & Login)
* 🎶 Pemutaran lagu dari file lokal
* 📂 Pembuatan playlist pengguna secara privat
* 🎨 Tampilan visual interaktif dengan JavaFX
* 💾 Penyimpanan data menggunakan file JSON
* 🌙 Tema warna hitam dan kuning yang bersahabat

## 🔎 Implementasi OOP BLOM FIX

Proyek ini menerapkan empat pilar utama OOP untuk mencapai desain yang bersih, mudah dipelihara, dan skalabel.

### 1. Enkapsulasi
- Semua field data dalam model seperti `User `, `Song`, dan `Playlist` bersifat privat atau terlindungi.
- Akses ke field ini dikendalikan melalui metode getter dan setter publik.
- Mengontrol akses langsung ke data kelas dan menjaga integritas.
- Contoh:  
  Kelas `User ` mengenkapsulasi data pengguna seperti nama pengguna, kata sandi, dan path gambar profil dengan getter dan setter.

### 2. Pewarisan
- Kelas mewarisi fitur dan perilaku umum dari kelas induk, mempromosikan penggunaan kembali kode dan hierarki.
- Contoh:  
  Kelas `HomeView` menyesuaikan perilaku dari `ListCell<Song>` dengan mengoverride metode `updateItem(Song item, boolean empty)` dan memanggil `super.updateItem(item, empty);` untuk mempertahankan logika pembaruan dasar sebelum menambahkan pembaruan UI khusus. Ini menunjukkan penggunaan pewarisan di mana subclass memperluas dan memodifikasi perilaku superclass-nya.

### 3. Abstraksi
- Kelas abstrak dan antarmuka digunakan untuk menyembunyikan detail implementasi internal dan hanya mengekspos fungsionalitas yang diperlukan.
- Contoh:  
  Kelas `Playlist` adalah kelas abstrak yang mendefinisikan perilaku umum seperti `addSong()` dan `removeSong()`, yang diimplementasikan secara berbeda oleh subclass seperti `StandardPlaylist` dan `SmartPlaylist`.

### 4. Polimorfisme
- Metode dengan nama yang sama berperilaku berbeda tergantung pada objek yang memanggilnya.
- Contoh:  
  Metode `addSong()` diimplementasikan secara berbeda dalam `StandardPlaylist` dan `SmartPlaylist`, memungkinkan jenis playlist untuk memperluas atau membatasi cara lagu dikelola.

---

## 📁 Struktur Folder BLOM FIX
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
### 1. Pastikan Java & Maven sudah terinstal
### 2. Clone repositori:
```bash
git clone https://github.com/username/music-player-AID.git
cd music-player-AID
```
### 3. Jalankan via Maven:
```bash
mvn clean javafx:run
atau
.\mvnw clean
.\mvnw javafx:run
```

## 👥 Anggota Tim dan pembagian tugas
* Azizah Nurul Izzah (Login dan Sign up)
* Indira Ramayani (Profil Pengguna)
* Abdurrahman Dzaky (Menu/halaman utama)

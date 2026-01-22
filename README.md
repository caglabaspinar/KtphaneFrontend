# KtphaneMobil – Android Frontend
Bu proje, Library Management System backend’i ile haberleşen Android mobil istemci uygulamasıdır.
Uygulamanın amacı; kütüphanelerin listelenmesi, kütüphane bazlı kitapların görüntülenmesi, 
kitap detaylarının incelenmesi ve öğrenci adına kitap ödünç alma işlemlerinin mobil ortamda gerçekleştirilmesidir.

Uygulama, RESTful Web API üzerinden veri alacak şekilde geliştirilmiş olup, fragment tabanlı bir yapı kullanmaktadır.

# Kullanılan Teknolojiler
Programlama Dili: Kotlin
Platform: Android
UI Yapısı: Activity + Fragment (Bottom Navigation)
View Binding: Aktif
HTTP İletişimi: Retrofit + OkHttp
JSON Dönüşümü: Gson Converter
Listeleme Yapısı: RecyclerView + Adapter
Basit Veri Saklama: SharedPreferences

# API Base URL Ayarı
Mobil uygulama, backend servislerine Retrofit üzerinden bağlanmaktadır.
API base URL adresi aşağıdaki dosyada tanımlanmıştır:

>app/kotlin+java/com.example.ktphanemobil/api/RetrofitClient.kt

Bu dosya içerisinde backend adresi sabit olarak tanımlanmıştır.
Android Emulator üzerinde çalışırken backend’e erişim için 10.0.2.2 adresi kullanılmaktadır.
Bu adres, emulator’un host makinede çalışan backend servisine erişebilmesi için gereklidir.
Fiziksel cihaz kullanılması durumunda base URL, bilgisayarın yerel IP adresi ile güncellenmelidir.


# Emulator Çalıştırma Adımları
Proje Android Studio ile açılır.
Gradle senkronizasyonu tamamlanır.
Device Manager üzerinden bir Android Emulator oluşturulur ve çalıştırılır.
RetrofitClient.kt dosyasındaki API base URL adresinin backend ile uyumlu olduğu kontrol edilir.
Android Studio üzerinden Run işlemi başlatılarak uygulama Android Emulator üzerinde çalıştırılır ve test edilir.


# Uygulama Akışı
Kullanıcı uygulamayı açar ve giriş yapar.
Ana ekranda kütüphaneler listelenir.
Seçilen kütüphaneye ait kitaplar görüntülenir.
Bir kitap seçildiğinde kitap detay bilgileri gösterilir.
Kullanıcı kitap için ödünç alma işlemini başlatabilir.
İşlem sonucunda kullanıcı bilgilendirilir.



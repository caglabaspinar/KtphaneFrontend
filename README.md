# KtphaneMobil – Android Frontend

Bu proje, KtphaneBackend RESTful Web API ile haberleşen Android mobil istemci uygulamasıdır. Uygulama; öğrenci ve admin kullanıcıların sisteme giriş yapabildiği, kütüphaneleri ve kitapları görüntüleyebildiği, kitap ödünç alma ve iade işlemlerini gerçekleştirebildiği mobil arayüz katmanını oluşturmaktadır.

Uygulama fragment tabanlı bir yapı ile geliştirilmiş olup JWT tabanlı kimlik doğrulama mekanizması backend ile entegre şekilde çalışmaktadır.

Kullanılan Teknolojiler

Programlama Dili: Kotlin
Platform: Android
Mimari: Activity + Fragment tabanlı yapı
View Binding: Aktif
HTTP İletişimi: Retrofit + OkHttp
JSON Dönüşümü: Gson Converter
Listeleme: RecyclerView + Adapter
Oturum Yönetimi: SharedPreferences
Yetkilendirme: JWT token saklama ve header üzerinden gönderim

# Genel Özellikler

Kullanıcı kayıt olma ve giriş yapma
JWT token üretimi sonrası oturumun mobilde saklanması
Role bazlı yönlendirme sistemi
Student ve Admin için farklı ekran akışları
Kütüphane listeleme
Kütüphaneye bağlı kitap listeleme
Kitap detay görüntüleme
Kitap ödünç alma işlemi
Kitap iade işlemi
Aktif ödünçlerim ekranı
Ödünç geçmişi ekranı
Admin paneli üzerinden kitap ekleme, güncelleme ve silme
Silme işlemi sırasında ödünç kontrolü
Şifre sıfırlama akışı
Mail ile gönderilen 6 haneli kod doğrulama
Yeni şifre belirleme

# Oturum ve Güvenlik

Kullanıcı giriş yaptıktan sonra backend tarafından üretilen JWT token SharedPreferences içerisinde saklanır. Uygulama kapatılıp açıldığında token kontrolü yapılır ve kullanıcı tekrar login ekranına düşmeden sisteme devam edebilir.

Tüm korumalı API çağrılarında Authorization header içerisine Bearer token eklenerek backend tarafındaki rol ve yetki kontrolü sağlanır.

API Base URL Ayarı

Mobil uygulama backend servislerine Retrofit üzerinden bağlanmaktadır. Base URL adresi RetrofitClient.kt dosyasında tanımlanmıştır.

Android Emulator kullanılırken backend erişimi için 10.0.2.2 adresi kullanılmaktadır. Fiziksel cihaz ile test yapılacaksa bilgisayarın yerel IP adresi base URL olarak güncellenmelidir.

# Uygulama Akışı

Kullanıcı uygulamayı açar.
Giriş yapmamışsa login ekranına yönlendirilir.
Giriş sonrası rol kontrolü yapılır.
Student kullanıcı ana ekranda kütüphaneleri görür ve kitap işlemlerini yapabilir.
Admin kullanıcı ek olarak kitap yönetim işlemlerine erişebilir.
Kullanıcı kitap ödünç alabilir, iade edebilir ve geçmişini görüntüleyebilir.
Şifresini unutan kullanıcı mail üzerinden gelen kod ile yeni şifre belirleyebilir.

# Teknik İyileştirmeler

Fragment geçişlerinde backstack yönetimi düzenlenmiştir.
API hataları kullanıcıya anlamlı mesajlarla gösterilmektedir.
Boş liste durumları için kontrol mekanizmaları eklenmiştir.
View Binding kullanımı ile null referans hatalarının önüne geçilmiştir.
SharedPreferences ile oturum tutarlılığı sağlanmıştır.
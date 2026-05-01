### Kod ve Diyagram Uyumu
- **Model güncellemeleri:** `Resident` sınıfındaki `duesDebt`, `extraDebt` alanları OOP kalıtım prensiplerine uygun olarak ana `User` sınıfına taşındı. Eski usül plaka (`licensePlate`) ve sakin isimleri (`residentNames`) tutma mantığı silindi.
- **Model güncellemeleri:** Otopark sistemi için eksik olan `Vehicle.java` entity'si projeye dahil edildi.
- **Daire-Sakin İlişkisi:** Daire atamaları artık ER diyagramında belirlendiği üzere `Apartments` tablosundaki `resident_id` Foreign Key'i üzerinden yapılıyor.

### Hata Düzeltmeleri
- **Finans:** Ödeme ekranındaki sınırsız ödeme açığı kapatıldı. Artık sistem, toplam borcu aşan ödemeleri otomatik reddediyor ve kullanıcı anlık borcunu ekrandan takip edebiliyor.
- **Arıza Atama:** Bir sakine yanlışlıkla görev (ticket) atanmasına neden olabilecek güvenlik açığı kapatıldı. Artık sadece sistemdeki geçerli yetkililere atama yapılabiliyor.
- **İletişim (Anket):** Kullanıcıların geçersiz anket şıklarına oy verip oylarının boşa gitmesi engellendi.(rollback kullanılarak)
- **Kayıt Yönetimi:** Aynı numara ile kayıt olma ve dolu bir daireye yanlışlıkla başka birini atayıp üzerine yazma sorunları engellendi.


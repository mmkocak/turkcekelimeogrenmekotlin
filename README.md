# Türkçe Kelime Öğrenme

Türkçe–İngilizce çift yönlü kelime öğrenme uygulaması. **SM-2 aralıklı tekrar** algoritması, **4 çalışma modu** ve **~300 kelimelik offline başlangıç seti** ile hazır gelir. Kendi kelimelerini de ekleyebilirsin.

Jetpack Compose · Material 3 · Room · Hilt · tamamen offline.

---

## Özellikler

- **4 çalışma modu**
  - **Flashcard** — 3D kart çevirme animasyonu, Anki tarzı Tekrar / Zor / İyi / Kolay butonları (her biri bir sonraki tekrar aralığını önizler)
  - **Quiz** — 4 seçenekli çoktan seçmeli; aynı kategoriden akıllı distractor'lar
  - **Yazarak** — Türkçe karakter duyarlı karşılaştırma (`ı ≠ i`), ipucu sistemi
  - **Dinleme** — Android TTS ile seslendirme, 0.75x / 1x hız seçenekleri
- **SM-2 Aralıklı Tekrar** — Her kelime için iki yönde ayrı kart durumu (EN→TR ve TR→EN). `easiness`, `repetition`, `intervalDays`, `dueAt` alanları; `NEW → LEARNING → REVIEW → MASTERED` ustalık seviyeleri.
- **İstatistikler** — Güncel & en uzun streak, ustalık donut grafiği, son 7 gün bar grafiği, 6 haftalık heatmap, kategori bazlı doğruluk.
- **Kitaplık** — Arama, kategori filtresi, favori / yeni / öğreniliyor / ustalaşıldı / kullanıcı-eklenen filtreleri, kelime detay sheet'i.
- **Kendi kelimelerini ekle** — Kategori, kelime türü, örnek cümleler, IPA. Eklenen kelimeler otomatik olarak her iki yönde SRS kuyruğuna dahil olur.
- **Tema** — Açık / Koyu / Sistem + Android 12+ dinamik renk. Sıcak amber ikincil, derin teal birincil palet.
- **Offline-first** — Hiç internet gerektirmez. Tüm veri Room + DataStore ile cihazda saklanır.
- **Erişilebilirlik** — `contentDescription`'lar, AutoMirrored ikonlar (RTL desteği), Material 3 kontrast oranları, `sp` tabanlı tipografi.

---

## Ekran haritası

| Ekran | Açıklama |
| --- | --- |
| **Onboarding** | 3 sayfalık pager: hoş geldin → yön seçimi → günlük hedef. İlk açılışta bir kez. |
| **Home** | Streak rozeti, bugünkü çalışma ilerleme halkası, 4 mod başlatıcı, kategori grid'i, "Kitaplığım" ve ayarlar girişleri. |
| **Library** | Arama, filtreler, kelime listesi, detay bottom sheet. |
| **Add/Edit Word** | Form: kelime, anlam, kategori dropdown, tür chip'leri, örnek cümleler, IPA. |
| **Study (Flashcard / Quiz / Typing / Listening)** | Paylaşımlı `StudySessionViewModel` üzerinden çalışan 4 mod. |
| **Stats** | Streak özeti, ustalık donut, haftalık bar, 6 haftalık heatmap. |
| **Settings** | Tema, dinamik renk, birincil yön, günlük hedef slider, TTS, haptik, ilerleme sıfırlama. |

---

## Mimari

Tek modül, katmanlı paket yapısı:

```
com.muhammetkocak.turkcekelimeapp
├── core/          # TTS, haptic, datetime, extension'lar
├── data/
│   ├── local/     # Room: entity, dao, converter, seed loader
│   ├── prefs/     # DataStore (UserPreferences)
│   ├── repository/
│   └── mapper/    # Entity ↔ Domain
├── domain/
│   ├── model/     # Word, Category, SrsRating, LearningDirection, CardMastery…
│   ├── srs/       # Sm2Scheduler (saf fonksiyon)
│   └── usecase/   # GetDueCards, ReviewCard, BuildQuizOptions, GetStats, UpsertWord…
├── di/            # Hilt modülleri
├── navigation/    # Type-safe Navigation Compose (sealed Screen + @Serializable)
└── ui/            # Compose ekranları + ViewModel'ler + tema
```

**Prensipler**

- UI state tek bir data class üzerinden `StateFlow`; yan etkiler `Channel<UiEffect>`.
- ViewModel'ler `@HiltViewModel`, Compose'da `hiltViewModel()` ile enjekte edilir.
- SRS algoritması saf fonksiyon — `Clock` soyutlamasıyla deterministik test edilebilir.
- Room'da `CardStateEntity` her kelime × yön için ayrı satır; JOIN'lar `DueCardView` projeksiyonu ile.

---

## SM-2 algoritması

`Sm2Scheduler.schedule(prev, rating, now)`:

- `Again (q=1)` → `repetition = 0`, `intervalDays = 1`, EF düşer.
- `q ≥ 3`:
  - `repetition == 0` → `1g`
  - `repetition == 1` → `6g`
  - else → `round(intervalDays × easiness)`
  - `Hard` modifikatörü × 1.2, `Easy` modifikatörü × 1.3
- `easiness = max(1.3, prev.EF + 0.1 − (5−q)(0.08 + (5−q)·0.02))`
- Ustalık: `0 → NEW`, `1–2 → LEARNING`, `3–6 → REVIEW`, `≥7 && EF ≥ 2.5 → MASTERED`.

Her değişiklik `ReviewEntity` olarak loglanır — istatistik ekranı bu logdan türetilir.

---

## Teknoloji yığını

| Katman | Araç |
| --- | --- |
| Dil / Build | Kotlin 2.0.21, Gradle KTS, version catalog |
| UI | Jetpack Compose, Material 3, Compose BOM |
| DI | Hilt 2.52 + KSP |
| Veri | Room 2.6.1, DataStore Preferences 1.1.1 |
| Navigasyon | Navigation Compose 2.8.4 (type-safe, `@Serializable`) |
| Async | Kotlin Coroutines 1.9 + Flow |
| Serileştirme | Kotlinx Serialization 1.7.3 |
| TTS | Android `TextToSpeech` (coroutine-aware sarmalayıcı) |
| Test | JUnit 4, Kotlinx Coroutines Test |

**Min SDK**: 24 · **Target / Compile SDK**: 36

---

## Build & çalıştırma

```bash
# Debug APK
./gradlew assembleDebug

# Bağlı cihaza yükle
./gradlew installDebug

# Unit testler
./gradlew testDebugUnitTest

# Release APK (imzalama yapılandırması gerekir)
./gradlew assembleRelease
```

İlk açılışta seed loader `assets/words.json`'dan ~300 kelime ve 8 kategoriyi veritabanına yazar; her kelime için iki yönde `CardStateEntity` üretir. Bu akış `firstRunCompleted` bayrağı ile tek seferliktir.

---

## Test kapsamı

- `Sm2SchedulerTest` — SM-2'nin 11 senaryosu (ilk Good → 1g, ikinci Good → 6g, Again sıfırlama, EF 1.3 tabanı, MASTERED eşiği…)
- `BuildQuizOptionsUseCaseTest` — aynı kategoriden distractor seçimi, global havuza fallback, correctIndex

```bash
./gradlew testDebugUnitTest  # hepsi yeşil
```

---

## Veri modeli (Room)

- `categories` — 8 kategori (sabit ID'ler)
- `words` — kelime + çeviri, kategori FK, `isUserCreated`, `isFavorite`
- `card_state` — `(wordId, direction)` kompozit PK, SM-2 alanları
- `reviews` — her tekrar için log (mastery ve streak hesaplamaları için)
- `sessions` — çalışma oturumları (mode, correct/wrong sayımları)

Şemalar `app/schemas/` dizinine export edilir.

---

## Yol haritası (v1.1+)

- JSON içe / dışa aktarma UI'ı
- Bulut yedekleme (opsiyonel)
- Widget (bugünkü due sayısı)
- Çoklu deck desteği
- UI lokalizasyonu (v1 yalnızca TR)

---

## Lisans

Henüz belirtilmedi.

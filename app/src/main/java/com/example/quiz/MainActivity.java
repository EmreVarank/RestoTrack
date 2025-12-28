package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    boolean[] masaDurumlari = new boolean[8];
    int aktifMasaIndex = -1;

    Button[] masalar = new Button[8];

    CheckBox chkYemek1, chkYemek2, chkYemek3,
            chkYemek4, chkYemek5, chkYemek6;

    Button btnOnayla;
    TextView txtSonuc;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        masalar[0] = findViewById(R.id.masa1);
        masalar[1] = findViewById(R.id.masa2);
        masalar[2] = findViewById(R.id.masa3);
        masalar[3] = findViewById(R.id.masa4);
        masalar[4] = findViewById(R.id.masa5);
        masalar[5] = findViewById(R.id.masa6);
        masalar[6] = findViewById(R.id.masa7);
        masalar[7] = findViewById(R.id.masa8);

        chkYemek1 = findViewById(R.id.yemek1);
        chkYemek2 = findViewById(R.id.yemek2);
        chkYemek3 = findViewById(R.id.yemek3);
        chkYemek4 = findViewById(R.id.yemek4);
        chkYemek5 = findViewById(R.id.yemek5);
        chkYemek6 = findViewById(R.id.yemek6);

        btnOnayla = findViewById(R.id.ucret);
        txtSonuc = findViewById(R.id.tvUcret);

        btnOnayla.setText("SİPARİŞİ ONAYLA");

        // Sayfa ilk açıldığında verileri yükle
        verileriYukleVeGuncelle();

        for (int i = 0; i < 8; i++) {
            final int index = i;
            masalar[i].setOnClickListener(v -> masaSec(index));
        }

        btnOnayla.setOnClickListener(v -> siparisOnayla());
    }

    // --- YENİ EKLENEN KISIM ---
    // Ödeme sayfasından geri dönüldüğünde renklerin güncellenmesi için
    @Override
    protected void onResume() {
        super.onResume();
        verileriYukleVeGuncelle();
    }

    private void verileriYukleVeGuncelle() {
        for (int i = 0; i < 8; i++) {
            masaDurumlari[i] = db.masaDurumuGetir(i);
            masaGuncelleUI(i);
        }
    }
    // --------------------------

    private void masaSec(int index) {
        if (masaDurumlari[index]) return;

        if (aktifMasaIndex != -1) {
            masaBosGoster(aktifMasaIndex);
        }

        aktifMasaIndex = index;
        masalar[index].setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.masaSecili)
        );
        masalar[index].setText("SEÇİLDİ");
    }

    private void siparisOnayla() {
        // 1. Durum: Masa seçilmediyse engelle
        if (aktifMasaIndex == -1) {
            txtSonuc.setText("Önce masa seçiniz!");
            return;
        }

        // Seçilen yemekleri ve toplam tutarı hesapla
        int toplam = 0;
        String yemekler = "";
        String masaAdi = "Masa " + (aktifMasaIndex + 1);

        // Seçimleri kontrol et ve DB'ye ekle
        if (chkYemek1.isChecked()) { toplam += 50;  yemekler += "Ezogelin, ";  db.siparisEkle(masaAdi, "Ezogelin", 50); }
        if (chkYemek2.isChecked()) { toplam += 75;  yemekler += "Mercimek, ";  db.siparisEkle(masaAdi, "Mercimek", 75); }
        if (chkYemek3.isChecked()) { toplam += 100; yemekler += "Tarhana, ";   db.siparisEkle(masaAdi, "Tarhana", 100); }
        if (chkYemek4.isChecked()) { toplam += 120; yemekler += "Paça, ";      db.siparisEkle(masaAdi, "Paça", 120); }
        if (chkYemek5.isChecked()) { toplam += 40;  yemekler += "İşkembe, ";   db.siparisEkle(masaAdi, "İşkembe", 40); }
        if (chkYemek6.isChecked()) { toplam += 20;  yemekler += "Yayla, ";     db.siparisEkle(masaAdi, "Yayla", 20); }

        // --- KRİTİK EKLEME: 2. Durum: Hiç yemek seçilmediyse engelle ---
        if (toplam == 0) {
            txtSonuc.setText("Lütfen en az bir yemek seçiniz!");
            return; // Metodun geri kalanını çalıştırma, burada dur.
        }
        // --------------------------------------------------------------

        // Virgülü temizle
        yemekler = yemekler.substring(0, yemekler.length() - 2);

        // Masayı dolu yap ve veritabanını güncelle
        masaDurumlari[aktifMasaIndex] = true;
        db.masaGuncelle(aktifMasaIndex, true);
        masaGuncelleUI(aktifMasaIndex);

        // Bilgileri ekrana yazdır
        txtSonuc.setText(
                "Masa: " + (aktifMasaIndex + 1) + "\n" +
                        "Yemekler: " + yemekler + "\n" +
                        "Ücret: " + toplam + " TL"
        );

        // Formu temizle
        chkYemek1.setChecked(false);
        chkYemek2.setChecked(false);
        chkYemek3.setChecked(false);
        chkYemek4.setChecked(false);
        chkYemek5.setChecked(false);
        chkYemek6.setChecked(false);

        aktifMasaIndex = -1;
    }



    private void masaGuncelleUI(int index) {
        if (masaDurumlari[index]) masaDoluGoster(index);
        else masaBosGoster(index);
    }

    private void masaBosGoster(int index) {
        masalar[index].setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.masaBos)
        );
        masalar[index].setText("MASA " + (index + 1));
    }

    private void masaDoluGoster(int index) {
        masalar[index].setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.masaDolu)
        );
        // DEĞİŞİKLİK: "DOLU" yerine masa numarasını yazıyoruz
        masalar[index].setText("MASA " + (index + 1));
    }

    public void buSayfa2(View view) {
        startActivity(new Intent(this, OdemeActivity.class));
    }
}

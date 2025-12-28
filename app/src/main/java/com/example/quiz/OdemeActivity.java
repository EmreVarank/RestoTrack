package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class OdemeActivity extends AppCompatActivity {

    TextView tvMenu, tvKasa, tvBanka;
    Button btnNakit, btnKart;

    DatabaseHelper dbHelper;

    // Masalar (ödeme ekranında dolu/boş göstermek için)
    boolean[] masaDurumlari = new boolean[8];
    Button[] masalar = new Button[8];

    // Seçili masa bilgisi
    private int seciliMasaIndex = -1; // 0..7
    private int seciliMasaToplam = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odeme);

        tvMenu = findViewById(R.id.tvMenu);
        tvKasa = findViewById(R.id.tvKasa);
        tvBanka = findViewById(R.id.tvBanka);

        btnNakit = findViewById(R.id.buNakit);
        btnKart  = findViewById(R.id.buKart);

        dbHelper = new DatabaseHelper(this);

        // Kasa & Banka toplamlarını yükle
        int kasaToplam  = getSharedPreferences("finans", MODE_PRIVATE).getInt("kasaToplam", 0);
        int bankaToplam = getSharedPreferences("finans", MODE_PRIVATE).getInt("bankaToplam", 0);

        tvKasa.setText("KASA: " + kasaToplam + " TL");
        tvBanka.setText("BANKA: " + bankaToplam + " TL");

        // Ödeme ekranındaki masa buton id'leri
        int[] masaButonlari = {
                R.id.masa11, R.id.masa12, R.id.masa13, R.id.masa14,
                R.id.masa15, R.id.masa16, R.id.masa17, R.id.masa18
        };

        // Masaları kur + dolu/boş göster + tıklanınca siparişi getir
        for (int i = 0; i < masaButonlari.length; i++) {
            final int masaIndex = i; // 0..7
            Button btn = findViewById(masaButonlari[i]);
            masalar[i] = btn;

            // Her durumda masa numarasını yaz (Örn: MASA 1)
            btn.setText("MASA " + (i + 1));

            // DB'den dolu/boş çek ve SADECE rengi güncelle
            masaDurumlari[i] = dbHelper.masaDurumuGetir(i);
            if (masaDurumlari[i]) {
                // Masa doluysa KIRMIZI yap (Yazı MASA X kalır)
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.masaDolu));
            } else {
                // Masa boşsa normal renginde (YEŞİL/MAVİ) kalsın
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.masaBos));
            }

            // Masa seç ve siparişi göster
            btn.setOnClickListener(v -> {
                seciliMasaIndex = masaIndex;
                String masaIsmi = "Masa " + (masaIndex + 1);

                String siparisDetayi = dbHelper.getMasaSiparisi(masaIsmi);
                seciliMasaToplam = dbHelper.getMasaToplam(masaIsmi);

                tvMenu.setText(
                        siparisDetayi +
                                "\n\nSeçilen: " + masaIsmi +
                                "\nÖdenecek: " + seciliMasaToplam + " TL"
                );
            });
        }

        // Nakit ödeme (KASA)
        btnNakit.setOnClickListener(v -> {
            if (seciliMasaIndex == -1) {
                tvMenu.setText("Önce ödeme yapılacak masayı seçiniz!");
                return;
            }

            if (seciliMasaToplam <= 0) {
                tvMenu.setText("Bu masada ödenecek tutar yok!");
                return;
            }

            int kasa = getSharedPreferences("finans", MODE_PRIVATE).getInt("kasaToplam", 0);
            kasa += seciliMasaToplam;

            getSharedPreferences("finans", MODE_PRIVATE)
                    .edit()
                    .putInt("kasaToplam", kasa)
                    .apply();

            tvKasa.setText("KASA: " + kasa + " TL");

            // Masayı boşalt + siparişleri sil
            String masaIsmi = "Masa " + (seciliMasaIndex + 1);
            dbHelper.masaGuncelle(seciliMasaIndex, false);
            dbHelper.siparisSil(masaIsmi);

            // Butonu anında BOŞ rengine çevir (Yazı zaten MASA X)
            masaDurumlari[seciliMasaIndex] = false;
            masalar[seciliMasaIndex].setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.masaBos)
            );

            tvMenu.setText(masaIsmi + " nakit ödendi: " + seciliMasaToplam + " TL\nMasa boşaltıldı.");

            seciliMasaIndex = -1;
            seciliMasaToplam = 0;
        });

        // Kartla ödeme (BANKA)
        btnKart.setOnClickListener(v -> {
            if (seciliMasaIndex == -1) {
                tvMenu.setText("Önce ödeme yapılacak masayı seçiniz!");
                return;
            }

            if (seciliMasaToplam <= 0) {
                tvMenu.setText("Bu masada ödenecek tutar yok!");
                return;
            }

            int banka = getSharedPreferences("finans", MODE_PRIVATE).getInt("bankaToplam", 0);
            banka += seciliMasaToplam;

            getSharedPreferences("finans", MODE_PRIVATE)
                    .edit()
                    .putInt("bankaToplam", banka)
                    .apply();

            tvBanka.setText("BANKA: " + banka + " TL");

            // Masayı boşalt + siparişleri sil
            String masaIsmi = "Masa " + (seciliMasaIndex + 1);
            dbHelper.masaGuncelle(seciliMasaIndex, false);
            dbHelper.siparisSil(masaIsmi);

            // Butonu anında BOŞ rengine çevir
            masaDurumlari[seciliMasaIndex] = false;
            masalar[seciliMasaIndex].setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.masaBos)
            );

            tvMenu.setText(masaIsmi + " kartla ödendi: " + seciliMasaToplam + " TL\nMasa boşaltıldı.");

            seciliMasaIndex = -1;
            seciliMasaToplam = 0;
        });
    }

    public void buSayfa1(View view) {
        Intent intent = new Intent(OdemeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

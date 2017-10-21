package com.example.service;

import java.util.List;

import com.example.model.KecamatanModel;
import com.example.model.KeluargaModel;
import com.example.model.KelurahanModel;
import com.example.model.KotaModel;
import com.example.model.PendudukModel;

public interface SidukService {
	// penduduk service
	// view penduduk
	PendudukModel selectPenduduk(String nik);

	// select view penduduk get data di keluarga
	KeluargaModel selectKeluargaById(int id_keluarga);

	// select view penduduk get data di keluarga, kelurahan
	KelurahanModel selectKelurahanById(int id_kelurahan);

	// select view penduduk get data di keluarga, kelurahan, kecamatan
	KecamatanModel selectKecamatanById(int id_kecamatan);

	// select view penduduk get data di keluarga, kelurahan, kecamatan
	KotaModel selectKotaById(int id_kota);
	
	// select nik untuk di cek
	String getCekNIK(String ceknik);
	
	// add penduduk
	void addPenduduk(PendudukModel penduduk);

	// update penduduk
	//void updatePenduduk(PendudukModel penduduk, int id);
	void updatePenduduk(PendudukModel penduduk);
	
	// keluarga service
	// view keluarga
	KeluargaModel selectKeluarga(String nomor_kk);

	// select view anggota keluarga penduduk di keluarga
	List<PendudukModel> selectPendudukById(int id_keluarga);
	
	// add keluarga
	void addKeluarga(KeluargaModel keluarga);
	
	// update keluarga
	void updateKeluarga(KeluargaModel keluarga);

	KelurahanModel getNamaKelurahan(String nama_kelurahan);

	KecamatanModel getNamaKecamatan(String nama_kecamatan);

	KotaModel getNamaKota(String nama_kota);

	String getCekNKK(String nkkbaru);

	int getKodeKelurahan(String kode_kelurahan);
}

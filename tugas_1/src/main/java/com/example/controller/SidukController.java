package com.example.controller;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.PendudukModel;
import com.example.model.KeluargaModel;
import com.example.model.KelurahanModel;
import com.example.model.KecamatanModel;
import com.example.model.KotaModel;
import com.example.service.SidukService;

import lombok.extern.slf4j.Slf4j;

@Controller
public class SidukController {
	@Autowired
	SidukService sidukDAO;

	// halaman home
	@RequestMapping("/")
	public String index() {
		return "index";
	}

	// halaman view penduduk
	@RequestMapping("/penduduk/view")
	public String viewPenduduk(Model model, @RequestParam(value = "nik") String nik) {
		PendudukModel penduduk = sidukDAO.selectPenduduk(nik);
		KeluargaModel keluarga = sidukDAO.selectKeluargaById(penduduk.getId_keluarga());
		KelurahanModel kelurahan = sidukDAO.selectKelurahanById(keluarga.getId_kelurahan());
		KecamatanModel kecamatan = sidukDAO.selectKecamatanById(kelurahan.getId_kecamatan());
		KotaModel kota = sidukDAO.selectKotaById(kecamatan.getId_kota());

		if (penduduk != null) {
			model.addAttribute("penduduk", penduduk);
			model.addAttribute("keluarga", keluarga);
			model.addAttribute("kelurahan", kelurahan);
			model.addAttribute("kecamatan", kecamatan);
			model.addAttribute("kota", kota);
			return "view-penduduk";
		} else {
			model.addAttribute("nik", nik);
			return "not-found-penduduk";
		}
	}

	// halaman view keluarga
	@RequestMapping("/keluarga/view")
	public String viewKeluarga(Model model, @RequestParam(value = "nomor_kk") String nomor_kk) {
		KeluargaModel keluarga = sidukDAO.selectKeluarga(nomor_kk);
		KelurahanModel kelurahan = sidukDAO.selectKelurahanById(keluarga.getId_kelurahan());
		KecamatanModel kecamatan = sidukDAO.selectKecamatanById(kelurahan.getId_kecamatan());
		KotaModel kota = sidukDAO.selectKotaById(kecamatan.getId_kota());
		List<PendudukModel> penduduk = sidukDAO.selectPendudukById(keluarga.getId());

		if (keluarga != null) {
			model.addAttribute("keluarga", keluarga);
			model.addAttribute("kelurahan", kelurahan);
			model.addAttribute("kecamatan", kecamatan);
			model.addAttribute("kota", kota);
			model.addAttribute("penduduk", penduduk);
			return "view-keluarga";
		} else {
			model.addAttribute("nomor_kk", nomor_kk);
			return "not-found-keluarga";
		}
	}

	// halaman add penduduk
	@RequestMapping("/penduduk/add")
	public String addPenduduk(Model model) {
		model.addAttribute("penduduk", new PendudukModel());
		return "form-penduduk";
	}

	// halaman success add penduduk tanpa generate nik
	// @RequestMapping(value = "/penduduk/add/submit", method = RequestMethod.GET)
	// public String addPendudukSubmit(Model model, @ModelAttribute PendudukModel
	// penduduk) {
	// sidukDAO.addPenduduk(penduduk);
	// return "success-penduduk";
	// }

	// halaman success
	// add penduduk generate nik
	@RequestMapping(value="/penduduk/add/submit",method=RequestMethod.GET)
	public String addPenduduk(Model model, @ModelAttribute PendudukModel penduduk) {
		KeluargaModel keluarga = sidukDAO.selectKeluargaById(penduduk.getId_keluarga());
		KelurahanModel kelurahan = sidukDAO.selectKelurahanById(keluarga.getId_kelurahan());
		KecamatanModel kecamatan = sidukDAO.selectKecamatanById(kelurahan.getId_kecamatan());
		
		
		if (keluarga != null) {
			String kode = kecamatan.getKode_kecamatan();
			
			String tanggal_lahir = penduduk.getTanggal_lahir();
			String tanggal = tanggal_lahir.substring(8, 10);
			int tgl_lahir = Integer.parseInt(tanggal);
			if (penduduk.getJenis_kelamin() == 1) {
				tgl_lahir = tgl_lahir + 40;
				tanggal = Integer.toString(tgl_lahir);
			}		
			String tgl = tanggal + tanggal_lahir.substring(5, 7) + tanggal_lahir.substring(2,4);
			String nikbaru = kode.substring(0, kode.length()-1) + tgl;
			
			String ceknik = sidukDAO.getCekNIK(nikbaru);
			Long nik = Long.parseLong(nikbaru + "0001");
			if (ceknik != null) {
				nik = Long.parseLong(ceknik) + 1;
			}
			
			String nik_penduduk = Long.toString(nik);
			penduduk.setNik(nik_penduduk);
			
			sidukDAO.addPenduduk(penduduk);
			model.addAttribute("nik", nik);
			return "success-penduduk";
		} else {
			model.addAttribute("nomor_kk", keluarga.getNomor_kk());
			return "not-found-keluarga";
		}

	}

	// halaman add keluarga
	@RequestMapping("/keluarga/add")
	public String add(Model model) {
		model.addAttribute("keluarga", new KeluargaModel());
		return "form-keluarga";
	}

	// halaman success add keluarga tanpa generate nkk
	// dan tanpa nambah data ke kelurahan
	// @RequestMapping(value = "/keluarga/add/submit", method = RequestMethod.GET)
	// public String addKeluargaSubmit(Model model, @ModelAttribute KeluargaModel
	// keluarga) {
	// sidukDAO.addKeluarga(keluarga);
	// return "success-keluarga";
	// }

	// halaman success add keluarga generate nkk
	@RequestMapping(value = "/keluarga/add/submit", method = RequestMethod.GET)
	public String addKeluarga(Model model, @ModelAttribute KeluargaModel keluarga) {
		String nama_kelurahan = keluarga.getNama_kelurahan();
		String nama_kecamatan = keluarga.getNama_kecamatan();
		String nama_kota = keluarga.getNama_kota();
		KelurahanModel kodekelurahan = sidukDAO.getNamaKelurahan(nama_kelurahan);
		KecamatanModel kodekecamatan = sidukDAO.getNamaKecamatan(nama_kecamatan);
		KotaModel kodekota = sidukDAO.getNamaKota(nama_kota);
		String kode_kota = kodekota.getKode_kota();
		String kode_kecamatan = kodekecamatan.getKode_kecamatan();
		String kode_kelurahan = kodekelurahan.getKode_kelurahan();
		
		if (kode_kelurahan != null && kode_kecamatan != null && kode_kota != null) {
			LocalDate tanggal = LocalDate.now();
			String tgl = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(tanggal);

			String tglterbit = tgl.substring(8, 10) + tgl.substring(5, 7) + tgl.substring(2, 4);
			String nkkbaru = kode_kecamatan.substring(0, kode_kecamatan.length() - 1) + tglterbit;

			String ceknkk = sidukDAO.getCekNKK(nkkbaru);
			Long nkk = Long.parseLong(nkkbaru + "0001");
			if (ceknkk != null) {
				nkk = Long.parseLong(ceknkk) + 1;
			}

			String nkk_keluarga = Long.toString(nkk);
			keluarga.setNomor_kk(nkk_keluarga);
			keluarga.setId_kelurahan(sidukDAO.getKodeKelurahan(kode_kelurahan));

			model.addAttribute("nomor_kk", nkk_keluarga);
			sidukDAO.addKeluarga(keluarga);
			return "success-keluarga";
		} else {
			model.addAttribute("alamat", keluarga.getAlamat());
			return "not-found";
		}
	}

	// halaman update penduduk
	@RequestMapping("/penduduk/update/{nik}")
	public String updatePenduduk(Model model, @PathVariable(value = "nik") String nik) {
		PendudukModel penduduk = sidukDAO.selectPenduduk(nik);
		if (penduduk != null) {
			model.addAttribute("penduduk", penduduk);
			return "form-update-penduduk";
		} else {
			model.addAttribute("nik", nik);
			return "not-found";
		}
	}

	// halaman success update penduduk, tanpa generate nik
	@RequestMapping(value = "/penduduk/update/submit", method = RequestMethod.POST)
	public String updateForm(Model model, @ModelAttribute PendudukModel penduduk) {
		String nik = penduduk.getNik();
		PendudukModel pendudukUpdate = sidukDAO.selectPenduduk(nik);

		if (penduduk.getJenis_kelamin() == 0 || penduduk.getJenis_kelamin() == 1) {
			penduduk.setJenis_kelamin(pendudukUpdate.getJenis_kelamin());
		}
		if (penduduk.getGolongan_darah() == null) {
			penduduk.setGolongan_darah(pendudukUpdate.getGolongan_darah());
		}
		if (penduduk.getStatus_perkawinan() == null) {
			penduduk.setStatus_perkawinan(pendudukUpdate.getStatus_perkawinan());
		}
		if (penduduk.getStatus_dalam_keluarga() == null) {
			penduduk.setStatus_dalam_keluarga(pendudukUpdate.getStatus_dalam_keluarga());
		}
		sidukDAO.updatePenduduk(penduduk);
		model.addAttribute("nik", pendudukUpdate.getNik());

		return "success-update-penduduk";
	}

	// halaman update keluarga
	@RequestMapping("/keluarga/update/{nomor_kk}")
	public String updateKeluarga(Model model, @PathVariable(value = "nomor_kk") String nomor_kk) {
		KeluargaModel keluarga = sidukDAO.selectKeluarga(nomor_kk);
		if (keluarga != null) {
			model.addAttribute("keluarga", keluarga);
			return "form-update-keluarga";
		} else {
			model.addAttribute("nomor_kk", nomor_kk);
			return "not-found";
		}
	}

	// halaman success update keluarga, tanpa generate nkk
	@RequestMapping(value = "/keluarga/update/submit", method = RequestMethod.POST)
	public String updateForm(Model model, @ModelAttribute KeluargaModel keluarga) {
		String nomor_kk = keluarga.getNomor_kk();
		KeluargaModel keluargaUpdate = sidukDAO.selectKeluarga(nomor_kk);

		sidukDAO.updateKeluarga(keluarga);
		model.addAttribute("nomor_kk", keluargaUpdate.getNomor_kk());

		return "success-update-keluarga";
	}

	// halaman update status kematian

	// halaman success update status kematian

	// find penduduk by kota
}

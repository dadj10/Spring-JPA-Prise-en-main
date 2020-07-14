package has.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import has.com.entities.Etudiant;
import has.com.repository.EtudiantRepository;

@Controller
@RequestMapping(value = "/etudiant")
public class EtudiantController {

	@Autowired /* injection des dependances */
	private EtudiantRepository etudiantRepo;

	@Value("${dir.images}") /* injection (recuperation du chemin referencé dans la fichier properties) */
	private String imgDir;

	@Value("${maxSize}")
	private int maxSize;

	@GetMapping()
	public String index(Model model) {

		model.addAttribute("etudiants", etudiantRepo.listeEtudiant());
		return "etudiant/index_view";
	}
	
	/* method pour rediriger vers le formulaire de creation */
	@RequestMapping(value = "/ajouter", method = RequestMethod.GET)
	public String ajouter(Model model) {
		model.addAttribute("etudiant", new Etudiant());

		return "etudiant/ajouter";
	}
	
	/* method pour enregistrer un etudiant */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Valid Etudiant etds, BindingResult bindingResult,
			@RequestParam(name = "picture") MultipartFile file)
			throws Exception /* les donnees du champs photo sont stocké dans l'objet de type file */
	{
		if (bindingResult.hasErrors()) {
			return "etudiant/ajouter";
		} else {
			
			etudiantRepo.save(etds);

			/*
			 * si le champs photo n'est pas vide on transfert le fichier verrs un repertoire
			 */
			if (!(file.isEmpty()) && file.getSize() < maxSize) {
				/* getOriginalFilename permet de retourner le nom original de la photo */
				etds.setPhoto(file.getOriginalFilename());
				file.transferTo(new File(imgDir + etds.getId()));
			}

			return "redirect:/etudiant";
		}
	}

	@RequestMapping(value = "/getphoto", produces = MediaType.IMAGE_JPEG_VALUE) // retour de la photo en jpeg
	@ResponseBody /* pour envoyer des donnes dans le corps de la reponse */
	public byte[] getphoto(Long id) throws Exception {
		File f = new File(imgDir + id);

		return IOUtils.toByteArray(new FileInputStream(f));
	}

	/* metod pour supprimer */
	/*@RequestMapping(value = "/supprimer")
	public String supprimer(Long id) {
		
		Etudiant id_etds = null;
		
		//id_etds = etudiantRepo.findEtudiantById(etds.getId());
		etudiantRepo.deleteById(id);

		return "redirect:/etudiant";
	}*/
	
		
	// Fonction de suppression d'un cdr
	@GetMapping(value = "/delete")
	public String delete(Long id) {
		Etudiant etds = null;
		try {
			etds = findEtudiantById(id);
			
			if (etds != null) {
				etds.setEtat(-1);
				
				etudiantRepo.save(etds);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/etudiant";
	}
	
	/* method pour rediriger vers le formulaire de modification */
	@RequestMapping(value = "/modifier")
	public String update(Long id, Model model) {
		/* je recherche les info par ID */
		Etudiant etds = etudiantRepo.getOne(id);

		model.addAttribute("etudiant", etds);

		return "etudiant/modifier";
	}
	
	/* method pour modifier un etudiant */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(@Valid Etudiant etds, BindingResult bindingResult,
			@RequestParam(name = "picture") MultipartFile file)
			throws Exception /* les donnees du champs photo sont stocké dans l'objet de type file */
	{
		if (bindingResult.hasErrors())
		{
			return "etudiant/modifier";
		}
		else
		{
			Etudiant id_etds = null;
			id_etds = etudiantRepo.findEtudiantById(etds.getId());
			
			if(id_etds!=null)
			{
				etudiantRepo.save(etds);

				/*
				 * si le champs photo n'est pas vide on transfert le fichier verrs un repertoire
				 */
				if (!(file.isEmpty()) && file.getSize() < maxSize) {
					/* getOriginalFilename permet de retourner le nom original de la photo */
					etds.setPhoto(file.getOriginalFilename());
					file.transferTo(new File(imgDir + etds.getId()));
				}
			}
			
			return "redirect:/etudiant";
		}
	}
	
	public Etudiant findEtudiantById(Long id)
	{
		Etudiant etudiant = null;
		
		etudiant = etudiantRepo.findEtudiantById(id);
		
		return etudiant;
	}

}

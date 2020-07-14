package has.com.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import has.com.entities.Etudiant;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

	public List<Etudiant> findByNom(String nom);
	
	@Query("SELECT e FROM Etudiant e WHERE e.etat <> -1")
	List<Etudiant> listeEtudiant();

	/* requete personnalisée :: chercher les etudiant ayant %nom dans leur nom */
	@Query("Select e from Etudiant e where e.nom like :x")
	public List<Etudiant> rechercheParNom(@Param("x") String nom);

	/* requete personnalisée :: recherche la liste des etudiant nee entre eux
	 * intervalle de date
	 */
	@Query("Select e from Etudiant e where e.dateNaissance > :x and e.dateNaissance < :y")
	public List<Etudiant> liste_date_naiss(@Param("x") Date d1, @Param("y") Date d2);
	
	@Query("SELECT e FROM Etudiant e WHERE e.id = ?1")
	Etudiant findEtudiantById(long id);

}

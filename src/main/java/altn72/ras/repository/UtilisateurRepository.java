package altn72.ras.repository;

import altn72.ras.modele.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{

    Optional<Utilisateur> findByLogin(String login);

}

package altn72.ras.controleur;

import altn72.ras.modele.NiveauAttention;
import altn72.ras.service.StationService;
import altn72.ras.service.TableauDeBordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TableauDeBordControleur {

    private final TableauDeBordService tableauDeBordService;
    private final StationService stationService;

    public TableauDeBordControleur(TableauDeBordService tableauDeBordService, StationService stationService) {
        this.tableauDeBordService = tableauDeBordService;
        this.stationService = stationService;
    }

    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(@RequestParam(required = false) Long station,
                                @RequestParam(required = false) NiveauAttention niveau,
                                Model model) {
        model.addAttribute("lignes", tableauDeBordService.incidentsActifs(station, niveau));
        model.addAttribute("indicateurs", tableauDeBordService.indicateurs());
        model.addAttribute("stations", stationService.lister());
        model.addAttribute("niveaux", NiveauAttention.values());
        model.addAttribute("stationChoisie", station);
        model.addAttribute("niveauChoisi", niveau);
        return "tableau-de-bord";
    }
}

package altn72.ras.controleur;

import altn72.ras.service.StationService;
import altn72.ras.service.EquipementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StationControleur {

    private final StationService stationService;
    private final EquipementService equipementService;

    public StationControleur(StationService stationService, EquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }
    @GetMapping("/stations")
    public String lister(Model model) {
        model.addAttribute("stations", stationService.lister());
        return "stations";
    }

    @GetMapping("/stations/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("station", stationService.trouver(id));
        model.addAttribute("equipements", equipementService.listerParStation(id));
        return "station";
    }
}

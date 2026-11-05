package altn72.ras.service;

import altn72.ras.modele.Incident;
import altn72.ras.modele.NiveauAttention;

import java.time.Duration;

public record LigneTableauDeBord(Incident incident, NiveauAttention niveau, Duration dureeOuverture,
                                 boolean equipementFragile) {
}

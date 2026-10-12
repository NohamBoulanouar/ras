package altn72.ras.modele;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Incident incident;

    @Column(nullable = false)
    private int note;

    private String commentaire;

    @Column(nullable = false)
    private LocalDateTime dateEvaluation;

    protected Evaluation() {
    }

    public Evaluation(Incident incident, int note, String commentaire, LocalDateTime dateEvaluation) {
        this.incident = incident;
        this.note = note;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
    }

    public Long getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public int getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }
}

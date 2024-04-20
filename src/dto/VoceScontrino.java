package dto;

public class VoceScontrino {
    
    private Integer idOggettoVendita;
    private Integer numeroProgressivo;
    private Integer posizioneElenco;
    private Integer quantità;
    private Double prezzoParziale;
    
    public VoceScontrino(Integer idOggettoVendita, Integer numeroProgressivo, Integer posizioneElenco, Integer quantità,
            Double prezzoParziale) {
        this.idOggettoVendita = idOggettoVendita;
        this.numeroProgressivo = numeroProgressivo;
        this.posizioneElenco = posizioneElenco;
        this.quantità = quantità;
        this.prezzoParziale = prezzoParziale;
    }

    public Integer getIdOggettoVendita() {
        return idOggettoVendita;
    }

    public Integer getNumeroProgressivo() {
        return numeroProgressivo;
    }

    public Integer getPosizioneElenco() {
        return posizioneElenco;
    }

    public Integer getQuantità() {
        return quantità;
    }

    public Double getPrezzoParziale() {
        return prezzoParziale;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idOggettoVendita == null) ? 0 : idOggettoVendita.hashCode());
        result = prime * result + ((numeroProgressivo == null) ? 0 : numeroProgressivo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VoceScontrino other = (VoceScontrino) obj;
        if (idOggettoVendita == null) {
            if (other.idOggettoVendita != null)
                return false;
        } else if (!idOggettoVendita.equals(other.idOggettoVendita))
            return false;
        if (numeroProgressivo == null) {
            if (other.numeroProgressivo != null)
                return false;
        } else if (!numeroProgressivo.equals(other.numeroProgressivo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "VoceScontrino [idOggettoVendita=" + idOggettoVendita + ", numeroProgressivo="
                + numeroProgressivo + ", posizioneElenco=" + posizioneElenco + ", quantità=" + quantità
                + ", prezzoParziale=" + prezzoParziale + "]";
    }
}

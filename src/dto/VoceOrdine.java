package dto;

public class VoceOrdine {
    
    private String aic_ean;
    private Integer numero_ordine;
    private Integer posizione_in_elenco;
    private Integer quantità;
    private Double costo_unitario;

    public VoceOrdine(String aic_ean, Integer numero_ordine, Integer posizione_in_elenco, Integer quantità,
            Double costo_unitario) {
        this.aic_ean = aic_ean;
        this.numero_ordine = numero_ordine;
        this.posizione_in_elenco = posizione_in_elenco;
        this.quantità = quantità;
        this.costo_unitario = costo_unitario;
    }

    public String getAic_ean() {
        return aic_ean;
    }

    public Integer getNumero_ordine() {
        return numero_ordine;
    }

    public Integer getPosizione_in_elenco() {
        return posizione_in_elenco;
    }

    public Integer getQuantità() {
        return quantità;
    }

    public Double getCosto_unitario() {
        return costo_unitario;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aic_ean == null) ? 0 : aic_ean.hashCode());
        result = prime * result + ((numero_ordine == null) ? 0 : numero_ordine.hashCode());
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
        VoceOrdine other = (VoceOrdine) obj;
        if (aic_ean == null) {
            if (other.aic_ean != null)
                return false;
        } else if (!aic_ean.equals(other.aic_ean))
            return false;
        if (numero_ordine == null) {
            if (other.numero_ordine != null)
                return false;
        } else if (!numero_ordine.equals(other.numero_ordine))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "VoceOrdine [aic_ean=" + aic_ean + ", numero_ordine=" + numero_ordine + ", posizione_in_elenco="
                + posizione_in_elenco + ", quantità=" + quantità + ", costo_unitario=" + costo_unitario + "]";
    }
    
    
    
}

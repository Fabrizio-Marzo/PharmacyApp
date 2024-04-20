package dto;

public class Offerta {
    private String nome;
    private Double costo;
    private String aic_ean;
    
    public Offerta(String nome, Double costo, String aic_ean) {
        this.nome = nome;
        this.costo = costo;
        this.aic_ean = aic_ean;
    }

    public String getNome() {
        return nome;
    }

    public Double getCosto() {
        return costo;
    }

    public String getAic_ean() {
        return aic_ean;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((aic_ean == null) ? 0 : aic_ean.hashCode());
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
        Offerta other = (Offerta) obj;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        if (aic_ean == null) {
            if (other.aic_ean != null)
                return false;
        } else if (!aic_ean.equals(other.aic_ean))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Offerta [nome=" + nome + ", costo=" + costo + ", aic_ean=" + aic_ean + "]";
    }
}

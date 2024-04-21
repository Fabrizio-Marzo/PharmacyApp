package dto;

public class Prodotto {

    private String classe_parafarmaco;
    private String codice_di_equivaleza;
    private String tipo_di_ricetta;
    private String aic_ean;
    private String tipo_prodotto;
    private String nome;
    private String produttore;
    private String quantità;
    private String metodo_di_somministrazione;
    private Integer giacenza_totale;

    // Costruttore di Prodotto
    public Prodotto(String classe_parafarmaco, String codice_di_equivaleza, String tipo_di_ricetta, String aic_ean,
            String tipo_prodotto, String nome, String produttore, String quantità, String metodo_di_somministrazione,
            Integer giacenza_totale) {
        this.classe_parafarmaco = classe_parafarmaco;
        this.codice_di_equivaleza = codice_di_equivaleza;
        this.tipo_di_ricetta = tipo_di_ricetta;
        this.aic_ean = aic_ean;
        this.tipo_prodotto = tipo_prodotto;
        this.nome = nome;
        this.produttore = produttore;
        this.quantità = quantità;
        this.metodo_di_somministrazione = metodo_di_somministrazione;
        this.giacenza_totale = giacenza_totale;
    }
    
    public String getClasse_parafarmaco() {
        return classe_parafarmaco;
    }

    public String getCodice_di_equivaleza() {
        return codice_di_equivaleza;
    }

    public String getTipo_di_ricetta() {
        return tipo_di_ricetta;
    }

    public String getAic_ean() {
        return aic_ean;
    }

    public String getTipo_prodotto() {
        return tipo_prodotto;
    }

    public String getNome() {
        return nome;
    }

    public String getProduttore() {
        return produttore;
    }

    public String getQuatità() {
        return quantità;
    }

    public String getMetodo_di_somministrazione() {
        return metodo_di_somministrazione;
    }

    public Integer getGiacenza_totale() {
        return giacenza_totale;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        Prodotto other = (Prodotto) obj;
        if (aic_ean == null) {
            if (other.aic_ean != null)
                return false;
        } else if (!aic_ean.equals(other.aic_ean))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Prodotto [classe_parafarmaco=" + classe_parafarmaco + ", codice_di_equivaleza=" + codice_di_equivaleza
                + ", tipo_di_ricetta=" + tipo_di_ricetta + ", aic_ean=" + aic_ean + ", tipo_prodotto=" + tipo_prodotto
                + ", nome=" + nome + ", produttore=" + produttore + ", quatità=" + quantità
                + ", metodo_di_somministrazione=" + metodo_di_somministrazione + ", giacenza_totale=" + giacenza_totale
                + "]";
    }

    



}

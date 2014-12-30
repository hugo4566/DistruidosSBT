/**
 * Created by Hugo on 29/12/2014.
 */
public class FileFTP {

    private int tipo;
    private String nome;
    private int tamanho;
    private String modificado;

    public FileFTP(String response) {
        String[] partes = response.split("\\s+");
        this.tipo = Integer.valueOf(partes[1]);
        this.nome = partes[8];
        this.tamanho = Integer.valueOf(partes[4]);
        this.modificado = partes[5]+" "+partes[6]+" "+partes[7];
    }

    public FileFTP(int tipo, String nome, int tamanho, String modificado) {
        this.tipo = tipo;
        this.nome = nome;
        this.tamanho = tamanho;
        this.modificado = modificado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    @Override
    public String toString() {
        return "FileFTP{" +
                "tipo=" + tipo +
                ", nome='" + nome + '\'' +
                ", tamanho=" + tamanho +
                ", modificado='" + modificado + '\'' +
                '}';
    }
}

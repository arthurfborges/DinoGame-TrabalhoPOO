package pacoteBase;


public interface Descritivel {
    int getX();
    int getY();
    int getSaude();

    default String descreverParaDebug() {
        return String.format("%-16s | Posição: [%2d,%2d] | Vida: %d",
                getClass().getSimpleName(), getX(), getY(), getSaude());
    }
}

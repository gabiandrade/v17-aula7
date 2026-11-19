package cerne.cooperados;

/** CPF como valor: só dígitos, sempre 11. A validação dos dígitos verificadores substitui UtilCerne.cpfValido. */
public record Cpf(String numero) {

    public Cpf {
        if (numero == null) {
            throw new IllegalArgumentException("CPF obrigatório");
        }
        numero = numero.replaceAll("\\D", "");
        if (numero.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos: " + numero);
        }
    }

    /** Dígitos verificadores conferem e não são todos iguais. */
    public boolean valido() {
        if (numero.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(9) == numero.charAt(9) - '0' && digitoVerificador(10) == numero.charAt(10) - '0';
    }

    public static boolean ehValido(String texto) {
        try {
            return new Cpf(texto).valido();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String formatado() {
        return numero.substring(0, 3) + "." + numero.substring(3, 6) + "." + numero.substring(6, 9) + "-"
                + numero.substring(9);
    }

    private int digitoVerificador(int posicao) {
        int soma = 0;
        for (int i = 0; i < posicao; i++) {
            soma += (numero.charAt(i) - '0') * (posicao + 1 - i);
        }
        int digito = (soma * 10) % 11;
        return digito == 10 ? 0 : digito;
    }
}

package cerne.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/** Utilitária em desmonte: sem sun.* desde o salto para o 11; o resto sai até o encontro 4. */
public final class UtilCerne {

    private static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATO_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DIA_MES_ANO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private UtilCerne() {
    }

    public static String formatarData(Date data) {
        return FORMATO_DATA.format(data);
    }

    /** DateTimeFormatter é imutável e thread-safe; convive com a versão Date até ela sair. */
    public static String formatarData(LocalDate data) {
        return DIA_MES_ANO.format(data);
    }

    public static String formatarDataHora(Date data) {
        return FORMATO_DATA_HORA.format(data);
    }

    public static Date parseData(String texto) throws Exception {
        return FORMATO_DATA.parse(texto);
    }

    /** Monta uma data à meia-noite no fuso da JVM (mês de 1 a 12). */
    public static Date data(int ano, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.clear();
        calendario.set(ano, mes - 1, dia);
        return calendario.getTime();
    }

    public static Date dataHora(int ano, int mes, int dia, int hora, int minuto) {
        Calendar calendario = Calendar.getInstance();
        calendario.clear();
        calendario.set(ano, mes - 1, dia, hora, minuto);
        return calendario.getTime();
    }

    public static Date somarDias(Date data, int dias) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(data);
        calendario.add(Calendar.DAY_OF_MONTH, dias);
        return calendario.getTime();
    }

    public static boolean cpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return false;
        }
        boolean todosIguais = true;
        for (int i = 1; i < 11; i++) {
            if (digitos.charAt(i) != digitos.charAt(0)) {
                todosIguais = false;
            }
        }
        if (todosIguais) {
            return false;
        }
        for (int posicao = 9; posicao <= 10; posicao++) {
            int soma = 0;
            for (int i = 0; i < posicao; i++) {
                soma += (digitos.charAt(i) - '0') * (posicao + 1 - i);
            }
            int digito = (soma * 10) % 11;
            if (digito == 10) {
                digito = 0;
            }
            if (digito != digitos.charAt(posicao) - '0') {
                return false;
            }
        }
        return true;
    }

    public static String formatarCpf(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    public static String formatarValor(BigDecimal valor) {
        return "R$ " + valor.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString().replace(".", ",");
    }

    /** Regra de tarifa duplicada (a "oficial" está em CalculadoraDeTarifa, com números diferentes). */
    public static BigDecimal calcularTarifa(String tipoDeTransferencia, BigDecimal valor) {
        if ("INTERNA".equals(tipoDeTransferencia)) {
            return BigDecimal.ZERO;
        } else if ("ENTRE_COOPERATIVAS".equals(tipoDeTransferencia)) {
            return new BigDecimal("1.50");
        } else if ("OUTRO_BANCO".equals(tipoDeTransferencia)) {
            return valor.multiply(new BigDecimal("0.005")).max(new BigDecimal("3.00"));
        }
        return BigDecimal.ZERO;
    }

    /** Horário comercial: das 8h às 18h, no fuso da JVM. */
    public static boolean horarioComercial(Date momento) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(momento);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        return hora >= 8 && hora < 18;
    }

    public static String gerarIdentificadorDeTransferencia() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /** Assinatura dos arquivos de conciliação. BASE64Encoder quebrava linha a cada 76 caracteres; getEncoder não. */
    public static String codificarBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void log(String mensagem) {
        System.out.println(FORMATO_DATA_HORA.format(new Date()) + " " + mensagem);
    }

    // ... no sistema real, mais 1.700 linhas: validação de agência, cálculo de saldo,
    // leitura de propriedades, montagem de XML à mão, conversões de moeda etc.
}

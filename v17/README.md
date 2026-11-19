# Cerne · marco v17 (resultado dos encontros 4, 6 e 7)

Java 17 · Jakarta EE 10 (`jakarta.*`) · JUnit 5.10 · JAXB 4 · XStream 1.4.9 ainda presente (com `--add-opens`) · sem Lombok.
É o ponto de partida dos encontros 5 e 8 e da avaliação do encontro 9.

## Compilar e testar

```sh
export JAVA_HOME=~/.sdkman/candidates/java/17-open
mvn -q -f v17/pom.xml test
mvn -q -f v17/pom.xml package      # v17/target/cerne-v17.jar + v17/target/lib/
mvn -q -f v17/pom.xml test -Dtest=ServicoDeAuditoriaTest -DargLine=""   # mostra a InaccessibleObjectException
```

## O que este marco demonstra

**Encontro 4, plataforma:**
- `jakarta.jakartaee-api:10.0.0` e `jakarta.*` em EJB, CDI, JPA, JAX-RS e JTA.
- JAXB 4.0 (`jakarta.xml.bind`), com o arquivo de referência do `v11` inalterado e verde.
- JUnit 5 sem Vintage, com `@DisplayName`, `@ParameterizedTest` e `@CsvSource`.
- `argLine` obrigatório para o XStream: quatro `--add-opens`.

**Encontro 4, domínio:**
- `LancamentoDTO`, `ResumoDeLancamento`, `LinhaDoRelatorio`, `Agencia`, `Cpf` e os registros de conciliação viraram records, com construtor compacto onde há regra.
- `Lancamento` é uma `sealed interface` com os records `Credito`, `Debito`, `Tarifa` e `Estorno`.
- `Transferencia` é uma `sealed interface` com os records `TransferenciaInterna`, `TransferenciaEntreCooperativas` e `TransferenciaParaOutroBanco`. Os destinos são `Conta`, `ContaCooperativa` e `ContaExterna`, e o valor positivo e os destinos preenchidos são validados no construtor compacto (desafio do encontro 4).
- `CalculadoraDeTarifa` traz as duas formas do desafio:
  - `calcular`: switch expression sobre o discriminador `TipoDeTransferencia`, sem `default`;
  - `calcularTarifa`: escada de `instanceof` com pattern matching, com o `throw` final que só o 21 elimina.
- `Conta` e `Cooperado` continuam classes, porque são entidades. `Conta` usa acessores no estilo record: `numero()`, `saldo()`, `ativa()`, `limiteDiario()`, `limiteDeChequeEspecial()`, `tipoDoTitular()`.
- `Cooperado.possuiConvenioEspecial()`. A massa tem um cooperado com convênio a cada cinquenta.

**Encontro 7:**
- `FabricaDeLancamentos.aPartirDe(LinhaDoArquivo)`, com `default` porque o seletor é `String`.
- `ServicoDeTransferencia` (interface de um método) + `ServicoDeTransferenciaPadrao`.
- Decoradores `ServicoDeTransferenciaAuditado` e `ServicoDeTransferenciaComDeduplicacao`.
- `DecoradorDeAuditoria`, a variante `@Decorator` do CDI, que só compila.
- `ConsultorDeCpfComCache` com `Clock` injetado. `RelogioAjustavel` fica no escopo de teste.
- O Visitor (`VisitanteDeLancamento` + `aceitar`) segue em `cerne.extratos` para o lado a lado com o `v21`.

**Encontro 6:** `ProcessamentoDeLancamentos` é igual nos quatro marcos.

**Ainda aqui, de propósito:**
- `ValidadorDeTransferencia` com `if` encadeados (encontro 8). Cheque especial só para pessoa jurídica.
- `ServicoDeCadastro.cadastrar` com `if` em sequência (avaliação do encontro 9).
- XStream em `ServicoDeAuditoria`.
- O módulo `cerne.conciliacao` (`ConciliadorDeLancamentos`, `GeradorDeRelatorioDeConciliacao`, `ClassificadorDeDivergencias`, `ExportadorDeConciliacao`) com 11 `instanceof` em escada.

## Medições

```sh
$JAVA_HOME/bin/java -Xms1g -Xmx1g "-Xlog:gc*:file=gc-v17.log:uptime,level,tags" \
  -cp v17/target/classes cerne.medicao.ProcessamentoDeLancamentos 3 5
mvn -q -f v17/pom.xml compile exec:java -Dexec.mainClass=cerne.medicao.MedicaoDoRelatorio
```

## Diferença para o v11

A plataforma subiu primeiro (`jakarta`, JUnit 5, `--add-opens`) e o domínio veio depois. `cerne.util` foi eliminado:
- `UtilCerne` foi desmontada: o CPF foi para `Cpf`, as datas para `DateTimeFormatter`, a assinatura Base64 para o gerador de conciliação e o identificador para o serviço.
- As três classes "com nome de pattern" deram lugar aos patterns de verdade.

O Lombok saiu do `pom.xml`. `Transferencia` deixou de ser uma classe com campos opcionais por tipo. O serviço de transferência virou interface, para permitir os decoradores. As datas do domínio são todas `java.time`.

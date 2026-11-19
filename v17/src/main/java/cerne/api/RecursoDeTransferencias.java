package cerne.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import cerne.extratos.ServicoDeExtrato;
import cerne.lancamentos.TipoDeLancamento;
import cerne.transferencias.ServicoDeTransferencia;
import cerne.transferencias.SolicitacaoDeTransferencia;
import cerne.transferencias.TransferenciaInterna;

/** Recurso JAX-RS de transferências (jakarta.ws.rs). Recebe o bean já decorado pelo contêiner. */
@Path("/transferencias")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecursoDeTransferencias {

    @Inject
    ServicoDeTransferencia servico;

    @Inject
    ServicoDeExtrato extratos;

    @POST
    @Path("/internas")
    public Response transferir(TransferenciaInterna transferencia) {
        var comprovante = servico.transferir(SolicitacaoDeTransferencia.de(transferencia));
        return Response.status(Response.Status.CREATED).entity(comprovante.identificador()).build();
    }

    @GET
    @Path("/extrato/{cpf}")
    public List<LancamentoDTO> lancamentos(@PathParam("cpf") String cpf,
                                           @QueryParam("inicio") String inicio,
                                           @QueryParam("fim") String fim) {
        try {
            return extratos.lancamentosDoPeriodo(cpf, LocalDate.parse(inicio), LocalDate.parse(fim)).stream()
                    .map(LancamentoDTO::de)
                    .toList();
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Período inválido");
        }
    }

    public BigDecimal totalDeCreditos(List<LancamentoDTO> dtos) {
        return dtos.stream()
                .filter(lancamentoDTO -> lancamentoDTO.tipo() == TipoDeLancamento.CREDITO)
                .map(LancamentoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

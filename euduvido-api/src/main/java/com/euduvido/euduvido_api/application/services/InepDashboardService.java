package com.euduvido.euduvido_api.application.services;

import com.euduvido.euduvido_api.entrypoint.dtos.response.*;
import com.euduvido.euduvido_api.infrastructure.inep.InepDashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InepDashboardService {

    private final InepDashboardRepository repository;

    public InepDashboardService(InepDashboardRepository repository) {
        this.repository = repository;
    }

    public List<ResumoGeralResponse>         getResumoGeral()          { return repository.findResumoGeral(); }
    public List<RankingCursosRegiaoResponse> getRankingCursosRegiao()   { return repository.findRankingCursosRegiao(); }
    public List<RankingCursosAreaResponse>   getRankingCursosArea()     { return repository.findRankingCursosArea(); }
    public List<GeneroIngressantesResponse>  getGeneroIngressantes()    { return repository.findGeneroIngressantes(); }
    public List<RacaIngressantesResponse>    getRacaIngressantes()      { return repository.findRacaIngressantes(); }
    public List<EtariaIngressantesResponse>  getEtariaIngressantes()    { return repository.findEtariaIngressantes(); }
    public List<EadVsPresencialResponse>     getEadVsPresencial()       { return repository.findEadVsPresencial(); }
    public List<TaxaConclusaoIesResponse>    getTaxaConclusaoIes()      { return repository.findTaxaConclusaoIes(); }
}

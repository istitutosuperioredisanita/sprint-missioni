package it.cnr.si.missioni.web.filter;

import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.specification.BaseSpecification;
import it.cnr.si.missioni.repository.specification.SpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

public class RimborsoMissioneFilterMapper {

    public static Specification<RimborsoMissione> mapBaseFilters(RimborsoMissioneFilter filter) {
        if (filter == null) {
            return Specification.where(null);
        }

        return new SpecificationBuilder<RimborsoMissione>()
                .and(BaseSpecification.eq("anno", filter.getAnno()))
                .and(BaseSpecification.ge("id", filter.getDaId()))
                .and(BaseSpecification.le("id", filter.getaId()))
                .and(BaseSpecification.eq("stato", filter.getStato()))
                .and(BaseSpecification.eq("statoFlusso", filter.getStatoFlusso()))
                .and(BaseSpecification.eq("validato", filter.getValidato()))
                .and(BaseSpecification.in("stato", filter.getListaStatiMissione()))
                .and(BaseSpecification.ge("numero", filter.getDaNumero()))
                .and(BaseSpecification.le("numero", filter.getaNumero()))
                .and(BaseSpecification.dataInserimentoDa(filter.getDaData()))
                .and(BaseSpecification.dataInserimentoA(filter.getaData()))
                .and(BaseSpecification.dataMissioneDa(filter.getDaDataMissione()))
                .and(BaseSpecification.dataMissioneA(filter.getaDataMissione()))
                .and(BaseSpecification.eq("cdsRich", filter.getCdsRich()))
                .and(BaseSpecification.eq("cup", filter.getCup()))
                .build();
    }
}
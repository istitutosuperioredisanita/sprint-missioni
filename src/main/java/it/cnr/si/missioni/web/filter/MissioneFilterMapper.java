package it.cnr.si.missioni.web.filter;

import it.cnr.si.missioni.repository.specification.BaseSpecification;
import it.cnr.si.missioni.repository.specification.SpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

public class MissioneFilterMapper {

    public static <T> Specification<T> mapBaseFilters(MissioneFilter filter) {

        if (filter == null) {
            return Specification.where(null);
        }

        return new SpecificationBuilder<T>()

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
package com.noxis.broker.persistence.jpa;

import com.noxis.broker.persistence.model.QuoteDTO;
import com.noxis.broker.persistence.model.QuoteEntity;
import com.noxis.broker.persistence.model.SymbolEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Integer> {

    @Override
    List<QuoteEntity> findAll();

    Optional<QuoteEntity> findBySymbol(SymbolEntity entity);

    List<QuoteDTO> listOrderByVolumeDesc();

    List<QuoteDTO> listOrderByVolumeAsc();

    // Filter & Ordering
    List<QuoteDTO> findByVolumeGreaterThanOrderByVolumeAsc(BigDecimal volume);

    // Pagination
    List<QuoteDTO> findByVolumeGreaterThan(BigDecimal volume, Pageable pageable);

    Slice<QuoteDTO> list(Pageable pageable);
}

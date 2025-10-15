package com.sap.adds_service.prices.infrastructure.output.jpa.adapater;

import com.sap.adds_service.prices.application.output.DeletePricePort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.application.output.SavePricePort;
import com.sap.adds_service.prices.domain.Price;
import com.sap.adds_service.prices.infrastructure.output.jpa.mapper.PriceMapper;
import com.sap.adds_service.prices.infrastructure.output.jpa.repository.PriceEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PriceJpaAdapter implements FindPricePort, SavePricePort, DeletePricePort {

    private final PriceEntityRepository priceEntityRepository;
    private final PriceMapper priceMapper;

    @Override
    public Optional<Price> findById(UUID id) {
        var result = priceEntityRepository.findById(id);
        return result.map(priceMapper::toDomain);
    }

    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return priceEntityRepository.existsByCinemaId(id);
    }

    @Override
    public Optional<Price> findByCinemaId(UUID cinemaId) {

        return priceEntityRepository.findByCinemaId(cinemaId)
                .map(priceMapper::toDomain);
    }

    @Override
    public Page<Price> findAll(int page) {
        var result = priceEntityRepository.findAll(PageRequest.of(page, 20));
        return result.map(priceMapper::toDomain);
    }

    @Override
    public Price save(Price price) {
        var entity = priceMapper.toEntity(price);
        var savedEntity = priceEntityRepository.save(entity);
        return priceMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteByCinemaId(UUID cinemaId) {
        priceEntityRepository.deleteByCinemaId(cinemaId);
    }

    @Override
    public void deleteById(UUID id) {
        priceEntityRepository.deleteById(id);
    }
}

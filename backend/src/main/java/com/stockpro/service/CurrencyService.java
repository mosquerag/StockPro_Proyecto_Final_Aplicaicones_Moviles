package com.stockpro.service;

import com.stockpro.dto.currency.ManualTasaCambioRequest;
import com.stockpro.dto.currency.TasaCambioDto;
import com.stockpro.model.entity.TasaCambio;
import com.stockpro.repository.TasaCambioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private final TasaCambioRepository tasaCambioRepository;
    private final RestTemplate restTemplate;

    public CurrencyService(TasaCambioRepository tasaCambioRepository, RestTemplateBuilder restTemplateBuilder) {
        this.tasaCambioRepository = tasaCambioRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    // Regla de redondeo por moneda: COP sin decimales, USD y EUR con dos
    public BigDecimal roundCurrency(BigDecimal amount, String currency) {
        if (amount == null) return BigDecimal.ZERO;
        if ("COP".equalsIgnoreCase(currency)) {
            return amount.setScale(0, RoundingMode.HALF_UP);
        } else {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getEffectiveRate(String origen, String destino, BigDecimal manualOverride) {
        if (origen.equalsIgnoreCase(destino)) {
            return BigDecimal.ONE;
        }

        if (manualOverride != null && manualOverride.compareTo(BigDecimal.ZERO) > 0) {
            return manualOverride;
        }

        Optional<TasaCambio> rateOpt = tasaCambioRepository.findByMonedaOrigenAndMonedaDestino(
                origen.toUpperCase(), destino.toUpperCase()
        );

        if (rateOpt.isPresent()) {
            return rateOpt.get().getTasa();
        }

        // Tasa inversa
        Optional<TasaCambio> inverseOpt = tasaCambioRepository.findByMonedaOrigenAndMonedaDestino(
                destino.toUpperCase(), origen.toUpperCase()
        );

        if (inverseOpt.isPresent() && inverseOpt.get().getTasa().compareTo(BigDecimal.ZERO) > 0) {
            return BigDecimal.ONE.divide(inverseOpt.get().getTasa(), 6, RoundingMode.HALF_UP);
        }

        // Valores de reserva de seguridad si la BD aún no estuviera poblada
        if ("USD".equalsIgnoreCase(origen) && "COP".equalsIgnoreCase(destino)) return new BigDecimal("4150.000000");
        if ("COP".equalsIgnoreCase(origen) && "USD".equalsIgnoreCase(destino)) return new BigDecimal("0.000241");
        if ("EUR".equalsIgnoreCase(origen) && "COP".equalsIgnoreCase(destino)) return new BigDecimal("4500.000000");
        if ("COP".equalsIgnoreCase(origen) && "EUR".equalsIgnoreCase(destino)) return new BigDecimal("0.000222");
        if ("EUR".equalsIgnoreCase(origen) && "USD".equalsIgnoreCase(destino)) return new BigDecimal("1.085000");
        if ("USD".equalsIgnoreCase(origen) && "EUR".equalsIgnoreCase(destino)) return new BigDecimal("0.921659");

        return BigDecimal.ONE;
    }

    @Transactional
    public List<TasaCambioDto> refreshRatesFromApi() {
        try {
            // Consulta API pública y gratuita de tasas
            String url = "https://open.er-api.com/v6/latest/USD";
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("rates")) {
                Map<?, ?> rates = (Map<?, ?>) response.get("rates");

                Number copRateNum = (Number) rates.get("COP");
                Number eurRateNum = (Number) rates.get("EUR");

                if (copRateNum != null && eurRateNum != null) {
                    BigDecimal usdToCop = new BigDecimal(copRateNum.toString());
                    BigDecimal usdToEur = new BigDecimal(eurRateNum.toString());
                    BigDecimal eurToUsd = BigDecimal.ONE.divide(usdToEur, 6, RoundingMode.HALF_UP);
                    BigDecimal eurToCop = usdToCop.multiply(eurToUsd).setScale(6, RoundingMode.HALF_UP);

                    saveOrUpdateRate("USD", "COP", usdToCop, "ExchangeRate-API Online", false);
                    saveOrUpdateRate("COP", "USD", BigDecimal.ONE.divide(usdToCop, 6, RoundingMode.HALF_UP), "ExchangeRate-API Online", false);
                    saveOrUpdateRate("USD", "EUR", usdToEur, "ExchangeRate-API Online", false);
                    saveOrUpdateRate("EUR", "USD", eurToUsd, "ExchangeRate-API Online", false);
                    saveOrUpdateRate("EUR", "COP", eurToCop, "ExchangeRate-API Online", false);
                    saveOrUpdateRate("COP", "EUR", BigDecimal.ONE.divide(eurToCop, 6, RoundingMode.HALF_UP), "ExchangeRate-API Online", false);

                    log.info("Tasas de cambio actualizadas exitosamente desde API externa");
                }
            }
        } catch (Exception ex) {
            log.warn("No se pudo conectar a la API de tasas de cambio; se conservan las últimas tasas almacenadas: {}", ex.getMessage());
        }

        return listAllRates();
    }

    @Transactional
    public TasaCambioDto setManualRate(ManualTasaCambioRequest request) {
        TasaCambio tasa = saveOrUpdateRate(
                request.getMonedaOrigen().toUpperCase(),
                request.getMonedaDestino().toUpperCase(),
                request.getTasa(),
                "Manual - Administrador",
                true
        );
        return new TasaCambioDto(tasa);
    }

    @Transactional(readOnly = true)
    public List<TasaCambioDto> listAllRates() {
        return tasaCambioRepository.findAll().stream()
                .map(TasaCambioDto::new)
                .collect(Collectors.toList());
    }

    private TasaCambio saveOrUpdateRate(String origen, String destino, BigDecimal tasaValor, String fuente, boolean esManual) {
        TasaCambio tasa = tasaCambioRepository.findByMonedaOrigenAndMonedaDestino(origen, destino)
                .orElse(new TasaCambio());

        tasa.setMonedaOrigen(origen);
        tasa.setMonedaDestino(destino);
        tasa.setTasa(tasaValor.setScale(6, RoundingMode.HALF_UP));
        tasa.setFechaActualizacion(OffsetDateTime.now());
        tasa.setFuente(fuente);
        tasa.setEsManual(esManual);

        return tasaCambioRepository.save(tasa);
    }
}

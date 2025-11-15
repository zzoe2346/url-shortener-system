package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.IdSupplier;
import com.jeongseonghun.urlshortener.domain.ShortKeyGenerator;
import com.jeongseonghun.urlshortener.support.Base62;
import org.springframework.stereotype.Component;

@Component
public class DefaultShortKeyGenerator implements ShortKeyGenerator {

    private final IdSupplier idSupplier;

    public DefaultShortKeyGenerator(IdSupplier idSupplier) {
        this.idSupplier = idSupplier;
    }

    @Override
    public String generateShortKey() {
        return Base62.Encoder.encode(idSupplier.getId());
    }
}

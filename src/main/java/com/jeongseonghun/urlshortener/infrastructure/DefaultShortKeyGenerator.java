package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.IdSupplier;
import com.jeongseonghun.urlshortener.domain.ShortKeyGenerator;
import com.jeongseonghun.urlshortener.support.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultShortKeyGenerator implements ShortKeyGenerator {

    private final IdSupplier idSupplier;

    @Override
    public String generateShortKey() {
        return Base62.Encoder.encode(idSupplier.getId());
    }
}

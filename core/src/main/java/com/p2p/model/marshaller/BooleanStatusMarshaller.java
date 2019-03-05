package com.p2p.model.marshaller;

import com.p2p.model.BooleanStatus;
import org.apache.commons.lang3.StringUtils;
import javax.persistence.AttributeConverter;

public class BooleanStatusMarshaller implements AttributeConverter<BooleanStatus, String> {

    @Override
    public String convertToDatabaseColumn(BooleanStatus attribute) {
        if (attribute == null) {
            return BooleanStatus.DISABLED.name();
        }
        return attribute.name();
    }

    @Override
    public BooleanStatus convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData)) {
            return BooleanStatus.DISABLED;
        }
        return BooleanStatus.valueOf(dbData);
    }

}

package com.p2p.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.p2p.exceptions.CoreException;
import com.p2p.validations.NotEmptyString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import net.sf.oval.constraint.NotNull;

/**
 * The Class ParameterParser.
 */
@Component
public class ParameterParser {

    private static final String REQUEST_PARAMETERS_ERROR_MESSAGE = "Request Parameters cannot be null";
    private static final String REQUEST_PARAM_NAME_ERROR_MESSAGE = "Parameter name cannot be null";
    @Autowired
    private DateTimeUtils dateTimeUtils;

    /**
     * Gets the string parameter.
     */
    public String getStringParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = (String) requestParameters.get(name);
        if (required && StringUtils.isEmpty(value)) {
            throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
        }
        return value;
    }

    /**
     * Gets the int parameter.
     */
    public Integer getIntParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = Objects.toString(requestParameters.get(name));
        return getIntParameter(value, name, required);
    }

    /**
     * Gets the long parameter.
     */
    public Long getLongParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = Objects.toString(requestParameters.get(name));
        if (NumberUtils.isCreatable(value)) {
            return NumberUtils.createLong(value);
        }
        if (required) {
            throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
        }
        return null;
    }

    /**
     * Gets the int parameter.
     */
    public Integer getIntParameter(String intString, String name, boolean required) {
        if (NumberUtils.isCreatable(intString)) {
            return NumberUtils.createInteger(intString);
        }
        if (required) {
            throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
        }
        return null;
    }

    /**
     * Gets the double parameter.
     */
    public Double getDoubleParameter(String intString, String name, boolean required) {
        if (NumberUtils.isCreatable(intString)) {
            return NumberUtils.createDouble(intString);
        }
        if (required) {
            throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
        }
        return null;
    }

    /**
     * Gets the double parameter.
     */
    public Double getDoubleParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = Objects.toString(requestParameters.get(name));
        return getDoubleParameter(value, name, required);
    }

    /**
     * Gets the local date parameter.
     */
    public LocalDate getLocalDateParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = (String) requestParameters.get(name);
        if (StringUtils.isEmpty(value)) {
            if (required) {
                throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
            }
            return null;
        } else {
            return dateTimeUtils.getDateFromString(value);
        }
    }

    /**
     * Gets the enum type from string.
     */
    public <T extends Enum<T>> T getEnumTypeFromString(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, Class<T> enumType,
            boolean required) {
        String value = (String) requestParameters.get(name);
        return getEnumTypeFromString(value, name, enumType, required);
    }

    /**
     * Gets the enum list from string list.
     */
    public <T extends Enum<T>> List<T> getEnumListFromStringList(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) List<String> enumListString,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, Class<T> enumType,
            boolean required) {
        if (CollectionUtils.isEmpty(enumListString)) {
            if (required) {
                throw new CoreException.NotValidException("%s cannot be empty", name);
            }
            return null;
        }
        List<T> enumList = new ArrayList<>();
        for (String enumString : enumListString) {
            enumList.add(getEnumTypeFromString(enumString, name, enumType, required));
        }
        return enumList;
    }

    /**
     * Gets the enum type from string.
     */
    public <T extends Enum<T>> T getEnumTypeFromString(String value, String name, Class<T> enumType, boolean required) {
        if (StringUtils.isEmpty(value)) {
            if (required) {
                throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
            }
            return null;
        } else {
            return Enum.valueOf(enumType, value.toUpperCase());
        }
    }

    /**
     * Gets the local date time parameter.
     */
    public LocalDateTime getLocalDateTimeParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = (String) requestParameters.get(name);
        return getLocalDateTimeParameter(value, name, required);
    }

    /**
     * Gets the local date time parameter.
     */
    public LocalDateTime getLocalDateTimeParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) String timeString,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        if (StringUtils.isEmpty(timeString)) {
            if (required) {
                throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
            }
            return null;
        } else {
            return dateTimeUtils.getTimeFromString(timeString);
        }
    }

    /**
     * Gets the list map parameter.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListMapParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) requestParameters.get(name);
        if (CollectionUtils.isEmpty(listMap) && required) {
            throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
        }
        return listMap;
    }

    /**
     * Gets the map parameter.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMapParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        Map<String, Object> map = (Map<String, Object>) requestParameters.get(name);
        if (map == null && required) {
            throw new CoreException.NotValidException("%s cannot be empty", name);
        }
        return map;
    }

    /**
     * Gets the list string parameters.
     */
    @SuppressWarnings("unchecked")
    public List<String> getListStringParameters(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {

        List<String> stringList = (List<String>) requestParameters.get(name);
        if (CollectionUtils.isEmpty(stringList) && required) {
            throw new CoreException.NotValidException(String.format("%s cannot be empty", name));
        }
        return stringList;
    }

    /**
     * Gets the boolean parameter.
     */
    public boolean getBooleanParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name) {
        String value = (String) requestParameters.get(name);
        return getBooleanParameter(value, name);
    }

    /**
     * Gets the boolean parameter.
     */
    public boolean getBooleanParameter(String value,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name) {
        return StringUtils.equalsIgnoreCase(value, "true");
    }

    /**
     * Gets the local time parameter.
     */
    public LocalTime getLocalTimeParameter(String value,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        if (StringUtils.isEmpty(value)) {
            if (required) {
                throw new CoreException.NotValidException("%s cannot be empty", name);
            }
            return null;
        }
        try {
            return dateTimeUtils.getLocalTime(value);
        } catch (Exception e) {
            if (required) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Gets the local time parameter.
     */
    public LocalTime getLocalTimeParameter(
            @NotNull(message = REQUEST_PARAMETERS_ERROR_MESSAGE) Map<String, Object> requestParameters,
            @NotEmptyString(message = REQUEST_PARAM_NAME_ERROR_MESSAGE) String name, boolean required) {
        String value = (String) requestParameters.get(name);
        return getLocalTimeParameter(value, name, required);
    }
}

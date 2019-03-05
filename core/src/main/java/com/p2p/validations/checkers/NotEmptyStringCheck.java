package com.p2p.validations.checkers;

import com.p2p.validations.NotEmptyString;
import org.apache.commons.lang3.StringUtils;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * The Class NotEmptyStringCheck.
 */
public class NotEmptyStringCheck extends AbstractAnnotationCheck<NotEmptyString> {

    private static final long serialVersionUID = -4514314645335079175L;

    /*
     * (non-Javadoc)
     *
     * @see net.sf.oval.Check#isSatisfied(java.lang.Object, java.lang.Object, net.sf.oval.context.OValContext,
     * net.sf.oval.Validator)
     */
    @Override
    public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator)
            throws OValException {
        if (valueToValidate instanceof String) {
            return StringUtils.isNotEmpty((String) valueToValidate);
        }
        return false;
    }

}

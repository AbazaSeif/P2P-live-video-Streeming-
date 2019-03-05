package com.p2p.validations.checkers;

import java.util.Collection;

import com.p2p.validations.NotEmptyList;
import com.p2p.validations.NotEmptyString;
import org.apache.commons.collections4.CollectionUtils;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * The Class NotEmptyListCheck.
 */
public class NotEmptyListCheck extends AbstractAnnotationCheck<NotEmptyList> {

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.oval.Check#isSatisfied(java.lang.Object, java.lang.Object, net.sf.oval.context.OValContext,
     * net.sf.oval.Validator)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator)
            throws OValException {
        if (valueToValidate instanceof Collection<?>) {
            return CollectionUtils.isNotEmpty((Collection) valueToValidate);
        }
        return false;
    }

}

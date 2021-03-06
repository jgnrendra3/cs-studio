/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VString;

/**
 *
 * @author carcassi
 */
public class ArgumentExpressions {
    public static ReadFunctionArgument<String> stringArgument(ReadFunction<?> function, String argumentName) {
        if (function == null) {
            return new ReadFunctionArgument<>();
        } else {
            return new ReadFunctionArgument<>(new VStringToStringReadFunction(new CheckedReadFunction<>(function, argumentName, VString.class)));
        }
    }
}

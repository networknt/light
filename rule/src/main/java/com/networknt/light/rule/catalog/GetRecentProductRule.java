package com.networknt.light.rule.catalog;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 18/01/16.
 *
 * AccessLevel A everyone can access
 *
 * This API get all the product regardless catalog order by the time of creation date.
 */
public class GetRecentProductRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getRecentProduct(objects);
    }
}

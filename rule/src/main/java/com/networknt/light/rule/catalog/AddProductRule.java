package com.networknt.light.rule.catalog;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 30/03/15.
 *
 * AccessLevel R [owner, admin, catalogAdmin]
 */
public class AddProductRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addProduct(objects);
    }
}

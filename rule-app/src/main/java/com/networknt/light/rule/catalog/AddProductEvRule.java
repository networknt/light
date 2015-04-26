package com.networknt.light.rule.catalog;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 30/03/15.
 */
public class AddProductEvRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addProductEv(objects);
    }
}

package com.networknt.light.rule.catalog;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 30/03/15.
 *
 * AccessLevel A
 */
public class GetCatalogTreeRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getBranchTree("catalog", objects);
    }
}

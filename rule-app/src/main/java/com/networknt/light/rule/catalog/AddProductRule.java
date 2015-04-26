package com.networknt.light.rule.catalog;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;

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

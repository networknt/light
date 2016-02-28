package com.networknt.light.rule.file;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 2/26/2016.
 *
 * AccessLevel: R [fileAdmin, admin, owner]
 *
 */
public class UplFileRule extends AbstractFileRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return uplFile(objects);
    }
}

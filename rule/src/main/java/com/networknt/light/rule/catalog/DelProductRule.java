/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.rule.catalog;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 25/04/15.
 *
 * productAdmin can only delete his or her products. However,
 * owner, admin and catalogAdmin can delete product and all the comments/reivews relate to the
 * product all together. This should not be done often only at extreme situation.
 *
 * Due to the implementation of orientdb delete graph scheduled for 2.2
 * https://github.com/orientechnologies/orientdb/issues/1108
 * We will be asking all the comments be deleted before deleting the product for now.
 *
 * TODO fix it after orientdb 2.2
 *
 * AccessLevel R [owner, admin, catalogAdmin, productAdmin]
 */
public class DelProductRule extends AbstractCatalogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return delProduct(objects);
    }
}

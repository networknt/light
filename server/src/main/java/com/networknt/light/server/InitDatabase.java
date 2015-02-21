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

package com.networknt.light.server;

import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by husteve on 9/4/2014.
 */
public class InitDatabase {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(InitDatabase.class);

    public static void main(final String[] args) {
        initDb();
    }

    public static void initDb() {
        logger.debug("Start initdb()");
        initStatus();
        initConfig();
        initRole();
        initUser();
        initCredential();
        initHost();
        initEvent();
        initRule();
        initMenu();
        initForm();
        initPage();
        /*
        initFeed();
        initTag();
        initBlog();
        initForum();
        initNews();
        initPost();
        initComment();
        initCatalog();
        initProduct();
        */
        initCounter();
        //initClient();
        initRequestTransform();
        initResponseTransform();
        initAccess();
        //initProxy();
        refreshDoc();
        logger.debug("End initdb()");
    }

    public static void initStatus() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Status")) {
                for (ODocument doc : db.browseClass("Status")) {
                    doc.delete();
                }
                schema.dropClass("Status");
            }
            OClass status = schema.createClass("Status");
            status.createProperty("host", OType.STRING);
            status.createProperty("app", OType.STRING);
            status.createProperty("map", OType.EMBEDDEDMAP);

            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initConfig() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Config")) {
                for (ODocument doc : db.browseClass("Config")) {
                    doc.delete();
                }
                schema.dropClass("Config");
            }
            OClass config = schema.createClass("Config");
            config.createProperty("host", OType.STRING);
            config.createProperty("app", OType.STRING);
            config.createProperty("category", OType.STRING);
            config.createProperty("name", OType.STRING);
            config.createProperty("version", OType.STRING);

            config.createProperty("id", OType.STRING);
            config.createProperty("properties", OType.EMBEDDEDMAP);
            config.createProperty("createUserId", OType.STRING);
            config.createProperty("createDate", OType.DATETIME);
            config.createProperty("updateUserId", OType.STRING);
            config.createProperty("updateDate", OType.DATETIME);
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initRole() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Role")) {
                for (ODocument doc : db.browseClass("Role")) {
                    doc.delete();
                }
                schema.dropClass("Role");
            }
            OClass role = schema.createClass("Role");
            role.createProperty("id", OType.STRING);
            role.createProperty("host", OType.STRING); // some roles are for specific host only.
            role.createProperty("desc", OType.STRING);
            role.createProperty("createUserRid", OType.STRING);
            role.createProperty("createUserId", OType.STRING);
            role.createProperty("createDate", OType.DATETIME);
            role.createProperty("updateUserRid", OType.STRING);
            role.createProperty("updateUserId", OType.STRING);
            role.createProperty("updateDate", OType.DATETIME);
            role.createIndex("Role.id", OClass.INDEX_TYPE.UNIQUE, "id");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initUser() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("User")) {
                for (ODocument doc : db.browseClass("User")) {
                    doc.delete();
                }
                schema.dropClass("User");
            }
            OClass user = schema.createClass("User");
            user.createProperty("host", OType.STRING);
            user.createProperty("userId", OType.STRING);
            user.createProperty("email", OType.STRING);
            user.createProperty("firstName", OType.STRING);
            user.createProperty("lastName", OType.STRING);
            user.createProperty("upUsers", OType.LINKSET);
            user.createProperty("downUsers", OType.LINKSET);
            user.createProperty("karma", OType.INTEGER);
            user.createProperty("locked", OType.BOOLEAN);
            user.createProperty("roles", OType.EMBEDDEDLIST);
            user.createProperty("credential", OType.LINK);
            user.createProperty("createDate", OType.DATETIME);
            user.createProperty("updateDate", OType.DATETIME);
            user.createIndex("User.email", OClass.INDEX_TYPE.UNIQUE, "email");
            user.createIndex("User.userId", OClass.INDEX_TYPE.UNIQUE, "userId");

            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initCredential() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Credential")) {
                for (ODocument doc : db.browseClass("Credential")) {
                    doc.delete();
                }
                schema.dropClass("Credential");
            }
            OClass credential = schema.createClass("Credential");
            credential.createProperty("password", OType.STRING);
            // each host has an entry with a list of refresh tokens up to 10 devices.
            // when 11 refresh token is created, the first one is removed for the host.
            // there is no expire date for refresh token unless it is removed by log out action from user.
            credential.createProperty("hostRefreshTokens", OType.EMBEDDEDMAP);
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initHost() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Host")) {
                for (ODocument doc : db.browseClass("Host")) {
                    doc.delete();
                }
                schema.dropClass("Host");
            }
            OClass host = schema.createClass("Host");
            host.createProperty("id", OType.STRING);
            host.createProperty("secret", OType.STRING);
            host.createProperty("active", OType.BOOLEAN);
            host.createProperty("refreshTokenLife", OType.INTEGER);
            host.createProperty("allowedOrigin", OType.STRING);
            host.createProperty("createDate", OType.DATETIME);
            host.createProperty("createUserRid", OType.LINK);
            host.createProperty("updateDate", OType.DATETIME);
            host.createProperty("updateUserRid", OType.LINK);
            host.createIndex("Host.id", OClass.INDEX_TYPE.UNIQUE, "id");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public static void initEvent() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Event")) {
                for (ODocument doc : db.browseClass("Event")) {
                    doc.delete();
                }
                schema.dropClass("Event");
            }
            OClass event = schema.createClass("Event");
            event.createProperty("id", OType.LONG);
            event.createProperty("version", OType.INTEGER);
            event.createProperty("host", OType.STRING);
            event.createProperty("app", OType.STRING);
            event.createProperty("category", OType.STRING);
            event.createProperty("name", OType.STRING);
            event.createProperty("createDate", OType.DATETIME);
            event.createProperty("createUserId", OType.STRING);
            event.createProperty("data", OType.EMBEDDEDMAP);
            event.createIndex("eventIdVersionIdx", OClass.INDEX_TYPE.UNIQUE, "id", "version");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initRule() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Rule")) {
                for (ODocument doc : db.browseClass("Rule")) {
                    doc.delete();
                }
                schema.dropClass("Rule");
            }
            OClass rule = schema.createClass("Rule");
            rule.createProperty("ruleClass", OType.STRING);
            rule.createProperty("host", OType.STRING);
            rule.createProperty("sourceCode", OType.STRING);
            rule.createProperty("createUserId", OType.STRING);
            rule.createProperty("createDate", OType.DATETIME);
            rule.createProperty("updateUserId", OType.STRING);
            rule.createProperty("updateDate", OType.DATETIME);
            rule.createIndex("Rule.ruleClass", OClass.INDEX_TYPE.UNIQUE, "ruleClass");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initMenu() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("MenuItem")) {
                for (ODocument doc : db.browseClass("MenuItem")) {
                    doc.delete();
                }
                schema.dropClass("MenuItem");
            }

            if (schema.existsClass("Menu")) {
                for (ODocument doc : db.browseClass("Menu")) {
                    doc.delete();
                }
                schema.dropClass("Menu");
            }

            OClass menuItem = schema.createClass("MenuItem");
            menuItem.createProperty("id", OType.STRING);      // unique id
            menuItem.createProperty("label", OType.STRING);   // label
            menuItem.createProperty("host", OType.STRING);    // host  some common menuItems have no host.
            menuItem.createProperty("path", OType.STRING);    // path url
            menuItem.createProperty("tpl", OType.STRING);     // template that angular use
            menuItem.createProperty("ctrl", OType.STRING);    // controller of angular
            menuItem.createProperty("left", OType.BOOLEAN);   // on the left side of nav bar if true
            menuItem.createProperty("roles", OType.EMBEDDEDLIST);  // shows up only if one of the role in the list exists in user profile
            menuItem.createProperty("createDate", OType.DATETIME);
            menuItem.createProperty("createUserId", OType.STRING);
            menuItem.createProperty("updateDate", OType.DATETIME);
            menuItem.createProperty("updateUserId", OType.STRING);  // don't want to see the password and other user content in menu
            menuItem.createProperty("menuItems", OType.LINKLIST, menuItem); // children to support only one level. (doesn't make sense to support multi-level for mobile devices)

            menuItem.createIndex("MenuItem.id", OClass.INDEX_TYPE.UNIQUE, "id");

            OClass menu = schema.createClass("Menu");
            menu.createProperty("host", OType.STRING);    // menu by the host
            menu.createProperty("createDate", OType.DATETIME);
            menu.createProperty("createUserId", OType.STRING);
            menu.createProperty("updateDate", OType.DATETIME);
            menu.createProperty("updateUserId", OType.STRING);
            menu.createProperty("menuItems", OType.LINKLIST, menuItem); // children to support only one level. (doesn't make sense to support multi-level for mobile devices)
            menu.createIndex("Menu.host", OClass.INDEX_TYPE.UNIQUE, "host");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initForm() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Form")) {
                for (ODocument doc : db.browseClass("Form")) {
                    doc.delete();
                }
                schema.dropClass("Form");
            }
            OClass form = schema.createClass("Form");
            form.createProperty("host", OType.STRING);
            form.createProperty("id", OType.STRING);
            form.createProperty("action", OType.EMBEDDEDLIST);
            form.createProperty("schema", OType.EMBEDDEDMAP);
            form.createProperty("form", OType.EMBEDDEDLIST);
            form.createProperty("modelData", OType.EMBEDDEDMAP);
            form.createProperty("createDate", OType.DATETIME);
            form.createProperty("createUserRid", OType.LINK);
            form.createProperty("createUserId", OType.STRING);
            form.createProperty("updateDate", OType.DATETIME);
            form.createProperty("updateUserRid", OType.LINK);
            form.createProperty("updateUserId", OType.STRING);
            schema.save();

            form.createIndex("Form.id", OClass.INDEX_TYPE.UNIQUE, "id");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initPage() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Page")) {
                for (ODocument doc : db.browseClass("Page")) {
                    doc.delete();
                }
                schema.dropClass("Page");
            }
            OClass page = schema.createClass("Page");
            page.createProperty("host", OType.STRING);
            page.createProperty("id", OType.STRING);
            page.createProperty("content", OType.STRING);
            page.createProperty("createDate", OType.DATETIME);
            page.createProperty("createUserRid", OType.LINK);
            page.createProperty("createUserId", OType.STRING);
            page.createProperty("updateDate", OType.DATETIME);
            page.createProperty("updateUserRid", OType.LINK);
            page.createProperty("updateUserId", OType.STRING);
            page.createIndex("Page.id", OClass.INDEX_TYPE.UNIQUE, "id");

            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    /*
    public static void initFeed() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Feed")) {
                for (ODocument doc : db.browseClass("Feed")) {
                    doc.delete();
                }
                schema.dropClass("Feed");
            }
            OClass feed = schema.createClass("Feed");
            feed.createProperty("requestId", OType.STRING);
            feed.createProperty("dataFeedType", OType.STRING);
            // TODO add rating system.
            feed.createProperty("processTypeCd", OType.STRING);
            feed.createProperty("processSubtypeCd", OType.STRING);
            feed.createProperty("createDate", OType.DATETIME);
            feed.createProperty("updateDate", OType.DATETIME);
            feed.createProperty("createUserId", OType.STRING);
            feed.createProperty("updateUserId", OType.STRING);

            feed.createIndex("Feed.requestId", OClass.INDEX_TYPE.UNIQUE, "requestId");
            feed.createIndex("Feed.dataFeedType", OClass.INDEX_TYPE.NOTUNIQUE, "dataFeedType");
            feed.createIndex("Feed.createDate", OClass.INDEX_TYPE.NOTUNIQUE, "createDate");
            feed.createIndex("Feed.updateDate", OClass.INDEX_TYPE.NOTUNIQUE, "updateDate");
            feed.createIndex("Feed.createUserId", OClass.INDEX_TYPE.NOTUNIQUE, "createUserId");
            feed.createIndex("Feed.updateUserId", OClass.INDEX_TYPE.NOTUNIQUE, "updateUserId");
            feed.createIndex("Feed.processTypeCd", OClass.INDEX_TYPE.NOTUNIQUE, "processTypeCd");
            feed.createIndex("Feed.processSubtypeCd", OClass.INDEX_TYPE.NOTUNIQUE, "processSubtypeCd");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initBlog() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Blog")) {
                for (ODocument doc : db.browseClass("Blog")) {
                    doc.delete();
                }
                schema.dropClass("Blog");
            }
            OClass blog = schema.createClass("Blog");
            blog.createProperty("host", OType.STRING);
            blog.createProperty("id", OType.STRING);
            blog.createProperty("desc", OType.STRING);
            blog.createProperty("parent", OType.LINK);
            blog.createProperty("children", OType.LINKSET);
            blog.createProperty("attributes", OType.EMBEDDEDMAP);
            blog.createProperty("upUsers", OType.LINKSET);
            blog.createProperty("downUsers", OType.LINKSET);
            blog.createProperty("rank", OType.INTEGER);
            blog.createProperty("posts", OType.LINKLIST);
            blog.createProperty("createDate", OType.DATETIME);
            blog.createProperty("createUserId", OType.STRING);
            blog.createProperty("updateDate", OType.DATETIME);
            blog.createProperty("updateUserId", OType.STRING);
            schema.save();
            blog.createIndex("blogHostIdIdx", OClass.INDEX_TYPE.UNIQUE, "host", "id");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initForum() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Forum")) {
                for (ODocument doc : db.browseClass("Forum")) {
                    doc.delete();
                }
                schema.dropClass("Forum");
            }
            OClass forum = schema.createClass("Forum");
            forum.createProperty("host", OType.STRING);
            forum.createProperty("id", OType.STRING);
            forum.createProperty("desc", OType.STRING);
            forum.createProperty("parent", OType.LINK);
            forum.createProperty("children", OType.LINKSET);
            forum.createProperty("attributes", OType.EMBEDDEDMAP);
            forum.createProperty("upUsers", OType.LINKSET);
            forum.createProperty("downUsers", OType.LINKSET);
            forum.createProperty("rank", OType.INTEGER);
            forum.createProperty("posts", OType.LINKLIST);
            forum.createProperty("createDate", OType.DATETIME);
            forum.createProperty("createUserId", OType.STRING);
            forum.createProperty("updateDate", OType.DATETIME);
            forum.createProperty("updateUserId", OType.STRING);
            schema.save();
            forum.createIndex("forumHostIdIdx", OClass.INDEX_TYPE.UNIQUE, "host", "id");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initNews() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("News")) {
                for (ODocument doc : db.browseClass("News")) {
                    doc.delete();
                }
                schema.dropClass("News");
            }
            OClass news = schema.createClass("News");
            news.createProperty("host", OType.STRING);
            news.createProperty("id", OType.STRING);
            news.createProperty("desc", OType.STRING);
            news.createProperty("parent", OType.STRING);
            news.createProperty("children", OType.LINKSET);
            news.createProperty("attributes", OType.EMBEDDEDMAP);
            news.createProperty("upUsers", OType.LINKSET);
            news.createProperty("downUsers", OType.LINKSET);
            news.createProperty("rank", OType.INTEGER);
            news.createProperty("posts", OType.STRING);
            news.createProperty("createDate", OType.DATETIME);
            news.createProperty("createUserRid", OType.STRING);
            news.createProperty("updateDate", OType.DATETIME);
            schema.save();
            news.createIndex("newsHostIdIdx", OClass.INDEX_TYPE.UNIQUE, "host", "id");
            news.createIndex("News.updateDate", OClass.INDEX_TYPE.NOTUNIQUE, "updateDate");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initPost() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Post")) {
                for (ODocument doc : db.browseClass("Post")) {
                    doc.delete();
                }
                schema.dropClass("Post");
            }
            OClass post = schema.createClass("Post");
            post.createProperty("host", OType.STRING);
            post.createProperty("id", OType.STRING);
            post.createProperty("parent", OType.LINK);
            post.createProperty("title", OType.STRING);
            post.createProperty("source", OType.STRING);
            post.createProperty("content", OType.STRING);
            post.createProperty("tags", OType.LINKMAP);
            post.createProperty("upUsers", OType.LINKSET);
            post.createProperty("downUsers", OType.LINKSET);
            post.createProperty("rank", OType.INTEGER);
            post.createProperty("children", OType.LINKLIST);
            post.createProperty("createDate", OType.DATETIME);
            post.createProperty("createUserId", OType.STRING);
            post.createProperty("updateDate", OType.DATETIME);
            schema.save();
            post.createIndex("Post.id", OClass.INDEX_TYPE.UNIQUE, "id");
            post.createIndex("Post.updateDate", OClass.INDEX_TYPE.NOTUNIQUE, "updateDate");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initComment() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Comment")) {
                for (ODocument doc : db.browseClass("Comment")) {
                    doc.delete();
                }
                schema.dropClass("Comment");
            }
            OClass comment = schema.createClass("Comment");
            comment.createProperty("id", OType.STRING);
            comment.createProperty("host", OType.STRING);
            comment.createProperty("parent", OType.LINK);
            comment.createProperty("content", OType.STRING);
            comment.createProperty("tags", OType.LINKMAP);
            comment.createProperty("upUsers", OType.LINKSET);
            comment.createProperty("downUsers", OType.LINKSET);
            comment.createProperty("rank", OType.INTEGER);
            comment.createProperty("children", OType.LINKLIST);
            comment.createProperty("createDate", OType.DATETIME);
            comment.createProperty("createUserId", OType.STRING);
            comment.createProperty("updateDate", OType.DATETIME);
            comment.createProperty("updateUserId", OType.STRING);
            schema.save();
            comment.createIndex("Comment.id", OClass.INDEX_TYPE.UNIQUE, "id");
            comment.createIndex("Comment.updateDate", OClass.INDEX_TYPE.NOTUNIQUE, "updateDate");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initTag() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Tag")) {
                for (ODocument doc : db.browseClass("Tag")) {
                    doc.delete();
                }
                schema.dropClass("Tag");
            }
            OClass tag = schema.createClass("Tag");
            tag.createProperty("host", OType.STRING);
            tag.createProperty("name", OType.STRING);
            tag.createProperty("class", OType.STRING);
            tag.createProperty("links", OType.LINKSET);
            tag.createProperty("createDate", OType.DATETIME);
            tag.createProperty("createUserId", OType.STRING);
            tag.createProperty("updateDate", OType.DATETIME);
            schema.save();
            tag.createIndex("hostNameClassIdx", OClass.INDEX_TYPE.UNIQUE, "host", "name", "class");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initCatalog() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Catalog")) {
                for (ODocument doc : db.browseClass("Catalog")) {
                    doc.delete();
                }
                schema.dropClass("Catalog");
            }
            OClass catalog = schema.createClass("Catalog");
            catalog.createProperty("host", OType.STRING);
            catalog.createProperty("id", OType.STRING);               // unique identifier
            catalog.createProperty("desc", OType.STRING);             // description of catalog
            catalog.createProperty("parent", OType.LINK);             // parent catalog
            catalog.createProperty("children", OType.LINKLIST);       // sub catalog
            catalog.createProperty("attributes", OType.EMBEDDEDMAP);  // attributes that associates with the catalog
            catalog.createProperty("products", OType.LINKLIST);       // products belong to this catalog
            catalog.createProperty("createDate", OType.DATETIME);
            catalog.createProperty("createUserRid", OType.STRING);
            catalog.createProperty("updateDate", OType.DATETIME);
            catalog.createProperty("updateUserRid", OType.STRING);
            schema.save();
            catalog.createIndex("catalogHostIdIdx", OClass.INDEX_TYPE.UNIQUE, "host", "id");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initProduct() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Product")) {
                for (ODocument doc : db.browseClass("Product")) {
                    doc.delete();
                }
                schema.dropClass("Product");
            }
            OClass product = schema.createClass("Product");
            product.createProperty("host", OType.STRING);
            product.createProperty("name", OType.STRING);
            product.createProperty("attributes", OType.EMBEDDEDMAP);
            product.createProperty("upUsers", OType.LINKLIST);
            product.createProperty("downUsers", OType.LINKLIST);
            product.createProperty("comments", OType.LINKLIST);
            product.createProperty("createDate", OType.DATETIME);
            product.createProperty("createUserId", OType.STRING);
            product.createProperty("updateDate", OType.DATETIME);
            schema.save();
            product.createIndex("hostNameIdx", OClass.INDEX_TYPE.UNIQUE, "host", "name");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    */

    /**
     * Client
     */
    /*
    public static void initClient() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Client")) {
                for (ODocument doc : db.browseClass("Client")) {
                    doc.delete();
                }
                schema.dropClass("Client");
            }
            OClass client = schema.createClass("Client");
            // www.networknt.com@Browser www.networknt.com@Android www.networknt.com@iOS
            client.createProperty("id", OType.STRING);
            client.createProperty("type", OType.INTEGER); // 0 - CONFIDENTIAL, 1 - PUBLIC
            client.createProperty("secret", OType.STRING);
            client.createProperty("desc", OType.STRING);
            client.createProperty("createDate", OType.DATETIME);
            client.createProperty("createUserId", OType.STRING);
            client.createProperty("updateDate", OType.DATETIME);
            client.createProperty("updateUserId", OType.STRING);
            client.createIndex("Client.id", OClass.INDEX_TYPE.UNIQUE, "id");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    */

    /**
     * For each rule class you can define a list of rules to be applied on request before
     * the endpoint is called
     *
     */
    public static void initRequestTransform() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("RequestTransform")) {
                for (ODocument doc : db.browseClass("RequestTransform")) {
                    doc.delete();
                }
                schema.dropClass("RequestTransform");
            }
            OClass requestTransform = schema.createClass("RequestTransform");
            requestTransform.createProperty("ruleClass", OType.STRING);
            requestTransform.createProperty("sequence", OType.INTEGER); // execution sequence of each phase.
            requestTransform.createProperty("transformRule", OType.STRING);
            requestTransform.createProperty("transformData", OType.EMBEDDEDMAP);
            requestTransform.createProperty("createDate", OType.DATETIME);
            requestTransform.createProperty("createUserId", OType.STRING);
            requestTransform.createProperty("updateDate", OType.DATETIME);
            requestTransform.createProperty("updateUserId", OType.STRING);
            requestTransform.createIndex("ReqRuleSequenceIdx", OClass.INDEX_TYPE.UNIQUE, "ruleClass", "sequence");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * For each rule class you can define a list of rules to be applied on response after
     * the endpoint is called
     *
     */
    public static void initResponseTransform() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("ResponseTransform")) {
                for (ODocument doc : db.browseClass("ResponseTransform")) {
                    doc.delete();
                }
                schema.dropClass("ResponseTransform");
            }
            OClass responseTransform = schema.createClass("ResponseTransform");
            responseTransform.createProperty("ruleClass", OType.STRING);
            responseTransform.createProperty("sequence", OType.INTEGER); // execution sequence of each phase.
            responseTransform.createProperty("transformRule", OType.STRING);
            responseTransform.createProperty("transformData", OType.EMBEDDEDMAP);
            responseTransform.createProperty("createDate", OType.DATETIME);
            responseTransform.createProperty("createUserId", OType.STRING);
            responseTransform.createProperty("updateDate", OType.DATETIME);
            responseTransform.createProperty("updateUserId", OType.STRING);
            responseTransform.createIndex("ResRuleSequenceIdx", OClass.INDEX_TYPE.UNIQUE, "ruleClass", "sequence");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * access is a piece of info that controls how endpoints are accessed from client
     */
    public static void initAccess() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Access")) {
                for (ODocument doc : db.browseClass("Access")) {
                    doc.delete();
                }
                schema.dropClass("Access");
            }
            OClass access = schema.createClass("Access");
            access.createProperty("ruleClass", OType.STRING);
            access.createProperty("accessLevel", OType.STRING); // C/R/U/CR/CU/RU/CRU
            access.createProperty("clients", OType.EMBEDDEDLIST); // usually host + device. Browser/Andrioid/iOS
            access.createProperty("roles", OType.EMBEDDEDLIST);
            access.createProperty("users", OType.EMBEDDEDLIST);
            access.createProperty("createDate", OType.DATETIME);
            access.createProperty("createUserId", OType.STRING);
            access.createProperty("updateDate", OType.DATETIME);
            access.createProperty("updateUserId", OType.STRING);
            access.createIndex("Access.ruleClass", OClass.INDEX_TYPE.UNIQUE, "ruleClass");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    /*
    public static void initProxy() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Proxy")) {
                for (ODocument doc : db.browseClass("Proxy")) {
                    doc.delete();
                }
                schema.dropClass("Proxy");
            }
            OClass proxy = schema.createClass("Proxy");
            proxy.createProperty("ruleClass", OType.STRING);
            proxy.createProperty("hosts", OType.EMBEDDEDLIST);   // ip and port with rest api for the rule.
            proxy.createProperty("createDate", OType.DATETIME);
            proxy.createProperty("createUserId", OType.STRING);
            proxy.createProperty("updateDate", OType.DATETIME);
            proxy.createProperty("updateUserId", OType.STRING);
            proxy.createIndex("Proxy.ruleClass", OClass.INDEX_TYPE.UNIQUE, "ruleClass");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    */

    public static void initWorkflow() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Process")) {
                for (ODocument doc : db.browseClass("Process")) {
                    doc.delete();
                }
                schema.dropClass("Process");
            }
            OClass process = schema.createClass("Process");
            process.createProperty("host", OType.STRING);
            process.createProperty("app", OType.STRING);
            process.createProperty("type", OType.STRING);
            process.createProperty("subType", OType.STRING);
            process.createProperty("status", OType.STRING);
            process.createProperty("customStatus", OType.STRING);
            process.createProperty("startDate", OType.DATETIME);
            process.createProperty("completeDate", OType.DATETIME);
            process.createProperty("result", OType.STRING);
            process.createProperty("source", OType.STRING);
            process.createProperty("partyId", OType.STRING);
            process.createProperty("partyName", OType.STRING);
            process.createProperty("counterPartyId", OType.STRING);
            process.createProperty("counterPartyName", OType.STRING);
            process.createProperty("transactionId", OType.STRING);
            process.createProperty("transactionName", OType.STRING);
            process.createProperty("transactionDate", OType.DATETIME);
            process.createProperty("amount", OType.DOUBLE);
            process.createProperty("productId", OType.STRING);
            process.createProperty("productName", OType.STRING);
            process.createProperty("productType", OType.STRING);
            process.createProperty("group", OType.STRING);
            process.createProperty("subGroup", OType.STRING);
            process.createProperty("eventStartDate", OType.DATETIME);
            process.createProperty("eventEndDate", OType.DATETIME);
            process.createProperty("eventOtherDate", OType.DATETIME);
            process.createProperty("risk", OType.INTEGER);
            process.createProperty("price", OType.FLOAT);
            process.createProperty("quantity", OType.DECIMAL);
            process.createProperty("currency", OType.STRING);
            process.createProperty("referenceId", OType.STRING);
            process.createProperty("referenceCd", OType.STRING);
            process.createProperty("parentId", OType.LONG);
            process.createProperty("deadline", OType.DATETIME);
            process.createProperty("parentGroup", OType.STRING);
            process.createProperty("owningGroup", OType.STRING);
            process.createProperty("language", OType.STRING);
            process.createProperty("createDate", OType.DATETIME);
            process.createProperty("createUserId", OType.STRING);
            process.createProperty("updateDate", OType.DATETIME);
            process.createProperty("updateUserId", OType.STRING);
            schema.save();

            if (schema.existsClass("Task")) {
                for (ODocument doc : db.browseClass("Task")) {
                    doc.delete();
                }
                schema.dropClass("Task");
            }
            OClass task = schema.createClass("Task");
            task.createProperty("type", OType.STRING);
            task.createProperty("subType", OType.STRING);
            task.createProperty("processId", OType.LINK, process);
            task.createProperty("status", OType.STRING);
            task.createProperty("customStatus", OType.STRING);
            task.createProperty("locked", OType.BOOLEAN);
            task.createProperty("priority", OType.INTEGER);
            task.createProperty("startDate", OType.DATETIME);
            task.createProperty("completeDate", OType.DATETIME);
            task.createProperty("completeUserId", OType.STRING);
            task.createProperty("result", OType.STRING);
            task.createProperty("lockingUserId", OType.STRING);
            task.createProperty("lockingRole", OType.STRING);
            task.createProperty("deadline", OType.DATETIME);
            task.createProperty("createUserId", OType.STRING);
            task.createProperty("updateDate", OType.DATETIME);
            task.createProperty("updateUserId", OType.STRING);
            schema.save();

            if (schema.existsClass("Assignment")) {
                for (ODocument doc : db.browseClass("Assignment")) {
                    doc.delete();
                }
                schema.dropClass("Assignment");
            }
            OClass assignment = schema.createClass("Assignment");
            assignment.createProperty("taskId", OType.LINK, task);
            assignment.createProperty("assignedDate", OType.DATETIME);
            assignment.createProperty("role", OType.STRING);
            assignment.createProperty("reason", OType.STRING);
            assignment.createProperty("active", OType.BOOLEAN);
            assignment.createProperty("unassignedDate", OType.DATETIME);
            schema.save();

            if (schema.existsClass("Audit")) {
                for (ODocument doc : db.browseClass("Audit")) {
                    doc.delete();
                }
                schema.dropClass("Audit");
            }
            OClass audit = schema.createClass("Audit");
            audit.createProperty("processId", OType.LINK, process);
            audit.createProperty("userId", OType.STRING);
            audit.createProperty("eventDate", OType.DATETIME);
            audit.createProperty("parameter0", OType.STRING);
            audit.createProperty("parameter1", OType.STRING);
            audit.createProperty("parameter2", OType.STRING);
            audit.createProperty("parameter3", OType.STRING);
            audit.createProperty("parameter4", OType.STRING);
            audit.createProperty("comment", OType.STRING);
            schema.save();

            if (schema.existsClass("Document")) {
                for (ODocument doc : db.browseClass("Document")) {
                    doc.delete();
                }
                schema.dropClass("Document");
            }
            OClass document = schema.createClass("Document");
            document.createProperty("host", OType.STRING);
            document.createProperty("app", OType.STRING);
            document.createProperty("docId", OType.STRING);
            document.createProperty("docName", OType.STRING);
            document.createProperty("docType", OType.STRING);   // D / T
            document.createProperty("checksum", OType.LONG);
            document.createProperty("contentType", OType.STRING);
            document.createProperty("contentLength", OType.STRING);
            document.createProperty("createUserId", OType.STRING);
            document.createProperty("createDate", OType.DATETIME);
            document.createProperty("updateUserId", OType.STRING);
            document.createProperty("updateDate", OType.DATETIME);

            schema.save();

            if (schema.existsClass("ProcessDoc")) {
                for (ODocument doc : db.browseClass("ProcessDoc")) {
                    doc.delete();
                }
                schema.dropClass("ProcessDoc");
            }
            OClass processDoc = schema.createClass("ProcessDoc");
            processDoc.createProperty("processId", OType.LINK, process);
            processDoc.createProperty("docId", OType.LINK, document);
            processDoc.createProperty("sequence", OType.INTEGER);
            processDoc.createProperty("fileName", OType.STRING);
            processDoc.createProperty("docName", OType.STRING);
            processDoc.createProperty("docType", OType.BOOLEAN);
            processDoc.createProperty("createUserId", OType.STRING);
            processDoc.createProperty("createDate", OType.DATETIME);
            schema.save();

            if (schema.existsClass("TaskLock")) {
                for (ODocument doc : db.browseClass("TaskLock")) {
                    doc.delete();
                }
                schema.dropClass("TaskLock");
            }
            OClass taskLock = schema.createClass("TaskLock");
            taskLock.createProperty("taskId", OType.LINK, task);
            taskLock.createProperty("taskStatus", OType.STRING);
            taskLock.createProperty("lockingRole", OType.STRING);
            taskLock.createProperty("lockingUserId", OType.STRING);
            taskLock.createProperty("lockDate", OType.DATETIME);
            taskLock.createProperty("unlockDate", OType.DATETIME);
            schema.save();

            if (schema.existsClass("WorkList")) {
                for (ODocument doc : db.browseClass("WorkList")) {
                    doc.delete();
                }
                schema.dropClass("WorkList");
            }
            OClass workList = schema.createClass("WorkList");
            workList.createProperty("role", OType.STRING);
            workList.createProperty("host", OType.STRING);
            workList.createProperty("app", OType.STRING);
            workList.createProperty("createUserId", OType.STRING);
            workList.createProperty("createDate", OType.DATETIME);
            schema.save();

            if (schema.existsClass("WorkListColumn")) {
                for (ODocument doc : db.browseClass("WorkListColumn")) {
                    doc.delete();
                }
                schema.dropClass("WorkListColumn");
            }
            OClass workListColumn = schema.createClass("WorkListColumn");
            workListColumn.createProperty("workListId", OType.LINK, workList);
            workListColumn.createProperty("sequence", OType.INTEGER);
            workListColumn.createProperty("columnId", OType.STRING);
            workListColumn.createProperty("columnLabel", OType.STRING);
            workListColumn.createProperty("columnValue", OType.STRING);
            schema.save();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public static void initCounter() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (schema.existsClass("Counter")) {
                for (ODocument doc : db.browseClass("Counter")) {
                    doc.delete();
                }
                schema.dropClass("Counter");
            }
            OClass counter = schema.createClass("Counter");
            counter.createProperty("name", OType.STRING);
            counter.createProperty("value", OType.LONG);
            counter.createIndex("Counter.name", OClass.INDEX_TYPE.UNIQUE, "name");
            schema.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    static void refreshDoc() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        db.begin();
        try {
            OSchema schema = db.getMetadata().getSchema();

            if (schema.existsClass("Template")) {
                for (ODocument doc : db.browseClass("Template")) {
                    doc.delete();
                }
            }

            if (schema.existsClass("Form")) {
                for (ODocument doc : db.browseClass("Form")) {
                    doc.delete();
                }
            }
            if (schema.existsClass("MenuItem")) {
                for (ODocument doc : db.browseClass("MenuItem")) {
                    doc.delete();
                }
            }
            if (schema.existsClass("Menu")) {
                for (ODocument doc : db.browseClass("Menu")) {
                    doc.delete();
                }
            }
            if (schema.existsClass("Rule")) {
                for (ODocument doc : db.browseClass("Rule")) {
                    doc.delete();
                }
            }
            if (schema.existsClass("Event")) {
                for (ODocument doc : db.browseClass("Event")) {
                    doc.delete();
                }
            }

            if (schema.existsClass("User")) {
                for (ODocument doc : db.browseClass("User")) {
                    doc.delete();
                }
            }

            if (schema.existsClass("Role")) {
                for (ODocument doc : db.browseClass("Role")) {
                    doc.delete();
                }
            }

            // add two roles.
            ODocument anon = new ODocument(schema.getClass("Role"));
            anon.field("id", "anonymous");
            anon.field("desc", "Anonymous or guest that have readonly access to certain things");
            anon.save();

            ODocument user = new ODocument(schema.getClass("Role"));
            user.field("id", "user");
            user.field("desc", "logged in user who can do certain things");
            user.save();

            ODocument dbAdmin = new ODocument(schema.getClass("Role"));
            dbAdmin.field("id", "dbAdmin");
            dbAdmin.field("desc", "admin database objects for the host");
            dbAdmin.save();

            ODocument hostAdmin = new ODocument(schema.getClass("Role"));
            hostAdmin.field("id", "hostAdmin");
            hostAdmin.field("desc", "admin hosts for the platform");
            hostAdmin.save();

            ODocument userAdmin = new ODocument(schema.getClass("Role"));
            userAdmin.field("id", "userAdmin");
            userAdmin.field("desc", "admin users for the host");
            userAdmin.save();

            ODocument statusAdmin = new ODocument(schema.getClass("Role"));
            statusAdmin.field("id", "statusAdmin");
            statusAdmin.field("desc", "admin status display for the host");
            statusAdmin.save();

            ODocument configAdmin = new ODocument(schema.getClass("Role"));
            configAdmin.field("id", "configAdmin");
            configAdmin.field("desc", "admin config for the host");
            configAdmin.save();

            ODocument formAdmin = new ODocument(schema.getClass("Role"));
            formAdmin.field("id", "formAdmin");
            formAdmin.field("desc", "admin forms for the host");
            formAdmin.save();

            ODocument pageAdmin = new ODocument(schema.getClass("Role"));
            pageAdmin.field("id", "pageAdmin");
            pageAdmin.field("desc", "admin pages for the host");
            pageAdmin.save();

            ODocument ruleAdmin = new ODocument(schema.getClass("Role"));
            ruleAdmin.field("id", "ruleAdmin");
            ruleAdmin.field("desc", "admin rules for the host");
            ruleAdmin.save();

            /*

            ODocument categoryAdmin = new ODocument(schema.getClass("Role"));
            categoryAdmin.field("id", "categoryAdmin");
            categoryAdmin.field("desc", "admin category for the host");
            categoryAdmin.save();

            ODocument prodAdmin = new ODocument(schema.getClass("Role"));
            prodAdmin.field("id", "productAdmin");
            prodAdmin.field("desc", "admin user that can do anything with product");
            prodAdmin.save();


            ODocument blogAdmin = new ODocument(schema.getClass("Role"));
            blogAdmin.field("id", "blogAdmin");
            blogAdmin.field("desc", "admin user that can do anything with blog");
            blogAdmin.save();

            ODocument blogUser = new ODocument(schema.getClass("Role"));
            blogUser.field("id", "blogUser");
            blogUser.field("desc", "user that can post blog and update his own blog");
            blogUser.save();

            ODocument forumAdmin = new ODocument(schema.getClass("Role"));
            forumAdmin.field("id", "forumAdmin");
            forumAdmin.field("desc", "admin user that can do anything with forum");
            forumAdmin.save();
            */

            ODocument menuAdmin = new ODocument(schema.getClass("Role"));
            menuAdmin.field("id", "menuAdmin");
            menuAdmin.field("desc", "admin menus for the host");
            menuAdmin.save();

            ODocument admin = new ODocument(schema.getClass("Role"));
            admin.field("id", "admin");
            admin.field("desc", "admin every thing for the host");
            admin.save();

            ODocument owner = new ODocument(schema.getClass("Role"));
            owner.field("id", "owner");
            owner.field("desc", "owner of the site who can do anything");
            owner.save();

            ODocument betaTester = new ODocument(schema.getClass("Role"));
            betaTester.field("id", "betaTester");
            betaTester.field("desc", "Beta Tester that can be routed to differnt version of API");
            betaTester.save();


            List roles = new ArrayList<String>();
            roles.add("owner");
            roles.add("user");

            ODocument credential1 = new ODocument(schema.getClass("Credential"));
            credential1.field("password", HashUtil.generateStorngPasswordHash(ServiceLocator.getInstance().getOwnerPass()));
            credential1.save();

            ODocument user1 = new ODocument(schema.getClass("User"));
            user1.field("userId", ServiceLocator.getInstance().getOwnerId());
            user1.field("email", ServiceLocator.getInstance().getOwnerEmail());
            user1.field("roles", roles);
            user1.field("credential", credential1);
            user1.field("createDate", new java.util.Date());
            user1.save();


            roles = new ArrayList<String>();
            roles.add("user");
            ODocument credential2 = new ODocument(schema.getClass("Credential"));
            credential2.field("password", HashUtil.generateStorngPasswordHash(ServiceLocator.getInstance().getTestPass())); // hash the password here.
            credential2.save();

            ODocument user2 = new ODocument(schema.getClass("User"));
            user2.field("userId", ServiceLocator.getInstance().getTestId());
            user2.field("host", "www.example.com");
            user2.field("email", ServiceLocator.getInstance().getTestEmail());
            user2.field("roles", roles);
            user2.field("credential", credential2);
            user2.field("createDate", new java.util.Date());
            user2.save();

            ODocument m_accessAdmin = new ODocument(schema.getClass("MenuItem"));
            m_accessAdmin.field("id", "accessAdmin");
            m_accessAdmin.field("label", "Access Admin");
            m_accessAdmin.field("path", "/page/com-networknt-light-v-access-admin-home");
            m_accessAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_accessAdmin.field("createDate", new java.util.Date());
            m_accessAdmin.save();

            ODocument m_proxyAdmin = new ODocument(schema.getClass("MenuItem"));
            m_proxyAdmin.field("id", "proxyAdmin");
            m_proxyAdmin.field("label", "Proxy Admin");
            m_proxyAdmin.field("path", "/page/com-networknt-light-v-proxy-admin-home");
            m_proxyAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_proxyAdmin.field("createDate", new java.util.Date());
            m_proxyAdmin.save();

            ODocument m_hostAdmin = new ODocument(schema.getClass("MenuItem"));
            m_hostAdmin.field("id", "hostAdmin");
            m_hostAdmin.field("label", "Host Admin");
            m_hostAdmin.field("path", "/page/com-networknt-light-v-host-admin-home");
            m_hostAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_hostAdmin.field("createDate", new java.util.Date());
            m_hostAdmin.save();


            ODocument m_pageAdmin = new ODocument(schema.getClass("MenuItem"));
            m_pageAdmin.field("id", "pageAdmin");
            m_pageAdmin.field("label", "Page Admin");
            m_pageAdmin.field("path", "/page/com-networknt-light-v-page-admin-home");
            m_pageAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_pageAdmin.field("createDate", new java.util.Date());
            m_pageAdmin.save();

            ODocument m_formAdmin = new ODocument(schema.getClass("MenuItem"));
            m_formAdmin.field("id", "formAdmin");
            m_formAdmin.field("label", "Form Admin");
            m_formAdmin.field("path", "/page/com-networknt-light-v-form-admin-home");
            m_formAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_formAdmin.field("createDate", new java.util.Date());
            m_formAdmin.save();

            ODocument m_ruleAdmin = new ODocument(schema.getClass("MenuItem"));
            m_ruleAdmin.field("id", "ruleAdmin");
            m_ruleAdmin.field("label", "Rule Admin");
            m_ruleAdmin.field("path", "/page/com-networknt-light-v-rule-admin-home");
            m_ruleAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_ruleAdmin.field("createDate", new java.util.Date());
            m_ruleAdmin.save();

            ODocument m_menuAdmin = new ODocument(schema.getClass("MenuItem"));
            m_menuAdmin.field("id", "menuAdmin");
            m_menuAdmin.field("label", "Menu Admin");
            m_menuAdmin.field("path", "/page/com-networknt-light-v-menu-admin-home");
            m_menuAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_menuAdmin.field("createDate", new java.util.Date());
            m_menuAdmin.save();

            ODocument m_dbAdmin = new ODocument(schema.getClass("MenuItem"));
            m_dbAdmin.field("id", "dbAdmin");
            m_dbAdmin.field("label", "DB Admin");
            m_dbAdmin.field("path", "/page/com-networknt-light-v-db-admin-home");
            m_dbAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_dbAdmin.field("createDate", new java.util.Date());
            m_dbAdmin.save();

            /*
            ODocument m_productAdmin = new ODocument(schema.getClass("MenuItem"));
            m_productAdmin.field("id", "productAdmin");
            m_productAdmin.field("label", "Product Admin");
            m_productAdmin.field("path", "/page/com-networknt-light-v-product-admin-home");
            m_productAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_productAdmin.field("createDate", new java.util.Date());
            m_productAdmin.save();

            ODocument m_forumAdmin = new ODocument(schema.getClass("MenuItem"));
            m_forumAdmin.field("id", "forumAdmin");
            m_forumAdmin.field("label", "Forum Admin");
            m_forumAdmin.field("path", "/page/com-networknt-light-v-forum-admin-home");
            m_forumAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_forumAdmin.field("createDate", new java.util.Date());
            m_forumAdmin.save();

            ODocument m_blogAdmin = new ODocument(schema.getClass("MenuItem"));
            m_blogAdmin.field("id", "blogAdmin");
            m_blogAdmin.field("label", "Blog Admin");
            m_blogAdmin.field("path", "/page/com-networknt-light-v-blog-admin-home");
            m_blogAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_blogAdmin.field("createDate", new java.util.Date());
            m_blogAdmin.save();

            ODocument m_newsAdmin = new ODocument(schema.getClass("MenuItem"));
            m_newsAdmin.field("id", "newsAdmin");
            m_newsAdmin.field("label", "News Admin");
            m_newsAdmin.field("path", "/page/com-networknt-light-v-news-admin-home");
            m_newsAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_newsAdmin.field("createDate", new java.util.Date());
            m_newsAdmin.save();

            */

            ODocument m_userAdmin = new ODocument(schema.getClass("MenuItem"));
            m_userAdmin.field("id", "userAdmin");
            m_userAdmin.field("label", "User Admin");
            m_userAdmin.field("path", "/page/com-networknt-light-v-user-admin-home");
            m_userAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_userAdmin.field("createDate", new java.util.Date());
            m_userAdmin.save();

            ODocument m_roleAdmin = new ODocument(schema.getClass("MenuItem"));
            m_roleAdmin.field("id", "roleAdmin");
            m_roleAdmin.field("label", "Role Admin");
            m_roleAdmin.field("path", "/page/com-networknt-light-v-role-admin-home");
            m_roleAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_roleAdmin.field("createDate", new java.util.Date());
            m_roleAdmin.save();

            List<ODocument> menuItems = new ArrayList<ODocument>();
            menuItems.add(m_ruleAdmin);
            menuItems.add(m_accessAdmin);
            menuItems.add(m_proxyAdmin);
            menuItems.add(m_hostAdmin);
            menuItems.add(m_roleAdmin);
            menuItems.add(m_userAdmin);
            menuItems.add(m_dbAdmin);
            menuItems.add(m_menuAdmin);
            menuItems.add(m_formAdmin);
            menuItems.add(m_pageAdmin);
            //menuItems.add(m_blogAdmin);
            //menuItems.add(m_newsAdmin);
            //menuItems.add(m_forumAdmin);
            //menuItems.add(m_productAdmin);

            ODocument m_admin = new ODocument(schema.getClass("MenuItem"));
            m_admin.field("id", "admin");
            m_admin.field("label", "Admin");
            m_admin.field("path", "/admin");
            m_admin.field("ctrl", "AdminCtrl");
            m_admin.field("left", true);
            m_admin.field("roles", "owner,admin,pageAdmin,formAdmin,ruleAdmin,menuAdmin,dbAdmin,productAdmin,forumAdmin,blogAdmin,newsAdmin,userAdmin,roleAdmin");  // make sure there is no space between ,
            m_admin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_admin.field("createDate", new java.util.Date());
            m_admin.field("menuItems", menuItems);
            m_admin.save();

            ODocument m_logOut = new ODocument(schema.getClass("MenuItem"));
            m_logOut.field("id", "logOut");
            m_logOut.field("label", "Log Out");
            m_logOut.field("path", "/page/com-networknt-light-v-user-logout");
            m_logOut.field("left", false);
            m_logOut.field("roles", "user,admin,owner");
            m_logOut.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_logOut.field("createDate", new java.util.Date());
            m_logOut.save();

            ODocument m_logIn = new ODocument(schema.getClass("MenuItem"));
            m_logIn.field("id", "logIn");
            m_logIn.field("label", "Log In");
            m_logIn.field("path", "/signin");
            m_logIn.field("tpl", "views/form.html");
            m_logIn.field("ctrl", "signinCtrl");
            m_logIn.field("left", false);
            m_logIn.field("roles", "anonymous");
            m_logIn.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_logIn.field("createDate", new java.util.Date());
            m_logIn.save();

            /*
            ODocument m_blog = new ODocument(schema.getClass("MenuItem"));
            m_blog.field("id", "blog");
            m_blog.field("label", "Blog");
            m_blog.field("path", "/page/com-networknt-light-v-blog-home");
            m_blog.field("left", true);
            m_blog.field("roles", "anonymous,user,blogAdmin,admin,owner");
            m_blog.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_blog.field("createDate", new java.util.Date());
            m_blog.save();

            ODocument m_news = new ODocument(schema.getClass("MenuItem"));
            m_news.field("id", "news");
            m_news.field("label", "News");
            m_news.field("path", "/page/com-networknt-light-v-news-home");
            m_news.field("left", true);
            m_news.field("roles", "anonymous,user,newsAdmin,admin,owner");
            m_news.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_news.field("createDate", new java.util.Date());
            m_news.save();

            ODocument m_forum = new ODocument(schema.getClass("MenuItem"));
            m_forum.field("id", "forum");
            m_forum.field("label", "Forum");
            m_forum.field("path", "/page/com-networknt-light-v-forum-home");
            m_forum.field("left", true);
            m_forum.field("roles", "anonymous,user,forumAdmin,admin,owner");
            m_forum.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_forum.field("createDate", new java.util.Date());
            m_forum.save();

            ODocument m_product = new ODocument(schema.getClass("MenuItem"));
            m_product.field("id", "product");
            m_product.field("label", "Product");
            m_product.field("path", "/page/com-networknt-light-v-product-home");
            m_product.field("left", true);
            m_product.field("roles", "anonymous,user,prodAdmin,admin,owner");
            m_product.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_product.field("createDate", new java.util.Date());
            m_product.save();
            */

            ODocument m_signUp = new ODocument(schema.getClass("MenuItem"));
            m_signUp.field("id", "signUp");
            m_signUp.field("label", "Sign Up");
            m_signUp.field("path", "/form/com.networknt.light.user.signup");
            m_signUp.field("tpl", "views/form.html");
            m_signUp.field("ctrl", "FormCtrl");
            m_signUp.field("left", false);
            m_signUp.field("roles", "anonymous");
            m_signUp.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_signUp.field("createDate", new java.util.Date());
            m_signUp.save();

            menuItems = new ArrayList<ODocument>();
            menuItems.add(m_admin);
            menuItems.add(m_logOut);
            menuItems.add(m_logIn);
            menuItems.add(m_signUp);

            ODocument m_injector = new ODocument(schema.getClass("Menu"));
            m_injector.field("host", "injector");
            m_injector.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_injector.field("createDate", new java.util.Date());
            m_injector.field("menuItems", menuItems);
            m_injector.save();

            menuItems = new ArrayList<ODocument>();
            menuItems.add(m_admin);
            //menuItems.add(m_news);
            //menuItems.add(m_blog);
            //menuItems.add(m_forum);
            //menuItems.add(m_product);
            menuItems.add(m_logOut);
            menuItems.add(m_logIn);
            menuItems.add(m_signUp);

            ODocument m_edibleforestgarden = new ODocument(schema.getClass("Menu"));
            m_edibleforestgarden.field("host", "www.edibleforestgarden.ca");
            m_edibleforestgarden.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_edibleforestgarden.field("createDate", new java.util.Date());
            m_edibleforestgarden.field("menuItems", menuItems);
            m_edibleforestgarden.save();

            ODocument m_networknt = new ODocument(schema.getClass("Menu"));
            m_networknt.field("host", "www.networknt.com");
            m_networknt.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_networknt.field("createDate", new java.util.Date());
            m_networknt.field("menuItems", menuItems);
            m_networknt.save();

            ODocument m_example = new ODocument(schema.getClass("Menu"));
            m_example.field("host", "example");
            m_example.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_example.field("createDate", new java.util.Date());
            m_example.field("menuItems", menuItems);
            m_example.save();

            // create rules to bootstrap the installation through event replay.
            // need signIn, replayEvent and impRule to start up.

            ODocument abstractRule = new ODocument(schema.getClass("Rule"));
            abstractRule.field("ruleClass", "com.networknt.light.rule.user.AbstractRule");
            abstractRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule;\n" +
                    "\n" +
                    "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                    "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                    "import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;\n" +
                    "import com.networknt.light.server.DbService;\n" +
                    "import com.networknt.light.util.ServiceLocator;\n" +
                    "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                    "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                    "import com.orientechnologies.orient.core.index.OCompositeKey;\n" +
                    "import com.orientechnologies.orient.core.index.OIndex;\n" +
                    "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                    "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                    "import org.slf4j.Logger;\n" +
                    "import org.slf4j.LoggerFactory;\n" +
                    "\n" +
                    "import java.util.Date;\n" +
                    "import java.util.HashMap;\n" +
                    "import java.util.Map;\n" +
                    "import java.util.concurrent.ConcurrentMap;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by husteve on 10/14/2014.\n" +
                    " */\n" +
                    "public abstract class AbstractRule implements Rule {\n" +
                    "    static final Logger logger = LoggerFactory.getLogger(AbstractRule.class);\n" +
                    "\n" +
                    "    protected ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                    "    public abstract boolean execute (Object ...objects) throws Exception;\n" +
                    "\n" +
                    "    protected ODocument getCategoryByRid(String categoryRid) {\n" +
                    "        Map<String, Object> categoryMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage(\"categoryMap\");\n" +
                    "        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)categoryMap.get(\"cache\");\n" +
                    "        if(cache == null) {\n" +
                    "            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                    "                    .maximumWeightedCapacity(1000)\n" +
                    "                    .build();\n" +
                    "            categoryMap.put(\"cache\", cache);\n" +
                    "        }\n" +
                    "        ODocument category = (ODocument)cache.get(\"categoryRid\");\n" +
                    "        if(category == null) {\n" +
                    "            // TODO warning to increase cache if this happens.\n" +
                    "            category = DbService.getODocumentByRid(categoryRid);\n" +
                    "            // put it into the category cache.\n" +
                    "            if(category != null) {\n" +
                    "                cache.put(categoryRid, category);\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return category;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument getProductByRid(String productRid) {\n" +
                    "        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage(\"productMap\");\n" +
                    "        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get(\"cache\");\n" +
                    "        if(cache == null) {\n" +
                    "            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                    "                    .maximumWeightedCapacity(1000)\n" +
                    "                    .build();\n" +
                    "            productMap.put(\"cache\", cache);\n" +
                    "        }\n" +
                    "        ODocument product = (ODocument)cache.get(\"productRid\");\n" +
                    "        if(product == null) {\n" +
                    "            // TODO warning to increase cache if this happens.\n" +
                    "            product = DbService.getODocumentByRid(productRid);\n" +
                    "            if(product != null) {\n" +
                    "                cache.put(productRid, product);\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return product;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected Map<String, Object> getEventMap(Map<String, Object> inputMap) {\n" +
                    "        Map<String, Object> eventMap = new HashMap<String, Object>();\n" +
                    "        Map<String, Object> payload = (Map<String, Object>)inputMap.get(\"payload\");\n" +
                    "        if(payload != null) {\n" +
                    "            Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                    "            if(user != null)  eventMap.put(\"createUserId\", user.get(\"userId\"));\n" +
                    "        }\n" +
                    "        // IP address is used to identify event owner if user is not logged in.\n" +
                    "        if(inputMap.get(\"ipAddress\") != null) {\n" +
                    "            eventMap.put(\"ipAddress\", inputMap.get(\"ipAddress\"));\n" +
                    "        }\n" +
                    "        if(inputMap.get(\"host\") != null) {\n" +
                    "            eventMap.put(\"host\", inputMap.get(\"host\"));\n" +
                    "        }\n" +
                    "        if(inputMap.get(\"app\") != null) {\n" +
                    "            eventMap.put(\"app\", inputMap.get(\"app\"));\n" +
                    "        }\n" +
                    "        eventMap.put(\"category\", inputMap.get(\"category\"));\n" +
                    "        eventMap.put(\"name\", inputMap.get(\"name\"));\n" +
                    "        eventMap.put(\"createDate\", new java.util.Date());\n" +
                    "        eventMap.put(\"data\", new HashMap<String, Object>());\n" +
                    "        return eventMap;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument getODocumentByHostId(String index, String host, String id) {\n" +
                    "        ODocument doc = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OIndex<?> hostIdIdx = db.getMetadata().getIndexManager().getIndex(index);\n" +
                    "            // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "            OCompositeKey key = new OCompositeKey(host, id);\n" +
                    "            OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);\n" +
                    "            if (oid != null) {\n" +
                    "                doc = (ODocument)oid.getRecord();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return doc;\n" +
                    "    }\n" +
                    "\n" +
                    "    public Map<String, Object> getAccessByRuleClass(String ruleClass) throws Exception {\n" +
                    "        Map<String, Object> access = null;\n" +
                    "        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                    "        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                    "        if(cache == null) {\n" +
                    "            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                    "                    .maximumWeightedCapacity(1000)\n" +
                    "                    .build();\n" +
                    "            accessMap.put(\"cache\", cache);\n" +
                    "            logger.error(\"accessMap cache created =\" + cache);\n" +
                    "        } else {\n" +
                    "            access = (Map<String, Object>)cache.get(ruleClass);\n" +
                    "        }\n" +
                    "        if(access == null) {\n" +
                    "            logger.error(\"access is null, cache =\" + cache);\n" +
                    "            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "            try {\n" +
                    "                OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex(\"Access.ruleClass\");\n" +
                    "                // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "                OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(ruleClass);\n" +
                    "                if (oid != null && oid.getRecord() != null) {\n" +
                    "                    String json = oid.getRecord().toJSON();\n" +
                    "                    access = mapper.readValue(json,\n" +
                    "                            new TypeReference<HashMap<String, Object>>() {\n" +
                    "                            });\n" +
                    "                    cache.put(ruleClass, access);\n" +
                    "                }\n" +
                    "            } catch (Exception e) {\n" +
                    "                logger.error(\"Exception:\", e);\n" +
                    "                throw e;\n" +
                    "            } finally {\n" +
                    "                db.close();\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return access;\n" +
                    "    }\n" +
                    "\n" +
                    "}\n");
            abstractRule.field("createDate", new java.util.Date());
            abstractRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            abstractRule.save();

            ODocument abstractRuleRule = new ODocument(schema.getClass("Rule"));
            abstractRuleRule.field("ruleClass", "com.networknt.light.rule.user.AbstractRuleRule");
            abstractRuleRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.rule;\n" +
                    "\n" +
                    "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                    "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                    "import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;\n" +
                    "import com.networknt.light.rule.AbstractRule;\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "import com.networknt.light.util.ServiceLocator;\n" +
                    "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                    "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                    "import com.orientechnologies.orient.core.index.OIndex;\n" +
                    "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                    "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                    "import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;\n" +
                    "import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;\n" +
                    "import org.slf4j.LoggerFactory;\n" +
                    "\n" +
                    "import java.util.*;\n" +
                    "import java.util.concurrent.ConcurrentMap;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by husteve on 10/8/2014.\n" +
                    " */\n" +
                    "public abstract class AbstractRuleRule extends AbstractRule implements Rule {\n" +
                    "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractRuleRule.class);\n" +
                    "\n" +
                    "    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                    "\n" +
                    "    public abstract boolean execute (Object ...objects) throws Exception;\n" +
                    "\n" +
                    "    protected String getRuleByRuleClass(String ruleClass) {\n" +
                    "        String json = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex(\"Rule.ruleClass\");\n" +
                    "            // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "            OIdentifiable rule = (OIdentifiable) ruleClassIdx.get(ruleClass);\n" +
                    "            if (rule != null && rule.getRecord() != null) {\n" +
                    "                json = ((ODocument) rule.getRecord()).toJSON();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected String addRule(Map<String, Object> data) throws Exception {\n" +
                    "        String json = null;\n" +
                    "        ODocument access = null;\n" +
                    "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                    "        String createUserId = (String)data.get(\"createUserId\");\n" +
                    "        Date createDate = (Date)data.get(\"createDate\");\n" +
                    "        String host = (String)data.get(\"host\");\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        OSchema schema = db.getMetadata().getSchema();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            ODocument rule = new ODocument(schema.getClass(\"Rule\"));\n" +
                    "            rule.field(\"ruleClass\", ruleClass);\n" +
                    "            if(host != null) rule.field(\"host\", host);\n" +
                    "            rule.field(\"sourceCode\", data.get(\"sourceCode\"));\n" +
                    "            rule.field(\"createDate\", createDate);\n" +
                    "            rule.field(\"createUserId\", createUserId);\n" +
                    "            rule.save();\n" +
                    "\n" +
                    "            // For all the newly added rules, the default security access is role based and only\n" +
                    "            // owner can access. For some of the rules, like getForm, getMenu, they are granted\n" +
                    "            // to anyone in the db script. Don't overwrite if access exists for these rules.\n" +
                    "\n" +
                    "            // check if access exists for the ruleClass and add access if not.\n" +
                    "            Map<String, Object> accessMap = getAccessByRuleClass(ruleClass);\n" +
                    "            if(accessMap == null) {\n" +
                    "                access = new ODocument(schema.getClass(\"Access\"));\n" +
                    "                access.field(\"ruleClass\", ruleClass);\n" +
                    "                access.field(\"accessLevel\", \"R\"); // role level access\n" +
                    "                List roles = new ArrayList();\n" +
                    "                roles.add(\"owner\");  // give owner access for all the rules by default.\n" +
                    "                access.field(\"roles\", roles);\n" +
                    "                access.field(\"createDate\", createDate);\n" +
                    "                access.field(\"createUserId\", createUserId);\n" +
                    "                access.save();\n" +
                    "            }\n" +
                    "            db.commit();\n" +
                    "            json  = rule.toJSON();\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        if(access != null) {\n" +
                    "            Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                    "            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                    "            if(cache == null) {\n" +
                    "                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                    "                        .maximumWeightedCapacity(1000)\n" +
                    "                        .build();\n" +
                    "                accessMap.put(\"cache\", cache);\n" +
                    "            }\n" +
                    "            cache.put(ruleClass, mapper.readValue(access.toJSON(),\n" +
                    "                    new TypeReference<HashMap<String, Object>>() {\n" +
                    "                    }));\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected String impRule(Map<String, Object> data) throws Exception {\n" +
                    "        String json = null;\n" +
                    "        ODocument access = null;\n" +
                    "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                    "        String createUserId = (String)data.get(\"createUserId\");\n" +
                    "        String host = (String)data.get(\"host\");\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        OSchema schema = db.getMetadata().getSchema();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            // remove the existing rule if there is.\n" +
                    "            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex(\"Rule.ruleClass\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get(\"ruleClass\"));\n" +
                    "            if (oid != null) {\n" +
                    "                ((ODocument) oid.getRecord()).delete();\n" +
                    "            }\n" +
                    "            // create a new rule\n" +
                    "            ODocument rule = new ODocument(schema.getClass(\"Rule\"));\n" +
                    "            rule.field(\"ruleClass\", ruleClass);\n" +
                    "            if(host != null) rule.field(\"host\", host);\n" +
                    "            rule.field(\"sourceCode\", data.get(\"sourceCode\"));\n" +
                    "            rule.field(\"createDate\", data.get(\"createDate\"));\n" +
                    "            rule.field(\"createUserId\", createUserId);\n" +
                    "            rule.save();\n" +
                    "            // For all the newly added rules, the default security access is role based and only\n" +
                    "            // owner can access. For some of the rules, like getForm, getMenu, they are granted\n" +
                    "            // to anyone in the db script. Don't overwrite if access exists for these rules.\n" +
                    "            // Also, if ruleClass contains \"Abstract\" then its access level should be N.\n" +
                    "\n" +
                    "            // check if access exists for the ruleClass and add access if not.\n" +
                    "            Map<String, Object> accessMap = getAccessByRuleClass(ruleClass);\n" +
                    "            if(accessMap == null) {\n" +
                    "                access = new ODocument(schema.getClass(\"Access\"));\n" +
                    "                access.field(\"ruleClass\", ruleClass);\n" +
                    "                if(ruleClass.contains(\"Abstract\")) {\n" +
                    "                    access.field(\"accessLevel\", \"N\");\n" +
                    "                } else if(ruleClass.endsWith(\"EvRule\")) {\n" +
                    "                    access.field(\"accessLevel\", \"A\"); // event rule can be only called internally.\n" +
                    "                } else {\n" +
                    "                    access.field(\"accessLevel\", \"R\"); // role level access\n" +
                    "                    List roles = new ArrayList();\n" +
                    "                    roles.add(\"owner\");  // give owner access for the rule by default.\n" +
                    "                    access.field(\"roles\", roles);\n" +
                    "                }\n" +
                    "                access.field(\"createDate\", data.get(\"createDate\"));\n" +
                    "                access.field(\"createUserId\", createUserId);\n" +
                    "                access.save();\n" +
                    "            }\n" +
                    "            db.commit();\n" +
                    "            json  = rule.toJSON();\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        if(access != null) {\n" +
                    "            Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                    "            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                    "            if(cache == null) {\n" +
                    "                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                    "                        .maximumWeightedCapacity(1000)\n" +
                    "                        .build();\n" +
                    "                accessMap.put(\"cache\", cache);\n" +
                    "            }\n" +
                    "            cache.put(ruleClass, mapper.readValue(access.toJSON(),\n" +
                    "                    new TypeReference<HashMap<String, Object>>() {\n" +
                    "                    }));\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "\n" +
                    "    protected void updRule(Map<String, Object> data) throws Exception {\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex(\"Rule.ruleClass\");\n" +
                    "            // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get(\"ruleClass\"));\n" +
                    "            if (oid != null && oid.getRecord() != null) {\n" +
                    "                ODocument rule = (ODocument) oid.getRecord();\n" +
                    "                String sourceCode = (String)data.get(\"sourceCode\");\n" +
                    "                if(sourceCode != null && !sourceCode.equals(rule.field(\"sourceCode\"))) {\n" +
                    "                    rule.field(\"sourceCode\", sourceCode);\n" +
                    "                }\n" +
                    "                rule.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                rule.field(\"updateUserId\", data.get(\"updateUserId\"));\n" +
                    "                rule.save();\n" +
                    "            }\n" +
                    "            db.commit();\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    protected void delRule(Map<String, Object> data) throws Exception {\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "\n" +
                    "            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex(\"Rule.ruleClass\");\n" +
                    "            // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get(\"ruleClass\"));\n" +
                    "            if (oid != null && oid.getRecord() != null) {\n" +
                    "                ODocument rule = (ODocument) oid.getRecord();\n" +
                    "                rule.delete();\n" +
                    "            }\n" +
                    "            db.commit();\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    protected String getRules(String host) {\n" +
                    "        String sql = \"SELECT FROM Rule\";\n" +
                    "        if(host != null) {\n" +
                    "            sql = sql + \" WHERE host = '\" + host;\n" +
                    "        }\n" +
                    "        String json = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);\n" +
                    "            List<ODocument> rules = db.command(query).execute();\n" +
                    "            json = OJSONWriter.listToJSON(rules, null);\n" +
                    "        } catch (Exception e) {\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected String getRuleMap(String host) throws Exception {\n" +
                    "        String sql = \"SELECT FROM Rule\";\n" +
                    "        if(host != null) {\n" +
                    "            sql = sql + \" WHERE host = '\" + host;\n" +
                    "        }\n" +
                    "        String json = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);\n" +
                    "            List<ODocument> rules = db.command(query).execute();\n" +
                    "            if(rules != null && rules.size() > 0) {\n" +
                    "                // covert list to map\n" +
                    "                Map<String, String> ruleMap = new HashMap<String, String> ();\n" +
                    "                for(ODocument rule: rules) {\n" +
                    "                    ruleMap.put((String)rule.field(\"ruleClass\"), (String)rule.field(\"sourceCode\"));\n" +
                    "                }\n" +
                    "                json = mapper.writeValueAsString(ruleMap);\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected String getRuleDropdown(String host) {\n" +
                    "        String sql = \"SELECT FROM Rule\";\n" +
                    "        if(host != null) {\n" +
                    "            sql = sql + \" WHERE host = '\" + host + \"' OR host IS NULL\";\n" +
                    "        }\n" +
                    "        String json = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);\n" +
                    "            List<ODocument> rules = db.command(query).execute();\n" +
                    "            if(rules.size() > 0) {\n" +
                    "                List<Map<String, String>> list = new ArrayList<Map<String, String>>();\n" +
                    "                for(ODocument doc: rules) {\n" +
                    "                    Map<String, String> map = new HashMap<String, String>();\n" +
                    "                    String ruleClass = doc.field(\"ruleClass\");\n" +
                    "                    map.put(\"label\", ruleClass);\n" +
                    "                    map.put(\"value\", ruleClass);\n" +
                    "                    list.add(map);\n" +
                    "                }\n" +
                    "                json = mapper.writeValueAsString(list);\n" +
                    "            }\n" +
                    "\n" +
                    "        } catch (Exception e) {\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            //throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "}\n");
            abstractRuleRule.field("createDate", new java.util.Date());
            abstractRuleRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            abstractRuleRule.save();

            ODocument abstractUserRule = new ODocument(schema.getClass("Rule"));
            abstractUserRule.field("ruleClass", "com.networknt.light.rule.user.AbstractUserRule");
            abstractUserRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.user;\n" +
                    "\n" +
                    "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                    "import com.networknt.light.rule.AbstractRule;\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "import com.networknt.light.server.DbService;\n" +
                    "import com.networknt.light.util.HashUtil;\n" +
                    "import com.networknt.light.util.JwtUtil;\n" +
                    "import com.networknt.light.util.ServiceLocator;\n" +
                    "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                    "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                    "import com.orientechnologies.orient.core.index.OIndex;\n" +
                    "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                    "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                    "import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;\n" +
                    "import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;\n" +
                    "import org.slf4j.LoggerFactory;\n" +
                    "\n" +
                    "import java.util.*;\n" +
                    "import java.util.regex.Matcher;\n" +
                    "import java.util.regex.Pattern;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by steve on 9/23/2014.\n" +
                    " */\n" +
                    "public abstract class AbstractUserRule extends AbstractRule implements Rule {\n" +
                    "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractUserRule.class);\n" +
                    "\n" +
                    "    public static final String EMAIL_PATTERN = \"^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@\"\n" +
                    "        + \"[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$\";\n" +
                    "    Pattern pattern = Pattern.compile(EMAIL_PATTERN);\n" +
                    "\n" +
                    "    public abstract boolean execute (Object ...objects) throws Exception;\n" +
                    "    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                    "\n" +
                    "    protected boolean isUserInDbByEmail(String email) {\n" +
                    "        boolean userInDb = false;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OIndex<?> emailIdx = db.getMetadata().getIndexManager().getIndex(\"User.email\");\n" +
                    "            // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "            userInDb = emailIdx.contains(email);\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return userInDb;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected boolean isUserInDbByUserId(String userId) {\n" +
                    "        boolean userInDb = false;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            // this is a unique index, so it retrieves a OIdentifiable\n" +
                    "            userInDb = userIdIdx.contains(userId);\n" +
                    "\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return userInDb;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument getUserByUserId(String userId) {\n" +
                    "        ODocument user = null;\n" +
                    "        StringBuilder sb = new StringBuilder(\"SELECT FROM User WHERE userId = '\");\n" +
                    "        sb.append(userId).append(\"'\");\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sb.toString());\n" +
                    "            List<ODocument> list = db.command(query.setFetchPlan(\"*:-1\")).execute();\n" +
                    "            if(list.size() > 0) {\n" +
                    "                user = list.get(0);\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument getUserByEmail(String email) {\n" +
                    "        ODocument user = null;\n" +
                    "        StringBuilder sb = new StringBuilder(\"SELECT FROM User WHERE email = '\");\n" +
                    "        sb.append(email).append(\"' FETCHPLAN credential:1\");\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sb.toString());\n" +
                    "            List<ODocument> list = db.command(query).execute();\n" +
                    "            if(list.size() > 0) {\n" +
                    "                user = list.get(0);\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument addUser(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument user = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OSchema schema = db.getMetadata().getSchema();\n" +
                    "            ODocument credential = new ODocument(schema.getClass(\"Credential\"));\n" +
                    "            credential.field(\"password\", data.get(\"password\"));\n" +
                    "            credential.save();\n" +
                    "            user = new ODocument(schema.getClass(\"User\"));\n" +
                    "            user.field(\"host\", data.get(\"host\"));\n" +
                    "            user.field(\"userId\", data.get(\"userId\"));\n" +
                    "            user.field(\"email\", data.get(\"email\"));\n" +
                    "            user.field(\"firstName\", data.get(\"firstName\"));\n" +
                    "            user.field(\"lastName\", data.get(\"lastName\"));\n" +
                    "            user.field(\"karma\", 0);\n" +
                    "            List<String> roles = new ArrayList<String>();\n" +
                    "            roles.add(\"user\"); // default role for sign up users, more roles can be added later by admin\n" +
                    "            user.field(\"roles\", roles);\n" +
                    "            user.field(\"credential\", credential);\n" +
                    "            user.field(\"createDate\", new java.util.Date());\n" +
                    "            user.save();\n" +
                    "            db.commit();\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected boolean delUser(Map<String, Object> data) throws Exception {\n" +
                    "        boolean result = false;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                ODocument user = (ODocument) oid.getRecord();\n" +
                    "                ODocument credential = (ODocument)user.field(\"credential\");\n" +
                    "                credential.delete();\n" +
                    "                user.delete();\n" +
                    "                db.commit();\n" +
                    "                result = true;\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return result;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument updPassword(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument credential = null;\n" +
                    "        ODocument user = getUserByUserId((String)data.get(\"userId\"));\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "            user.save();\n" +
                    "            credential = user.field(\"credential\");\n" +
                    "            if (credential != null) {\n" +
                    "                credential.field(\"password\", data.get(\"password\"));\n" +
                    "                credential.save();\n" +
                    "            }\n" +
                    "            db.commit();\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return credential;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument updRole(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument user = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                user = (ODocument) oid.getRecord();\n" +
                    "                user.field(\"roles\", data.get(\"roles\"));\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.field(\"updateUserId\", data.get(\"updateUserId\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument addRole(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument user = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                user = (ODocument) oid.getRecord();\n" +
                    "                List roles = user.field(\"roles\");\n" +
                    "                roles.add((String)data.get(\"role\"));\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.field(\"updateUserId\", data.get(\"updateUserId\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument delRole(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument user = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                user = (ODocument) oid.getRecord();\n" +
                    "                List roles = user.field(\"roles\");\n" +
                    "                roles.remove((String)data.get(\"role\"));\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.field(\"updateUserId\", data.get(\"updateUserId\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument updLockByUserId(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument user = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                user = (ODocument) oid.getRecord();\n" +
                    "                user.field(\"locked\", data.get(\"locked\"));\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.field(\"updateUserId\", data.get(\"updateUserId\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected ODocument updUser(Map<String, Object> data) throws Exception {\n" +
                    "        ODocument user = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                user = (ODocument) oid.getRecord();\n" +
                    "                String firstName = (String)data.get(\"firstName\");\n" +
                    "                if(firstName != null && !firstName.equals(user.field(\"firstName\"))) {\n" +
                    "                    user.field(\"firstName\", firstName);\n" +
                    "                }\n" +
                    "                String lastName = (String)data.get(\"lastName\");\n" +
                    "                if(lastName != null && !lastName.equals(user.field(\"lastName\"))) {\n" +
                    "                    user.field(\"lastName\", lastName);\n" +
                    "                }\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return user;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected void revokeRefreshToken(Map<String, Object> data) throws Exception {\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            if (oid != null) {\n" +
                    "                ODocument user = (ODocument) oid.getRecord();\n" +
                    "                ODocument credential = (ODocument)user.field(\"credential\");\n" +
                    "                if(credential != null) {\n" +
                    "                    // remove hostRefreshTokens map here. That means all the refresh token will be\n" +
                    "                    // removed for the user even the token is for other hosts.\n" +
                    "                    credential.removeField(\"hostRefreshTokens\");\n" +
                    "                    credential.save();\n" +
                    "                    db.commit();\n" +
                    "                }\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            logger.error(\"Exception:\", e);\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "\n" +
                    "    protected void signIn(Map<String, Object> data) throws Exception {\n" +
                    "        String hashedRefreshToken = (String)data.get(\"hashedRefreshToken\");\n" +
                    "        if(hashedRefreshToken != null) {\n" +
                    "            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "            try {\n" +
                    "                db.begin();\n" +
                    "                OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "                OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "                if (oid != null) {\n" +
                    "                    ODocument user = (ODocument) oid.getRecord();\n" +
                    "                    ODocument credential = (ODocument)user.field(\"credential\");\n" +
                    "                    if(credential != null) {\n" +
                    "                        String host = (String)data.get(\"host\");\n" +
                    "                        // get hostRefreshTokens map here.\n" +
                    "                        Map hostRefreshTokens = credential.field(\"hostRefreshTokens\");\n" +
                    "                        if(hostRefreshTokens != null) {\n" +
                    "                            // logged in before, check if logged in from the host.\n" +
                    "                            List<String> refreshTokens = (List)hostRefreshTokens.get(host);\n" +
                    "                            if(refreshTokens != null) {\n" +
                    "                                // max refresh tokens for user is 10. max 10 devices.\n" +
                    "                                if(refreshTokens.size() >= 10) {\n" +
                    "                                    refreshTokens.remove(0);\n" +
                    "                                }\n" +
                    "                                refreshTokens.add(hashedRefreshToken);\n" +
                    "                            } else {\n" +
                    "                                refreshTokens = new ArrayList<String>();\n" +
                    "                                refreshTokens.add(hashedRefreshToken);\n" +
                    "                                hostRefreshTokens.put(host, refreshTokens);\n" +
                    "                            }\n" +
                    "\n" +
                    "                        } else {\n" +
                    "                            // never logged in, create the map.\n" +
                    "                            hostRefreshTokens = new HashMap<String, List<String>>();\n" +
                    "                            List<String> refreshTokens = new ArrayList<String>();\n" +
                    "                            refreshTokens.add(hashedRefreshToken);\n" +
                    "                            hostRefreshTokens.put(host, refreshTokens);\n" +
                    "                            credential.field(\"hostRefreshTokens\", hostRefreshTokens);\n" +
                    "                        }\n" +
                    "                        credential.save();\n" +
                    "                        db.commit();\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            } catch (Exception e) {\n" +
                    "                db.rollback();\n" +
                    "                logger.error(\"Exception:\", e);\n" +
                    "                throw e;\n" +
                    "            } finally {\n" +
                    "                db.close();\n" +
                    "            }\n" +
                    "        } else {\n" +
                    "            logger.debug(\"There is no hashedRefreshToken as user didn't select remember me. Do nothing\");\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    protected void logOut(Map<String, Object> data) throws Exception {\n" +
                    "        String refreshToken = (String)data.get(\"refreshToken\");\n" +
                    "        if(refreshToken != null) {\n" +
                    "            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "            try {\n" +
                    "                db.begin();\n" +
                    "                OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "                OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "                if (oid != null) {\n" +
                    "                    ODocument user = (ODocument) oid.getRecord();\n" +
                    "                    ODocument credential = (ODocument)user.field(\"credential\");\n" +
                    "                    if(credential != null) {\n" +
                    "                        // now remove the refresh token\n" +
                    "                        String host = (String)data.get(\"host\");\n" +
                    "                        logger.debug(\"logOut to remove refreshToken {} from host {}\" , refreshToken, host);\n" +
                    "                        Map hostRefreshTokens = credential.field(\"hostRefreshTokens\");\n" +
                    "                        if(hostRefreshTokens != null) {\n" +
                    "                            // logged in before, check if logged in from the host.\n" +
                    "                            List<String> refreshTokens = (List)hostRefreshTokens.get(host);\n" +
                    "                            if(refreshTokens != null) {\n" +
                    "                                String hashedRefreshToken = HashUtil.md5(refreshToken);\n" +
                    "                                refreshTokens.remove(hashedRefreshToken);\n" +
                    "                            }\n" +
                    "                        } else {\n" +
                    "                            logger.error(\"There is no refresh tokens\");\n" +
                    "                        }\n" +
                    "                        credential.save();\n" +
                    "                        db.commit();\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            } catch (Exception e) {\n" +
                    "                db.rollback();\n" +
                    "                e.printStackTrace();\n" +
                    "                throw e;\n" +
                    "            } finally {\n" +
                    "                db.close();\n" +
                    "            }\n" +
                    "        } else {\n" +
                    "            logger.debug(\"There is no hashedRefreshToken as user didn't pass in refresh token when logging out. Do nothing\");\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    boolean checkRefreshToken(ODocument credential, String host, String refreshToken) throws Exception {\n" +
                    "        boolean result = false;\n" +
                    "        if(credential != null && refreshToken != null) {\n" +
                    "            Map hostRefreshTokens = credential.field(\"hostRefreshTokens\");\n" +
                    "            if(hostRefreshTokens != null) {\n" +
                    "                List<String> refreshTokens = (List)hostRefreshTokens.get(host);\n" +
                    "                if(refreshTokens != null) {\n" +
                    "                    String hashedRefreshToken = HashUtil.md5(refreshToken);\n" +
                    "                    for(String token: refreshTokens) {\n" +
                    "                        if(hashedRefreshToken.equals(token)) {\n" +
                    "                            result = true;\n" +
                    "                            break;\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            } else {\n" +
                    "                logger.error(\"There is no refresh tokens\");\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return result;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected void upVoteUser(Map<String, Object> data) {\n" +
                    "        ODocument user = null;\n" +
                    "        ODocument voteUser = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable userOid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            OIdentifiable voteUserOid = (OIdentifiable)userIdIdx.get((String)data.get(\"voteUserId\"));\n" +
                    "            if (userOid != null && voteUserOid != null) {\n" +
                    "                user = (ODocument) userOid.getRecord();\n" +
                    "                voteUser = (ODocument)voteUserOid.getRecord();\n" +
                    "                Set upSet = user.field(\"upUsers\");\n" +
                    "                if(upSet == null) {\n" +
                    "                    upSet = new HashSet<String>();\n" +
                    "                    upSet.add(voteUser);\n" +
                    "                    user.field(\"upUsers\", upSet);\n" +
                    "                } else {\n" +
                    "                    upSet.add(voteUser);\n" +
                    "                }\n" +
                    "                Set downSet = user.field(\"downUsers\");\n" +
                    "                if(downSet != null) {\n" +
                    "                    downSet.remove(voteUser);\n" +
                    "                }\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    protected void downVoteUser(Map<String, Object> data) {\n" +
                    "        ODocument user = null;\n" +
                    "        ODocument voteUser = null;\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            db.begin();\n" +
                    "            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex(\"User.userId\");\n" +
                    "            OIdentifiable userOid = (OIdentifiable) userIdIdx.get((String)data.get(\"userId\"));\n" +
                    "            OIdentifiable voteUserOid = (OIdentifiable)userIdIdx.get((String)data.get(\"voteUserId\"));\n" +
                    "            if (userOid != null && voteUserOid != null) {\n" +
                    "                user = (ODocument) userOid.getRecord();\n" +
                    "                voteUser = (ODocument)voteUserOid.getRecord();\n" +
                    "                Set downSet = user.field(\"downUsers\");\n" +
                    "                if(downSet == null) {\n" +
                    "                    downSet = new HashSet<String>();\n" +
                    "                    downSet.add(voteUser);\n" +
                    "                    user.field(\"downUsers\", downSet);\n" +
                    "                } else {\n" +
                    "                    downSet.add(voteUser);\n" +
                    "                }\n" +
                    "                Set upSet = user.field(\"upUsers\");\n" +
                    "                if(upSet != null) {\n" +
                    "                    upSet.remove(voteUser);\n" +
                    "                }\n" +
                    "                user.field(\"updateDate\", data.get(\"updateDate\"));\n" +
                    "                user.save();\n" +
                    "                db.commit();\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            db.rollback();\n" +
                    "            e.printStackTrace();\n" +
                    "            throw e;\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    // TODO refactor it to be generic. table name as part of the criteria? or a parameter?\n" +
                    "    protected long getTotalNumberUserFromDb(Map<String, Object> criteria) {\n" +
                    "        long total = 0;\n" +
                    "        StringBuilder sql = new StringBuilder(\"SELECT COUNT(*) as count FROM User\");\n" +
                    "\n" +
                    "        String whereClause = DbService.getWhereClause(criteria);\n" +
                    "        if(whereClause != null && whereClause.length() > 0) {\n" +
                    "            sql.append(whereClause);\n" +
                    "        }\n" +
                    "\n" +
                    "        System.out.println(\"sql=\" + sql);\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            total = ((ODocument)db.query(new OSQLSynchQuery<ODocument>(sql.toString())).get(0)).field(\"count\");\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return total;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected List<String> getRoles() {\n" +
                    "        List<String> roles = null;\n" +
                    "        String sql = \"SELECT id FROM Role\";\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);\n" +
                    "            List<ODocument> list = db.command(query).execute();\n" +
                    "            if(list.size() > 0) {\n" +
                    "                roles = new ArrayList<String>();\n" +
                    "                for(ODocument doc: list) {\n" +
                    "                    roles.add((String)doc.field(\"id\"));\n" +
                    "                }\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return roles;\n" +
                    "    }\n" +
                    "\n" +
                    "    protected String getUserFromDb(Map<String, Object> criteria) {\n" +
                    "        String json = null;\n" +
                    "        StringBuilder sql = new StringBuilder(\"SELECT FROM User \");\n" +
                    "        String whereClause = DbService.getWhereClause(criteria);\n" +
                    "        if(whereClause != null && whereClause.length() > 0) {\n" +
                    "            sql.append(whereClause);\n" +
                    "        }\n" +
                    "\n" +
                    "        String sortedBy = (String)criteria.get(\"sortedBy\");\n" +
                    "        String sortDir = (String)criteria.get(\"sortDir\");\n" +
                    "        if(sortedBy != null) {\n" +
                    "            sql.append(\" ORDER BY \").append(sortedBy);\n" +
                    "            if(sortDir != null) {\n" +
                    "                sql.append(\" \").append(sortDir);\n" +
                    "            }\n" +
                    "        }\n" +
                    "        Integer pageSize = (Integer)criteria.get(\"pageSize\");\n" +
                    "        Integer pageNo = (Integer)criteria.get(\"pageNo\");\n" +
                    "        if(pageNo != null && pageSize != null) {\n" +
                    "            sql.append(\" SKIP \").append((pageNo - 1) * pageSize);\n" +
                    "            sql.append(\" LIMIT \").append(pageSize);\n" +
                    "        }\n" +
                    "        System.out.println(\"sql=\" + sql);\n" +
                    "        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();\n" +
                    "        try {\n" +
                    "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());\n" +
                    "            List<ODocument> list = db.command(query).execute();\n" +
                    "            if(list.size() > 0) {\n" +
                    "                json = OJSONWriter.listToJSON(list, null);\n" +
                    "            }\n" +
                    "        } catch (Exception e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        } finally {\n" +
                    "            db.close();\n" +
                    "        }\n" +
                    "        return json;\n" +
                    "    }\n" +
                    "\n" +
                    "    boolean isEmail(String userIdEmail) {\n" +
                    "        Matcher matcher = pattern.matcher(userIdEmail);\n" +
                    "        return matcher.matches();\n" +
                    "    }\n" +
                    "\n" +
                    "    String generateToken(ODocument user, String clientId) throws Exception {\n" +
                    "        Map<String, Object> jwtMap = new LinkedHashMap<String, Object>();\n" +
                    "        jwtMap.put(\"userId\", user.field(\"userId\"));\n" +
                    "        jwtMap.put(\"clientId\", clientId);\n" +
                    "        jwtMap.put(\"roles\", user.field(\"roles\"));\n" +
                    "        return JwtUtil.getJwt(jwtMap);\n" +
                    "    }\n" +
                    "\n" +
                    "    boolean checkPassword(ODocument user, String inputPassword) throws Exception {\n" +
                    "        ODocument credential = (ODocument)user.field(\"credential\");\n" +
                    "        String storedPassword = (String) credential.field(\"password\");\n" +
                    "        return HashUtil.validatePassword(inputPassword, storedPassword);\n" +
                    "    }\n" +
                    "\n" +
                    "}\n");
            abstractUserRule.field("createDate", new java.util.Date());
            abstractUserRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            abstractUserRule.save();

            ODocument signinUserRule = new ODocument(schema.getClass("Rule"));
            signinUserRule.field("ruleClass", "com.networknt.light.rule.user.SignInUserRule");
            signinUserRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.user;\n" +
                    "\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "import com.networknt.light.util.HashUtil;\n" +
                    "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                    "\n" +
                    "import java.util.HashMap;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by steve on 14/09/14.\n" +
                    " *\n" +
                    " */\n" +
                    "public class SignInUserRule extends AbstractUserRule implements Rule {\n" +
                    "    public boolean execute (Object ...objects) throws Exception {\n" +
                    "        Map<String, Object> inputMap = (Map<String, Object>) objects[0];\n" +
                    "        Map<String, Object> data = (Map<String, Object>) inputMap.get(\"data\");\n" +
                    "        String userIdEmail = (String) data.get(\"userIdEmail\");\n" +
                    "        String inputPassword = (String) data.get(\"password\");\n" +
                    "        Boolean rememberMe = (Boolean)data.get(\"rememberMe\");\n" +
                    "        String clientId = (String)data.get(\"clientId\");\n" +
                    "        String error = null;\n" +
                    "        // check if clientId is passed in.\n" +
                    "        if(clientId == null || clientId.trim().length() == 0) {\n" +
                    "            error = \"ClientId is required\";\n" +
                    "            inputMap.put(\"responseCode\", 400);\n" +
                    "        } else {\n" +
                    "            ODocument user = null;\n" +
                    "            if(isEmail(userIdEmail)) {\n" +
                    "                user = getUserByEmail(userIdEmail);\n" +
                    "            } else {\n" +
                    "                user = getUserByUserId(userIdEmail);\n" +
                    "            }\n" +
                    "            if(user != null) {\n" +
                    "                if(checkPassword(user, inputPassword)) {\n" +
                    "                    String jwt = generateToken(user, clientId);\n" +
                    "                    if(jwt != null) {\n" +
                    "                        Map eventMap = getEventMap(inputMap);\n" +
                    "                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get(\"data\");\n" +
                    "                        inputMap.put(\"eventMap\", eventMap);\n" +
                    "                        Map<String, String> tokens = new HashMap<String, String>();\n" +
                    "                        tokens.put(\"accessToken\", jwt);\n" +
                    "                        if(rememberMe != null && rememberMe) {\n" +
                    "                            // generate refreshToken\n" +
                    "                            String refreshToken = HashUtil.generateUUID();\n" +
                    "                            tokens.put(\"refreshToken\", refreshToken);\n" +
                    "                            String hashedRefreshToken = HashUtil.md5(refreshToken);\n" +
                    "                            eventData.put(\"hashedRefreshToken\", hashedRefreshToken);\n" +
                    "                        }\n" +
                    "                        inputMap.put(\"result\", mapper.writeValueAsString(tokens));\n" +
                    "                        eventData.put(\"userId\", user.field(\"userId\"));\n" +
                    "                        eventData.put(\"host\", data.get(\"host\"));  // add host as refreshToken will be associate with host.\n" +
                    "                        eventData.put(\"logInDate\", new java.util.Date());\n" +
                    "                    }\n" +
                    "                } else {\n" +
                    "                    error = \"Invalid password\";\n" +
                    "                    inputMap.put(\"responseCode\", 400);\n" +
                    "                }\n" +
                    "            } else {\n" +
                    "                error = \"Invalid userId or email\";\n" +
                    "                inputMap.put(\"responseCode\", 400);\n" +
                    "            }\n" +
                    "        }\n" +
                    "        if(error != null) {\n" +
                    "            inputMap.put(\"error\", error);\n" +
                    "            return false;\n" +
                    "        } else {\n" +
                    "            return true;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            signinUserRule.field("createDate", new java.util.Date());
            signinUserRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            signinUserRule.save();

            ODocument signinUserEvRule = new ODocument(schema.getClass("Rule"));
            signinUserEvRule.field("ruleClass", "com.networknt.light.rule.user.SignInUserEvRule");
            signinUserEvRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.user;\n" +
                    "\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by husteve on 8/28/2014.\n" +
                    " */\n" +
                    "public class SignInUserEvRule extends AbstractUserRule implements Rule {\n" +
                    "    public boolean execute (Object ...objects) throws Exception {\n" +
                    "        Map<String, Object> eventMap = (Map<String, Object>) objects[0];\n" +
                    "        Map<String, Object> data = (Map<String, Object>) eventMap.get(\"data\");\n" +
                    "        signIn(data);\n" +
                    "\n" +
                    "        // TODO update global online user count\n" +
                    "        return true;\n" +
                    "    }\n" +
                    "}\n");
            signinUserEvRule.field("createDate", new java.util.Date());
            signinUserEvRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            signinUserEvRule.save();

            ODocument replayEventRule = new ODocument(schema.getClass("Rule"));
            replayEventRule.field("ruleClass", "com.networknt.light.rule.db.ReplayEventRule");
            replayEventRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.db;\n" +
                    "\n" +
                    "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                    "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "import com.networknt.light.rule.RuleEngine;\n" +
                    "import com.networknt.light.util.ServiceLocator;\n" +
                    "import com.networknt.light.util.Util;\n" +
                    "import org.slf4j.LoggerFactory;\n" +
                    "\n" +
                    "import java.util.HashMap;\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by steve on 14/12/14.\n" +
                    " */\n" +
                    "public class ReplayEventRule implements Rule {\n" +
                    "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReplayEventRule.class);\n" +
                    "    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                    "\n" +
                    "    public boolean execute (Object ...objects) throws Exception {\n" +
                    "        Map<String, Object> inputMap = (Map<String, Object>)objects[0];\n" +
                    "        Map<String, Object> data = (Map<String, Object>)inputMap.get(\"data\");\n" +
                    "        String error = null;\n" +
                    "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                    "        if(payload == null) {\n" +
                    "            error = \"Login is required\";\n" +
                    "            inputMap.put(\"responseCode\", 401);\n" +
                    "        } else {\n" +
                    "            Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                    "            List roles = (List)user.get(\"roles\");\n" +
                    "            if(!roles.contains(\"owner\") && !roles.contains(\"admin\") && !roles.contains(\"dbAdmin\")) {\n" +
                    "                error = \"Role owner or admin or dbAdmin is required to replay events\";\n" +
                    "                inputMap.put(\"responseCode\", 403);\n" +
                    "            } else {\n" +
                    "                String content = (String)data.get(\"content\");\n" +
                    "                // content may contains several events, parse it.\n" +
                    "                List<Map<String, Object>> events = mapper.readValue(content,\n" +
                    "                    new TypeReference<List<HashMap<String, Object>>>() {});\n" +
                    "\n" +
                    "                // replay event one by one.\n" +
                    "                for(Map<String, Object> event: events) {\n" +
                    "                    RuleEngine.getInstance().executeRuleAsync(Util.getEventRuleId(event), event);\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "        if(error != null) {\n" +
                    "            inputMap.put(\"error\", error);\n" +
                    "            return false;\n" +
                    "        } else {\n" +
                    "            return true;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            replayEventRule.field("createDate", new java.util.Date());
            replayEventRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            replayEventRule.save();

            ODocument getRuleMapRule = new ODocument(schema.getClass("Rule"));
            getRuleMapRule.field("ruleClass", "com.networknt.light.rule.rule.GetRuleMapRule");
            getRuleMapRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.rule;\n" +
                    "\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by steve on 14/02/15.\n" +
                    " *\n" +
                    " * This rule is used by the rule:load plugin in maven-plugin project to check if source code\n" +
                    " * has been changed. It returns a map from ruleClass to sourceCode for easy comparison.\n" +
                    " *\n" +
                    " * accessLevel is owner by default\n" +
                    " *\n" +
                    " */\n" +
                    "public class GetRuleMapRule extends AbstractRuleRule implements Rule {\n" +
                    "    public boolean execute (Object ...objects) throws Exception {\n" +
                    "        Map<String, Object> inputMap = (Map<String, Object>) objects[0];\n" +
                    "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                    "        Map<String, Object> user = (Map<String, Object>) payload.get(\"user\");\n" +
                    "        String host = (String) user.get(\"host\");\n" +
                    "        String hostRuleMap = getRuleMap(host);\n" +
                    "        if(hostRuleMap != null) {\n" +
                    "            inputMap.put(\"result\", hostRuleMap);\n" +
                    "            return true;\n" +
                    "        } else {\n" +
                    "            inputMap.put(\"result\", \"No rule can be found.\");\n" +
                    "            inputMap.put(\"responseCode\", 404);\n" +
                    "            return false;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            getRuleMapRule.field("createDate", new java.util.Date());
            getRuleMapRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            getRuleMapRule.save();

            ODocument impRuleRule = new ODocument(schema.getClass("Rule"));
            impRuleRule.field("ruleClass", "com.networknt.light.rule.rule.ImpRuleRule");
            impRuleRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.rule;\n" +
                    "\n" +
                    "import com.networknt.light.rule.AbstractRule;\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "import com.networknt.light.rule.RuleEngine;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by steve on 30/12/14.\n" +
                    " * This is the rule that will be loaded by the db script in initDatabase to bootstrap rule\n" +
                    " * loading for others. Also, it can be used to import rules developed and tested locally from\n" +
                    " * Rule Admin interface.\n" +
                    " *\n" +
                    " * Warning: it will replace any existing rules if Rule Class is the same.\n" +
                    " *\n" +
                    " */\n" +
                    "public class ImpRuleRule extends AbstractRule implements Rule {\n" +
                    "    public boolean execute (Object ...objects) throws Exception {\n" +
                    "        Map<String, Object> inputMap = (Map<String, Object>)objects[0];\n" +
                    "        Map<String, Object> data = (Map<String, Object>)inputMap.get(\"data\");\n" +
                    "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                    "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                    "        String error = null;\n" +
                    "        if(payload == null) {\n" +
                    "            error = \"Login is required\";\n" +
                    "            inputMap.put(\"responseCode\", 401);\n" +
                    "        } else {\n" +
                    "            Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                    "            List roles = (List)user.get(\"roles\");\n" +
                    "            if(!roles.contains(\"owner\") && !roles.contains(\"admin\") && !roles.contains(\"ruleAdmin\")) {\n" +
                    "                error = \"Role owner or admin or ruleAdmin is required to add rule\";\n" +
                    "                inputMap.put(\"responseCode\", 403);\n" +
                    "            } else {\n" +
                    "                String host = (String)user.get(\"host\");\n" +
                    "                if(host != null) {\n" +
                    "                    if(!host.equals(data.get(\"host\"))) {\n" +
                    "                        error = \"User can only import rule from host: \" + host;\n" +
                    "                        inputMap.put(\"responseCode\", 403);\n" +
                    "                    } else {\n" +
                    "                        // make sure the ruleClass contains the host.\n" +
                    "                        if(!ruleClass.contains(host)) {\n" +
                    "                            // you are not allowed to update rule as it is not owned by the host.\n" +
                    "                            error = \"ruleClass is not owned by the host: \" + host;\n" +
                    "                            inputMap.put(\"responseCode\", 403);\n" +
                    "                        } else {\n" +
                    "                            // remove the rule instance from Rule Engine Cache\n" +
                    "                            RuleEngine.getInstance().removeRule(ruleClass);\n" +
                    "\n" +
                    "                            // Won't check if rule exists or not here.\n" +
                    "                            Map eventMap = getEventMap(inputMap);\n" +
                    "                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get(\"data\");\n" +
                    "                            inputMap.put(\"eventMap\", eventMap);\n" +
                    "                            eventData.put(\"host\", host);\n" +
                    "\n" +
                    "                            eventData.put(\"ruleClass\", ruleClass);\n" +
                    "                            eventData.put(\"sourceCode\", data.get(\"sourceCode\"));\n" +
                    "                            eventData.put(\"createDate\", new java.util.Date());\n" +
                    "                            eventData.put(\"createUserId\", user.get(\"userId\"));\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                } else {\n" +
                    "                    // check if access exist for the rule exists or not. If exists, then there is\n" +
                    "                    // remove the rule instance from Rule Engine Cache\n" +
                    "                    RuleEngine.getInstance().removeRule(ruleClass);\n" +
                    "\n" +
                    "                    // This is owner to import rule, notice that no host is passed in.\n" +
                    "                    Map eventMap = getEventMap(inputMap);\n" +
                    "                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get(\"data\");\n" +
                    "                    inputMap.put(\"eventMap\", eventMap);\n" +
                    "\n" +
                    "                    eventData.put(\"ruleClass\", ruleClass);\n" +
                    "                    eventData.put(\"sourceCode\", data.get(\"sourceCode\"));\n" +
                    "                    eventData.put(\"createDate\", new java.util.Date());\n" +
                    "                    eventData.put(\"createUserId\", user.get(\"userId\"));\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "        if(error != null) {\n" +
                    "            inputMap.put(\"error\", error);\n" +
                    "            return false;\n" +
                    "        } else {\n" +
                    "            return true;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            impRuleRule.field("createDate", new java.util.Date());
            impRuleRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            impRuleRule.save();

            ODocument impRuleEvRule = new ODocument(schema.getClass("Rule"));
            impRuleEvRule.field("ruleClass", "com.networknt.light.rule.rule.ImpRuleEvRule");
            impRuleEvRule.field("sourceCode", "/*\n" +
                    " * Copyright 2015 Network New Technologies Inc.\n" +
                    " *\n" +
                    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " * you may not use this file except in compliance with the License.\n" +
                    " * You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " * Unless required by applicable law or agreed to in writing, software\n" +
                    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " * See the License for the specific language governing permissions and\n" +
                    " * limitations under the License.\n" +
                    " */\n" +
                    "\n" +
                    "package com.networknt.light.rule.rule;\n" +
                    "\n" +
                    "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                    "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                    "import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;\n" +
                    "import com.networknt.light.rule.Rule;\n" +
                    "import com.networknt.light.util.ServiceLocator;\n" +
                    "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                    "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                    "import com.orientechnologies.orient.core.index.OIndex;\n" +
                    "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                    "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                    "import org.slf4j.LoggerFactory;\n" +
                    "\n" +
                    "import java.util.*;\n" +
                    "import java.util.concurrent.ConcurrentMap;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by steve on 30/12/14.\n" +
                    " */\n" +
                    "public class ImpRuleEvRule extends AbstractRuleRule implements Rule {\n" +
                    "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImpRuleEvRule.class);\n" +
                    "    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                    "\n" +
                    "    public boolean execute (Object ...objects) throws Exception {\n" +
                    "        Map<String, Object> eventMap = (Map<String, Object>) objects[0];\n" +
                    "        Map<String, Object> data = (Map<String, Object>) eventMap.get(\"data\");\n" +
                    "        impRule(data);\n" +
                    "        return true;\n" +
                    "    }\n" +
                    "\n" +
                    "}\n");
            impRuleEvRule.field("createDate", new java.util.Date());
            impRuleEvRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            impRuleEvRule.save();


            // grant access to certain rules to everybody.
            ODocument accessGetMenuRule = new ODocument(schema.getClass("Access"));
            accessGetMenuRule.field("ruleClass", "com.networknt.light.rule.menu.GetMenuRule");
            accessGetMenuRule.field("accessLevel", "A");
            accessGetMenuRule.field("createDate", new java.util.Date());
            accessGetMenuRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessGetMenuRule.save();

            ODocument accessGetFormRule = new ODocument(schema.getClass("Access"));
            accessGetFormRule.field("ruleClass", "com.networknt.light.rule.form.GetFormRule");
            accessGetFormRule.field("accessLevel", "A");
            accessGetFormRule.field("createDate", new java.util.Date());
            accessGetFormRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessGetFormRule.save();

            ODocument accessLogEventRule = new ODocument(schema.getClass("Access"));
            accessLogEventRule.field("ruleClass", "com.networknt.light.rule.log.LogEventRule");
            accessLogEventRule.field("accessLevel", "A");
            accessLogEventRule.field("createDate", new java.util.Date());
            accessLogEventRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessLogEventRule.save();

            ODocument accessSignInUserRule = new ODocument(schema.getClass("Access"));
            accessSignInUserRule.field("ruleClass", "com.networknt.light.rule.user.SignInUserRule");
            accessSignInUserRule.field("accessLevel", "A");
            accessSignInUserRule.field("createDate", new java.util.Date());
            accessSignInUserRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessSignInUserRule.save();

            ODocument accessGetPageRule = new ODocument(schema.getClass("Access"));
            accessGetPageRule.field("ruleClass", "com.networknt.light.rule.page.GetPageRule");
            accessGetPageRule.field("accessLevel", "A");
            accessGetPageRule.field("createDate", new java.util.Date());
            accessGetPageRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessGetPageRule.save();

            ODocument accessRefreshTokenRule = new ODocument(schema.getClass("Access"));
            accessRefreshTokenRule.field("ruleClass", "com.networknt.light.rule.user.RefreshTokenRule");
            accessRefreshTokenRule.field("accessLevel", "A");
            accessRefreshTokenRule.field("createDate", new java.util.Date());
            accessRefreshTokenRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessRefreshTokenRule.save();

            ODocument accessSignUpUserRule = new ODocument(schema.getClass("Access"));
            accessSignUpUserRule.field("ruleClass", "com.networknt.light.rule.user.SignUpUserRule");
            accessSignUpUserRule.field("accessLevel", "A");
            accessSignUpUserRule.field("createDate", new java.util.Date());
            accessSignUpUserRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessSignUpUserRule.save();


            ODocument accessGetRoleDropdownRule = new ODocument(schema.getClass("Access"));
            accessGetRoleDropdownRule.field("ruleClass", "com.networknt.light.rule.role.GetRoleDropdownRule");
            accessGetRoleDropdownRule.field("accessLevel", "R");
            roles = new ArrayList<String>();
            roles.add("user");
            accessGetRoleDropdownRule.field("roles", roles);
            accessGetRoleDropdownRule.field("createDate", new java.util.Date());
            accessGetRoleDropdownRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessGetRoleDropdownRule.save();

            ODocument accessGetClientDropdownRule = new ODocument(schema.getClass("Access"));
            accessGetClientDropdownRule.field("ruleClass", "com.networknt.light.rule.client.GetClientDropdownRule");
            accessGetClientDropdownRule.field("accessLevel", "R");
            roles = new ArrayList<String>();
            roles.add("user");
            accessGetClientDropdownRule.field("roles", roles);
            accessGetClientDropdownRule.field("createDate", new java.util.Date());
            accessGetClientDropdownRule.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            accessGetClientDropdownRule.save();


            // create a counter for feed injector requestId
            ODocument requestId = new ODocument(schema.getClass("Counter"));
            requestId.field("name", "injector.requestId");
            requestId.field("value", 1);
            requestId.save();

            // create a counter for class loan number
            ODocument loanNumber = new ODocument(schema.getClass("Counter"));
            loanNumber.field("name", "injector.loanNumber");
            loanNumber.field("value", 1000000000);
            loanNumber.save();

            // create a counter for post id
            ODocument postId = new ODocument(schema.getClass("Counter"));
            postId.field("name", "postId");
            postId.field("value", 10000);
            postId.save();

            // create a counter for comment id
            ODocument commentId = new ODocument(schema.getClass("Counter"));
            commentId.field("name", "commentId");
            commentId.field("value", 10000);
            commentId.save();

            db.commit();

        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.close();
        }

        logger.debug("Done refreshing db");
    }

    static void listClass() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSchema schema = db.getMetadata().getSchema();
            for(OClass oClass: schema.getClasses()) {
                System.out.println(oClass.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}

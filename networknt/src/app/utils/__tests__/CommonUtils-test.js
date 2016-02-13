jest.dontMock('../CommonUtils');
const CommonUtils = require('../CommonUtils');

describe('CommonUtils', () => {

    it('returns the root element with findCategory', () => {
        var category = [
            {
                "@rid": "#34:0",
                "host": "example",
                "description": "All about computer",
                "categoryId": "Computer",
                "createDate": "2015-05-03T01:03:24.217",
                "out_Own": [
                    {
                        "@rid": "#34:1",
                        "host": "example",
                        "description": "All about software",
                        "categoryId": "Software",
                        "createDate": "2015-05-03T01:03:39.835",
                        "in_Own": [
                            "#34:0"
                        ]
                    },
                    {
                        "@rid": "#34:2",
                        "host": "example",
                        "description": "All about hardware",
                        "categoryId": "Hardware",
                        "createDate": "2015-05-03T01:03:53.532",
                        "in_Own": [
                            "#34:0"
                        ]
                    }
                ]
            }
        ];

        var result = CommonUtils.findCategory(category, 'Computer');
        expect(result.categoryId === 'Computer');
    });

    it('returns the out_Own element with findCategory', () => {
        var category = [
            {
                "@rid": "#34:0",
                "host": "example",
                "description": "All about computer",
                "categoryId": "Computer",
                "createDate": "2015-05-03T01:03:24.217",
                "out_Own": [
                    {
                        "@rid": "#34:1",
                        "host": "example",
                        "description": "All about software",
                        "categoryId": "Software",
                        "createDate": "2015-05-03T01:03:39.835",
                        "in_Own": [
                            "#34:0"
                        ]
                    },
                    {
                        "@rid": "#34:2",
                        "host": "example",
                        "description": "All about hardware",
                        "categoryId": "Hardware",
                        "createDate": "2015-05-03T01:03:53.532",
                        "in_Own": [
                            "#34:0"
                        ]
                    }
                ]
            }
        ];

        var result = CommonUtils.findCategory(category, 'Hardware');
        expect(result.categoryId === 'Hardware');
    });

    it('returns the menuItem with findMenuItem', () => {
        var menuItems = [
            {
                "@rid": "#20:0",
                "menuItemId": "user",
                "text": "User",
                "route": "/user",
                "createDate": "2016-01-29T15:18:29.042",
                "out_Own": [
                    {
                        "@rid": "#20:1",
                        "menuItemId": "signUp",
                        "text": "Sign Up",
                        "route": "/signup",
                        "roles": [
                            "anonymous"
                        ],
                        "createDate": "2016-01-29T15:18:29.033"
                    },
                    {
                        "@rid": "#20:2",
                        "menuItemId": "logIn",
                        "text": "Log In",
                        "route": "/login",
                        "roles": [
                            "anonymous"
                        ],
                        "createDate": "2016-01-29T15:18:29.032"
                    },
                    {
                        "@rid": "#20:3",
                        "menuItemId": "logOut",
                        "text": "Log Out",
                        "route": "/logout",
                        "roles": [
                            "user"
                        ],
                        "createDate": "2016-01-29T15:18:29.030"
                    }
                ]
            },
            {
                "@rid": "#20:4",
                "menuItemId": "main",
                "text": "Main",
                "route": "/main",
                "createDate": "2016-01-29T15:18:29.029",
                "out_Own": [
                    {
                        "@rid": "#20:5",
                        "menuItemId": "about",
                        "text": "About",
                        "route": "/about",
                        "roles": [
                            "anonymous"
                        ],
                        "createDate": "2016-01-29T15:18:29.027"
                    },
                    {
                        "@rid": "#20:6",
                        "menuItemId": "profile",
                        "text": "User Profile",
                        "route": "/profile",
                        "roles": [
                            "user"
                        ],
                        "createDate": "2016-01-29T15:18:29.020"
                    },
                    {
                        "@rid": "#20:7",
                        "menuItemId": "admin",
                        "text": "Admin",
                        "route": "/admin",
                        "roles": [
                            "owner",
                            "admin",
                            "pageAdmin",
                            "formAdmin",
                            "ruleAdmin",
                            "menuAdmin",
                            "dbAdmin",
                            "productAdmin",
                            "forumAdmin",
                            "blogAdmin",
                            "newsAdmin",
                            "userAdmin",
                            "roleAdmin"
                        ],
                        "createDate": "2016-01-29T15:18:29.006",
                        "out_Own": [
                            {
                                "@rid": "#20:8",
                                "menuItemId": "roleAdmin",
                                "text": "Role Admin",
                                "route": "/admin/roleAdmin",
                                "createDate": "2016-01-29T15:18:29.003"
                            },
                            {
                                "@rid": "#20:9",
                                "menuItemId": "userAdmin",
                                "text": "User Admin",
                                "route": "/admin/userAdmin",
                                "createDate": "2016-01-29T15:18:28.999"
                            },
                            {
                                "@rid": "#20:10",
                                "menuItemId": "dbAdmin",
                                "text": "DB Admin",
                                "route": "/admin/dbAdmin",
                                "createDate": "2016-01-29T15:18:28.999"
                            },
                            {
                                "@rid": "#20:11",
                                "menuItemId": "menuAdmin",
                                "text": "Menu Admin",
                                "route": "/admin/menuAdmin",
                                "createDate": "2016-01-29T15:18:28.998"
                            },
                            {
                                "@rid": "#20:12",
                                "menuItemId": "ruleAdmin",
                                "text": "Rule Admin",
                                "route": "/admin/ruleAdmin",
                                "createDate": "2016-01-29T15:18:28.997"
                            },
                            {
                                "@rid": "#20:13",
                                "menuItemId": "formAdmin",
                                "text": "Form Admin",
                                "route": "/admin/formAdmin",
                                "createDate": "2016-01-29T15:18:28.996"
                            },
                            {
                                "@rid": "#20:14",
                                "menuItemId": "pageAdmin",
                                "text": "Page Admin",
                                "route": "/admin/pageAdmin",
                                "createDate": "2016-01-29T15:18:28.995"
                            },
                            {
                                "@rid": "#20:15",
                                "menuItemId": "hostAdmin",
                                "text": "Host Admin",
                                "route": "/admin/hostAdmin",
                                "createDate": "2016-01-29T15:18:28.992"
                            },
                            {
                                "@rid": "#20:16",
                                "menuItemId": "accessAdmin",
                                "text": "Access Admin",
                                "route": "/admin/accessAdmin",
                                "createDate": "2016-01-29T15:18:28.979"
                            },
                            {
                                "@rid": "#20:17",
                                "menuItemId": "newsAdmin",
                                "text": "News Admin",
                                "route": "/admin/newsAdmin"
                            },
                            {
                                "@rid": "#20:18",
                                "menuItemId": "forumAdmin",
                                "text": "Forum Admin",
                                "route": "/admin/forumAdmin"
                            },
                            {
                                "@rid": "#20:19",
                                "menuItemId": "blogAdmin",
                                "text": "Blog Admin",
                                "route": "/admin/blogAdmin"
                            },
                            {
                                "@rid": "#20:23",
                                "menuItemId": "productAdmin",
                                "text": "Product Admin",
                                "route": "/admin/productAdmin"
                            },
                            {
                                "@rid": "#20:24",
                                "menuItemId": "catalogAdmin",
                                "text": "Catalog Admin",
                                "route": "/admin/catalogAdmin"
                            }
                        ]
                    },
                    {
                        "@rid": "#20:20",
                        "menuItemId": "news",
                        "text": "News",
                        "route": "/news"
                    },
                    {
                        "@rid": "#20:21",
                        "menuItemId": "forum",
                        "text": "Forum",
                        "route": "/forum"
                    },
                    {
                        "@rid": "#20:22",
                        "menuItemId": "blog",
                        "text": "Blog",
                        "route": "/blog"
                    },
                    {
                        "@rid": "#20:25",
                        "menuItemId": "catalog",
                        "text": "Catalog",
                        "route": "/catalog"
                    }
                ]
            },
            {
                "@rid": "#20:26",
                "menuItemId": "cart",
                "text": "Cart",
                "route": "/cart"
            }
        ];
        var result = CommonUtils.findMenuItem(menuItems, 'cart');
        expect(result.text === 'Cart');
    });

    it('returns true if one of roles in the menuItem', () => {
        var menuItem = {
            "@rid": "#20:7",
            "menuItemId": "admin",
            "text": "Admin",
            "route": "/admin",
            "roles": [
                "owner",
                "admin",
                "pageAdmin",
                "formAdmin",
                "ruleAdmin",
                "menuAdmin",
                "dbAdmin",
                "productAdmin",
                "forumAdmin",
                "blogAdmin",
                "newsAdmin",
                "userAdmin",
                "roleAdmin"
            ],
            "createDate": "2016-01-29T15:18:29.006"
        };

        var roles = ["user", "dbAdmin"];

        var result = CommonUtils.hasMenuAccess(menuItem, roles);
        expect(result === true);
    });

});
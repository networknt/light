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

});
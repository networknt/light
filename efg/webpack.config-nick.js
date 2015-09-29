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
var webpack = require('webpack');
var path = require('path');
var nodeModulesPath = path.resolve(__dirname, 'node_modules');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
//require('es6-promise').polyfill();

var host = "example";
var port = "8001";
var publicPath = "http://" + host + ":" + port + "/";

module.exports = {
    entry: {
        app: ['webpack-dev-server/client?http://' + host + ":" + port, 'webpack/hot/dev-server', './app/index.js'],
        // should mirror package.dependencies
        vendor: ['react',
            'react-router',
            'react-bootstrap',
            'react-router-bootstrap'
        ]
    },
    output: {
        path: __dirname,
        filename: 'dist/bundle.js',
        publicPath: publicPath
    },
    module: {

        loaders: [
            {
                test: /\.js$/,
                loaders: ['react-hot', 'babel'],
                exclude: [nodeModulesPath]
            },
            {
                test: /\.css$/,
                loaders: [
                    "style-loader",
                    "css-loader",
                    "autoprefixer-loader?browsers=last 2 versions"
                ]
            },
            {
                test: /\.scss$/,
                loader: ExtractTextPlugin.extract("style-loader", "css-loader!autoprefixer-loader?browsers=last 2 versions!sass-loader?indentedSyntax=sass&includePaths[]=" + path.resolve(__dirname, "/app/assets/stylesheets_old"))
            },
            {test: /\.less$/, loader: "style!css!less"},

            {test: /\.(png|woff|woff2|eot|ttf|svg)$/, loader: 'url-loader?limit=100000' }
        ]
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin("vendors", "dist/vendor.bundle.js"),
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin(),
        new ExtractTextPlugin('dist/style.css', {
            allChunks: true
        })
    ],

    devServer: {
        proxy:       [{
            // proxy all requests not containing ".hot-update.js"
            // regex is still crappy because JS doesn't have negative lookbehind
            path:   /\/api(.*)/,
            // koa running on 3001 with koa-send and isomorphic react
            target:  'http://example:8080'
        }],
        port: port,
        publicPath: publicPath,
        hot: true,
        historyApiFallback: true
    }
};
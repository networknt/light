const webpack = require('webpack');
const path = require('path');
const buildPath = path.resolve(__dirname, 'src/www');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

const config = {
  //Entry point to the project
  entry: [
    'webpack/hot/dev-server',
    'webpack/hot/only-dev-server',
    path.join(__dirname, '/src/app/app.jsx')
  ],
  //Webpack config options on how to obtain modules
  resolve: {
    //When requiring, you don't need to add these extensions
    extensions: ['', '.js', '.jsx', '.md', '.txt'],
    //Modules will be searched for in these directories
    modulesDirectories: [
      // We need /docs/node_modules to be resolved before /node_modules
      path.resolve(__dirname, 'node_modules'),
      'node_modules',
      path.resolve(__dirname, '../src')
    ]
  },

  //Configuration for dev server
  //devServer: {
  //  contentBase: 'src/www',
  //  devtool: 'eval',
  //  hot: true,
  // inline: true,
  //  port: 3000,
  //},

  devServer: {
    proxy:       [{
      // proxy all requests not containing ".hot-update.js"
      // regex is still crappy because JS doesn't have negative lookbehind
      path:   /\/api(.*)/,
      // koa running on 3001 with koa-send and isomorphic react
      target:  'http://example:8080'
    }],
    contentBase: 'src/www',
    hot: true,
    port: 3000,
    host: '0.0.0.0',
    historyApiFallback: true
  },

  devtool: 'eval',
  //Output file config
  output: {
    path: buildPath,    //Path of output file
    filename: 'app.js'  //Name of output file
  },
  plugins: [
    //Used to include index.html in build folder
    new HtmlWebpackPlugin({
      inject: false,
      template: path.join(__dirname, '/src/www/index.html')
    }),
    //Allows for sync with browser while developing (like BorwserSync)
    new webpack.HotModuleReplacementPlugin(),
    //Allows error warninggs but does not stop compiling. Will remove when eslint is added
    new webpack.NoErrorsPlugin(),
    new ExtractTextPlugin(path.resolve(__dirname,'/style.css') , {
      allChunks: true
    })
  ],
  externals: {
    fs: 'js' // To remove once https://github.com/benjamn/recast/pull/238 is released
  },
  module: {
    //eslint loader
    preLoaders: [
      {
        test: /\.(js|jsx)$/,
        loader: 'eslint-loader',
        include: [path.resolve(__dirname, '../src')],
        exclude: [
          path.resolve(__dirname, '../src/svg-icons'),
          path.resolve(__dirname, '../src/utils/modernizr.custom.js')
        ]
      }
    ],
    //Allow loading of non-es
    loaders: [
      {
        test: /\.jsx$/,
        loaders: [
          'react-hot',
          'babel-loader'
        ],
        exclude: /node_modules/
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },
      {
        test: /\.txt$/,
        loader: 'raw-loader',
        include: path.resolve(__dirname, 'src/app/components/raw-code')
      },
      {
        test: /\.md$/,
        loader: 'raw-loader',
        include: path.resolve(__dirname, 'src/app/components')
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
        loader: ExtractTextPlugin.extract("style-loader", "css-loader!autoprefixer-loader?browsers=last 2 versions!sass-loader?indentedSyntax=sass&includePaths[]=" + path.resolve(__dirname, "/src/www/assets/stylesheets"))
      }
    ]
  },
  eslint: {
    configFile: '../.eslintrc'
  }
};

module.exports = config;

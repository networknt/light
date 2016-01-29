import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableBody from 'material-ui/lib/table/table-body';
import TableFooter from 'material-ui/lib/table/table-footer';
import TableHeader from 'material-ui/lib/table/table-header';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import RaisedButton from 'material-ui/lib/raised-button';
import NewsAdminStore from '../../../stores/NewsAdminStore';
import FormStore from '../../../stores/FormStore';
import NewsActionCreators from '../../../actions/NewsActionCreators';
import FormActionCreators from'../../../actions/FormActionCreators';

var NewsAdminHome = React.createClass({
    displayName: 'NewsAdminHome',

    getInitialState: function() {
        return {
            news: []
        };
    },

    componentWillMount: function() {
        NewsAdminStore.addChangeListener(this._onNewsChange);
        NewsActionCreators.getNews();
    },

    componentWillUnmount: function() {
        NewsAdminStore.removeChangeListener(this._onNewsChange);
    },

    _onNewsChange: function() {
        this.setState({
            news: NewsAdminStore.getNews()
        });
    },

    _onDeleteNews: function(news) {
        console.log("_onDeleteNews", news);
        NewsActionCreators.delNews(news['@rid']);
    },

    _onUpdateNews: function(news) {
        console.log("_onUpdateNews", news);
        let formId = 'com.networknt.light.news.update';
        FormActionCreators.setFormModel(formId, news);
        this.props.history.push('/form/' + formId);
    },

    _onAddNews: function() {
        let formId = 'com.networknt.light.news.add';
        this.props.history.push('/form/' + formId);
    },

    render: function() {
        return (
            <span>
                <Table
                    height={'1080px'}
                    fixedHeader={true}
                    fixedFooter={true}
                    selectable={false}
                    multiSelectable={false}>
                    <TableHeader enableSelectAll={false}>
                        <TableRow>
                            <TableHeaderColumn colSpan="8" tooltip='News' style={{textAlign: 'center'}}>
                                News
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.news.map((news, index) => {
                            let boundDelete = this._onDeleteNews.bind(this, news);
                            let boundUpdate = this._onUpdateNews.bind(this, news);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn>{news.host}</TableRowColumn>
                                    <TableRowColumn><a onClick={boundUpdate}>{news.categoryId}</a></TableRowColumn>
                                    <TableRowColumn>{news.description}</TableRowColumn>
                                    <TableRowColumn>{news.createDate}</TableRowColumn>
                                    <TableRowColumn>{news.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="6" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add News" primary={true} onTouchTap={this._onAddNews} />
                            </TableRowColumn>
                        </TableRow>
                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = NewsAdminHome;

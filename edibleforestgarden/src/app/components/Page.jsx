import React from 'react';
import PageStore from '../stores/PageStore';
import PageActionCreators from '../actions/PageActionCreators';
import RaisedButton from 'material-ui/lib/raised-button';
import CircularProgress from 'material-ui/lib/circular-progress';
import WebAPIUtils from '../utils/WebAPIUtils';
import Markdown from './Markdown';
import utils from 'react-schema-form/lib/utils';


let Page = React.createClass({

    displayName: 'Page',

    getInitialState: function() {
        return {
            content: null
        };
    },

    componentWillMount: function() {
        PageStore.addChangeListener(this._onPageChange);
        PageActionCreators.getPage(this.props.params.pageId);
    },

    componentWillUnmount: function() {
        PageStore.removeChangeListener(this._onPageChange);
    },

    _onPageChange: function() {
        let page = PageStore.getPage(this.props.params.pageId)? PageStore.getPage(this.props.params.pageId): null;
        if(page) {
            this.setState({
                pageId: page.pageId,
                content: page.content,
                rid: page['@rid'],
                createDate: page.createDate
            });
        }
    },

    render: function() {
        if(this.state.content) {
            return (
                <div>
                    <Markdown text={this.state.content}/>
                    <div>Posted by Steve Hu at {this.state.createDate}</div>
                </div>
            )
        } else {
            return (<CircularProgress mode="indeterminate"/>)
        }
    }
});

module.exports = Page;

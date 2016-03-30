import React from 'react';
import ReactDOM from 'react-dom';
import TextField from 'material-ui/lib/TextField';
import Tabs from 'material-ui/lib/tabs/tabs';
import Tab from 'material-ui/lib/tabs/tab';
import Markdown from '../Markdown';

var CommentForm = React.createClass({

    propTypes: {
        parentRid: React.PropTypes.string,
        onAddComment: React.PropTypes.func.isRequired
    },

    getInitialState: function() {
        return {
            value: ''
        };
    },

    onReply: function(e) {
        e.preventDefault();
        if (!this.state.value || this.state.value.length === 0) {
            return;
        }
        this.props.onAddComment(this.props.parentRid, this.state.value);
        this.setState({value: ''});
    },

    onChange: function(e) {
        this.setState({
            value: e.target.value
        })
    },

    render: function() {
        return (
            <div style={{width: '100%'}}>
                <Tabs initialSelectedIndex={0}>
                    <Tab label="Content">
                        <TextField style={{width: '100%'}} hintText="Reply to thread..." multiLine={true} rows={4} onChange={this.onChange} />
                    </Tab>
                    <Tab label="Review">
                        <Markdown text={this.state.value} />
                    </Tab>
                </Tabs>
                <input type="button" onClick={this.onReply} value="Reply" />
            </div>
        );
    }
});

module.exports = CommentForm;

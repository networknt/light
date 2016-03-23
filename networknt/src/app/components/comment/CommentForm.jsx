import React from 'react';
import ReactDOM from 'react-dom';
import TextField from 'material-ui/lib/TextField';
import Tabs from 'material-ui/lib/tabs/tabs';
import Tab from 'material-ui/lib/tabs/tab';
import RaisedButton from 'material-ui/lib/raised-button';
import Markdown from '../Markdown';

var CommentForm = React.createClass({

    propTypes: {
        parentRid: React.PropTypes.string
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
        this.props.onCommentSubmit(this.props.parentRid, this.state.value);
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
                <RaisedButton primary={true} label="Reply" onTouchTap={this.onReply} />
            </div>
        );
    }
});

module.exports = CommentForm;

/*
 <form style={{width: '100%'}} className='new-comment-form' onSubmit={this.handleSubmit}>
 <Tabs initialSelectedIndex={1}>
 <Tab label="Content">
 <TextField hintText="Reply to thread..." multiLine={true} rows={4} onChange={this.onChange} />
 </Tab>
 <Tab label="Review">
 <Markdown text={this.state.value} />
 </Tab>
 </Tabs>

 <br />
 <input type="submit" value="reply" />
 </form>

 */
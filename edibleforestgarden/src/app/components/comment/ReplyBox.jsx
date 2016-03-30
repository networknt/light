var React = require('react');
import TextField from 'material-ui/lib/TextField';
import Tabs from 'material-ui/lib/tabs/tabs';
import Tab from 'material-ui/lib/tabs/tab';
import IconButton from 'material-ui/lib/icon-button';
import Markdown from '../Markdown';

var ReplyBox = React.createClass({
    propTypes: {
        onAddComment: React.PropTypes.func.isRequired
    },
    getInitialState: function() {
        return {
            isSubmitting: false,
            vlaue: ''
        };
    },

    onAddComment: function(text) {
        this.props.onAddComment(text);
        this.setState({isSubmitting: true});
    },

    render: function() {
        var ReplyButtonProps = {
            isSubmitting: this.state.isSubmitting,
            onAddComment: this.onAddComment
        };

        return (
            <span>
                <ReplyButton  {...ReplyButtonProps}/>
            </span>
        );
    }
});

var ReplyButton = React.createClass({
    propTypes: {
        isSubmitting: React.PropTypes.bool.isRequired,
        onAddComment: React.PropTypes.func.isRequired,
    },
    getInitialState: function() {
        return {isSubmitting: this.props.isSubmitting};
    },

    startSubmit: function() {
        this.setState({isSubmitting: true});
    },

    onAddComment: function(e) {
        this.setState({isSubmitting: false});
        var text = this.state.value;
        if (text === '') {
            return;
        }
        this.props.onAddComment(text);
    },

    getOnClick: function() {
        return this.state.isSubmitting ? this.onAddComment : this.startSubmit;
    },

    cancel: function() {
        this.setState({isSubmitting: false});
    },

    onChange: function(e) {
        this.setState({
            value: e.target.value
        })
    },

    render: function() {
        var textareaComponent, cancelButtonComponent;
        if (this.state.isSubmitting) {
            textareaComponent = (
                <div style={{width: '100%'}}>
                    <Tabs initialSelectedIndex={0}>
                        <Tab label="Content">
                            <TextField style={{width: '100%'}} hintText="Reply to thread..." multiLine={true} rows={4} onChange={this.onChange} />
                        </Tab>
                        <Tab label="Review">
                            <Markdown text={this.state.value} />
                        </Tab>
                    </Tabs>
                </div>
            );

            cancelButtonComponent = (
                <span>
                    {' • '}
                    <a className={"reply-box-cancel-button"} onClick={this.cancel}>cancel</a>
                </span>
            );
        }

        return (
            <span className={"reply-box-banner"}>
                {textareaComponent}
                <a className={"reply-box-reply-button"} onClick={this.getOnClick()}>reply</a>
                {cancelButtonComponent}
            </span>
        );
    }
});

module.exports = ReplyBox;

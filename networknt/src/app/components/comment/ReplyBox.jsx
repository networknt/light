var React = require('react');
import TextField from 'material-ui/lib/TextField';
import Tabs from 'material-ui/lib/tabs/tabs';
import Tab from 'material-ui/lib/tabs/tab';
import Markdown from '../Markdown';

var ReplyBox = React.createClass({
    propTypes: {
        onReply: React.PropTypes.func.isRequired,
        defaultClassName: React.PropTypes.string.isRequired
    },
    getInitialState: function() {
        return {
            isSubmitting: false,
            vlaue: ''
        };
    },

    handleReplySubmit: function(text) {
        this.props.onReply(text);
        this.setState({isSubmitting: true});
    },
    render: function() {
        var ReplyButtonProps = {
            isSubmitting: this.state.isSubmitting,
            onReplySubmit: this.handleReplySubmit,
            defaultClassName: this.props.defaultClassName
        };

        return (
            <span className={this.props.defaultClassName}>
                <ReplyButton  {...ReplyButtonProps}/>
            </span>
        );
    }
});

var ReplyButton = React.createClass({
    propTypes: {
        isSubmitting: React.PropTypes.bool.isRequired,
        onReplySubmit: React.PropTypes.func.isRequired,
        defaultClassName: React.PropTypes.string.isRequired
    },
    getInitialState: function() {
        return {isSubmitting: this.props.isSubmitting};
    },

    startSubmit: function() {
        this.setState({isSubmitting: true});
    },

    handleSubmit: function(e) {
        this.setState({isSubmitting: false});
        var text = this.state.value;
        if (text === '') {
            return;
        }
        this.props.onReplySubmit(text);
    },

    getOnClickFunction: function() {
        return this.state.isSubmitting ? this.handleSubmit : this.startSubmit;
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
                    <a className={this.props.defaultClassName + "-cancel-button"} onClick={this.cancel}>cancel</a>
                </span>
            );
        }

        return (
            <span className={this.props.defaultClassName + "-banner"}>
                {textareaComponent}
                <a className={this.props.defaultClassName + "-reply-button"} onClick={this.getOnClickFunction()}>reply</a>
                {cancelButtonComponent}
            </span>
        );
    }
});

module.exports = ReplyBox;

import React from 'react';
import ReactDOM from 'react-dom';
import TextField from 'material-ui/lib/TextField';

var CommentForm = React.createClass({


    propTypes: {
        parentRid: React.PropTypes.string
    },

    handleSubmit: function(e) {
        e.preventDefault();

        var message = ReactDOM.findDOMNode(this.refs.message).value.trim();

        if (!message) {
            return;
        }

        this.props.onCommentSubmit(this.props.parentRid, message);
        ReactDOM.findDOMNode(this.refs.message).value = '';
    },
    render: function() {
        return (
            <form className='new-comment-form' onSubmit={this.handleSubmit}>
                <TextField hintText="Reply to thread..." multiLine={true} rows={4} ref='message'/>
                <br />
                <input type="submit" value="reply" />
            </form>
        );
    }
});

module.exports = CommentForm;

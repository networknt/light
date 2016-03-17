import React from 'react';
import ReactDOM from 'react-dom';

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
                <textarea type='text' ref='message' maxLength='300' placeholder='Reply to thread...' className="thread-reply-textarea"/>
                <br />
                <input type="submit" value="reply" />
            </form>
        );
    }
});

module.exports = CommentForm;

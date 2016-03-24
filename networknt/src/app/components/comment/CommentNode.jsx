import React from 'react';
import classNames from 'classnames';
import Card from 'material-ui/lib/card/card';
import CardActions from 'material-ui/lib/card/card-actions';
import CardHeader from 'material-ui/lib/card/card-header';
import CardTitle from 'material-ui/lib/card/card-title';
import CardText from 'material-ui/lib/card/card-text';
import RaisedButton from 'material-ui/lib/raised-button';
import Gravatar from '../Gravatar';
import Avatar from 'material-ui/lib/avatar';
import IconButton from 'material-ui/lib/icon-button';
import ReplyBox from './ReplyBox';

var CommentNode = React.createClass({

    getInitialState: function() {
        return {
            comments: []
        };
    },

    onCategorySelect: function (ev) {
        if (this.props.onCommentSelect) {
            this.props.onCommentSelect(this);
        }
        ev.preventDefault();
        ev.stopPropagation();
    },

    onChildDisplayToggle: function (ev) {
        if (this.props.comment.out_HasComment) {
            if (this.state.out_HasComment && this.state.out_HasComment.length) {
                this.setState({out_HasComment: null});
            } else {
                this.setState({out_HasComment: this.props.comment.out_HasComment});
            }
        }
        ev.preventDefault();
        ev.stopPropagation();
    },

    handleReply: function(rid, text) {
        console.log('handleReply', rid, text);
        this.props.onReply(rid, text);
    },

    render: function () {
        if (!this.state.out_HasComment) this.state.out_HasComment = [];
        var classes = classNames({
            'has-comment': (this.props.comment.out_HasComment ? true : false),
            'open': (this.state.out_HasComment.length ? true : false),
            'closed': (this.state.out_HasComment ? false : true),
            'selected': (this.state.selected ? true : false)
        });
        console.log('this.props', this.props);
        let boundHandleReply = this.handleReply.bind(this, this.props.comment['@rid']);
        return (
            <li ref="node" className={classes}
                onClick={this.onChildDisplayToggle}>
                <Gravatar md5={this.props.comment.gravatar} />
                <div style={{display: 'inline-block', verticalAlign: 'top', paddingRight: '90px'}}>
                    <span style={{display: 'block', fontSize: 13}}>{'Submitted by ' + this.props.comment.in_Create[0].userId + ' on ' + this.props.comment.createDate}</span>
                    <span style={{fontSize: 14}}>2<IconButton iconStyle={{width: '12px', height:'12px', padding: '1px'}} style={{width: '48px', height: '48px', padding: '1px'}} iconClassName="material-icons" tooltip='Refresh' onTouchTap={this._onRefresh}>expand_less</IconButton><IconButton iconClassName="material-icons" tooltip='Refresh' onTouchTap={this._onRefresh}>expand_more</IconButton>5<IconButton iconClassName="material-icons" tooltip='Refresh' onTouchTap={this._onRefresh}>visibility_off</IconButton><IconButton iconClassName="material-icons" tooltip='Refresh' onTouchTap={this._onRefresh}>reply</IconButton></span>
                    <ReplyBox onReply={boundHandleReply} defaultClassName="reply-box"/>
                </div>
                <span style={{display: 'block', fontSize: 15}}>{this.props.comment.comment}</span>
                <ul>
                    {this.state.out_HasComment.map(function(child) {
                        return <CommentNode key={child.commentId}
                                         comment={child}
                                         onCommentSelect={this.props.onCommentSelect}/>;
                    }.bind(this))}
                </ul>
            </li>
        );
    }
});

module.exports = CommentNode;
/*
 <CardHeader title={this.props.comment.comment} subtitle= {'Submitted by ' + this.props.comment.userId + ' on ' + this.props.comment.createDate} avatar={<Avatar icon={<Gravatar md5={this.props.comment.gravatar} />} />} />

 */
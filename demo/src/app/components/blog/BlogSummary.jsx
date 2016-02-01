import React from 'react';
import Paper from 'material-ui/lib/paper';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';

class BlogSummary extends React.Component {

    render() {
        return (
            <Paper className="blogPostPaper">
                <div className="blogPost">
                    <Gravatar md5={this.props.post.gravatar} /><h2 className="title"><a onClick={this.props.onClick}>{this.props.post.title}</a></h2>
                    <span>Submitted by {this.props.post.createUserId} on {this.props.post.createDate}</span>
                    <Markdown text={this.props.post.summary} />
                </div>
            </Paper>
        );
    }
}

BlogSummary.propTypes = {
    post: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired
};

export default BlogSummary;

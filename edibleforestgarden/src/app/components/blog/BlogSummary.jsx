import React from 'react';
import Paper from 'material-ui/lib/paper';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';

class BlogSummary extends React.Component {

    render() {
        return (
            <Paper className="blogPostPaper">
                <div className="blogPost">
                    <Gravatar md5={post.gravatar} /><h2 className="title"><a onClick={boundClick}>{post.title}</a></h2>
                    <span>Submitted by {post.createUserId} on {post.createDate}</span>
                    <Markdown text={post.summary} />
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

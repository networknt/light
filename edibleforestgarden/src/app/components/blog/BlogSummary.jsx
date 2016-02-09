import React from 'react';
import Paper from 'material-ui/lib/paper';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';
import moment from 'moment';

class BlogSummary extends React.Component {

    render() {
        let time = moment(this.props.post.createDate).format("DD-MM-YYYY HH:mm:ss");
        return (
            <Paper className="blogPostPaper">
                <div className="blogPost">
                    <Gravatar md5={this.props.post.gravatar} /><h2 className="title"><a onClick={this.props.onClick}>{this.props.post.title}</a></h2>
                    <span>Submitted by {this.props.post.createUserId} on {time}</span>
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

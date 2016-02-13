import React from 'react';
import Paper from 'material-ui/lib/paper';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';
import moment from 'moment';

import Card from 'material-ui/lib/card/card';
import CardActions from 'material-ui/lib/card/card-actions';
import CardHeader from 'material-ui/lib/card/card-header';
import CardTitle from 'material-ui/lib/card/card-title';
import CardText from 'material-ui/lib/card/card-text';
import RaisedButton from 'material-ui/lib/raised-button';
import Avatar from 'material-ui/lib/avatar';

// Deprecated, use common/Summary instead.
class BlogSummary extends React.Component {

    render() {
        let time = moment(this.props.post.createDate).format("DD-MM-YYYY HH:mm:ss");
        return (
            <Paper>
                <Card>
                    <CardHeader title={"Created by: " + this.props.post.createUserId} subtitle= {'Submitted by ' + this.props.post.createUserId + "On: " + time} avatar={<Avatar icon={<Gravatar md5={this.props.post.gravatar} />} />} />
                    <CardTitle title={this.props.post.title}/>
                    <CardText>
                        <Markdown text={this.props.post.summary} />
                    </CardText>
                    <CardActions>
                        <RaisedButton label="Read More" primary={true} onClick={this.props.onClick}/>
                    </CardActions>
                </Card>
            </Paper>
        );
    }
}

BlogSummary.propTypes = {
    post: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired
};

export default BlogSummary;

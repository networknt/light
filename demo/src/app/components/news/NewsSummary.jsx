import React from 'react';
import Paper from 'material-ui/lib/paper';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';

import Card from 'material-ui/lib/card/card';
import CardActions from 'material-ui/lib/card/card-actions';
import CardHeader from 'material-ui/lib/card/card-header';
import CardTitle from 'material-ui/lib/card/card-title';
import CardText from 'material-ui/lib/card/card-text';
import RaisedButton from 'material-ui/lib/raised-button';
import Avatar from 'material-ui/lib/avatar';

// Deprecated, use common/Summary instead.
class NewsSummary extends React.Component {

    render() {
        return (
            <Paper>
                <Card>
                    <CardHeader title={"Created by: " + this.props.post.createUserId} subtitle= {"On: " + this.props.post.createDate} avatar={<Avatar icon={<Gravatar md5={this.props.post.gravatar} />} />} />
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

NewsSummary.propTypes = {
    post: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired
};

export default NewsSummary;

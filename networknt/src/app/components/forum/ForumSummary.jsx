import React from 'react';
import Paper from 'material-ui/lib/paper';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';
import moment from 'moment';
import CommonUtils from '../../utils/CommonUtils';
import Card from 'material-ui/lib/card/card';
import CardActions from 'material-ui/lib/card/card-actions';
import CardHeader from 'material-ui/lib/card/card-header';
import CardTitle from 'material-ui/lib/card/card-title';
import CardText from 'material-ui/lib/card/card-text';
import RaisedButton from 'material-ui/lib/raised-button';
import Avatar from 'material-ui/lib/avatar';

// Deprecated, use common/Summary instead.
class ForumSummary extends React.Component {

    render() {
        let time = moment(this.props.post.createDate).format("DD-MM-YYYY HH:mm:ss");
        return (
            <Paper>
                <Card>
                    <a onClick={this.props.onClick}><CardHeader title={this.props.post.title} subtitle= {'Submitted by ' + this.props.post.createUserId + ' on ' + time} avatar={<Avatar icon={<Gravatar md5={this.props.post.gravatar} />} />} /></a>
                </Card>
            </Paper>
        );
    }
}

ForumSummary.propTypes = {
    post: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired
};

export default ForumSummary;

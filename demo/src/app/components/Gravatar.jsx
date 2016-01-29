import React from 'react';
import Avatar from 'material-ui/lib/avatar';

const Gravatar = React.createClass({

    propTypes: {
        md5: React.PropTypes.string,
        https: React.PropTypes.bool
    },

    getDefaultProps() {
        return {
            https: true
        };
    },

    render() {
        let base = 'https://secure.gravatar.com/avatar/';
        if(this.props.https == false) {
            base = 'http://www.gravatar.com/avatar/'
        }
        let src = base + this.props.md5;
        return (<Avatar src={src} />)
    }
});

export default Gravatar;

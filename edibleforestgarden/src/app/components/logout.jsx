import React from 'react';
import AuthActionCreators from '../actions/AuthActionCreators';

class Logout extends React.Component {

    componentDidMount() {
        AuthActionCreators.logout();
    }

    render() {
        return (
            <h2>Logout</h2>
        );
    }
}

export default Logout;

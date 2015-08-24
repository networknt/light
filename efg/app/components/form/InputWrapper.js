/**
 * Created by steve on 22/08/15.
 */
var React = require('react');

class InputWrapper extends React.Component {

    render() {
        let className = classNames('form-group', {
            'has-error': this.props.errors !== undefined
        });

        return (
            <div className={className}>
                {this.props.children}
                {this.props.errors}
            </div>
        );
    }

}

InputWrapper.propTypes = {
    errors: React.PropTypes.array
};

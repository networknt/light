/**
 * Created by steve on 22/08/15.
 */
var React = require('react');

class TextareaInput extends BaseInput {

    render() {
        return (
            <InputWrapper errors={this.props.errors}>
                <label>{this.props.name}</label>
        <textarea
            className={this.props.className}
            name={this.props.name}
            value={this.props.value}
            onChange={this.handleChange} />
            </InputWrapper>
        )
    }

}
import React from 'react';
import Menu from 'material-ui/lib/menus/menu';
import MenuItem from 'material-ui/lib/menus/menu-item';

let menuItems = [
    { route: '/admin/dbAdmin/exportDatabase', text: 'Export Database' },
    { route: '/admin/form/com.networknt.light.db.execSchemaCommand', text: 'Execute Schema Command' },
    { route: '/admin/form/com.networknt.light.db.execUpdateCommand', text: 'Execute Update Command' },
    { route: '/admin/dbAdmin/execQueryCommand', text: 'Execute Query Command' },
    { route: '/admin/dbAdmin/downloadEvent', text: 'Download Event' },
    { route: '/admin/form/com.networknt.light.db.replayEvent', text: 'Replay Event' }
];

class DbAdminHome extends React.Component {

    constructor(props) {
        super(props);
        this._onItemTouchTap = this._onItemTouchTap.bind(this);
    }

    _onItemTouchTap(event, item) {
        console.log('DbAdminHome props', this.props);
        this.props.history.push(item.props.value);
    }

    render() {
        return (
            <Menu onItemTouchTap={this._onItemTouchTap}>
                {menuItems.map((item, index) => {
                    return (
                        <MenuItem
                            key={index}
                            primaryText={item.text}
                            value={item.route}
                            />
                    );
                })}
            </Menu>
        );
    }
}

export default DbAdminHome;

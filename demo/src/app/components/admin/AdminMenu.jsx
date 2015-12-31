import React from 'react';
import Menu from 'material-ui/lib/menus/menu';
import MenuItem from 'material-ui/lib/menus/menu-item';

let menuItems = [
    { route: 'admin/blogAdmin', text: 'Blog Admin' },
    { route: 'admin/newsAdmin', text: 'News Admin' },
    { route: 'admin/forumAdmin', text: 'Forum Admin' },
    { route: 'admin/catalogAdmin', text: 'Catalog Admin' },
    { route: 'admin/userAdmin', text: 'User Admin' }
];

class AdminMenu extends React.Component {

    constructor(props) {
        super(props);
        this._onItemTouchTap = this._onItemTouchTap.bind(this);
    }

    _onItemTouchTap(event, item) {
        console.log('AdminMenu props', this.props);
        console.log('AdminMenu._onItemTouchTap', item.props.value);
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

export default AdminMenu;
